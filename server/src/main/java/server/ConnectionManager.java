package server;

import jakarta.websocket.Session;

import javax.management.Notification;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class ConnectionManager {
    public final Map<Integer,Session> connections = new HashMap<>();

    public void add(int gameID, Session session) {
        connections.put(gameID,session);
    }

    public void remove(int gameID,Session session) throws Exception {
        try {
            connections.remove(gameID);
        } catch (Exception e) {
            throw new Exception("GameID not found in map of sessions",e);
        }
    }

    public void broadcast(Session excludeSession, Notification notification) throws IOException {
        String msg = notification.toString();
        for (Session c : connections.values()) {
            if (c.isOpen()) {
                if (!c.equals(excludeSession)) {
                    c.getBasicRemote().sendText(msg);
                }
            }
        }
    }
}