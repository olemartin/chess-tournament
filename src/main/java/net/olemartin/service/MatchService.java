package net.olemartin.service;

import net.olemartin.business.*;
import net.olemartin.repository.MatchRepository;
import net.olemartin.repository.PlayerRepository;
import net.olemartin.repository.RoundRepository;
import net.olemartin.repository.TournamentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.neo4j.support.Neo4jTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
public class MatchService {

    private final MatchRepository matchRepository;
    private final TournamentRepository tournamentRepository;
    private final RoundRepository roundRepository;
    private final PlayerRepository playerRepository;

    private final Neo4jTemplate template;


    @Autowired
    public MatchService(MatchRepository matchRepository, TournamentRepository tournamentRepository, RoundRepository roundRepository, PlayerRepository playerRepository, Neo4jTemplate template) {
        this.matchRepository = matchRepository;
        this.tournamentRepository = tournamentRepository;
        this.roundRepository = roundRepository;
        this.playerRepository = playerRepository;
        this.template = template;
    }

    public List<Match> nextRound(long tournamentId) {
        Tournament tournament = tournamentRepository.findOne(tournamentId);
        if (tournament.isFinished()) {
            throw new IllegalArgumentException("Tournament is finished.");
        }
        if (!tournament.isCurrentRoundFinished()) {
            throw new IllegalArgumentException("Current round is not finished.");
        }
        int roundNumber = tournament.increaseRound();
        TournamentEngine tournamentEngine = TournamentEngineFactory.getEngine(
                new Randomizer(),
                playerRepository.playersInTournament(tournamentId),
                tournament.getEngine());
        List<Match> matches = tournamentEngine.round(roundNumber);

        Round round = new Round(tournament, roundNumber);
        matches.stream().forEach(round::addMatch);

        round = roundRepository.save(round);
        tournament.addRound(round);
        matchRepository.save(matches);
        tournamentRepository.save(tournament);
        playerRepository.save(tournamentEngine.getPlayers());
        return matches;
    }

    public List<Match> findMatchesPlayerPlayed(long playerId) {
        List<Match> matches = new ArrayList<>();
        for (Match m : matchRepository.findMatchesPlayerPlayed(playerId)) {
            matches.add(m);
        }
        for (Match match : matches) {
            if (match.getResult() == Result.REMIS) {
                match.setDisplayResult("Remis");
            } else {
                if (match.getWinner().getId() == playerId) {
                    match.setDisplayResult("Won");
                } else {
                    match.setDisplayResult("Lost");
                }
            }
        }
        return matches;
    }

    public Match reportResult(long matchId, Result result) {
        Match match = matchRepository.findOne(matchId);
        match.reportResult(result);
        matchRepository.save(match);
        playerRepository.save(match.getBlack());
        playerRepository.save(match.getWhite());
        updateMonradAndBerger(match.getWhite(), match.getBlack());

        playerRepository.save(match.getBlack());
        playerRepository.save(match.getWhite());
        return match;
    }


    private void updateMonradAndBerger(Player... players) {
        for (Player player : players) {
            player.setMonradAndBerger(
                    playerRepository.findOpponentsPlayerBeat(player.getId()),
                    playerRepository.findOpponentsRemis(player.getId()));
        }
    }
}
