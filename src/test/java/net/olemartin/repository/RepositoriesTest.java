package net.olemartin.repository;


import net.olemartin.domain.*;
import net.olemartin.spring.Bootstrap;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.neo4j.helpers.collection.Iterables;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = Bootstrap.class)
@ActiveProfiles("test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class RepositoriesTest {

    @Autowired
    private MatchRepository matchRepository;

    @Autowired
    private PlayerRepository playerRepository;

    @Autowired
    private PersonRepository personRepository;

    @Autowired
    private TournamentRepository tournamentRepository;

    @Test
    public void testBottomLine() {
        assertThat(matchRepository.count(), equalTo(0L));
    }

    @Test
    public void testOnePlayer() {
        Player player = new Player("Ole-Martin");
        playerRepository.save(player);
        assertThat(playerRepository.count(), equalTo(1L));
    }

    @Test
    public void testOnePerson() {
        Person person = new Person("Ole-Martin");
        personRepository.save(person);
        assertThat(personRepository.count(), equalTo(1L));
    }

    @Test
    public void testOneTournament() {
        Tournament tournament = new Tournament("Ole-Martin");
        tournamentRepository.save(tournament);
        assertThat(tournamentRepository.count(), equalTo(1L));
    }

    @Test
    public void testOneMatchWithPlayers() {
        Player white = new Player("Line");
        Player black = new Player("Ole-Martin");
        white = playerRepository.save(white);
        black = playerRepository.save(black);
        Match match = new Match(white, black);
        match.reportResult(Result.WHITE);
        matchRepository.save(match, 1);

        Iterable<Player> players = playerRepository.findAll();
        List<Player> list = new ArrayList<>();
        Iterables.addAll(list, players);

        assertThat(matchRepository.count(), equalTo(1L));
        assertThat(list.size(), equalTo(2));
        players.forEach(System.out::println);
    }



}