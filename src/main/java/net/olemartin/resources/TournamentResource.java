package net.olemartin.resources;

import net.olemartin.business.Player;
import net.olemartin.business.Tournament;
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

    @Autowired
    public TournamentResource(TournamentService tournamentService ) {
        this.tournamentService = tournamentService;
    }

    @POST
    @Path("new")
    public Tournament registerNewTournament(Tournament tournament) {
        return tournamentService.save(tournament);
    }

    @POST
    @Path("{tournamentId}/add")
    public void addPlayersToTournament(@PathParam("tournamentId")Long tournamentId, List<Player> players) {
        tournamentService.addPlayers(tournamentId, players);
    }

    @GET
    @Path("{tournamentId}")
    public Tournament retrieve(@PathParam("tournamentId")Long tournamentId) {
        return tournamentService.retrieve(tournamentId);
    }
}
