package net.olemartin.database;

import net.olemartin.business.Round;
import org.springframework.data.neo4j.repository.GraphRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RoundRepository extends GraphRepository<Round> {
}
