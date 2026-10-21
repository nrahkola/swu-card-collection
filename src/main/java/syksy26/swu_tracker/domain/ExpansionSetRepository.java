package syksy26.swu_tracker.domain;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

public interface ExpansionSetRepository extends CrudRepository<ExpansionSet, Long> {
    Optional<ExpansionSet> findByCode(String code);

    @Query(value = """
        SELECT * FROM expansion_set
        ORDER BY 
            CASE code
                WHEN 'SOR' THEN 1
                WHEN 'SHD' THEN 2
                WHEN 'TWI' THEN 3
                WHEN 'JTL' THEN 4
                WHEN 'LOF' THEN 5
                WHEN 'IBH' THEN 6
                WHEN 'SEC' THEN 7
                WHEN 'LAW' THEN 8
                WHEN 'ASH' THEN 9
                WHEN 'HMW' THEN 10
                ELSE 99
            END ASC
        """, nativeQuery = true)
    List<ExpansionSet> findAll();
}