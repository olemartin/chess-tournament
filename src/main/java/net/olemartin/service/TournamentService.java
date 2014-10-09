package net.olemartin.service;

import net.olemartin.business.Match;
import net.olemartin.business.Player;
import net.olemartin.business.Round;
import net.olemartin.business.Tournament;
import net.olemartin.rating.EloRatingSystem;
import net.olemartin.repository.MatchRepository;
import net.olemartin.repository.PersonRepository;
import net.olemartin.repository.PlayerRepository;
import net.olemartin.repository.TournamentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.neo4j.conversion.Result;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Transactional
public class TournamentService {

    private final TournamentRepository tournamentRepository;
    private MatchRepository matchRepository;
    private PlayerRepository playerRepository;
    private PersonRepository personRepository;

    @Autowired
    public TournamentService(TournamentRepository tournamentRepository, MatchRepository matchRepository, PlayerRepository playerRepository, PersonRepository personRepository) {
        this.tournamentRepository = tournamentRepository;
        this.matchRepository = matchRepository;
        this.playerRepository = playerRepository;
        this.personRepository = personRepository;
    }

    public Tournament save(Tournament tournament) {
        return tournamentRepository.save(tournament);
    }

    public Tournament retrieve(Long tournamentId) {
        return tournamentRepository.findOne(tournamentId);
    }

    public void finishTournament(Long tournamentId) {
        Tournament tournament = retrieve(tournamentId);
        tournament.setFinished(true);
        tournament.calculateRatings(EloRatingSystem.getInstance(tournament.getName()));
        save(tournament);
        playerRepository.save(tournament.getPlayers());
        personRepository.save(tournament.getPlayers().stream().map(Player::getPerson).collect(Collectors.toList()));
    }

    public void addPlayer(Long tournamentId, Player player) {
        Tournament tournament = retrieve(tournamentId);
        tournament.addPlayer(player);
        save(tournament);
    }

    public List<Tournament> retrieveAll() {
        Result<Tournament> tournaments = tournamentRepository.query("MATCH (tournament:Tournament) RETURN tournament", new HashMap<>());
        List<Tournament> list = new ArrayList<>();
        for (Tournament tournament : tournaments) {
            list.add(tournament);
        }
        return list;
    }

    public List<Match> retrieveCurrentRoundsMatches(Long tournamentId) {
        Result<Match> matches = matchRepository.retrieveCurrentRoundsMatches(tournamentId);
        List<Match> list = new ArrayList<>();
        for (Match match : matches) {
            list.add(match);
        }
        return list;
    }

    public List<Round> retrieveRounds(Long tournamentId) {
        return tournamentRepository.findOne(tournamentId).getRounds().stream().sorted().collect(Collectors.toList());
    }

    public void delete(Long tournamentId) {
        tournamentRepository.delete(tournamentId);
    }

    public List<Player> retrievePlayers(Long tournamentId) {
        Set<Player> players = retrieve(tournamentId).getPlayers();
        for (Player player : players) {
            player.setRoundScore(
                    matchRepository.findMatchesPlayerPlayed(player.getId()),
                    tournamentRepository.findPlayersTournament(player.getId()).getPlayers());
        }
        return players.stream().sorted().collect(Collectors.toList());
    }

}
