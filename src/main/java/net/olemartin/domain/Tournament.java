package net.olemartin.domain;

import com.google.gson.*;
import net.olemartin.tools.rating.EloRatingSystem;
import org.neo4j.ogm.annotation.GraphId;
import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Relationship;

import java.lang.reflect.Type;
import java.util.HashSet;
import java.util.Set;

import static java.util.stream.Collectors.toList;

@NodeEntity
public class Tournament {

    @GraphId
    private Long id;

    private String name;

    private int currentRound;

    @Relationship(type = "PLAYS_IN", direction = Relationship.INCOMING)
    private Set<Player> players = new HashSet<>();;

    @Relationship(type = "ROUND_OF", direction = Relationship.OUTGOING)
    private Set<Round> rounds = new HashSet<>();;

    @Relationship(type = "CURRENT_ROUND", direction = Relationship.OUTGOING)
    private Round round;
    private boolean finished;
    private String engine;

    @SuppressWarnings("UnusedDeclaration")
    private Tournament() {

    }

    public Tournament(String name) {
        this.name = name;
    }

    public void calculateRatings(EloRatingSystem system) {
        for (Round round : rounds.stream().sorted().collect(toList())) {
            for (Match match : round.getMatches().stream().filter(m -> !m.isWalkover()).collect(toList())) {

                if (match.getResult() == Result.REMIS) {
                    Person white = match.getWhite().getPerson();
                    Person black = match.getBlack().getPerson();
                    white.setCurrentRating(
                            system.getNewRating(
                                    white.getCurrentRating(),
                                    black.getCurrentRating(),
                                    PlayerResult.DRAW));
                    black.setCurrentRating(
                            system.getNewRating(
                                    black.getCurrentRating(),
                                    white.getCurrentRating(),
                                    PlayerResult.DRAW));
                } else {
                    Person winner = match.getWinner().getPerson();
                    Person looser = match.getLooser().getPerson();
                    winner.setCurrentRating(
                            system.getNewRating(
                                    winner.getCurrentRating(),
                                    looser.getCurrentRating(),
                                    PlayerResult.WIN));
                    looser.setCurrentRating(
                            system.getNewRating(
                                    looser.getCurrentRating(),
                                    winner.getCurrentRating(),
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

    public Long getId() {
        return id;
    }

    public String getEngine() {
        return engine;
    }

    public void setEngine(String engine) {
        this.engine = engine;
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
            root.addProperty("finished", tournament.finished);
            return root;
        }
    }
}
