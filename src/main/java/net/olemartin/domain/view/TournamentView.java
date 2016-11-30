package net.olemartin.domain.view;

import org.neo4j.ogm.annotation.Property;
import org.springframework.data.neo4j.annotation.QueryResult;

@QueryResult
public class TournamentView {

    @Property(name = "id")
    private Long id;
    @Property(name = "name")
    private String name;
    @Property(name = "finished")
    private boolean finished;

    public TournamentView() {
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

    public boolean isFinished() {
        return finished;
    }

    public void setFinished(boolean finished) {
        this.finished = finished;
    }
}
