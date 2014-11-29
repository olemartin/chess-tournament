package net.olemartin.service.match;

import io.dropwizard.auth.Auth;
import net.olemartin.domain.Match;
import net.olemartin.domain.Result;
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

import static net.olemartin.push.ChangeEndpoint.MessageType.NEW_RESULT;

@Path("/match")
@Consumes(MediaType.APPLICATION_JSON)
@Service
@Resource
public class MatchResource {

    private final MatchService matchService;
    private final Set<ChangeEndpoint> endpoints = new HashSet<>();

    @Autowired
    public MatchResource(MatchService matchService) {
        this.matchService = matchService;
    }

    @Path("{matchId}/report/{result}")
    @POST
    public Match reportResult(@Auth User user, @PathParam("matchId") long matchId, @PathParam("result") String result) {
        Match match =  matchService.reportResult(matchId, Result.valueOf(result));
        sendNotification();
        return match;
    }

    @Path("player/{playerId}")
    @GET
    public List<Match> getMatchesByPlayer(@PathParam("playerId") long playerId) {
        return matchService.findMatchesPlayerPlayed(playerId);
    }

    public void registerEndpoint(ChangeEndpoint changeEndpoint) {
        endpoints.add(changeEndpoint);
    }

    private void sendNotification() {
        for (ChangeEndpoint endpoint : endpoints) {
            endpoint.sendPush(NEW_RESULT);
        }
    }
}
