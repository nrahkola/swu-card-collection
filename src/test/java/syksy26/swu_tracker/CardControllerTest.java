package syksy26.swu_tracker;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.ui.ExtendedModelMap;
import org.springframework.ui.Model;

import syksy26.swu_tracker.web.CardController;

@SpringBootTest
class CardControllerTest {

    @Autowired
    private CardController cardController;

    @Test
    void cardListReturnsViewAndPopulatesModel() {
        Model model = new ExtendedModelMap();
        String viewName = cardController.showCardList(null, null, null, model, null);

        assertThat(viewName).isEqualTo("cardlist");
        assertThat(model.asMap()).containsKey("cards");
    }
}