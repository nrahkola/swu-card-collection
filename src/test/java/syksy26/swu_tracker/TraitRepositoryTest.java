package syksy26.swu_tracker;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import syksy26.swu_tracker.domain.Trait;
import syksy26.swu_tracker.domain.TraitRepository;

@SpringBootTest
class TraitRepositoryTest {

    @Autowired
    private TraitRepository traitRepository;

    @Test
    void createNewTrait() {
        Trait trait = new Trait("Trooper");
        traitRepository.save(trait);
        assertThat(trait.getId()).isNotNull();
    }

    @Test
    void findByNameIgnoreCaseShouldReturnTrait() {
        String uniqueName = "UniqueTestTrait" + System.currentTimeMillis();
        Trait trait = new Trait(uniqueName);
        traitRepository.save(trait);

        Optional<Trait> found = traitRepository.findByNameIgnoreCase(uniqueName.toLowerCase());
        assertThat(found).isPresent();
        assertThat(found.get().getName()).isEqualTo(uniqueName);
    }

    @Test
    void deleteTrait() {
        Trait trait = new Trait("Vehicle");
        traitRepository.save(trait);
        Long id = trait.getId();

        traitRepository.deleteById(id);
        assertThat(traitRepository.findById(id)).isEmpty();
    }
}