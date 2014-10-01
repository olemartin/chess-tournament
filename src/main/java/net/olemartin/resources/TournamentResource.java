package net.olemartin.resources;

import net.olemartin.business.Match;
import net.olemartin.business.Player;
import net.olemartin.business.Tournament;
import net.olemartin.service.MatchService;
import net.olemartin.service.TournamentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.List;

@Path("/tournament")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@Service
@Resource
public class TournamentResource {


    private final TournamentService tournamentService;
    private MatchService matchService;

    @Autowired
    public TournamentResource(TournamentService tournamentService, MatchService matchService) {
        this.tournamentService = tournamentService;
        this.matchService = matchService;
    }

    @POST
    @Path("new")
    public Tournament registerNewTournament(Tournament tournament) {
        return tournamentService.save(tournament);
    }

    @POST
    @Path("{tournamentId}/add")
    public Player addPlayerToTournament(@PathParam("tournamentId")Long tournamentId, Player player) {
        tournamentService.addPlayer(tournamentId, player);
        return player;
    }

    @GET
    @Path("{tournamentId}")
    public Tournament retrieve(@PathParam("tournamentId")Long tournamentId) {
        return tournamentService.retrieve(tournamentId);
    }

    @POST
    @Path("{tournamentId}/next-round")
    public List<Match> nextRound(@PathParam("tournamentId")Long tournamentId) {
        return matchService.nextRound(tournamentId);
    }
}
