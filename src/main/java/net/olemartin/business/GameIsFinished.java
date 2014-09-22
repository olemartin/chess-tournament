package net.olemartin.business;

public class GameIsFinished extends RuntimeException {
    private final Player player;

    public GameIsFinished(Player player) {

        this.player = player;
    }

    public Player getPlayer() {
        return player;
    }
}
