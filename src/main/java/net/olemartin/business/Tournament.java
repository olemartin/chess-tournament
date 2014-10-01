package net.olemartin.business;

import com.google.gson.*;
import com.google.gson.annotations.Expose;
import org.neo4j.graphdb.Direction;
import org.springframework.data.neo4j.annotation.*;

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

    @Query(value = "start n=node({self}) match n-[:ROUND_OF]->round where round.number = n.currentRound return round", elementClass=Round.class)
    private Round round;

    private Tournament() {

    }

    public String getName() {
        return name;
    }

    public void addPlayer(Player player) {
        players.add(player);
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

    public boolean isCurrentRoundFinished() {
        return round == null || round.isFinished();
    }

    public static class TournamentSerializer implements JsonSerializer<Tournament> {

        @Override
        public JsonElement serialize(Tournament tournament, Type typeOfSrc, JsonSerializationContext context) {
            JsonSerializer<Player> playerSerializer = new Player.PlayerSerializer();
            JsonSerializer<Round> roundSerializer = new Round.RoundSerializer();
            JsonObject root = new JsonObject();
            root.addProperty("id", tournament.id);
            root.addProperty("name", tournament.name);
            JsonArray playerArray = new JsonArray();
            tournament.players.stream().sorted((p1, p2) -> {
                double score1 = p1.getScore();
                double score2 = p2.getScore();
                if (score1 == score2) {
                    return 0;
                } else if (score1 > score2) {
                    return -1;
                } else {
                    return 1;
                }
            }).forEach(player -> playerArray.add(playerSerializer.serialize(player, Player.class, context)));
            root.add("players", playerArray);

            JsonObject roundsObject = new JsonObject();
            tournament.rounds.forEach(round -> roundsObject.add(String.valueOf(round.getNumber()), roundSerializer.serialize(round, Round.class, context)));
            root.add("rounds", roundsObject);
            if (tournament.round != null) {
                root.add("currentRound", roundSerializer.serialize(tournament.round, Round.class, context));
            }
            return root;
        }
    }
}
