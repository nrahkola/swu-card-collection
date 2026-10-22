package syksy26.swu_tracker.web;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
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

    @GetMapping
    public List<Card> getAllCards() {
        return (List<Card>) cardRepository.findAll();
    }

    @GetMapping("/{id}")
    public Optional<Card> getCardById(@PathVariable("id") Long id) {
        return cardRepository.findById(id);
    }

    @PostMapping
    public Card saveCardRest(@RequestBody Card card) {
        return cardRepository.save(card);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Card> updateCardRest(@PathVariable Long id, @RequestBody Card updatedCard) {
        return cardRepository.findById(id)
                .map(card -> {
                    card.setName(updatedCard.getName());
                    card.setCardNumber(updatedCard.getCardNumber());
                    card.setType(updatedCard.getType());
                    card.setCost(updatedCard.getCost());
                    card.setPower(updatedCard.getPower());
                    card.setHp(updatedCard.getHp());
                    card.setRarity(updatedCard.getRarity());
                    card.setImageUrl(updatedCard.getImageUrl());
                    card.setExpansionSet(updatedCard.getExpansionSet());
                    return ResponseEntity.ok(cardRepository.save(card));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCardRest(@PathVariable Long id) {
        if (cardRepository.existsById(id)) {
            cardRepository.deleteById(id);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }

}