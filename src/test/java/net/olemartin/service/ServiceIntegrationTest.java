package net.olemartin.service;

import net.olemartin.business.*;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;


@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("/beans.xml")
@Transactional
public class ServiceIntegrationTest {

    @Autowired
    private PersonService personService;

    @Autowired
    private MatchService matchService;

    @Autowired
    private TournamentService tournamentService;

    @Test
    public void testFullTournament() {
        Tournament tournament = tournamentService.save(new Tournament("Testturnering"));

        List<Person> persons = getPersons();
        Long tournamentId = tournament.getId();
        tournamentService.addPlayers(tournamentId, persons);

        try {
            for (int i = 0; i < persons.size() - 1; i++) {
                List<Match> matches = matchService.nextRound(tournamentId);
                matches.stream().filter(match -> !match.isWalkover()).forEach(match -> {
                    matchService.reportResult(match.getId(), Result.WHITE);
                });
            }
        } catch (NotPossibleException e) {
            System.out.println("Not possible to generate more rounds");
        }

        List<Player> players = tournamentService.retrievePlayers(tournamentId);
        for (Player player : players) {
            System.out.println(player);
            Color lastColor = player.getColors().getFirst();
            int count = 1;
            for (int i = 1; i < player.getColors().size(); i++) {
                if (player.getColors().get(i) == lastColor) {
                    if (++count == 3) {
                        fail(player.toString());
                    }
                } else {
                    lastColor = player.getColors().get(i);
                    count = 1;
                }
            }
        }


        tournamentService.finishTournament(tournamentId);

        List<Person> allPersons = personService.getPersons();

        assertFalse(allPersons.size() == 0);
        for (Person person : allPersons) {
            assertTrue(person.getRating() != 1200);
        }
    }

    private List<Person> getPersons() {
        Person ole = personService.createPerson(new Person("Ole"));
        Person jan = personService.createPerson(new Person("Jan"));
        Person per = personService.createPerson(new Person("Per"));
        Person otto = personService.createPerson(new Person("Otto"));
        Person janne = personService.createPerson(new Person("Janne"));
        Person janne1 = personService.createPerson(new Person("Janne1"));
        Person janne2 = personService.createPerson(new Person("Janne2"));
        Person janne3 = personService.createPerson(new Person("Janne3"));
        Person janne4 = personService.createPerson(new Person("Janne4"));
        Person janne5 = personService.createPerson(new Person("Janne5"));
        Person janne6 = personService.createPerson(new Person("Janne6"));


        return Arrays.asList(ole, jan, per, otto, janne, janne1, janne2, janne3, janne4, janne5, janne6);
    }
}
