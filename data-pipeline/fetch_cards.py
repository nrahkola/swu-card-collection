import os
import time
import requests
from sqlalchemy import create_engine, text

# Luetaan tietokannan tiedot turvallisesti ympäristömuuttujista
DB_USER = os.getenv("DB_USER", "swuadmin")
DB_PASSWORD = os.getenv("DB_PASSWORD", "salasana_tähän")
DB_HOST = os.getenv("POSTGRESQL_SERVICE_HOST", "localhost")
DB_NAME = os.getenv("DB_NAME", "swu-tracker")

DATABASE_URL = f"postgresql+psycopg2://{DB_USER}:{DB_PASSWORD}@{DB_HOST}:5432/{DB_NAME}"

engine = create_engine(DATABASE_URL)

SETIT = ["SOR", "SHD", "TWI", "JTL", "LOF", "IBH", "SEC", "LAW", "ASH", "HMW"]

SETTI_NIMET = {
    "SOR": "Spark of Rebellion",
    "SHD": "Shadows of the Galaxy",
    "TWI": "Twilight of the Republic",
    "JTL": "Jump to Lightspeed",
    "LOF": "Legends of the Force",
    "IBH": "Intro Battle: Hoth",
    "SEC": "Secrets of Power",
    "LAW": "A Lawless Time",
    "ASH": "Ashes of the Empire",
    "HMW": "Homeworlds",
}


def muunna_int(arvo):
    """Apufunktio muuttamaan API-arvot turvallisesti kokonaisluvuiksi."""
    try:
        return int(arvo) if arvo is not None else None
    except (ValueError, TypeError):
        return None


def hae_ja_siivoa_data():
    """Hakee SWU-DB:n API:sta kortit ja muotoilee ne tietokantaa varten."""
    headers = {"User-Agent": "Mozilla/5.0"}
    kaikki_siivotut_kortit = []

    print("1/3 Haetaan korttidataa kaikista seteistä...")

    for setti in SETIT:
        url = f"https://api.swu-db.com/cards/{setti.lower()}"
        print(f" -> Haetaan setti: {setti}...")

        try:
            vastaus = requests.get(url, headers=headers, timeout=20)
            vastaus.raise_for_status()
            korttilista = vastaus.json()

            if isinstance(korttilista, dict) and "data" in korttilista:
                korttilista = korttilista["data"]

            loytyneet = 0
            for kortti in korttilista:
                if kortti.get("VariantType") == "Normal":
                    nimi = kortti.get("Name")
                    setti_koodi = kortti.get("Set")
                    kortti_nro = str(
                        kortti.get("CardNumber") or kortti.get("Number") or ""
                    ).strip()

                    # Piirteet (traits)
                    traitit_raw = kortti.get("Traits")
                    traitit_lista = traitit_raw if isinstance(traitit_raw, list) else []

                    # Aspektit (aspects) - huomioi myös neutraalit kortit
                    aspektit_raw = kortti.get("Aspects")
                    aspektit_lista = aspektit_raw if isinstance(aspektit_raw, list) else []

                    if nimi and setti_koodi and kortti_nro:
                        kaikki_siivotut_kortit.append({
                            "nimi": nimi,
                            "setti": setti_koodi,
                            "kortti_numero": kortti_nro,
                            "tyyppi": kortti.get("Type"),
                            "hinta": muunna_int(kortti.get("Cost")),
                            "power": muunna_int(kortti.get("Power")),
                            "hp": muunna_int(kortti.get("HP")),
                            "harvinaisuus": kortti.get("Rarity"),
                            "kuva_url": kortti.get("FrontArt"),
                            "traitit": [t.strip() for t in traitit_lista if t],
                            "aspektit": [a.strip() for a in aspektit_lista if a],
                        })
                        loytyneet += 1

            print(f"    OK! Löytyi {loytyneet} peruskorttia.")
            time.sleep(0.3)

        except requests.exceptions.RequestException as e:
            print(f"    Virhe haettaessa settiä {setti}: {e}")

    print(f"\nData noudettu! Yhteensä poimittu {len(kaikki_siivotut_kortit)} korttia.")
    return kaikki_siivotut_kortit


