package net.olemartin.business;

import com.google.gson.*;
import net.olemartin.rating.EloRatingSystem;
import org.neo4j.graphdb.Direction;
import org.springframework.data.neo4j.annotation.*;

import java.lang.reflect.Type;
import java.util.Set;
import java.util.stream.Collectors;

@NodeEntity
public class Tournament {

    @GraphId
    private Long id;

    private String name;

    private int currentRound;

    @RelatedTo(type = "PLAYS_IN", direction = Direction.INCOMING)
    @Fetch
    private Set<Player> players;

    @RelatedTo(type = "ROUND_OF", direction = Direction.OUTGOING)
    @Fetch
    private Set<Round> rounds;

    @Query(value = "start n=node({self}) match n-[:ROUND_OF]->round where round.number = n.currentRound return round", elementClass = Round.class)
    private Round round;
    private boolean finished;

    private Tournament() {

    }

    public void calculateRatings(EloRatingSystem system) {
        for (Round round : rounds.stream().sorted().collect(Collectors.toList())) {
            for (Match match : round.getMatches()) {

                if (match.getResult() == Result.REMIS) {
                    Person white = match.getWhite().getPerson();
                    Person black = match.getBlack().getPerson();
                    white.setRating(system.getNewRating(white.getRating(), black.getRating(), PlayerResult.DRAW));
                    black.setRating(system.getNewRating(black.getRating(), white.getRating(), PlayerResult.DRAW));
                } else {
                    Person winner = match.getWinner().getPerson();
                    Person looser = match.getLooser().getPerson();
                    winner.setRating(
                            system.getNewRating(
                                    winner.getRating(),
                                    looser.getRating(),
                                    PlayerResult.WIN));
                    looser.setRating(
                            system.getNewRating(
                                    looser.getRating(),
                                    winner.getRating(),
                                    PlayerResult.LOSS));
                }
            }
        }
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

    public Set<Round> getRounds() {
        return rounds;
    }

    public void setFinished(boolean finished) {
        this.finished = finished;
    }

    public boolean isFinished() {
        return finished;
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
            tournament.players.stream().sorted().forEach(player -> playerArray.add(playerSerializer.serialize(player, Player.class, context)));
            root.add("players", playerArray);

            JsonArray roundsArray = new JsonArray();
            tournament.rounds.stream().sorted().forEach(
                    round -> roundsArray.add(roundSerializer.serialize(round, Round.class, context)));
            root.add("rounds", roundsArray);
            if (tournament.round != null) {
                root.add("currentRound", roundSerializer.serialize(tournament.round, Round.class, context));
            }
            return root;
        }
    }
}
