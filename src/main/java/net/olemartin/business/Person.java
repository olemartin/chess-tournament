package net.olemartin.business;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import net.olemartin.rating.EloRatingSystem;
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

    private int rating;


    private Person() {
    }

    public void calculateRating(EloRatingSystem system, Set<Round> rounds) {


    }

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    public static class PersonSerializer implements JsonSerializer<Person> {
        @Override
        public JsonElement serialize(Person person, Type typeOfSrc, JsonSerializationContext context) {
            JsonObject root = new JsonObject();
            root.addProperty("id", person.id);
            root.addProperty("name", person.name);
            root.addProperty("rating", person.rating);

            return root;
        }
    }
}
