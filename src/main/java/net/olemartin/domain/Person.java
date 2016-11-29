package net.olemartin.domain;

import com.google.gson.*;
import org.neo4j.ogm.annotation.GraphId;
import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Relationship;

import java.lang.reflect.Type;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

@NodeEntity
public class Person {

    @GraphId
    private Long id;

    @Relationship(type = "IS_PLAYER", direction = Relationship.OUTGOING)
    private Set<Player> players = new HashSet<>();

    @Relationship(type = "RATING", direction = Relationship.OUTGOING)
    private Rating rating;

    private String name;

    @SuppressWarnings("UnusedDeclaration")
    private Person() {
    }

    public Person(String name) {
        this.name = name;
    }

    public Rating getRating() {
        return rating;
    }

    public int getCurrentRating() {
        return rating == null ? 1200 : rating.getRating();
    }

    public void setCurrentRating(int rating) {
        this.rating = new Rating(new Date(), rating, this.rating);
    }

    public String getName() {
        return name;
    }

    public Set<Player> getPlayers() {
        return players;
    }

    public void addPlayer(Player player) {
        players.add(player);
    }

    public Long getId() {
        return id;
    }

    public static class PersonSerializer implements JsonSerializer<Person> {
        @Override
        public JsonElement serialize(Person person, Type typeOfSrc, JsonSerializationContext context) {
            JsonSerializer<Player> playerSerializer = new Player.PlayerSerializer();
            JsonObject root = new JsonObject();
            root.addProperty("id", person.id);
            root.addProperty("name", person.name);
            JsonArray playerArray = new JsonArray();
            person.players.forEach(player -> playerArray.add(playerSerializer.serialize(player, Player.class, context)));
            root.add("players", playerArray);

            return root;
        }
    }
}
