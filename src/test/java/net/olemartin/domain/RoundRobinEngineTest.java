package net.olemartin.domain;

import net.olemartin.engine.RoundRobinEngine;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class RoundRobinEngineTest {


    @Test
    public void testRotateOneRound() {


        Iterable<Player> players = Arrays.asList(
                new Player("1"), new Player("2"), new Player("3"), new Player("4"),
                new Player("5"), new Player("6"), new Player("7"), new Player("8"));
        RoundRobinEngine engine = new RoundRobinEngine(players);
        List<Match> matches = engine.round(1);
        assertEquals("8", matches.get(0).getBlack().getName());
        assertEquals("4", matches.get(3).getWhite().getName());
    }

    @Test
    public void testRotateTwoRounds() {


        Iterable<Player> players = Arrays.asList(
                new Player("1"), new Player("2"), new Player("3"), new Player("4"),
                new Player("5"), new Player("6"), new Player("7"), new Player("8"));
        RoundRobinEngine engine = new RoundRobinEngine(players);
        List<Match> matches = engine.round(2);
        assertEquals("7", matches.get(0).getBlack().getName());
        assertEquals("3", matches.get(3).getWhite().getName());
    }


}