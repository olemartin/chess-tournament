package net.olemartin.service;

import com.google.common.collect.Lists;
import net.olemartin.business.Player;
import net.olemartin.repository.PlayerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
        return Lists.newArrayList(playerRepository.findAll());
    }

    public void save(Player player) {
        playerRepository.save(player);
    }

    public Player getPlayer(Long playerId) {
        return playerRepository.findOne(playerId);
    }
}
