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
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static net.olemartin.business.Color.BLACK;
import static net.olemartin.business.Color.WHITE;

@NodeEntity
public class Player implements Comparable<Player> {

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

    private volatile String roundResults;
    private volatile double monrad2;
    private volatile double monrad1;
    private volatile double monrad;
    private volatile double berger;

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

    public void updateRoundScore(Set<Player> players, Set<Round> rounds) {
        List<Player> allPlayers = new ArrayList<>();
        allPlayers.addAll(players.stream().collect(Collectors.toList()));
        Collections.sort(allPlayers);
        final Set<Player> remis = new HashSet<>();
        final Set<Player> won = new HashSet<>();

        roundResults = rounds.stream()
                .sorted((o1, o2) -> o1.getNumber() - o2.getNumber())
                .map(round -> round.getMatches().stream().filter(playerInMatch()).findFirst().get())
                .map(match -> {
                    Player opponent = match.getPlayers().stream().filter(playerInequalThisPlayer()).findFirst().get();
                    int position = allPlayers.indexOf(opponent) + 1;
                    if (match.hasResult()) {
                        if (match.getResult() == Result.REMIS) {
                            remis.add(opponent);
                            return "=" + position;
                        } else if (match.getWinner().id.equals(id)) {
                            won.add(opponent);
                            return "+" + position;
                        } else {
                            return "-" + position;
                        }
                    } else {
                        return "?" + position;
                    }
                }).collect(Collectors.joining(", "));

        LinkedList<Player> opponents = new LinkedList<>();
        opponents.addAll(playersMet.stream().sorted().collect(Collectors.toList()));
        if (opponents.size() >= 2) {
            berger = sumOpponentPoints(won) + (sumOpponentPoints(remis) / 2.0);
            monrad = sumOpponentPoints(opponents);
            opponents.removeLast();
            monrad1 = sumOpponentPoints(opponents);
            opponents.removeLast();
            monrad2 = sumOpponentPoints(opponents);
        }
    }

    private Double sumOpponentPoints(Collection<Player> players) {
        return players.stream().map(player -> player.score).reduce((d, d2) -> d + d2).orElse(0.0);
    }

    private Predicate<Player> playerInequalThisPlayer() {
        return player -> !player.id.equals(id);
    }

    private Predicate<Match> playerInMatch() {
        return match -> match.getBlack().id.equals(id) || match.getWhite().id.equals(id);
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

        if (id != null ? !id.equals(player.id) : player.id != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
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
