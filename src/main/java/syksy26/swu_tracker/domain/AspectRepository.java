package syksy26.swu_tracker.domain;

import java.util.List;
import org.springframework.data.repository.CrudRepository;

public interface AspectRepository extends CrudRepository<Aspect, Long> {
    List<Aspect> findAll();
    Aspect findByName(String name);
}