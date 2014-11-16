package net.olemartin.business;

import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;

public class MonradTest {


    private Monrad monrad;

    @Test
    public void testInit() {
        assertThat(monrad.getPlayers().get(0).getName(), equalTo("Ole"));
    }

    @Before
    public void setup() {
        Randomizer random = new MyRandom();
        monrad = new Monrad(random, Arrays.asList(
                new Player("Ole"),
                new Player("Per"),
                new Player("Jan"),
                new Player("Ola")));

    }

    @Test
    public void drawRoundOne() {
        List<Match> matches = monrad.round(1);
        assertThat(matches.size(), equalTo(2));
        assertThat(matches.get(0).getWhite().getName(), equalTo("Per"));
        assertThat(matches.get(1).getBlack().getName(), equalTo("Jan"));
    }

    @Test
    public void drawRoundTwo() {
        List<Match> matches = monrad.round(1);

       // monrad.reportResult(matches.get(0), Result.WHITE);
        //monrad.reportResult(matches.get(1), Result.BLACK);

        matches = monrad.round(2);

        assertThat(matches.size(), equalTo(2));
        assertThat(matches.get(0).getWhite().getName(), equalTo("Jan"));
        assertThat(matches.get(0).getBlack().getName(), equalTo("Per"));

        assertThat(matches.get(1).getWhite().getName(), equalTo("Ole"));
        assertThat(matches.get(1).getBlack().getName(), equalTo("Ola"));
    }

    @Test
    public void testSix() {
        drawSeveralRounds(6);
    }

    @Test
    public void testEight() {
        drawSeveralRounds(8);
    }

    @Test
    public void testTen() {
        drawSeveralRounds(10);
    }

    @Test
    public void testTwelve() {
        drawSeveralRounds(12);
    }

    @Test
    public void testFourteen() {
        drawSeveralRounds(14);
    }

    @Test
    public void testSixteen() {
        drawSeveralRounds(16);
    }

    @Test
    public void testEighteen() {
        drawSeveralRounds(18);
    }

    @Test
    public void testTwenty() {
        drawSeveralRounds(20);
    }

    public void drawSeveralRounds(int numPlayers) {
        TournamentEngine tournamentEngine = new Monrad(new MyRandom(), Arrays.asList());

        double rounds = 1 + Math.log(numPlayers) / Math.log(2);
        for (int i = 0; i < numPlayers; i++) {
            //tournamentEngine.addPlayer("Player_" + i);
        }
        for (int i = 1; i < rounds; i++) {
            List<Match> matches = tournamentEngine.round(i);
            for (Match match : matches) {
         //       monrad.reportResult(match, Math.random() > 0.6 ?Result.BLACK : Result.WHITE);
            }
        }

    }


    public class MyRandom extends Randomizer {
        @Override
        public void shuffle(List players) {
        }
    }
}
