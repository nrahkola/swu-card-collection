package syksy26.swu_tracker;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import syksy26.swu_tracker.domain.Card;
import syksy26.swu_tracker.domain.CardRepository;
import syksy26.swu_tracker.domain.ExpansionSet;
import syksy26.swu_tracker.domain.ExpansionSetRepository;

@SpringBootTest
class CardRepositoryTest {

    @Autowired
    private CardRepository cardRepository;

    @Autowired
    private ExpansionSetRepository expansionSetRepository;

    @Test
    void createNewCard() {
        ExpansionSet set = expansionSetRepository.findAll().iterator().next();

        Card card = new Card();
        card.setName("Luke Skywalker");
        card.setCardNumber("998");
        card.setType("Unit");
        card.setRarity("Legendary");
        card.setExpansionSet(set);

        cardRepository.save(card);
        assertThat(card.getId()).isNotNull();
    }

    @Test
    void findAllCardsShouldNotBeEmpty() {
        List<Card> cards = (List<Card>) cardRepository.findAll();
        assertThat(cards).isNotEmpty();
    }

    @Test
    void deleteCard() {
        ExpansionSet set = expansionSetRepository.findAll().iterator().next();

        Card card = new Card();
        card.setName("Test Unit");
        card.setCardNumber("999");
        card.setType("Unit");
        card.setRarity("Common");
        card.setExpansionSet(set);

        cardRepository.save(card);
        Long id = card.getId();

        cardRepository.deleteById(id);
        assertThat(cardRepository.findById(id)).isEmpty();
    }
}