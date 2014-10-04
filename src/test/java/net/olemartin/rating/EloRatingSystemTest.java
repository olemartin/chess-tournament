package net.olemartin.rating;

import net.olemartin.business.PlayerResult;
import org.junit.Test;

public class EloRatingSystemTest {

    @Test
    public void testRating() {
        EloRatingSystem system = EloRatingSystem.getInstance("game");
        int tore = 1200;
        int nilshelge=1200;
        int stale = 1200;
        int arild = 1200;
        int ketil = 1200;
        int olemartin = 1200;
        int hakon = 1200;
        int henrik = 1200;





        tore = system.getNewRating(tore, nilshelge, PlayerResult.LOSS);
        nilshelge = system.getNewRating(nilshelge, tore, PlayerResult.WIN);

        stale = system.getNewRating(stale, arild, PlayerResult.LOSS);
        arild = system.getNewRating(arild, stale, PlayerResult.WIN);

        ketil = system.getNewRating(ketil, olemartin, PlayerResult.WIN);
        olemartin = system.getNewRating(olemartin, ketil, PlayerResult.LOSS);

        hakon = system.getNewRating(ketil, olemartin, PlayerResult.WIN);
        henrik = system.getNewRating(olemartin, ketil, PlayerResult.LOSS);

        //System.out.println(rating);
    }
}
