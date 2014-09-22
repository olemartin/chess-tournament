package net.olemartin.business;

import org.neo4j.graphdb.Direction;
import org.springframework.data.neo4j.annotation.GraphId;
import org.springframework.data.neo4j.annotation.NodeEntity;
import org.springframework.data.neo4j.annotation.RelatedTo;

import java.util.Set;

@NodeEntity
public class Tournament {

    @GraphId
    private Long id;

    private String name;

    private int currentRound;

    @RelatedTo(type = "PLAYS_IN", direction = Direction.INCOMING)
    private Set<Player> players;

    public Tournament(String name) {
        this.name = name;
    }

    private Tournament(){

    }

    public String getName() {
        return name;
    }

    public void addPlayer(Player player) {
        players.add(player);
    }

    public int getCurrentRound() {
        return currentRound;
    }

}
