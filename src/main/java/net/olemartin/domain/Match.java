package net.olemartin.domain;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import org.neo4j.ogm.annotation.GraphId;
import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Relationship;
import org.neo4j.ogm.annotation.Transient;

import java.lang.reflect.Type;
import java.util.HashSet;
import java.util.Set;

@NodeEntity
public class Match implements Comparable<Match> {

    @GraphId
    private Long id;

    private boolean walkover;

    private Result result;

    @Transient
    private String displayResult;

    @Relationship(type = "WHITE", direction = Relationship.OUTGOING)
    private Player white;

    @Relationship(type = "BLACK", direction = Relationship.OUTGOING)
    private Player black;

    @Relationship(type = "WINNER", direction = Relationship.OUTGOING)
    private Player winner;

    @Relationship(type = "LOOSER", direction = Relationship.OUTGOING)
    private Player looser;

    @Relationship(type = "PLAYER", direction = Relationship.OUTGOING)
    private Set<Player> players = new HashSet<>();

    private Match() {
    }

    public Match(Player white, Player black) {
        this.white = white;
        this.black = black;
        players.add(white);
        players.add(black);

        white.countRound(Color.WHITE, black);
        black.countRound(Color.BLACK, white);
    }

    public Match(Player walkover) {
        this.white = walkover;
        this.walkover = true;
        players.add(white);
        white.countWalkover();
    }

    public Player getWhite() {
        return white;
    }

    public Player getBlack() {
        return black;
    }

    public void reportResult(Result result) {
        this.result = result;
        switch (result) {
            case WHITE:
                white.increaseScore(1);
                winner = white;
                looser = black;
                break;
            case BLACK:
                winner = black;
                looser = white;
                black.increaseScore(1);
                break;
            case REMIS:
                white.increaseScore(0.5);
                black.increaseScore(0.5);
                break;
            case WALKOVER:
                white.increaseScore(1);
                winner = white;
                break;
        }
    }

    public Result getResult() {
        return result;
    }

    public boolean hasResult() {
        return result != null;
    }

    @Override
    public int compareTo(Match o) {
        return white.getName().compareTo(o.white.getName());
    }

    public Set<Player> getPlayers() {
        return players;
    }

    public Player getWinner() {
        return winner;
    }

    public Player getLooser() {
        return looser;
    }

    public Long getId() {
        return id;
    }

    public boolean isWalkover() {
        return walkover;
    }

    public void setDisplayResult(String displayResult) {
        this.displayResult = displayResult;
    }

    public static class MatchSerializer implements JsonSerializer<Match> {

        @Override
        public JsonElement serialize(Match src, Type typeOfSrc, JsonSerializationContext context) {
            JsonSerializer<Player> playerSerializer = new Player.PlayerSerializer();
            JsonObject root = new JsonObject();
            root.addProperty("id", src.id);
            if (src.black != null) {
                root.add("white", playerSerializer.serialize(src.white, Player.class, context));
            }
            if (src.black != null) {
                root.add("black", playerSerializer.serialize(src.black, Player.class, context));
            }
            if (src.result != null) {
                root.addProperty("result", src.result.name());
            }
            root.addProperty("displayResult", src.displayResult);
            return root;
        }
    }
}
