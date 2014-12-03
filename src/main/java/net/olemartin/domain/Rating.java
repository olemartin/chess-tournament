package net.olemartin.domain;

import org.neo4j.graphdb.Direction;
import org.springframework.data.annotation.Id;
import org.springframework.data.neo4j.annotation.NodeEntity;
import org.springframework.data.neo4j.annotation.RelatedTo;

import java.util.Date;

@NodeEntity
public class Rating {

    @SuppressWarnings("UnusedDeclaration")
    @Id
    private Long id;
    private Date date;
    private int rating;

    @RelatedTo(type = "PREVIOUS_RATING", direction = Direction.OUTGOING)
    private Rating next;

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

    public Date getDate() {
        return date;
    }
}
