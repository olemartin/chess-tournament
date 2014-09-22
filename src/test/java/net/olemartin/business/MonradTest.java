package net.olemartin.business;

import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static java.util.stream.Collectors.toList;
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
        monrad = new Monrad(random);
        monrad.addPlayer("Ole");
        monrad.addPlayer("Per");
        monrad.addPlayer("Jan");
        monrad.addPlayer("Ola");

        monrad.init();
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
        Monrad monrad = new Monrad(new MyRandom());

        double rounds = 1 + Math.log(numPlayers) / Math.log(2);
        for (int i = 0; i < numPlayers; i++) {
            monrad.addPlayer("Player_" + i);
        }
        for (int i = 1; i < rounds; i++) {
            List<Match> matches = monrad.round(i);
            for (Match match : matches) {
         //       monrad.reportResult(match, Math.random() > 0.6 ?Result.BLACK : Result.WHITE);
            }
        }

        System.out.println("Score:");
        for (Player player : monrad.getPlayers()) {
            System.out.println(
                    player.getName() + ": " + player.getScore() +
                            ". Has met: " + player.hasMet()
                            .stream()
                            .map(Player::getName)
                            .collect(toList()) + " with colors: " +
                            player.getColors()
            );
        }
    }


    public class MyRandom extends Randomizer {
        @Override
        public void shuffle(List players) {
        }
    }
}
