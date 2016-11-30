package net.olemartin.domain;

import net.olemartin.engine.MonradEngine;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;

public class MonradTest {


    private MonradEngine monrad;

    @Test
    public void testInit() {
        assertThat(monrad.getPlayers().get(0).getName(), equalTo("Ole"));
    }

    @Before
    public void setup() {
        Randomizer random = new MyRandom();
        monrad = new MonradEngine(Arrays.asList(
                new Player("Ole"),
                new Player("Per"),
                new Player("Jan"),
                new Player("Ola")));

    }

    @Test
    public void drawRoundOne() {
        List<Match> matches = monrad.round(1);
        assertThat(matches.size(), equalTo(2));
        assertThat(matches.get(0).getWhite().getName(), equalTo("Ola"));
        assertThat(matches.get(0).getBlack().getName(), equalTo("Jan"));
        assertThat(matches.get(1).getWhite().getName(), equalTo("Per"));
        assertThat(matches.get(1).getBlack().getName(), equalTo("Ole"));
    }

    @Test
    public void drawRoundTwo() {
        monrad.round(1);


        List<Match> matches = monrad.round(2);

        assertThat(matches.size(), equalTo(2));
        assertThat(matches.get(0).getWhite().getName(), equalTo("Ole"));
        assertThat(matches.get(0).getBlack().getName(), equalTo("Jan"));

        assertThat(matches.get(1).getWhite().getName(), equalTo("Per"));
        assertThat(matches.get(1).getBlack().getName(), equalTo("Ola"));
    }

    public class MyRandom extends Randomizer {
        @Override
        public void shuffle(List players) {
        }
    }
}
