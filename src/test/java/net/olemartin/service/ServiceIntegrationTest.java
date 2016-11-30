package net.olemartin.service;

import jdk.nashorn.internal.ir.annotations.Ignore;
import net.olemartin.domain.*;
import net.olemartin.domain.view.PersonView;
import net.olemartin.service.match.MatchService;
import net.olemartin.service.person.PersonService;
import net.olemartin.service.tournament.TournamentService;
import net.olemartin.spring.Bootstrap;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.neo4j.ogm.config.Configuration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.*;


@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = Bootstrap.class)
@ActiveProfiles("test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class ServiceIntegrationTest {

    @Autowired
    private PersonService personService;

    @Autowired
    private MatchService matchService;

    @Autowired
    private TournamentService tournamentService;

    @Bean
    public Configuration configuration() {
        Configuration config = new Configuration();
        config
                .driverConfiguration()
                .setDriverClassName("org.neo4j.ogm.drivers.embedded.driver.EmbeddedDriver");
        return config;
    }

    @Ignore
    public void test100Tournaments() {
        for (int i = 0; i < 100; i++) {
            testFullTournament();
        }
    }

    @Test
    public void testFullTournament() {
        Tournament testturnering = new Tournament("Testturnering");
        testturnering.setEngine("MONRAD");
        Tournament tournament = tournamentService.save(testturnering);

        Long tournamentId = tournament.getId();
        tournamentService.addPlayers(tournamentId, getPersons());

        for (int i = 0; i < 7; i++) {
            Set<Match> matches = matchService.nextRound(tournamentId);
            matches.stream().filter(match -> !match.isWalkover()).forEach(
                    match -> matchService.reportResult(
                            match.getId(),
                            Math.random() > 0.6 ? Result.BLACK : Result.WHITE)
            );
        }

        List<Player> players = tournamentService.retrievePlayers(tournamentId);
        Collections.sort(players);
        for (Player player : players) {
            System.out.println(player);
            Color lastColor = player.getColorsAsList().getFirst();
            int count = 1;
            for (int i = 1; i < player.getColorsAsList().size(); i++) {
                if (player.getColorsAsList().get(i) == lastColor) {
                    if (++count == 3) {
                        // fail(player.toString());
                    }
                } else {
                    lastColor = player.getColorsAsList().get(i);
                    count = 1;
                }
            }
        }


        tournamentService.finishTournament(tournamentId, false);

        List<PersonView> allPersons = personService.getPersons();

        assertFalse(allPersons.size() == 0);
        for (PersonView person : allPersons) {
            assertTrue(person.getRating() != 1200);
        }
    }

    @Test
    public void shouldCreateMatches() {
        Tournament testturnering = new Tournament("Testturnering");
        testturnering.setEngine("MONRAD");
        Tournament tournament = tournamentService.save(testturnering);
        tournamentService.addPlayers(tournament.getId(), getPersons());
        matchService.nextRound(tournament.getId());
        Set<Match> matches = tournamentService.retrieveCurrentRoundsMatches(tournament.getId());
        assertThat(matches.size(), equalTo(6));
    }

    @Test
    public void shouldStoreMatchResult() {
        Tournament testturnering = new Tournament("Testturnering");
        testturnering.setEngine("MONRAD");
        Tournament tournament = tournamentService.save(testturnering);
        tournamentService.addPlayers(tournament.getId(), getPersons());
        matchService.nextRound(tournament.getId());
        Set<Match> matches = tournamentService.retrieveCurrentRoundsMatches(tournament.getId());
        matches.forEach(match ->  matchService.reportResult(match.getId(), Result.BLACK));

        tournament = tournamentService.retrieve(tournament.getId());
        long count = tournament.getPlayers().stream().filter(Player::hasPlayedMatches).count();
        assertThat(count, equalTo(12L));
    }

    private List<Person> getPersons() {


        return Arrays.asList(
                personService.createPerson(new Person("Ole1")),
                personService.createPerson(new Person("Ole2")),
                personService.createPerson(new Person("Ole3")),
                personService.createPerson(new Person("Jan1")),
                personService.createPerson(new Person("Jan2")),
                personService.createPerson(new Person("Jan3")),
                personService.createPerson(new Person("Per1")),
                personService.createPerson(new Person("Per2")),
                personService.createPerson(new Person("Per3")),
                personService.createPerson(new Person("Otto1")),
                personService.createPerson(new Person("Otto2")),
                personService.createPerson(new Person("Otto3")),
                personService.createPerson(new Person("Janne1")));
    }
}
