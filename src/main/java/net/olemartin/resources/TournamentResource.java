package net.olemartin.resources;

import net.olemartin.business.Match;
import net.olemartin.business.Player;
import net.olemartin.business.Tournament;
import net.olemartin.push.ChangeEndpoint;
import net.olemartin.service.MatchService;
import net.olemartin.service.TournamentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Path("/tournament")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@Service
@Resource
public class TournamentResource {

    private final TournamentService tournamentService;
    private final MatchService matchService;
    private final Set<ChangeEndpoint> endpoints = new HashSet<>();

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
        sendNotification("player added");
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
        List<Match> matches =  matchService.nextRound(tournamentId);
        sendNotification("new match");
        return matches;
    }

    public void registerEndpoint(ChangeEndpoint changeEndpoint) {
        endpoints.add(changeEndpoint);
    }


    private void sendNotification(String message) {
        for (ChangeEndpoint endpoint : endpoints) {
            endpoint.sendPush(message);
        }
    }
}
