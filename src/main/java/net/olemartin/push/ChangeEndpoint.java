package net.olemartin.push;

import net.olemartin.tools.SpringContext;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;

import java.io.IOException;

@WebSocket
public class ChangeEndpoint {

    public static enum MessageType {NEW_RESULT, PLAYER_ADDED, PLAYER_DELETED, NEW_MATCH}
    private Session session;

    @OnWebSocketMessage
    public void onMessage(Session session, String s) {
        this.session = session;
        SpringContext.getTournamentResource().registerEndpoint(this);
        SpringContext.getMatchResource().registerEndpoint(this);
        SpringContext.getPlayerResource().registerEndpoint(this);
    }

    public void sendPush(MessageType message) {
        if (session.isOpen()) {
            try {
                session.getRemote().sendString("{\"message\":\""+message.name()+"\"}");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}