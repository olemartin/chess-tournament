package net.olemartin.database;

import net.olemartin.business.Match;
import org.springframework.data.neo4j.repository.GraphRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MatchRepository extends GraphRepository<Match>{

}
