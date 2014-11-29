/*
 * JOGRE (Java Online Gaming Real-time Engine) - API
 * Copyright (C) 2004  Bob Marks (marksie531@yahoo.com)
 * http://jogre.sourceforge.org
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */
package net.olemartin.tools.rating;

import net.olemartin.domain.PlayerResult;

import java.util.HashMap;
import java.util.StringTokenizer;

/**
 * JOGRE's implementation of the ELO rating system.  The following is an
 * example of how to use the Elo Rating System.
 * <code>
 * EloRatingSystem elo = new EloRatingSystem();
 * int userRating = 1600;
 * int opponentRating = 1650;
 * int newUserRating = elo.getNewRating(userRating, opponentRating, WIN);
 * int newOpponentRating = elo.getNewRating(opponentRating, userRating, LOSS);
 * </code>
 *
 * @author Garrett Lehman (gman)
 */
public class EloRatingSystem {

    public KFactor[] kFactors = {};

    // List of singletons are stored in this HashMap
    private static HashMap<String, EloRatingSystem> ratingSystems = null;

    private EloRatingSystem() {

        // Read k factor in from server properties
        String kFactorStr = "0-4000=64";

        // Split each of the kFactor ranges up (kfactor1,factor2, etc)
        StringTokenizer st1 = new StringTokenizer(kFactorStr, ",");
        kFactors = new KFactor[st1.countTokens()];

        int index = 0;
        while (st1.hasMoreTokens()) {
            String kfr = st1.nextToken();

            // Split the range from the value (range=value)
            StringTokenizer st2 = new StringTokenizer(kfr, "=");
            String range = st2.nextToken();

            // Retrieve value
            double value = Double.parseDouble(st2.nextToken());

            // Retrieve start end index from the range
            st2 = new StringTokenizer(range, "-");
            int startIndex = Integer.parseInt(st2.nextToken());
            int endIndex = Integer.parseInt(st2.nextToken());

            // Add kFactor to range
            kFactors[index++] = new KFactor(startIndex, endIndex, value);
        }
    }

    /**
     * Return instance of an ELO rating system.
     *
     * @param game Game to key of.
     * @return ELO rating system for specified game.
     */
    public static synchronized EloRatingSystem getInstance(String game) {
        if (ratingSystems == null) {
            ratingSystems = new HashMap<>();
        }

        // Retrieve rating system
        EloRatingSystem ratingSystem = ratingSystems.get(game);

        // If null then create new one and add to hash keying off the game
        if (ratingSystem == null) {
            ratingSystem = new EloRatingSystem();
            ratingSystems.put(game, ratingSystem);

            return ratingSystem;
        } else {
            return ratingSystem;
        }
    }

    public int getNewRating(int rating, int opponentRating, PlayerResult result) {
        switch (result) {
            case WIN:
                return getNewRating(rating, opponentRating, 1.0);
            case LOSS:
                return getNewRating(rating, opponentRating, 0.0);
            case DRAW:
                return getNewRating(rating, opponentRating, 0.5);
        }
        return -1;              // no score this time.
    }

    /**
     * Get new rating.
     *
     * @param rating         Rating of either the current player or the average of the
     *                       current team.
     * @param opponentRating Rating of either the opponent player or the average of the
     *                       opponent team or teams.
     * @param score          Score: 0=Loss 0.5=Draw 1.0=Win
     * @return the new rating
     */
    private int getNewRating(int rating, int opponentRating, double score) {
        double kFactor = getKFactor(rating);
        double expectedScore = getExpectedScore(rating, opponentRating);

        return calculateNewRating(rating, score, expectedScore, kFactor);
    }

    /**
     * Calculate the new rating based on the ELO standard formula.
     * newRating = oldRating + constant * (score - expectedScore)
     *
     * @param oldRating     Old Rating
     * @param score         Score
     * @param expectedScore Expected Score
     * @return the new rating of the player
     */
    private int calculateNewRating(int oldRating, double score, double expectedScore, double kFactor) {
        return oldRating + (int) (kFactor * (score - expectedScore));
    }

    /**
     * This is the standard chess constant.  This constant can differ
     * based on different games.  The higher the constant the faster
     * the rating will grow.  That is why for this standard chess method,
     * the constant is higher for weaker players and lower for stronger
     * players.
     *
     * @param rating Rating
     * @return Constant
     */
    private double getKFactor(int rating) {
        // Return the correct k factor.
        for (KFactor kFactor : kFactors) {
            if (rating >= kFactor.getStartIndex() && rating <= kFactor.getEndIndex()) {
                return kFactor.value;
            }
        }
        return 32;
    }

    /**
     * Get expected score based on two players.  If more than two players
     * are competing, then opponentRating will be the average of all other
     * opponent's ratings.  If there is two teams against each other, rating
     * and opponentRating will be the average of those players.
     *
     * @param rating         Rating
     * @param opponentRating Opponent(s) rating
     * @return the expected score
     */
    private double getExpectedScore(int rating, int opponentRating) {
        return 1.0 / (1.0 + Math.pow(10.0, ((double) (opponentRating - rating) / 400.0)));
    }

    /**
     * Small inner class data structure to describe a KFactor range.
     */
    public class KFactor {

        private int startIndex, endIndex;
        private double value;

        public KFactor(int startIndex, int endIndex, double value) {
            this.startIndex = startIndex;
            this.endIndex = endIndex;
            this.value = value;
        }

        public int getStartIndex() {
            return startIndex;
        }

        public int getEndIndex() {
            return endIndex;
        }

        public double getValue() {
            return value;
        }

        public String toString() {
            return "kfactor: " + startIndex + " " + endIndex + " " + value;
        }
    }
}