package net.olemartin.business;

import org.neo4j.graphdb.Direction;
import org.springframework.data.neo4j.annotation.*;

import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import static net.olemartin.business.Color.BLACK;
import static net.olemartin.business.Color.WHITE;

@NodeEntity
@Labels(value = "Player")
public class Player {

    @GraphId
    private Long id;

    @GraphProperty
    private String name;
    @GraphProperty
    private double score;

    private LinkedList<Color> matches = new LinkedList<>();

    @RelatedTo(type = "MET", direction = Direction.BOTH)
    private Set<Player> playersMet;

    private Player() {}
    public Player(String name) {
        this.name = name;
    }

    public void increaseScore(double score) {
        this.score += score;
    }

    public String getName() {
        return name;
    }

    public void countRound(Color color, Player otherPlayer) {
        playersMet.add(otherPlayer);
        matches.add(color);
    }

    private long numberOfRounds(Color color) {
        return matches.stream().filter(c -> c == color).count();
    }

    public Color nextOptimalColor() {
        long blacks = numberOfRounds(BLACK);
        long white = numberOfRounds(WHITE);
        if (blacks == 0 && white == 0) {
            return WHITE;
        } else if (blacks > white) {
            return WHITE;
        } else if (white > blacks) {
            return BLACK;
        } else {
            return matches.get(matches.size() - 1).getOther();
        }
    }

    public boolean hasMet(Player otherPlayer) {
        return playersMet.contains(otherPlayer);
    }

    public Color mustHaveColor() {
        if (matches.size() >= 2) {
            if (matches.get(matches.size() - 2) == matches.get(matches.size() - 1)) {
                return matches.get(matches.size() - 2).getOther();
            }
        }
        return null;
    }

    @Override
    public String toString() {
        return "Player{" +
                "score=" + score +
                ", name='" + name + '\'' +
                '}';
    }

    public double getScore() {
        return score;
    }

    public void removeLastOpponent() {
        //playersMet.removeLast();
        //matches.removeLast();
    }

    public List<Player> hasMet() {
        return null;//playersMet;
    }

    public List<Color> getColors() {
        return matches;
    }
}
