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
import java.util.HashSet;
import java.util.Set;

@NodeEntity
public class Match implements Comparable<Match>{

    @GraphId
    private Long id;

    private Result result;
    
    @RelatedTo(type = "WHITE", direction = Direction.OUTGOING)
    @Fetch
    private Player white;

    @RelatedTo(type = "BLACK", direction = Direction.OUTGOING)
    @Fetch
    private Player black;

    @RelatedTo(type = "WINNER", direction = Direction.OUTGOING)
    @Fetch
    private Player winner;

    @RelatedTo(type = "LOOSER", direction = Direction.OUTGOING)
    @Fetch
    private Player looser;

    @RelatedTo(type = "PLAYER", direction = Direction.OUTGOING)
    @Fetch
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

    public static class MatchSerializer implements JsonSerializer<Match> {

        @Override
        public JsonElement serialize(Match src, Type typeOfSrc, JsonSerializationContext context) {
            JsonSerializer<Player> playerSerializer = new Player.PlayerSerializer();
            JsonObject root = new JsonObject();
            root.addProperty("id", src.id);
            root.add("white", playerSerializer.serialize(src.white, Player.class, context));
            root.add("black", playerSerializer.serialize(src.black, Player.class, context));
            if (src.result != null) {
                root.addProperty("result", src.result.name());
            }
            return root;
        }
    }
}
