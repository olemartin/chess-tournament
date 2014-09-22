package net.olemartin.service;

import net.olemartin.business.Player;
import net.olemartin.business.Tournament;
import net.olemartin.database.TournamentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class TournamentService {

    private final TournamentRepository tournamentRepository;

    @Autowired
    public TournamentService(TournamentRepository tournamentRepository) {
        this.tournamentRepository = tournamentRepository;
    }

    public Tournament save(Tournament tournament) {
        return tournamentRepository.save(tournament);
    }

    public Tournament retrieve(Long tournamentId) {
        return tournamentRepository.findOne(tournamentId);
    }

    public void addPlayers(Long tournamentId, List<Player> players) {
        Tournament tournament = retrieve(tournamentId);
        players.stream().forEach(tournament::addPlayer);
        save(tournament);
    }
}
