package net.olemartin.database;

import net.olemartin.business.Player;
import org.springframework.data.neo4j.repository.GraphRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PlayerRepository extends GraphRepository<Player> {

}