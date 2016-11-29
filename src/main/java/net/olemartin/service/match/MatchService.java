package net.olemartin.service.match;

import net.olemartin.domain.*;
import net.olemartin.engine.TournamentEngine;
import net.olemartin.engine.TournamentEngineFactory;
import net.olemartin.repository.MatchRepository;
import net.olemartin.repository.PlayerRepository;
import net.olemartin.repository.RoundRepository;
import net.olemartin.repository.TournamentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
@Transactional
public class MatchService {

    private final MatchRepository matchRepository;
    private final TournamentRepository tournamentRepository;
    private final RoundRepository roundRepository;
    private final PlayerRepository playerRepository;

    @Autowired
    public MatchService(MatchRepository matchRepository, TournamentRepository tournamentRepository, RoundRepository roundRepository, PlayerRepository playerRepository) {
        this.matchRepository = matchRepository;
        this.tournamentRepository = tournamentRepository;
        this.roundRepository = roundRepository;
        this.playerRepository = playerRepository;
    }

    public List<Match> nextRound(long tournamentId) {
        Tournament tournament = tournamentRepository.findOne(tournamentId);
        if (tournament.isFinished()) {
            throw new IllegalArgumentException("Tournament is finished.");
        }
        if (!tournament.isCurrentRoundFinished()) {
            throw new IllegalArgumentException("Current round is not finished.");
        }
        TournamentEngine tournamentEngine = TournamentEngineFactory.getEngine(
                new Randomizer(),
                playerRepository.playersInTournament(tournamentId),
                tournament.getEngine());

        int roundNumber = tournament.increaseRound();
        List<Match> matches = tournamentEngine.round(roundNumber);

        Round round = new Round(tournament, roundNumber);
        matches.forEach(round::addMatch);

        round = roundRepository.save(round, 3);
        tournament.addRound(round);
        matchRepository.save(matches, 2);
        tournamentRepository.save(tournament, 2);
        playerRepository.save(tournamentEngine.getPlayers(), 2);
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
        if (match.hasResult()) {
            throw new IllegalStateException("Match already has result");
        }
        match.reportResult(result);
        matchRepository.save(match);
        playerRepository.save(match.getBlack());
        playerRepository.save(match.getWhite());
        updateMonradAndBerger(match.getWhite(), match.getBlack());

        playerRepository.save(match.getBlack());
        playerRepository.save(match.getWhite());
        return match;
    }

    public void updateMonradAndBerger(Iterable<Player> players) {
        for (Player player : players) {
            player.setMonradAndBerger(
                    playerRepository.findOpponentsPlayerBeat(player.getId()),
                    playerRepository.findOpponentsRemis(player.getId()));
        }
    }

    public void updateMonradAndBerger(Player... players) {
        updateMonradAndBerger(Arrays.asList(players));
    }
}
