package net.olemartin.domain;

import com.google.gson.*;
import org.neo4j.graphdb.Direction;
import org.springframework.data.neo4j.annotation.Fetch;
import org.springframework.data.neo4j.annotation.GraphId;
import org.springframework.data.neo4j.annotation.NodeEntity;
import org.springframework.data.neo4j.annotation.RelatedTo;

import java.lang.reflect.Type;
import java.util.Date;
import java.util.Iterator;
import java.util.Set;

@NodeEntity
public class Person {

    @GraphId
    private Long id;

    @RelatedTo(type = "IS_PLAYER", direction = Direction.OUTGOING)
    @Fetch
    private Set<Player> players;

    @RelatedTo(type = "RATING", direction = Direction.OUTGOING)
    @Fetch
    private Rating rating;

    private String name;

    @SuppressWarnings("UnusedDeclaration")
    private Person() {
    }

    public Person(String name) {
        this.name = name;
    }

    public int getRating() {
        return rating == null ? 1200 : rating.getRating();
    }

    public void setRating(int rating) {
        this.rating = new Rating(new Date(), rating, this.rating);
    }

    public Iterator<Rating> getRatings() {
        return rating.iterator();
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
            JsonSerializer<Rating> ratingSerializer = new Rating.RatingSerializer();
            JsonObject root = new JsonObject();
            root.addProperty("id", person.id);
            root.addProperty("name", person.name);
            root.addProperty("rating", person.getRating());
            JsonArray playerArray = new JsonArray();
            person.players.stream().forEach(player -> playerArray.add(playerSerializer.serialize(player, Player.class, context)));
            root.add("players", playerArray);

            JsonArray ratingsArray = new JsonArray();
            person.rating.forEach(rating -> ratingsArray.add(ratingSerializer.serialize(rating, Rating.class, context)));
            root.add("ratings", ratingsArray);

            return root;
        }
    }
}
