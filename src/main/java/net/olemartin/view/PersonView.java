package net.olemartin.view;

import java.util.List;

public class PersonView {

    private String name;
    private long rating;
    private List<TournamentView> tournaments;

    public PersonView(String name, int rating, List<TournamentView> tournaments) {

        this.name = name;
        this.rating = rating;
        this.tournaments = tournaments;
    }
}
