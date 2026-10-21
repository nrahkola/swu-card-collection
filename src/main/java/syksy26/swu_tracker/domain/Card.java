package syksy26.swu_tracker.domain;

import java.util.HashSet;
import java.util.Set;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Entity
public class Card {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Nimi on pakollinen")
    private String name;

    private String type;

    @Min(value = 0, message = "Kustannus ei voi olla negatiivinen")
    private Integer cost;

    private Integer power;
    private Integer hp;
    private String rarity;
    private String imageUrl;
    @Column(name = "card_number")
    private String cardNumber;

    @NotNull(message = "Laajennussetti on valittava")
    @ManyToOne
    @JoinColumn(name = "expansion_set_id")
    private ExpansionSet expansionSet;

    @ManyToMany
    @JoinTable(
        name = "card_trait",
        joinColumns = @JoinColumn(name = "card_id"),
        inverseJoinColumns = @JoinColumn(name = "trait_id")
    )
    private Set<Trait> traits = new HashSet<>();

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
        name = "card_aspect",
        joinColumns = @JoinColumn(name = "card_id"),
        inverseJoinColumns = @JoinColumn(name = "aspect_id")
    )
    private Set<Aspect> aspects = new HashSet<>();

    public Card() {
    }

    public Card(String name, String cardNumber, String type, Integer cost, Integer power, Integer hp, String rarity, String imageUrl, ExpansionSet expansionSet) {
        this.name = name;
        this.cardNumber = cardNumber;
        this.type = type;
        this.cost = cost;
        this.power = power;
        this.hp = hp;
        this.rarity = rarity;
        this.imageUrl = imageUrl;
        this.expansionSet = expansionSet;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Integer getCost() {
        return cost;
    }

    public void setCost(Integer cost) {
        this.cost = cost;
    }

    public Integer getPower() {
        return power;
    }

    public void setPower(Integer power) {
        this.power = power;
    }

    public Integer getHp() {
        return hp;
    }

    public void setHp(Integer hp) {
        this.hp = hp;
    }

    public String getRarity() {
        return rarity;
    }

    public void setRarity(String rarity) {
        this.rarity = rarity;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public ExpansionSet getExpansionSet() {
        return expansionSet;
    }

    public void setExpansionSet(ExpansionSet expansionSet) {
        this.expansionSet = expansionSet;
    }

    public Set<Trait> getTraits() {
        return traits;
    }

    public void setTraits(Set<Trait> traits) {
        this.traits = traits;
    }

    public String getCardNumber() {
        return cardNumber;
    }

    public void setCardNumber(String cardNumber) {
        this.cardNumber = cardNumber;
    }

    public Set<Aspect> getAspects() {
    return aspects;
}

    public void setAspects(Set<Aspect> aspects) {
        this.aspects = aspects;
}
}
