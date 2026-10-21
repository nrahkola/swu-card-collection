package syksy26.swu_tracker.web;

import java.security.Principal;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import syksy26.swu_tracker.domain.AspectRepository;
import syksy26.swu_tracker.domain.Card;
import syksy26.swu_tracker.domain.CardRepository;
import syksy26.swu_tracker.domain.ExpansionSetRepository;
import syksy26.swu_tracker.domain.Trait;
import syksy26.swu_tracker.domain.TraitRepository;

@Controller
public class CardController {

    private static final Map<String, Integer> SET_ORDER = Map.ofEntries(
            Map.entry("SOR", 1),
            Map.entry("SHD", 2),
            Map.entry("TWI", 3),
            Map.entry("JTL", 4),
            Map.entry("LOF", 5),
            Map.entry("IBH", 6),
            Map.entry("SEC", 7),
            Map.entry("LAW", 8),
            Map.entry("ASH", 9),
            Map.entry("HMW", 10));

    private final CardRepository cardRepository;
    private final AspectRepository aspectRepository;
    private final ExpansionSetRepository expansionSetRepository;
    private final TraitRepository traitRepository;

    public CardController(CardRepository cardRepository,
                          AspectRepository aspectRepository,
                          ExpansionSetRepository expansionSetRepository,
                          TraitRepository traitRepository) {
        this.cardRepository = cardRepository;
        this.aspectRepository = aspectRepository;
        this.expansionSetRepository = expansionSetRepository;
        this.traitRepository = traitRepository;
    }

    @GetMapping("/api/cards")
    @ResponseBody
    public List<Card> getCardsRest() {
        return (List<Card>) cardRepository.findAll();
    }

    @GetMapping({ "/", "/cards", "/cardlist" })
    public String showCardList(
            @RequestParam(name = "sets", required = false) List<String> sets,
            @RequestParam(name = "aspects", required = false) List<String> aspects,
            @RequestParam(name = "types", required = false) List<String> types,
            Model model,
            Principal principal) {

        boolean hasSets = (sets != null && !sets.isEmpty());
        boolean hasAspects = (aspects != null && !aspects.isEmpty());
        boolean hasTypes = (types != null && !types.isEmpty());

        List<Card> cardList;

        if (!hasSets && !hasAspects && !hasTypes) {
            cardList = cardRepository.findAllInChronologicalOrder();
        } else {
            cardList = cardRepository.findCardsFiltered(
                    hasSets,
                    hasSets ? sets : List.of(""),
                    hasAspects,
                    hasAspects ? aspects : List.of(""),
                    hasTypes,
                    hasTypes ? types : List.of(""));

            cardList.sort((c1, c2) -> {
                String code1 = (c1.getExpansionSet() != null) ? c1.getExpansionSet().getCode() : "";
                String code2 = (c2.getExpansionSet() != null) ? c2.getExpansionSet().getCode() : "";

                int setOrder1 = SET_ORDER.getOrDefault(code1, 99);
                int setOrder2 = SET_ORDER.getOrDefault(code2, 99);

                int setCompare = Integer.compare(setOrder1, setOrder2);
                if (setCompare != 0) {
                    return setCompare;
                }

                String num1 = (c1.getCardNumber() != null) ? c1.getCardNumber() : "";
                String num2 = (c2.getCardNumber() != null) ? c2.getCardNumber() : "";
                return num1.compareTo(num2);
            });
        }

        model.addAttribute("cards", cardList);
        model.addAttribute("allSets", expansionSetRepository.findAll());
        model.addAttribute("allAspects", aspectRepository.findAll());
        model.addAttribute("allTypes", List.of("Leader", "Base", "Unit", "Event", "Upgrade"));
        model.addAttribute("selectedSets", sets != null ? sets : List.of());
        model.addAttribute("selectedAspects", aspects != null ? aspects : List.of());
        model.addAttribute("selectedTypes", types != null ? types : List.of());
        model.addAttribute("username", principal != null ? principal.getName() : "User");

        return "cardlist";
    }

    @GetMapping("/delete/{id}")
    public String deleteCard(@PathVariable("id") Long id) {
        cardRepository.deleteById(id);
        return "redirect:/cards";
    }

    @GetMapping("/add")
    public String showAddForm(Model model) {
        model.addAttribute("card", new Card());
        model.addAttribute("allSets", expansionSetRepository.findAll());
        model.addAttribute("allAspects", aspectRepository.findAll());
        return "addcard";
    }

    @PostMapping("/save")
    public String saveNewCard(@ModelAttribute("card") Card card,
                              @RequestParam(name = "traitNames", required = false) String traitNames) {
        card.setTraits(parseTraits(traitNames));
        cardRepository.save(card);
        return "redirect:/cards";
    }

    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable("id") Long id, Model model) {
        Card card = cardRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Virheellinen kortin ID: " + id));
        model.addAttribute("card", card);
        model.addAttribute("allSets", expansionSetRepository.findAll());
        model.addAttribute("allAspects", aspectRepository.findAll());
        return "editcard";
    }

    @PostMapping("/edit")
    public String saveEditedCard(@ModelAttribute("card") Card card,
                                 @RequestParam(name = "traitNames", required = false) String traitNames) {
        card.setTraits(parseTraits(traitNames));
        cardRepository.save(card);
        return "redirect:/cards";
    }

    // Apumetodi: pilkkoo pilkulla erotetut piirteet ja tallentaa puuttuvat kantaan
    private Set<Trait> parseTraits(String traitNames) {
        Set<Trait> traits = new HashSet<>();
        if (traitNames != null && !traitNames.trim().isEmpty()) {
            for (String name : traitNames.split(",")) {
                String cleanName = name.trim();
                if (!cleanName.isEmpty()) {
                    Trait trait = traitRepository.findByName(cleanName);
                    if (trait == null) {
                        trait = traitRepository.save(new Trait(cleanName));
                    }
                    traits.add(trait);
                }
            }
        }
        return traits;
    }
}