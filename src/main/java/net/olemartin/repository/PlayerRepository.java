package net.olemartin.repository;

import net.olemartin.domain.Player;
import org.springframework.data.neo4j.annotation.Depth;
import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.neo4j.repository.GraphRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface PlayerRepository extends GraphRepository<Player> {

    @Query("MATCH (player) <-[:WINNER]- (match:Match), " +
            "(match) -[:LOOSER]-> (otherPlayer:Player) " +
            "WHERE id(player)={who} " +
            "RETURN otherPlayer")
    Iterable<Player> findOpponentsPlayerBeat(@Param("who") long player);

    @Query("MATCH (player) <-[:PLAYER]- (match:Match {result:\"REMIS\"}), " +
            "(match) -[:PLAYER]-> (otherPlayer:Player) " +
            "WHERE id(player)={who} " +
            "RETURN otherPlayer")
    Iterable<Player> findOpponentsRemis(@Param("who") long player);

    @Query("MATCH (p1:Player) -[:PLAYS_IN]- (t:Tournament)\n" +
            "where id(t) = {who}\n" +
            "return p1")
    @Depth(2)
    Iterable<Player> playersInTournament(@Param("who") Long tournamentId);

    @Query("MATCH (p:Player) <-[rel]- ()\n" +
            "WHERE NOT (p) --> (:Tournament)\n" +
            "DELETE rel, p")
    void deleteLoosePlayers();

}
