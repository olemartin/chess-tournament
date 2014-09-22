package net.olemartin.business;

import org.neo4j.graphdb.Direction;
import org.springframework.data.neo4j.annotation.GraphId;
import org.springframework.data.neo4j.annotation.NodeEntity;
import org.springframework.data.neo4j.annotation.RelatedTo;

@NodeEntity
public class Match {

    @GraphId
    private Long id;

    @RelatedTo(type = "WHITE", direction = Direction.OUTGOING)
    public final Player white;
    @RelatedTo(type = "BLACK", direction = Direction.OUTGOING)
    public final Player black;

    public Match(Player white, Player black) {
        this.white = white;
        this.black = black;

        white.countRound(Color.WHITE, black);
        black.countRound(Color.BLACK, white);
    }

    public Player getWhite() {
        return white;
    }

    public Player getBlack() {
        return black;
    }

    public void reportResult(Result result) {
        switch (result) {
            case WHITE:
                white.increaseScore(1);
                break;
            case BLACK:
                black.increaseScore(1);
                break;
            case REMIS:
                white.increaseScore(0.5);
                black.increaseScore(0.5);
                break;
        }
    }
}
