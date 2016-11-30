package net.olemartin.repository;

import net.olemartin.domain.Match;
import org.springframework.data.neo4j.annotation.Depth;
import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.neo4j.repository.GraphRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface MatchRepository extends GraphRepository<Match> {


    @Query("MATCH (player) <-[:PLAYER]- (match:Match) <-[:CONSIST_OF]- (round) " +
            "WHERE id(player)={who} " +
            "RETURN match " +
            "ORDER BY round.number")
    Iterable<Match> findMatchesPlayerPlayed(@Param("who") long player);


    @Query("MATCH (tournament) -[:CURRENT_ROUND]- (round) -[:CONSIST_OF]-> (match) " +
            "WHERE id(tournament)={tournamentId} AND " +
            "match.result IS NULL " +
            "RETURN match")
    @Depth(2)
    Iterable<Match> retrieveCurrentRoundsMatches(@Param("tournamentId") Long tournamentId);

    @Query("MATCH (m:Match) -[rel]- () " +
            "WHERE NOT m -- (:Round) " +
            "DELETE rel, m")
    void deleteLooseMatches();
}
