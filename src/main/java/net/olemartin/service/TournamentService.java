package net.olemartin.service;

import net.olemartin.business.Player;
import net.olemartin.business.Tournament;
import net.olemartin.database.PlayerRepository;
import net.olemartin.database.TournamentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.neo4j.support.Neo4jTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class TournamentService {

    private final TournamentRepository tournamentRepository;
    private final PlayerRepository playerRepository;
    private final Neo4jTemplate neo4jTemplate;

    @Autowired
    public TournamentService(TournamentRepository tournamentRepository, PlayerRepository playerRepository, @SuppressWarnings("SpringJavaAutowiringInspection") Neo4jTemplate neo4jTemplate) {
        this.tournamentRepository = tournamentRepository;
        this.playerRepository = playerRepository;
        this.neo4jTemplate = neo4jTemplate;
    }

    public Tournament save(Tournament tournament) {
        return tournamentRepository.save(tournament);
    }

    public Tournament retrieve(Long tournamentId) {
        return tournamentRepository.findOne(tournamentId);
    }

    public void addPlayer(Long tournamentId, Player player) {
        Tournament tournament = retrieve(tournamentId);
        tournament.addPlayer(player);
        save(tournament);
    }
}
