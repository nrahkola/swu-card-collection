package syksy26.swu_tracker.domain;

import java.util.List;
import org.springframework.data.repository.CrudRepository;

public interface TraitRepository extends CrudRepository<Trait, Long> {
    List<Trait> findAll();
    Trait findByName(String name);
}