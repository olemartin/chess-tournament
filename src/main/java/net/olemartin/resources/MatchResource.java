package net.olemartin.resources;

import net.olemartin.business.*;
import net.olemartin.database.MatchRepository;
import net.olemartin.database.RoundRepository;
import net.olemartin.database.TournamentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;
import java.util.List;

@Path("/match")
@Consumes(MediaType.APPLICATION_JSON)
@Service
@Resource
public class MatchResource {

    private final MatchRepository matchRepository;
    private final TournamentRepository tournamentRepository;
    private final RoundRepository roundRepository;

    @Autowired
    public MatchResource(MatchRepository matchRepository, TournamentRepository tournamentRepository, RoundRepository roundRepository) {
        this.matchRepository = matchRepository;
        this.tournamentRepository = tournamentRepository;
        this.roundRepository = roundRepository;
    }

    @Path("next-round")
    @POST
    public List<Match> nextRound(long tournamentId) {
        Tournament tournament = tournamentRepository.findOne(tournamentId);
        int roundNumber = tournament.getCurrentRound();
        Round round = new Round(tournament, roundNumber);
        Monrad monrad = new Monrad(new Randomizer());
        List<Match> matches = monrad.round(roundNumber);

        matches.stream().forEach(round::addMatch);

        roundRepository.save(round);
        return matches;
    }

    @Path("report")
    @POST
    public void reportResult(long matchId, Result result) {
        Match match = matchRepository.findOne(matchId);
        match.reportResult(result);
        matchRepository.save(match);
    }
}
