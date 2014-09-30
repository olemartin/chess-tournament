package net.olemartin.business;

import com.google.gson.*;
import com.google.gson.annotations.Expose;
import org.neo4j.graphdb.Direction;
import org.springframework.data.neo4j.annotation.Fetch;
import org.springframework.data.neo4j.annotation.GraphId;
import org.springframework.data.neo4j.annotation.NodeEntity;
import org.springframework.data.neo4j.annotation.RelatedTo;

import java.lang.reflect.Type;
import java.util.Set;

@NodeEntity
public class Tournament {

    @GraphId
    @Expose
    private Long id;

    @Expose
    private String name;

    @Expose
    private int currentRound;

    @RelatedTo(type = "PLAYS_IN", direction = Direction.INCOMING)
    @Expose
    @Fetch
    private Set<Player> players;

    @RelatedTo(type = "ROUND_OF", direction = Direction.OUTGOING)
    @Fetch
    private Set<Round> rounds;

    private Tournament() {

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

    public Set<Player> getPlayers() {
        return players;
    }

    public int increaseRound() {
        return ++this.currentRound;
    }

    public void addRound(Round round) {
        rounds.add(round);
    }

    public static class TournamentSerializer implements JsonSerializer<Tournament> {

        @Override
        public JsonElement serialize(Tournament tournament, Type typeOfSrc, JsonSerializationContext context) {
            JsonSerializer<Player> playerSerializer = new Player.PlayerSerializer();
            JsonSerializer<Round> roundSerializer = new Round.RoundSerializer();
            JsonObject root = new JsonObject();
            root.addProperty("id", tournament.id);
            root.addProperty("name", tournament.name);
            root.addProperty("currentRound", tournament.currentRound);
            JsonArray playerArray = new JsonArray();
            tournament.players.forEach(player -> playerArray.add(playerSerializer.serialize(player, Player.class, context)));
            root.add("players", playerArray);

            JsonObject roundsObject = new JsonObject();
            tournament.rounds.forEach(round -> roundsObject.add(String.valueOf(round.getNumber()), roundSerializer.serialize(round, Round.class, context)));
            root.add("rounds", roundsObject);
            return root;
        }
    }
}
