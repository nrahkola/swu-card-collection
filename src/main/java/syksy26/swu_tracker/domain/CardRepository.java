package syksy26.swu_tracker.domain;

import java.util.List;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

public interface CardRepository extends CrudRepository<Card, Long> {

    List<Card> findByName(String name);

    @Query(value = """
        SELECT c.* FROM card c
        JOIN expansion_set es ON c.expansion_set_id = es.id
        ORDER BY 
            CASE es.code
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
            END ASC,
            c.card_number ASC
        """, nativeQuery = true)
    List<Card> findAllInChronologicalOrder();

    @Query("""
        SELECT c FROM Card c
        WHERE (:filterSets = false OR c.expansionSet.code IN :setCodes)
          AND (:filterTypes = false OR c.type IN :types)
          AND (
                :filterAspects = false 
                OR (
                    EXISTS (
                        SELECT a1 FROM c.aspects a1 
                        WHERE a1.name IN :aspectNames
                    )
                    AND NOT EXISTS (
                        SELECT a2 FROM c.aspects a2 
                        WHERE a2.name NOT IN :aspectNames
                    )
                )
              )
        ORDER BY c.cardNumber ASC
        """)
    List<Card> findCardsFiltered(
        @Param("filterSets") boolean filterSets,
        @Param("setCodes") List<String> setCodes,
        @Param("filterAspects") boolean filterAspects,
        @Param("aspectNames") List<String> aspectNames,
        @Param("filterTypes") boolean filterTypes,
        @Param("types") List<String> types
    );
}    