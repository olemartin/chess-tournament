package net.olemartin.domain;

import com.google.common.collect.Lists;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import org.apache.commons.lang3.StringUtils;
import org.neo4j.ogm.annotation.GraphId;
import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Relationship;
import org.neo4j.ogm.annotation.Transient;

import java.lang.reflect.Type;
import java.util.*;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toList;
import static net.olemartin.domain.Color.BLACK;
import static net.olemartin.domain.Color.WHITE;

@NodeEntity
public class Player implements Comparable<Player> {

    @GraphId
    private Long id;

    private double score;
    private String colors = "";

    @Relationship(type = "MET", direction = Relationship.UNDIRECTED)
    private Set<Player> playersMet = new HashSet<>();

    @Transient
    private Player newOpponent;

    @Transient
    private String roundResults;
    private double monrad2;
    private double monrad1;
    private double monrad;
    private double berger;

    private int tournamentRank;

    @Relationship(type = "IS_PLAYER", direction = Relationship.INCOMING)
    private Person person;

    @Relationship(type = "PLAYS_IN", direction = Relationship.OUTGOING)
    private Tournament tournament;
    private boolean walkover;

    private Player() {
    }

    public Player(String name) {
        this(new Person(name));
    }

    public Player(long id, String name) {
        this(new Person(name));
        this.id = id;
    }

    public Player(Person person) {
        this.person = person;
        person.addPlayer(this);
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

    public boolean hasPlayedMatches() {
        return !playersMet.isEmpty();
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

    public void setRoundScore(Iterable<Match> matches, Iterable<Player> players) {

        List<Player> allPlayers = new ArrayList<>();
        allPlayers.addAll(Lists.newArrayList(players).stream().collect(toList()));
        Collections.sort(allPlayers);

        List<String> result = new ArrayList<>();

        matches.forEach(match -> {

            if (match.hasResult()) {
                if (match.getResult() == Result.WALKOVER) {
                    result.add("WO");
                } else {
                    Player opponent = getOpponent(match);
                    int position = allPlayers.indexOf(opponent) + 1;
                    if (match.getResult() == Result.REMIS) {
                        result.add("=" + position);
                    } else if (match.getWinner().id.equals(id)) {
                        result.add("+" + position);
                    } else {
                        result.add("-" + position);
                    }
                }
            } else {
                result.add("?");
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

    public Player increaseScore(double score) {
        this.score += score;
        return this;
    }

    public String getName() {
        return person != null ? person.getName() : "no-name";
    }

    public void countRound(Color color, Player otherPlayer) {
        this.newOpponent = otherPlayer;
        playersMet.add(otherPlayer);
        addColor(color);
    }

    public void countWalkover() {
        this.walkover = true;
        addColor(Color.WHITE);
    }

    private void addColor(Color color) {
        if (colors == null) {
            colors = "";
        }
        colors += color.name() + ":";
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
            return asLinkedList().getLast().getOther();
        }
    }

    private LinkedList<Color> asLinkedList() {
        if (StringUtils.isNotEmpty(colors)) {
            return new LinkedList<>(Arrays.stream(
                    colors.split(":"))
                    .map(Color::valueOf)
                    .collect(toList()));
        } else {
            return new LinkedList<>();
        }

    }

    public boolean hasMet(Player otherPlayer) {
        return playersMet.contains(otherPlayer);
    }

    public Color mustHaveColor() {
        LinkedList<Color> matches = asLinkedList();
        if (matches.size() >= 2) {
            if (matches.get(matches.size() - 2) == matches.getLast()) {
                return matches.getLast().getOther();
            } else {
                long blacks = numberOfRounds(BLACK);
                long white = numberOfRounds(WHITE);
                if (blacks - white >= 2) {
                    return WHITE;
                } else if (white - blacks >= 2) {
                    return BLACK;
                }
            }

        }
        return null;
    }

    @Override
    public String toString() {
        return "Player{" +
                " id=" + id +
                ", score=" + score +
                ", name='" + getName() + '\'' +
                ", met='" + playersMet.stream().map(p -> String.valueOf(p.getName())).collect(Collectors.joining(",")) + '\'' +
                ", colors='" + asLinkedList() + '\'' +
                ", must='" + mustHaveColor() + '\'' +
                '}';
    }

    public double getScore() {
        return score;
    }

    public void removeLastOpponent() {
        playersMet.remove(newOpponent);
        LinkedList<Color> matches = asLinkedList();
        matches.removeLast();
        colors = matches.stream().map(Enum::name).collect(joining(":", "", ":"));
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

            return
                    monrad2 == 0 ?
                            monrad1 == 0 ?
                                    monrad == 0 ?
                                            berger == 0 ?
                                                    getName().compareTo(p2.getName())
                                                    : berger
                                            : monrad
                                    : monrad1
                            : monrad2;
        } else {
            return (int) (1000.0 * (score2 - score1));
        }
    }


    public Long getId() {
        return id;
    }

    public Person getPerson() {
        return person;
    }

    public void setTournamentRank(int tournamentRank) {
        this.tournamentRank = tournamentRank;
    }

    public int getTournamentRank() {
        return tournamentRank;
    }

    public Tournament getTournament() {
        return tournament;
    }

    public boolean hasWalkover() {
        return walkover;
    }

    public LinkedList<Color> getColorsAsList() {
        return asLinkedList();
    }

    public double getMonrad() {
        return monrad;
    }

    public double getMonrad1() {
        return monrad1;
    }

    public double getMonrad2() {
        return monrad2;
    }

    public static class PlayerSerializer implements JsonSerializer<Player> {
        @Override
        public JsonElement serialize(Player player, Type typeOfSrc, JsonSerializationContext context) {
            JsonObject root = new JsonObject();
            root.addProperty("id", player.id);
            root.addProperty("name", player.getName());
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
