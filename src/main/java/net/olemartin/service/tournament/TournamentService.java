package net.olemartin.service.tournament;

import net.olemartin.domain.*;
import net.olemartin.domain.view.TournamentView;
import net.olemartin.repository.*;
import net.olemartin.service.match.MatchService;
import net.olemartin.tools.rating.EloRatingSystem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static java.util.stream.Collectors.toList;

@Service
@Transactional
public class TournamentService {

    private final TournamentRepository tournamentRepository;
    private MatchRepository matchRepository;
    private PlayerRepository playerRepository;
    private PersonRepository personRepository;
    private RoundRepository roundRepository;
    private MatchService matchService;

    @Autowired
    public TournamentService(TournamentRepository tournamentRepository, MatchRepository matchRepository, PlayerRepository playerRepository, PersonRepository personRepository, RoundRepository roundRepository, MatchService matchService) {
        this.tournamentRepository = tournamentRepository;
        this.matchRepository = matchRepository;
        this.playerRepository = playerRepository;
        this.personRepository = personRepository;
        this.roundRepository = roundRepository;
        this.matchService = matchService;
    }

    public Tournament save(Tournament tournament) {
        return tournamentRepository.save(tournament);
    }

    public Tournament retrieve(Long tournamentId) {
        return tournamentRepository.findOne(tournamentId);
    }

    public void finishTournament(Long tournamentId, boolean override) {
        Tournament tournament = retrieve(tournamentId);
        if (tournament.isFinished() && !override) {
            throw new IllegalStateException("Tournament already finished");
        }
        tournament.setFinished(true);
        tournament.calculateRatings(EloRatingSystem.getInstance(tournament.getName()));
        List<Player> players = tournament.getPlayers().stream().sorted().collect(toList());

        for (int i = 0; i < players.size(); i++) {
            players.get(i).setTournamentRank(i + 1);
        }
        save(tournament);
        playerRepository.save(tournament.getPlayers());
        personRepository.save(tournament.getPlayers().stream().map(Player::getPerson).collect(toList()));
    }

    public void addPlayers(Long tournamentId, List<Person> persons) {
        Tournament tournament = retrieve(tournamentId);
        for (Person person : persons) {
            Player player = new Player(person);
            player = playerRepository.save(player);
            tournament.addPlayer(player);
        }
        save(tournament);
    }

    public List<TournamentView> retrieveAll() {
        return tournamentRepository.retrieveAllTournaments();
    }

    public List<Match> retrieveCurrentRoundsMatches(Long tournamentId) {
        Iterable<Match> matches = matchRepository.retrieveCurrentRoundsMatches(tournamentId);
        List<Match> list = new ArrayList<>();
        for (Match match : matches) {
            list.add(match);
        }
        return list;
    }

    public List<Round> retrieveRounds(Long tournamentId) {
        return tournamentRepository.findOne(tournamentId).getRounds().stream().sorted().collect(toList());
    }

    public void delete(Long tournamentId) {
        tournamentRepository.delete(tournamentId);
        playerRepository.deleteLoosePlayers();
        roundRepository.deleteLooseRounds();
        matchRepository.deleteLooseMatches();
    }

    public List<Player> updateMonradAndBerger(Long id) {
        List<Player> players = retrievePlayers(id);
        matchService.updateMonradAndBerger(players);
        playerRepository.save(players);
        return players;
    }

    public List<Player> retrievePlayers(Long tournamentId) {
        Set<Player> players = retrieveTournamentPlayers(tournamentId);

        for (Player player : players) {
            player.setRoundScore(
                    matchRepository.findMatchesPlayerPlayed(player.getId()),
                    tournamentRepository.findPlayersTournament(player.getId()).getPlayers());
        }
        return players.stream().sorted().collect(toList());
    }

    private Set<Player> retrieveTournamentPlayers(Long tournamentId) {
        return tournamentRepository.findPlayersInTournament(tournamentId);
    }

}
