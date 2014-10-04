package net.olemartin.resources;

import net.olemartin.business.Match;
import net.olemartin.business.Result;
import net.olemartin.push.ChangeEndpoint;
import net.olemartin.service.MatchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
    public Match reportResult(@PathParam("matchId") long matchId, @PathParam("result") String result) {
        Match match =  matchService.reportResult(matchId, Result.valueOf(result));
        sendNotification("new result");
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

    private void sendNotification(String message) {
        for (ChangeEndpoint endpoint : endpoints) {
            endpoint.sendPush(message);
        }
    }
}
