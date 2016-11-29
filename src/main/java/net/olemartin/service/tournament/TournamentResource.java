package net.olemartin.service.tournament;

import io.dropwizard.auth.Auth;
import net.olemartin.domain.*;
import net.olemartin.domain.view.TournamentView;
import net.olemartin.push.ChangeEndpoint;
import net.olemartin.service.match.MatchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static net.olemartin.push.ChangeEndpoint.MessageType.NEW_MATCH;
import static net.olemartin.push.ChangeEndpoint.MessageType.PLAYER_ADDED;

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
    public Tournament registerNewTournament(@Auth User user, Tournament tournament) {
        return tournamentService.save(tournament);
    }

    @POST
    @Path("{tournamentId}/add")
    public List<Person> addPlayerToTournament(@Auth User user, @PathParam("tournamentId") Long tournamentId, List<Person> persons) {
        tournamentService.addPlayers(tournamentId, persons);
        sendNotification(PLAYER_ADDED);
        return persons;
    }

    @GET
    @Path("{tournamentId}")
    public Tournament retrieve(@PathParam("tournamentId")Long tournamentId) {
        return tournamentService.retrieve(tournamentId);
    }

    @DELETE
    @Path("{tournamentId}")
    public void delete(@Auth User user, @PathParam("tournamentId") Long tournamentId) {
        tournamentService.delete(tournamentId);
    }

    @GET
    @Path("{tournamentId}/players")
    public List<Player> retrievePlayers(@PathParam("tournamentId") Long tournamentId) {
        return tournamentService.retrievePlayers(tournamentId);
    }

    @GET
    @Path("{tournamentId}/matches")
    public List<Match> retrieveCurrentRoundsMatches(@PathParam("tournamentId") Long tournamentId) {
        return tournamentService.retrieveCurrentRoundsMatches(tournamentId);
    }

    @GET
    @Path("{tournamentId}/rounds")
    public List<Round> retrieveRounds(@PathParam("tournamentId") Long tournamentId) {
        return tournamentService.retrieveRounds(tournamentId);
    }

    @POST
    @Path("{tournamentId}/finish")
    public String finish(@Auth User user, @PathParam("tournamentId") Long tournamentId, @QueryParam("override") boolean override) {
        tournamentService.finishTournament(tournamentId, override);
        return "OK";
    }

    @POST
    @Path("{tournamentId}/next-round")
    public Set<Match> nextRound(@Auth User user, @PathParam("tournamentId")Long tournamentId) {
        Set<Match> matches =  matchService.nextRound(tournamentId);
        sendNotification(NEW_MATCH);
        return matches;
    }

    @Path("{tournamentId}/monrad")
    @POST
    public List<Player> updateMonradAndBerger(@Auth User user, @PathParam("tournamentId") Long id) {
        return tournamentService.updateMonradAndBerger(id);
    }

    @GET
    public List<TournamentView> allTournaments() {
        return tournamentService.retrieveAll();
    }

    public void registerEndpoint(ChangeEndpoint changeEndpoint) {
        endpoints.add(changeEndpoint);
    }


    private void sendNotification(ChangeEndpoint.MessageType message) {
        for (ChangeEndpoint endpoint : endpoints) {
            endpoint.sendPush(message);
        }
    }
}
