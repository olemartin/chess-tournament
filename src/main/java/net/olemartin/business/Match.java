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

@NodeEntity
public class Match {

    @GraphId
    private Long id;

    @RelatedTo(type = "WHITE", direction = Direction.OUTGOING)
    @Fetch
    private Player white;

    @RelatedTo(type = "BLACK", direction = Direction.OUTGOING)
    @Fetch
    private Player black;

    private Result result;


    private Match() {
    }

    public Match(Player white, Player black) {
        this.white = white;
        this.black = black;

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
                break;
            case BLACK:
                black.increaseScore(1);
                break;
            case REMIS:
                white.increaseScore(0.5);
                black.increaseScore(0.5);
                break;
        }
    }

    public boolean hasResult() {
        return result != null;
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
