# Star Wars: Unlimited Card Tracker (SWU Tracker)

Tämä projekti on Haaga-Helian opintoihin liittyvä back end-verkkosovellus, joka toimii kortti- ja dekkityökaluna Star Wars: Unlimited (SWU) -keräilykorttipelille. Sovellus mahdollistaa korttivalikoiman selailun, suodatuksen ja tietojen hallinnan.

## 🚀 Live-versio
Sovellus on julkaistu ja ajossa Rahdissa:
👉 [https://swu-card-collection-swu-tracker.2.rahtiapp.fi](https://swu-card-collection-swu-tracker.2.rahtiapp.fi)

## 🛠️ Teknologiat
* **Backend:** Java, Spring Boot, Spring Data JPA, Spring Security, Thymeleaf
* **Tietokanta:** PostgreSQL (hosting: Pukki)
* **Data Pipeline (ETL):** Python (`requests`, `SQLAlchemy`, `psycopg2`)
* **Deployment & Infastructure:** CSC Rahti (OpenShift / Kubernetes), Docker, Git & GitHub

## 📂 Arkkitehtuuri
Projektissa on erillinen **data-pipeline**-kansio, joka sisältää Python-skriptin korttitietojen hakemiseen SWU-DB API:sta. Skripti parsii ja lataa kortit, setit, aspektit ja kuvat suoraan PostgreSQL-tietokantaan.

### Tietokannan vaatimukset (`card`-taulu)
Korttitaulu vaatii uniikkirajoitteen `ON CONFLICT` -päivityksiä varten:
```sql
ALTER TABLE card 
ADD CONSTRAINT uk_card_set_number UNIQUE (expansion_set_id, card_number);
```

## 🔑 Testitunnukset
Sovelluksessa on käytössä testitunnukset, joilla pääset kirjautumaan sisään:
* **Tavallinen käyttäjä:** `user` / `user123`
* **Pääkäyttäjä (Admin):** `admin` / `admin123`

## 🔮 Tulevaisuudensuunnitelmat / Jatkokehitys

Projekti on tällä hetkellä perustoimiva, mutta siihen on suunnitteilla seuraavia lisäominaisuuksia:
* Käyttäjien omien korttikokoelmien ja pakkojen (Decks) hallinta.
* Parannettu suodatus- ja hakutoiminnallisuus korteille.
* Mahdollinen korttien hintojen seuranta ulkoisesta rajapinnasta.
* Käyttäjähallinnan kehitys: Siirtyminen kovakoodatuista testitunnuksista tietokantapohjaiseen käyttäjärekisteriin (Spring Security + JPA) ja roolipohjaisen hallinnan laajentaminen.

## 📝 Tekijä
Niko Rahkola  

Business Information Technology (IT-tradenomi), Haaga-Helia ammattikorkeakoulu