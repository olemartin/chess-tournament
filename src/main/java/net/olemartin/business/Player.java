package net.olemartin.business;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import org.neo4j.graphdb.Direction;
import org.springframework.data.annotation.Transient;
import org.springframework.data.neo4j.annotation.Fetch;
import org.springframework.data.neo4j.annotation.GraphId;
import org.springframework.data.neo4j.annotation.NodeEntity;
import org.springframework.data.neo4j.annotation.RelatedTo;

import java.lang.reflect.Type;
import java.util.*;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toList;
import static net.olemartin.business.Color.BLACK;
import static net.olemartin.business.Color.WHITE;

@NodeEntity
public class Player implements Comparable<Player> {

    @GraphId
    private Long id;

    private String name;
    private double score;
    private StringBuffer colors;

    @RelatedTo(type = "MET", direction = Direction.BOTH)
    @Fetch
    private Set<Player> playersMet = new HashSet<>();

    @Transient
    private Player newOpponent;
    @Transient
    private String roundResults;
    @Transient
    private double monrad2;
    @Transient
    private double monrad1;
    @Transient
    private double monrad;
    @Transient
    private double berger;

    @RelatedTo(type = "IS_PLAYER", direction = Direction.INCOMING)
    @Fetch
    private Person person;

    private Player() {
    }

    public Player(String name) {
        this.name = name;
    }

    public void setMonradAndBerger(Iterable<Player> wonIterable, Iterable<Player> remisIterable) {

        setBerger(wonIterable, remisIterable);

        LinkedList<Player> opponents = getOpponents();

        if (opponents.size() >= 2) {

            monrad = sumOpponentPoints(opponents);
            opponents.removeLast();
            monrad1 = sumOpponentPoints(opponents);
            opponents.removeLast();
            monrad2 = sumOpponentPoints(opponents);
        }
    }

    private LinkedList<Player> getOpponents() {
        LinkedList<Player> opponents = new LinkedList<>();
        opponents.addAll(playersMet.stream().sorted().collect(toList()));
        return opponents;
    }

    private void setBerger(Iterable<Player> wonIterable, Iterable<Player> remisIterable) {
        final Set<Player> remis = new HashSet<>();
        final Set<Player> won = new HashSet<>();
        remisIterable.forEach(remis::add);
        wonIterable.forEach(won::add);

        berger = sumOpponentPoints(won) + (sumOpponentPoints(remis) / 2.0);
    }

    public void setRoundScore(Iterable<Match> matches, Set<Player> players) {

        List<Player> allPlayers = new ArrayList<>();
        allPlayers.addAll(players.stream().collect(toList()));
        Collections.sort(allPlayers);

        List<String> result = new ArrayList<>();

        matches.forEach(match -> {
            Player opponent = getOpponent(match);
            int position = allPlayers.indexOf(opponent) + 1;
            if (match.hasResult()) {
                if (match.getResult() == Result.REMIS) {
                    result.add("=" + position);
                } else if (match.getWinner().id.equals(id)) {
                    result.add("+" + position);
                } else {
                    result.add("-" + position);
                }
            } else {
                result.add("?" + position);
            }
        });
        roundResults = result.stream().collect(joining(", "));
    }

    private Player getOpponent(Match match) {
        return match.getPlayers()
                .stream()
                .filter(player -> !player.id.equals(id)).findFirst().get();
    }

    private Double sumOpponentPoints(Collection<Player> players) {
        return players
                .stream()
                .map(player -> player.score).reduce((d, d2) -> d + d2).orElse(0.0);
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
        if (colors == null) {
            colors = new StringBuffer();
        }
        colors.append(color.name()).append(":");
    }

    private long numberOfRounds(Color color) {
        return asLinkedList().stream().filter(c -> c == color).count();
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
            return asLinkedList().getLast();
        }
    }

    private LinkedList<Color> asLinkedList() {
        return new LinkedList<>(Arrays.asList(
                colors.toString().split(":"))
                .stream()
                .map(Color::valueOf)
                .collect(Collectors.toList()));
    }

    public boolean hasMet(Player otherPlayer) {
        return playersMet.contains(otherPlayer);
    }

    public Color mustHaveColor() {
        LinkedList<Color> matches = asLinkedList();
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
        LinkedList<Color> matches = asLinkedList();
        matches.removeLast();
        colors = new StringBuffer(matches.stream().map(Enum::name).collect(joining(":", "", ":")));
    }

    public Set<Player> hasMet() {
        return playersMet;
    }

    @Override
    public int compareTo(Player p2) {
        double score1 = getScore();
        double score2 = p2.getScore();
        if (score1 == score2) {
            int monrad2 = (int) (1000.0 * (p2.monrad2 - this.monrad2));
            int monrad1 = (int) (1000.0 * (p2.monrad1 - this.monrad1));
            int monrad = (int) (1000.0 * (p2.monrad - this.monrad));
            int berger = (int) (1000.0 * (p2.berger - this.berger));

            return monrad2 == 0 ? monrad1 == 0 ? monrad == 0 ? berger : monrad : monrad1 : monrad2;
        } else {
            return (int) (1000.0 * (score2 - score1));
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Player player = (Player) o;

        return !(id != null ? !id.equals(player.id) : player.id != null);

    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }

    public Long getId() {
        return id;
    }

    public Person getPerson() {
        return person;
    }

    public static class PlayerSerializer implements JsonSerializer<Player> {
        @Override
        public JsonElement serialize(Player player, Type typeOfSrc, JsonSerializationContext context) {
            JsonObject root = new JsonObject();
            root.addProperty("id", player.id);
            root.addProperty("name", player.name);
            root.addProperty("score", player.score);
            root.addProperty("roundResults", player.roundResults);
            root.addProperty("monrad", player.monrad);
            root.addProperty("monrad1", player.monrad1);
            root.addProperty("monrad2", player.monrad2);
            root.addProperty("berger", player.berger);

            return root;
        }
    }
}
