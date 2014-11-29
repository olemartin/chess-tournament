package net.olemartin.repository;

import net.olemartin.domain.Round;
import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.neo4j.repository.GraphRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RoundRepository extends GraphRepository<Round> {

    @Query("MATCH (r:Round) -[rel]- () " +
            "WHERE NOT r -- (:Tournament) " +
            "DELETE rel, r")
    void deleteLooseRounds();
}
