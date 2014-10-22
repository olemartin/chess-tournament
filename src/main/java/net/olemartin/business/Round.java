package net.olemartin.business;

import com.google.gson.*;
import org.neo4j.graphdb.Direction;
import org.springframework.data.neo4j.annotation.Fetch;
import org.springframework.data.neo4j.annotation.GraphId;
import org.springframework.data.neo4j.annotation.NodeEntity;
import org.springframework.data.neo4j.annotation.RelatedTo;

import java.lang.reflect.Type;
import java.util.HashSet;
import java.util.Set;

@NodeEntity
public class Round implements Comparable<Round> {

    @GraphId
    private Long id;

    private int number;

    @RelatedTo(type = "CONSIST_OF", direction = Direction.OUTGOING)
    @Fetch
    private Set<Match> matches = new HashSet<>();

    @RelatedTo(type = "ROUND_OF", direction = Direction.INCOMING)
    private Tournament tournament;

    private Round() {
    }

    public Round(Tournament tournament, int number) {
        this.tournament = tournament;
        this.number = number;
    }

    public int getNumber() {
        return number;
    }

    public void addMatch(Match match) {
        matches.add(match);
    }


    public boolean isFinished() {
        for (Match match : matches) {
            if (!match.hasResult()) {
                return false;
            }
        }
        return true;
    }

    @Override
    public int compareTo(Round o) {
        return o.number - number;
    }

    public Set<Match> getMatches() {
        return matches;
    }

    public static class RoundSerializer implements JsonSerializer<Round> {

        @Override
        public JsonElement serialize(Round src, Type typeOfSrc, JsonSerializationContext context) {
            Match.MatchSerializer matchSerializer = new Match.MatchSerializer();
            JsonObject root = new JsonObject();
            root.addProperty("id", src.id);
            root.addProperty("number", src.number);
            JsonArray array = new JsonArray();
            src.matches.stream().filter(m -> !m.isWalkover()).forEach(match -> array.add(matchSerializer.serialize(match, Match.class, context)));
            root.add("matches", array);
            return root;
        }
    }
}
