package syksy26.swu_tracker;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import syksy26.swu_tracker.web.CardController;

@SpringBootTest
class SmokeTest {

    @Autowired
    private CardController cardController;

    @Test
    void contextLoads() {
        assertThat(cardController).isNotNull();
    }
}