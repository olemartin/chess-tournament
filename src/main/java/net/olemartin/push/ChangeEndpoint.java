package net.olemartin.push;

import net.olemartin.tools.SpringContext;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;

import java.io.IOException;

@WebSocket
public class ChangeEndpoint {

    private Session session;

    @OnWebSocketMessage
    public void onMessage(Session session, String s) throws IOException {
        this.session = session;
        SpringContext.getTournamentResource().registerEndpoint(this);
        SpringContext.getMatchResource().registerEndpoint(this);
    }

    public void sendPush(String message) {
        if (session.isOpen()) {
            try {
                session.getRemote().sendString("{\"message\":\""+message+"\"}");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}