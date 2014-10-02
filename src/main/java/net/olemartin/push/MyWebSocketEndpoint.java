package net.olemartin.push;

import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@WebSocket
public class MyWebSocketEndpoint implements ApplicationContextAware {


    List<Session> sessions = new ArrayList<>();
    private ApplicationContext applicationContext;

    @OnWebSocketMessage
    public void onMessage(Session session, String s) throws IOException {
        sessions.add(session);
        session.getRemote().sendString("Returned; " + s);
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        applicationContext.
        this.applicationContext = applicationContext;
    }
}