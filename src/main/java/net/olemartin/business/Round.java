package net.olemartin.business;

import org.neo4j.graphdb.Direction;
import org.springframework.data.neo4j.annotation.GraphId;
import org.springframework.data.neo4j.annotation.NodeEntity;
import org.springframework.data.neo4j.annotation.RelatedTo;

import java.util.Set;

@NodeEntity
public class Round {

    @GraphId
    private Long id;

    private int number;

    @RelatedTo(type = "CONSIST_OF", direction = Direction.OUTGOING)
    private Set<Match> matches;

    @RelatedTo(type = "ROUND_OF", direction = Direction.INCOMING)
    private Tournament tournament;

    public Round(Tournament tournament, int number) {
        this.tournament = tournament;
        this.number = number;
    }


    public void addMatch(Match match) {
        matches.add(match);
    }
}
