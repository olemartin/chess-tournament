package net.olemartin.service.player;

import io.dropwizard.auth.Auth;
import net.olemartin.domain.Player;
import net.olemartin.domain.User;
import net.olemartin.push.ChangeEndpoint;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static net.olemartin.push.ChangeEndpoint.MessageType.PLAYER_DELETED;

@Path("/player")
@Service
@Resource
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class PlayerResource {

    private final PlayerService playerService;

    private final Set<ChangeEndpoint> endpoints = new HashSet<>();

    @Autowired
    public PlayerResource(PlayerService playerService) {
        this.playerService = playerService;
    }

    @Path("list")
    @GET
    public List<Player> getPlayers() {
        return playerService.getPlayers();
    }

    @Path("{playerId}")
    @GET
    public Player getPlayer(@PathParam("playerId") Long playerId) {
        return playerService.getPlayer(playerId);
    }


    @Path("{playerId}")
    @DELETE
    public String deletePlayer(@Auth User user, @PathParam("playerId") Long playerId) {
        playerService.deletePlayer(playerId);
        sendNotification();
        return "OK";
    }

    public void registerEndpoint(ChangeEndpoint changeEndpoint) {
        endpoints.add(changeEndpoint);
    }

    private void sendNotification() {
        for (ChangeEndpoint endpoint : endpoints) {
            endpoint.sendPush(PLAYER_DELETED);
        }
    }


}
