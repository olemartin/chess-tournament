package net.olemartin.business;

import org.neo4j.graphdb.Direction;
import org.springframework.data.neo4j.annotation.GraphId;
import org.springframework.data.neo4j.annotation.RelatedTo;

import java.util.Set;

public class Person {

    @GraphId
    private Long id;

    @RelatedTo(type = "IS_PLAYER", direction = Direction.OUTGOING)
    private Set<Player> players;

    private String name;

    private int rating;


    private Person() {
    }


    public void calculateRating(Set<Round> rounds) {



    }


}
