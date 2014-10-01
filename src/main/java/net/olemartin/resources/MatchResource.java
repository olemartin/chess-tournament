package net.olemartin.resources;

import net.olemartin.business.Match;
import net.olemartin.business.Result;
import net.olemartin.service.MatchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.MediaType;

@Path("/match")
@Consumes(MediaType.APPLICATION_JSON)
@Service
@Resource
public class MatchResource {

    private final MatchService matchService;

    @Autowired
    public MatchResource(MatchService matchService) {
        this.matchService = matchService;
    }

    @Path("{matchId}/report/{result}")
    @POST
    public Match reportResult(@PathParam("matchId") long matchId, @PathParam("result") String result) {
        return matchService.reportResult(matchId, Result.valueOf(result));
    }
}
