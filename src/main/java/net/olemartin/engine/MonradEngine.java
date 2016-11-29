package net.olemartin.engine;

import com.google.common.collect.Lists;
import net.olemartin.domain.*;

import java.util.*;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.joining;
import static net.olemartin.domain.Color.BLACK;
import static net.olemartin.domain.Color.WHITE;

public class MonradEngine implements TournamentEngine {

    private final List<Player> players;
    private Set<String> triedCombinations = new HashSet<>();

    public MonradEngine(Randomizer random, Iterable<Player> players) {
        this.players = Lists.newArrayList(players);
        random.shuffle(this.players);
    }

    @Override
    public List<Player> getPlayers() {
        return players;
    }

    @Override
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

        if (players.size() % 2 == 1) {
            Player walkoverPlayer = findLowestRankedPlayerWithoutWalkover(players);
            pickedPlayers.add(walkoverPlayer);
            Match walkoverMatch = new Match(walkoverPlayer);
            walkoverMatch.reportResult(Result.WALKOVER);
            matches.add(walkoverMatch);
        }

        try {
            triedCombinations = new HashSet<>();
            while (pickedPlayers.size() != players.size()) {
                Player player = players.stream().filter(p -> !pickedPlayers.contains(p)).findFirst().get();
                System.out.println("Trying player " + player.getName());
                pickPlayer(matches, pickedPlayers, player, false);
            }
        } catch (NotPossibleException e) {
            triedCombinations = new HashSet<>();
            while (pickedPlayers.size() != players.size()) {
                Player player = players.stream().filter(p -> !pickedPlayers.contains(p)).findFirst().get();
                System.out.println("Trying player " + player.getName());
                pickPlayer(matches, pickedPlayers, player, true);
            }
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


        Color color1 = player1.nextOptimalColor();

        List<Player> opponents = players.stream()
                .filter(p -> p != player1)
                .filter(p -> !pickedPlayers.contains(p))
                .filter(p -> !p.hasMet(player1))
                .filter(p -> matchColor(overrideThreeRule, player1.mustHaveColor(), p))
                .filter(p -> !triedCombination(pickedPlayers, player1, p))
                .collect(Collectors.toList());

        Player player2;
        if (opponents.isEmpty()) {
            rollback(matches, pickedPlayers, player1, overrideThreeRule);
            return;
        } else {
            player2 = opponents.get(0);
        }

        Color color2 = player2.nextOptimalColor();

        if (player1.mustHaveColor() == WHITE || player2.mustHaveColor() == BLACK) {
            matches.add(new Match(player1, player2));
        } else if (player1.mustHaveColor() == BLACK || player2.mustHaveColor() == WHITE) {
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

    private void rollback(LinkedList<Match> matches, LinkedList<Player> pickedPlayers, Player player, boolean override) {
        System.out.println("Rolling back because of player " + player.getName());
        if (pickedPlayers.size() == 0 || (matches.size() == 0 || (matches.size() == 1 && matches.get(0).isWalkover()))) {
            System.out.println("Tried combinations: " + triedCombinations.size());
            System.out.println("\n\n\n\n FAILED \n\n\n\n");
            throw new NotPossibleException();
        } else {
            pickedPlayers.removeLast();
            pickedPlayers.removeLast();
            matches.getLast().getBlack().removeLastOpponent();
            matches.getLast().getWhite().removeLastOpponent();
            matches.removeLast();
            pickPlayer(matches, pickedPlayers, player, override);
        }
    }

}