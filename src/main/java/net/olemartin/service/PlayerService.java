package net.olemartin.service;

import net.olemartin.business.Player;
import net.olemartin.database.PlayerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.neo4j.conversion.Result;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.LinkedList;
import java.util.List;

@Service
@Transactional
public class PlayerService {

    private final PlayerRepository playerRepository;

    @Autowired
    public PlayerService(PlayerRepository playerRepository) {
        this.playerRepository = playerRepository;
    }

    public List<Player> getPlayers() {
        Result<Player> result = playerRepository.findAll();
        List<Player> players = new LinkedList<>();
        for (Player player : result) {
            players.add(player);
        }
        return players;
    }

    public void save(Player player) {
        playerRepository.save(player);
    }
}
