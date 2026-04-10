package server;

import org.eclipse.jetty.websocket.api.Session;
import websocket.messages.Notification;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class ConnectionManager {
    public final Set<Session> connections = new HashSet<>();

    public void add(Session session) {
        connections.add(session);
    }

    public void remove(Session session) throws Exception {
        try {
            connections.remove(session);
        } catch (Exception e) {
            throw new Exception("Session not found in map of sessions",e);
        }
    }

    public void broadcast(Session excludeSession, Notification notification) throws IOException {
        String msg = notification.toString();
        for (Session c : connections) {
            if (c.isOpen()) {
                if (!c.equals(excludeSession)) {
                    c.getRemote().sendString(msg);
                }
            }
        }
    }
}