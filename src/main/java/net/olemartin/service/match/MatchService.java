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
import java.util.Set;

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

    public Set<Match> nextRound(long tournamentId) {
        Tournament tournament = tournamentRepository.findOne(tournamentId, 2);
        if (tournament.isFinished()) {
            throw new IllegalArgumentException("Tournament is finished.");
        }
        if (!tournament.isCurrentRoundFinished()) {
            throw new IllegalArgumentException("Current round is not finished.");
        }
        TournamentEngine tournamentEngine = TournamentEngineFactory.getEngine(
                new Randomizer(),
                tournament.getPlayers(),
                tournament.getEngine());

        Round round = tournament.startNewRound();
        List<Match> matches = tournamentEngine.round(round.getNumber());
        matchRepository.save(matches, 2).forEach(round::addMatch);
        roundRepository.save(round);
        tournamentRepository.save(tournament);
        return round.getMatches();
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
        Match match = matchRepository.findOne(matchId, 1);
        if (match.hasResult()) {
            throw new IllegalStateException("Match already has result");
        }
        match.reportResult(result);
        matchRepository.save(match, 2);
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
