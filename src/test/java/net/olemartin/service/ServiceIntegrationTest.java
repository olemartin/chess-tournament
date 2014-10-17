package net.olemartin.service;

import net.olemartin.business.Match;
import net.olemartin.business.Person;
import net.olemartin.business.Result;
import net.olemartin.business.Tournament;
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

        Person ole = personService.createPerson(new Person("Ole"));
        Person jan = personService.createPerson(new Person("Jan"));
        Person per = personService.createPerson(new Person("Per"));
        Person otto = personService.createPerson(new Person("Otto"));


        tournamentService.addPlayers(tournament.getId(), Arrays.asList(ole, jan, per, otto));

        List<Match> matches = matchService.nextRound(tournament.getId());
        for (Match match : matches) {
            matchService.reportResult(match.getId(), Result.WHITE);
        }

        matches = matchService.nextRound(tournament.getId());
        for (Match match : matches) {
            matchService.reportResult(match.getId(), Result.WHITE);
        }

        tournamentService.finishTournament(tournament.getId());

        List<Person> persons = personService.getPersons();

        assertFalse(persons.size() == 0);
        for (Person person : persons) {
            assertTrue(person.getRating() != 1200);
        }
    }
}
