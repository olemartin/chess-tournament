package net.olemartin.domain;

import com.google.gson.*;
import org.neo4j.ogm.annotation.GraphId;
import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Relationship;

import java.lang.reflect.Type;
import java.util.HashSet;
import java.util.Set;

@NodeEntity
public class Round implements Comparable<Round> {

    @GraphId
    private Long id;

    private int number;

    @Relationship(type = "CONSIST_OF", direction = Relationship.OUTGOING)
    private Set<Match> matches = new HashSet<>();

    private Round() {
    }

    public Round(int number) {
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

    public Long getId() {
        return id;
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
