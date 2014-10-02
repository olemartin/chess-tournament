package net.olemartin.database;

import net.olemartin.business.Tournament;
import org.springframework.data.neo4j.repository.GraphRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TournamentRepository extends GraphRepository<Tournament> {

}