def tallenna_tietokantaan(kortit):
    """Tallentaa ja synkronoi kortit, setit, piirteet ja aspektit tietokantaan."""
    if not kortit:
        print("Ei kortteja tallennettavaksi.")
        return

    print("2/3 Synkronoidaan setit, piirteet ja aspektit PostgreSQL-kantaan...")

    with engine.begin() as conn:
        # 1. Synkronoidaan laajennussetit
        loytyneet_setit = {k["setti"] for k in kortit}
        for code in loytyneet_setit:
            nimi = SETTI_NIMET.get(code, code)
            conn.execute(
                text("""
                    INSERT INTO expansion_set (code, name)
                    VALUES (:code, :name)
                    ON CONFLICT (code) DO UPDATE SET name = EXCLUDED.name;
                """),
                {"code": code, "name": nimi},
            )

        set_rows = conn.execute(text("SELECT id, code FROM expansion_set")).fetchall()
        set_map = {row[1]: row[0] for row in set_rows}

        # 2. Synkronoidaan uniikit piirteet (traits)
        kaikki_traitit = {t for k in kortit for t in k["traitit"]}
        for trait in kaikki_traitit:
            conn.execute(
                text("""
                    INSERT INTO trait (name)
                    SELECT :name
                    WHERE NOT EXISTS (SELECT 1 FROM trait WHERE name = :name);
                """),
                {"name": trait},
            )

        trait_rows = conn.execute(text("SELECT id, name FROM trait")).fetchall()
        trait_map = {row[1]: row[0] for row in trait_rows}

        # 3. Synkronoidaan uniikit aspektit (aspects)
        kaikki_aspektit = {a for k in kortit for a in k["aspektit"]}
        for aspect in kaikki_aspektit:
            conn.execute(
                text("""
                    INSERT INTO aspect (name)
                    SELECT :name
                    WHERE NOT EXISTS (SELECT 1 FROM aspect WHERE name = :name);
                """),
                {"name": aspect},
            )

        aspect_rows = conn.execute(text("SELECT id, name FROM aspect")).fetchall()
        aspect_map = {row[1]: row[0] for row in aspect_rows}

        # 4. Tallennetaan kortit ja muodostetaan liitosrivit
        print("3/3 Tallennetaan kortit ja liitostaulujen rivit...")
        
        card_traits_to_insert = []
        card_aspects_to_insert = []

        for k in kortit:
            exp_id = set_map.get(k["setti"])
            if not exp_id:
                continue

            res = conn.execute(
                text("""
                    INSERT INTO card (name, card_number, type, cost, power, hp, rarity, image_url, expansion_set_id)
                    VALUES (:name, :card_number, :type, :cost, :power, :hp, :rarity, :image_url, :expansion_set_id)
                    ON CONFLICT (expansion_set_id, card_number)
                    DO UPDATE SET
                        name = EXCLUDED.name,
                        type = EXCLUDED.type,
                        cost = EXCLUDED.cost,
                        power = EXCLUDED.power,
                        hp = EXCLUDED.hp,
                        rarity = EXCLUDED.rarity,
                        image_url = EXCLUDED.image_url
                    RETURNING id;
                """),
                {
                    "name": k["nimi"],
                    "card_number": k["kortti_numero"],
                    "type": k["tyyppi"],
                    "cost": k["hinta"],
                    "power": k["power"],
                    "hp": k["hp"],
                    "rarity": k["harvinaisuus"],
                    "image_url": k["kuva_url"],
                    "expansion_set_id": exp_id,
                },
            )
            card_id = res.scalar()

            # Kerätään piirteiden liitokset
            for t in k["traitit"]:
                t_id = trait_map.get(t)
                if t_id and card_id:
                    card_traits_to_insert.append({"card_id": card_id, "trait_id": t_id})

            # Kerätään aspektien liitokset
            for a in k["aspektit"]:
                a_id = aspect_map.get(a)
                if a_id and card_id:
                    card_aspects_to_insert.append({"card_id": card_id, "aspect_id": a_id})

        # 5. Tyhjennetään vanhat ja lisätään uudet liitokset nopeasti eräajona
        if card_traits_to_insert:
            conn.execute(
                text("""
                    INSERT INTO card_trait (card_id, trait_id)
                    VALUES (:card_id, :trait_id)
                    ON CONFLICT DO NOTHING;
                """),
                card_traits_to_insert,
            )

        if card_aspects_to_insert:
            conn.execute(
                text("""
                    INSERT INTO card_aspect (card_id, aspect_id)
                    VALUES (:card_id, :aspect_id)
                    ON CONFLICT DO NOTHING;
                """),
                card_aspects_to_insert,
            )

    print("\nValmis! Koko korttikanta ja aspektit on nyt synkronoitu tietokantaan.")


if __name__ == "__main__":
    kortit = hae_ja_siivoa_data()
    tallenna_tietokantaan(kortit)