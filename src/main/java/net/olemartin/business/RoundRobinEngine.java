package net.olemartin.business;

import com.google.common.collect.Lists;

import java.util.ArrayList;
import java.util.List;

public class RoundRobinEngine implements TournamentEngine {

    private final ArrayList<Player> players;

    private List<Player> list1 = new ArrayList<>();
    private List<Player> list2 = new ArrayList<>();
    private int sublistSize;


    public RoundRobinEngine(Iterable<Player> players) {
        this.players = Lists.newArrayList(players);
        sublistSize = this.players.size() / 2;
    }

    void rotateLists(int round) {
        Player pivot = players.get(0);
        List move = players.subList(players.size() - round, players.size());
        List stand = players.subList(1, players.size() - round);
        List finale = new ArrayList<>();
        finale.add(pivot);
        finale.addAll(move);
        finale.addAll(stand);
        list1 = finale.subList(0, sublistSize);
        list2 = Lists.reverse(finale.subList(sublistSize, finale.size()));
    }

    @Override
    public List<Player> getPlayers() {
        return players;
    }

    @Override
    public List<Match> round(int round) {
        rotateLists(round -1);
        List<Match> matches = new ArrayList<>();
        for (int i = 0; i < list1.size(); i++) {
            matches.add(new Match(list1.get(i), list2.get(i)));
        }
        return matches;
    }
}
