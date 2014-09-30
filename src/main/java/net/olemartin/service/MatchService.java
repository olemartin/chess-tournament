package net.olemartin.service;

import net.olemartin.business.*;
import net.olemartin.database.MatchRepository;
import net.olemartin.database.RoundRepository;
import net.olemartin.database.TournamentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class MatchService {

    private MatchRepository matchRepository;
    private TournamentRepository tournamentRepository;
    private final RoundRepository roundRepository;


    @Autowired
    public MatchService(MatchRepository matchRepository, TournamentRepository tournamentRepository, RoundRepository roundRepository) {
        this.matchRepository = matchRepository;
        this.tournamentRepository = tournamentRepository;
        this.roundRepository = roundRepository;
    }

    public List<Match> nextRound(long tournamentId) {
        Tournament tournament = tournamentRepository.findOne(tournamentId);
        int roundNumber = tournament.increaseRound();
        Round round = new Round(tournament, roundNumber);
        Monrad monrad = new Monrad(new Randomizer(), tournament.getPlayers());
        List<Match> matches = monrad.round(roundNumber);

        matches.stream().forEach(round::addMatch);

        round = roundRepository.save(round);
        tournament.addRound(round);
        matchRepository.save(matches);
        tournamentRepository.save(tournament);
        return matches;
    }

    public void reportResult(long matchId, Result result) {
        Match match = matchRepository.findOne(matchId);
        match.reportResult(result);
        matchRepository.save(match);
    }
}
