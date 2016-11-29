package net.olemartin.service.match;

import net.olemartin.domain.Match;
import net.olemartin.domain.Player;
import net.olemartin.domain.Result;
import net.olemartin.domain.Tournament;
import net.olemartin.repository.PlayerRepository;
import net.olemartin.repository.TournamentRepository;
import net.olemartin.spring.Bootstrap;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Iterator;
import java.util.List;
import java.util.Set;

import static org.junit.Assert.assertTrue;


@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = Bootstrap.class)
@ActiveProfiles("test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class MatchServiceTest {

    @Autowired
    MatchService matchService;

    @Autowired
    TournamentRepository tournamentRepository;

    @Autowired
    PlayerRepository playerRepository;

    @Test
    public void shouldCreateNewRoundAndUpdateEntities() {
        Tournament tournament = new Tournament("olemartin").setEngine("ROUND_ROBIN");

        tournament.addPlayer(playerRepository.save(new Player("Ole-Martin")));
        tournament.addPlayer(playerRepository.save(new Player("Line")));
        tournament.addPlayer(playerRepository.save(new Player("Leonora")));
        tournament.addPlayer(playerRepository.save(new Player("Louise")));
        tournament = tournamentRepository.save(tournament);
        Set<Match> matches = matchService.nextRound(tournament.getId());
        Iterator<Match> iterator = matches.iterator();
        matchService.reportResult(iterator.next().getId(), Result.BLACK);
        matchService.reportResult(iterator.next().getId(), Result.WHITE);

        Player player = (Player) ((List)playerRepository.findAll()).get(0);
        assertTrue(player.hasPlayedMatches());




    }

}