package syksy26.swu_tracker;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import syksy26.swu_tracker.domain.Card;
import syksy26.swu_tracker.web.CardRestController;

@SpringBootTest
class CardRestControllerTest {

    @Autowired
    private CardRestController cardRestController;

    @Test
    void getAllCardsRestReturnsList() {
        List<Card> cards = cardRestController.getAllCards();
        assertThat(cards).isNotNull();
    }
}