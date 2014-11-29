package net.olemartin.domain;

import org.junit.Test;

import java.util.Arrays;

import static org.junit.Assert.assertEquals;

public class PlayerTest {

    @Test
    public void should_calculate_correct_monrad() {

        Player p1 = new Player("ole");
        p1.increaseScore(10);

        Player p2 = new Player("per");
        p2.increaseScore(10);

        Player p3 = new Player("jan");
        p3.increaseScore(10);

        p1.countRound(Color.BLACK, p2);
        p1.countRound(Color.BLACK, p3);


        p1.setMonradAndBerger(Arrays.asList(), Arrays.asList());
        assertEquals(20.0, p1.getMonrad(), 0.1);
        assertEquals(10.0, p1.getMonrad1(), 0.1);
        assertEquals(0, p1.getMonrad2(), 0.1);
    }
}
