package net.olemartin.repository;

import net.olemartin.business.Match;
import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.neo4j.conversion.Result;
import org.springframework.data.neo4j.repository.GraphRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface MatchRepository extends GraphRepository<Match> {


    @Query("MATCH (player) <-[:PLAYER]- (match:Match) <-[:CONSIST_OF]- (round) " +
            "WHERE id(player)={who} " +
            "RETURN match " +
            "ORDER BY round.number")
    public Iterable<Match> findMatchesPlayerPlayed(@Param("who") long player);


    @Query("MATCH (tournament) -[:ROUND_OF]-> (round) -[:CONSIST_OF]-> (match) " +
            "WHERE id(tournament)={tournamentId} AND " +
            "round.number = tournament.currentRound AND " +
            "match.result IS NULL " +
            "RETURN match")
    Result<Match> retrieveCurrentRoundsMatches(@Param("tournamentId") Long tournamentId);
}
