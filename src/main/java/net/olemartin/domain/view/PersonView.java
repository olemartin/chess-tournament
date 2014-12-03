package net.olemartin.domain.view;

import org.springframework.data.neo4j.annotation.QueryResult;
import org.springframework.data.neo4j.annotation.ResultColumn;

import java.util.List;

@QueryResult
public class PersonView {

    @ResultColumn("id")
    private Long id;
    @ResultColumn("name")
    private String name;
    @ResultColumn("rating")
    private long rating;
    private List<TournamentView> tournaments;

    public PersonView() {
    }

    public PersonView(Long id, String name, int rating, List<TournamentView> tournaments) {
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
