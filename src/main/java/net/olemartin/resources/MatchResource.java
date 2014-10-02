package net.olemartin.resources;

import net.olemartin.business.Match;
import net.olemartin.business.Result;
import net.olemartin.event.TournamentUpdated;
import net.olemartin.service.MatchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;
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
public class MatchResource implements ApplicationEventPublisherAware {

    private final MatchService matchService;
    private ApplicationEventPublisher applicationEventPublisher;

    @Autowired
    public MatchResource(MatchService matchService) {
        this.matchService = matchService;
    }

    @Path("{matchId}/report/{result}")
    @POST
    public Match reportResult(@PathParam("matchId") long matchId, @PathParam("result") String result) {
        Match match =  matchService.reportResult(matchId, Result.valueOf(result));
        applicationEventPublisher.publishEvent(new TournamentUpdated(this));
        return match;
    }

    @Override
    public void setApplicationEventPublisher(ApplicationEventPublisher applicationEventPublisher) {
        this.applicationEventPublisher = applicationEventPublisher;
    }
}
