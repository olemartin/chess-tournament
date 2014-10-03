package net.olemartin.service;

import net.olemartin.business.Player;
import net.olemartin.business.Tournament;
import net.olemartin.database.TournamentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
        Tournament tournament = tournamentRepository.findOne(tournamentId);
        tournament.getPlayers().forEach(player -> player.setMonradEtc(tournament.getRounds()));
        tournament.getPlayers().forEach(player -> player.setRoundScore(tournament.getPlayers(), tournament.getRounds()));
        return tournament;
    }

    public void addPlayer(Long tournamentId, Player player) {
        Tournament tournament = retrieve(tournamentId);
        tournament.addPlayer(player);
        save(tournament);
    }
}
