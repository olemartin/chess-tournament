package net.olemartin.domain;


import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Relationship;
import org.springframework.data.annotation.Id;

import java.util.Date;

@NodeEntity
public class Rating {

    @SuppressWarnings("UnusedDeclaration")
    @Id
    private Long id;
    private Date date;
    private int rating;

    @Relationship(type = "PREVIOUS_RATING", direction = Relationship.OUTGOING)
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
