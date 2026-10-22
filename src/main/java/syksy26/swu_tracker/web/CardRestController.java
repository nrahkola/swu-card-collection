package syksy26.swu_tracker.web;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import syksy26.swu_tracker.domain.Card;
import syksy26.swu_tracker.domain.CardRepository;

@RestController
@RequestMapping("/api/cards")
public class CardRestController {

    @Autowired
    private CardRepository cardRepository;

    // Palauttaa kaikki kortit JSON-muodossa: GET /api/cards
    @GetMapping
    public List<Card> getAllCards() {
        return (List<Card>) cardRepository.findAll();
    }

    // Palauttaa yhden kortin ID:llä JSON-muodossa: GET /api/cards/{id}
    @GetMapping("/{id}")
    public Optional<Card> getCardById(@PathVariable("id") Long id) {
        return cardRepository.findById(id);
    }

    // Tallentaa uuden kortin JSON-rungosta: POST /api/cards
    @PostMapping
    public Card saveCardRest(@RequestBody Card card) {
        return cardRepository.save(card);
    }
}