package net.olemartin.repository;

import net.olemartin.domain.Tournament;
import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.neo4j.repository.GraphRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface TournamentRepository extends GraphRepository<Tournament> {

    @Query("MATCH (p1:Player) -[:PLAYS_IN]- (t:Tournament)\n" +
            "where id(p1) = {who}\n" +
            "return t")
    public Tournament findPlayersTournament(@Param("who") long playerId);

}
