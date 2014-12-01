package net.olemartin.domain;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import org.neo4j.graphdb.Direction;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.neo4j.annotation.NodeEntity;
import org.springframework.data.neo4j.annotation.RelatedTo;

import java.lang.reflect.Type;
import java.util.Date;
import java.util.Iterator;

@NodeEntity
public class Rating implements Iterable<Rating> {

    @Id
    private Long id;

    @CreatedDate
    private Date date;
    private int rating;

    @RelatedTo(type = "PREVIOUS_RATING", direction = Direction.OUTGOING)
    private Rating next;

    @Transient
    private Rating that = this;

    @SuppressWarnings("UnusedDeclaration")
    private Rating() {
    }

    public Rating(Date date, int rating, Rating next) {
        this.date = date;
        this.rating = rating;
        this.next = next;
    }

    public int getRating() {
        return rating;
    }


    @Override
    public Iterator<Rating> iterator() {
        return new Iterator<Rating>() {
            private Rating current = that;

            @Override
            public boolean hasNext() {
                return current.next != null;
            }

            @Override
            public Rating next() {
                current = next;
                return current;
            }
        };
    }

    public static class RatingSerializer implements JsonSerializer<Rating> {
        @Override
        public JsonElement serialize(Rating src, Type typeOfSrc, JsonSerializationContext context) {
            JsonObject root = new JsonObject();
            root.addProperty("id", src.id);
            root.addProperty("date", src.date.getTime());
            root.addProperty("rating", src.rating);
            return root;
        }
    }
}
