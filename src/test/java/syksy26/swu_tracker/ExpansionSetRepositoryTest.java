package syksy26.swu_tracker;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import syksy26.swu_tracker.domain.ExpansionSet;
import syksy26.swu_tracker.domain.ExpansionSetRepository;

@SpringBootTest
class ExpansionSetRepositoryTest {

    @Autowired
    private ExpansionSetRepository expansionSetRepository;

    @Test
    void createNewExpansionSet() {
        String code = "TEST" + (System.currentTimeMillis() % 10000);
        ExpansionSet set = new ExpansionSet("Test Set", code);
        expansionSetRepository.save(set);

        assertThat(set.getId()).isNotNull();
    }

    @Test
    void findAllSetsShouldNotBeEmpty() {
        List<ExpansionSet> sets = (List<ExpansionSet>) expansionSetRepository.findAll();
        assertThat(sets).isNotEmpty();
    }

    @Test
    void deleteExpansionSet() {
        String code = "DEL" + (System.currentTimeMillis() % 10000);
        ExpansionSet set = new ExpansionSet("Delete Set", code);
        expansionSetRepository.save(set);
        Long id = set.getId();

        expansionSetRepository.deleteById(id);
        assertThat(expansionSetRepository.findById(id)).isEmpty();
    }
}