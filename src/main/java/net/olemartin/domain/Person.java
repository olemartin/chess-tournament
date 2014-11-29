package net.olemartin.domain;

import com.google.gson.*;
import org.neo4j.graphdb.Direction;
import org.springframework.data.neo4j.annotation.Fetch;
import org.springframework.data.neo4j.annotation.GraphId;
import org.springframework.data.neo4j.annotation.NodeEntity;
import org.springframework.data.neo4j.annotation.RelatedTo;

import java.lang.reflect.Type;
import java.util.Set;

@NodeEntity
public class Person {

    @GraphId
    private Long id;

    @RelatedTo(type = "IS_PLAYER", direction = Direction.OUTGOING)
    @Fetch
    private Set<Player> players;

    private String name;

    private int rating = 1200;


    private Person() {
    }

    public Person(String name) {
        this.name = name;
    }

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
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

    public static class PersonSerializer implements JsonSerializer<Person> {
        @Override
        public JsonElement serialize(Person person, Type typeOfSrc, JsonSerializationContext context) {
            JsonSerializer<Player> playerSerializer = new Player.PlayerSerializer();
            JsonObject root = new JsonObject();
            root.addProperty("id", person.id);
            root.addProperty("name", person.name);
            root.addProperty("rating", person.rating);

            JsonArray playerArray = new JsonArray();
            person.players.stream().forEach(player -> playerArray.add(playerSerializer.serialize(player, Player.class, context)));
            root.add("players", playerArray);


            return root;
        }
    }
}
