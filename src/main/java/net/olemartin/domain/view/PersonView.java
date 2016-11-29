package net.olemartin.domain.view;


import org.neo4j.ogm.annotation.Property;
import org.springframework.data.neo4j.annotation.QueryResult;

import java.util.List;

@QueryResult
public class PersonView {

    @Property(name = "id")
    private Long id;
    @Property(name = "name")
    private String name;
    @Property(name = "rating")
    private long rating;
    private List<PersonInTournamentView> tournaments;

    public PersonView() {
    }

    public PersonView(Long id, String name, int rating, List<PersonInTournamentView> tournaments) {
        this.id = id;
        this.name = name;
        this.rating = rating;
        this.tournaments = tournaments;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getRating() {
        return rating;
    }

    public void setRating(long rating) {
        this.rating = rating;
    }
}
