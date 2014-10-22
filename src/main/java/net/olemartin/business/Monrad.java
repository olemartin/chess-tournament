package net.olemartin.business;

import com.google.common.collect.Lists;

import java.util.*;

import static java.util.stream.Collectors.joining;
import static net.olemartin.business.Color.BLACK;
import static net.olemartin.business.Color.WHITE;

public class Monrad {

    private final Randomizer random;
    private final List<Player> players;
    private Set<String> triedCombinations = new HashSet<>();

    public Monrad(Randomizer random, Iterable<Player> players) {
        this.random = random;
        this.players = Lists.newArrayList(players);
    }

    public void addPlayer(String name) {
        players.add(new Player(name));
    }

    public void init() {
        random.shuffle(players);
    }

    public List<Player> getPlayers() {
        return players;
    }

    public List<Match> round(int round) {
        Collections.sort(players);
        LinkedList<Match> matches = new LinkedList<>();
        if (round == 1) {
            for (int i = 0; i < players.size() - 1; i += 2) {
                matches.add(new Match(players.get(i + 1), players.get(i)));
            }
            if (players.size() % 2 == 1) {
                Match walkoverMatch = new Match(players.get(players.size() - 1));
                walkoverMatch.reportResult(Result.WALKOVER);
                matches.add(walkoverMatch);
            }
        } else {
            System.out.println("Current round: " + round + "\n\n");
            otherRounds(matches);
        }
        return matches;
    }

    private void otherRounds(LinkedList<Match> matches) {

        LinkedList<Player> pickedPlayers = new LinkedList<>();
        triedCombinations = new HashSet<>();

        if (players.size() % 2 == 1) {
            Player walkoverPlayer = findLowestRankedPlayerWithoutWalkover(players);
            pickedPlayers.add(walkoverPlayer);
            Match walkoverMatch = new Match(walkoverPlayer);
            walkoverMatch.reportResult(Result.WALKOVER);
            matches.add(walkoverMatch);
        }

        while (pickedPlayers.size() != players.size()) {
            Player player = players.stream().filter(p -> !pickedPlayers.contains(p)).findFirst().get();
            System.out.println("Trying player " + player.getName());
            pickPlayer(matches, pickedPlayers, player, false);
        }
    }

    private Player findLowestRankedPlayerWithoutWalkover(List<Player> players) {
        for (int i = players.size() - 1; i >= 0; i--) {
            Player player = players.get(i);
            if (!player.hasWalkover() && player.mustHaveColor() != Color.BLACK) {
                return player;
            }
        }
        throw new IllegalStateException("All players has had walkover");
    }

    private void pickPlayer(LinkedList<Match> matches, LinkedList<Player> pickedPlayers, Player player1, boolean overrideThreeRule) {


        Color mustHave = player1.mustHaveColor();

        Object[] opponents = players.stream()
                .filter(p -> p != player1)
                .filter(p -> !pickedPlayers.contains(p))
                .filter(p -> !p.hasMet(player1))
                .filter(p -> matchColor(overrideThreeRule, mustHave, p))
                .filter(p -> !triedCombination(pickedPlayers, player1, p))
                .toArray();

        Player player2;
        if (opponents.length == 0) {
            rollback(matches, pickedPlayers, player1);
            return;
        } else {
            player2 = (Player) opponents[0];
        }
        Color color1 = player1.nextOptimalColor();
        Color color2 = player2.nextOptimalColor();

        if (mustHave == WHITE) {
            matches.add(new Match(player1, player2));
        } else if (mustHave == BLACK) {
            matches.add(new Match(player2, player1));
        } else if (color1 == color2) {
            matches.add(new Match(player2, player1));
        } else {
            if (color1 == WHITE) {
                matches.add(new Match(player1, player2));
            } else {
                matches.add(new Match(player2, player1));
            }
        }
        pickedPlayers.addAll(Arrays.asList(player1, player2));
        updateTriedCombination(pickedPlayers);
    }

    private boolean matchColor(boolean overrideThreeRule, Color mustHave, Player p) {
        return overrideThreeRule
                || mustHave == null
                || p.mustHaveColor() == null
                || mustHave != p.mustHaveColor();
    }

    private void updateTriedCombination(LinkedList<Player> pickedPlayers) {
        String collect = pickedPlayers.stream().map(Player::getName).collect(joining(", "));
        triedCombinations.add(collect);
        System.out.println("Storing " + collect);
        Player p1 = pickedPlayers.removeLast();
        Player p2 = pickedPlayers.removeLast();

        pickedPlayers.add(p1);
        pickedPlayers.add(p2);

        triedCombinations.add(pickedPlayers.stream().map(Player::getName).collect(joining(", ")));

    }

    private boolean triedCombination(LinkedList<Player> pickedPlayers, Player player1, Player player2) {
        List<Player> tempPlayers = (List<Player>) pickedPlayers.clone();
        tempPlayers.add(player1);
        tempPlayers.add(player2);
        String combination = tempPlayers.stream().map(Player::getName).collect(joining(", "));
        return triedCombinations.contains(combination);
    }

    private void rollback(LinkedList<Match> matches, LinkedList<Player> pickedPlayers, Player player) {
        System.out.println("Rolling back because of player " + player.getName());
        if (pickedPlayers.size() == 0 || (matches.size() == 0 || (matches.size() == 1 && matches.get(0).isWalkover()))) {
            System.out.println(triedCombinations.size() + ". " + pickedPlayers.stream().map(Player::getName).collect(joining(", ")));
            pickPlayer(matches, pickedPlayers, player, true);
        } else {
            pickedPlayers.removeLast();
            pickedPlayers.removeLast();
            matches.getLast().getBlack().removeLastOpponent();
            matches.getLast().getWhite().removeLastOpponent();
            matches.removeLast();
            pickPlayer(matches, pickedPlayers, player, false);
        }
    }

}