package net.olemartin.business;

import java.util.List;

public interface TournamentEngine {
    List<Player> getPlayers();

    List<Match> round(int round);
}
