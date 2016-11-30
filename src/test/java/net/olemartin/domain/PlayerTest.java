package net.olemartin.domain;

import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class PlayerTest {

    @Test
    public void should_calculate_correct_monrad() {

        Player p1 = new Player(1,"ole");
        p1.increaseScore(10);

        Player p2 = new Player(2, "per");
        p2.increaseScore(10);

        Player p3 = new Player(3, "jan");
        p3.increaseScore(10);

        p1.countRound(Color.BLACK, p2);
        p1.countRound(Color.BLACK, p3);


        p1.setMonradAndBerger(Arrays.asList(), Arrays.asList());
        assertEquals(20.0, p1.getMonrad(), 0.1);
        assertEquals(10.0, p1.getMonrad1(), 0.1);
        assertEquals(0, p1.getMonrad2(), 0.1);
    }

    @Test
    public void should_calculate_correct_monrad_wc() {
        List<Player> players = new ArrayList<>();
        players.add(new Player("Arnulf").increaseScore(9));
        players.add(new Player("Ole").increaseScore(7));
        players.add(new Player("Mats").increaseScore(7));
        players.add(new Player("Henrik").increaseScore(5));
        players.add(new Player("Håkon").increaseScore(5));
        players.add(new Player("Carl").increaseScore(4));
        players.add(new Player("Torstei").increaseScore(4));
        players.add(new Player("Snorre").increaseScore(3));
        players.add(new Player("Stian").increaseScore(1));

        for (Player player : players) {
            for (Player player1 : players) {
                if (player != player1) {
                    player.countRound(Color.BLACK, player1);
                }
            }
            player.setMonradAndBerger(Arrays.asList(), Arrays.asList());
        }


        System.out.println(players);
    }
}
