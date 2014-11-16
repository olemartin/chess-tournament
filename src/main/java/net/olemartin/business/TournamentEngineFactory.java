package net.olemartin.business;

public class TournamentEngineFactory {

    private enum EngineType {ROUND_ROBIN, MONRAD}

    public static TournamentEngine getEngine(Randomizer randomizer, Iterable<Player> players, String engineType) {
        EngineType type = EngineType.valueOf(engineType);
        switch (type) {
            case ROUND_ROBIN:
                return new RoundRobinEngine(players);
            case MONRAD:
                return new Monrad(randomizer, players);
            default:
                throw new IllegalArgumentException();
        }
    }
}
