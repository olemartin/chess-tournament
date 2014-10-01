package net.olemartin.business;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import org.neo4j.graphdb.Direction;
import org.springframework.data.neo4j.annotation.Fetch;
import org.springframework.data.neo4j.annotation.GraphId;
import org.springframework.data.neo4j.annotation.NodeEntity;
import org.springframework.data.neo4j.annotation.RelatedTo;

import java.lang.reflect.Type;
import java.util.*;
import java.util.stream.Collectors;

import static net.olemartin.business.Color.BLACK;
import static net.olemartin.business.Color.WHITE;

@NodeEntity
public class Player implements Comparable<Player>{

    @GraphId
    private Long id;

    private String name;

    private double score;

    private StringBuffer colors;

    private volatile LinkedList<Color> matches = new LinkedList<>();
    private volatile Player newOpponent;

    @RelatedTo(type = "MET", direction = Direction.BOTH)
    @Fetch
    private Set<Player> playersMet = new HashSet<>();

    private Player() {
        if (colors != null) {
            matches = new LinkedList<>(Arrays.asList(colors.toString().split(":"))
                    .stream().map(s -> Color.valueOf(s))
                    .collect(Collectors.toList()));
        }
    }

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
        this.newOpponent = otherPlayer;
        playersMet.add(otherPlayer);
        matches.add(color);
        if (colors == null) {
            colors = new StringBuffer();
        }
        colors.append(color.name()).append(":");
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
        playersMet.remove(newOpponent);
        matches.removeLast();
        colors = new StringBuffer(matches.stream().map(Enum::name).collect(Collectors.joining(":")));
    }

    public Set<Player> hasMet() {
        return playersMet;
    }

    public List<Color> getColors() {
        return matches;
    }

    @Override
    public int compareTo(Player p2) {
        double score1 = getScore();
        double score2 = p2.getScore();
        if (score1 == score2) {
            return 0;
        } else if (score1 > score2) {
            return -1;
        } else {
            return 1;
        }
    }

    public static class PlayerSerializer implements JsonSerializer<Player> {
        @Override
        public JsonElement serialize(Player player, Type typeOfSrc, JsonSerializationContext context) {
            JsonObject root = new JsonObject();
            root.addProperty("id", player.id);
            root.addProperty("name", player.name);
            root.addProperty("score", player.score);
            return root;
        }
    }
}
