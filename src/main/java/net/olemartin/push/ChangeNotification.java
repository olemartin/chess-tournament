package net.olemartin.push;


import org.eclipse.jetty.websocket.servlet.WebSocketServlet;
import org.eclipse.jetty.websocket.servlet.WebSocketServletFactory;
import org.springframework.stereotype.Component;

@Component
public class ChangeNotification extends WebSocketServlet {

    @Override
    public void configure(WebSocketServletFactory factory) {
        factory.getPolicy().setIdleTimeout(1000 * 3600);
        factory.register(ChangeEndpoint.class);
    }
}
