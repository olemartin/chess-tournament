package net.olemartin.engine;

import net.olemartin.domain.Match;
import net.olemartin.domain.Player;

import java.util.List;

public interface TournamentEngine {
    List<Player> getPlayers();

    List<Match> round(int round);
}
