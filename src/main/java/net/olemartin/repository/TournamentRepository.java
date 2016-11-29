package net.olemartin.repository;

import net.olemartin.domain.Player;
import net.olemartin.domain.Tournament;
import net.olemartin.domain.view.TournamentView;
import org.springframework.data.neo4j.annotation.Depth;
import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.neo4j.repository.GraphRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

@Repository
public interface TournamentRepository extends GraphRepository<Tournament> {

    @Query("MATCH (p1:Player) -[:PLAYS_IN]- (t:Tournament)\n" +
            "where id(p1) = {who}\n" +
            "return t")
    public Tournament findPlayersTournament(@Param("who") long playerId);

    @Query("MATCH (t:Tournament) " +
            "RETURN id(t) as id, t.name as name, t.finished as finished")
    public List<TournamentView> retrieveAllTournaments();


    @Query("MATCH (t:Tournament) -[:PLAYS_IN]- (p:Player)\n" +
            "where id(t) = {tournamentId}\n"+
            "RETURN p")
    @Depth(1)
    Set<Player> findPlayersInTournament(@Param("tournamentId") Long tournamentId);
}
