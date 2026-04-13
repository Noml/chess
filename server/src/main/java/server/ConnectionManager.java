package server;

import com.google.gson.Gson;
import org.eclipse.jetty.websocket.api.Session;
import websocket.messages.Notification;
import websocket.messages.NotificationMessage;
import websocket.messages.ServerMessage;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static websocket.messages.ServerMessage.ServerMessageType.NOTIFICATION;

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
        Gson gson = new Gson();
        NotificationMessage n = new NotificationMessage(NOTIFICATION,notification.message());
        String msg =  gson.toJson(n);
        for (Session c : connections) {
            if (c.isOpen()) {
                if (!c.equals(excludeSession)) {
                    c.getRemote().sendString(msg);
                }
            }
        }
    }

    public void broadcast(Session excludeSession, ServerMessage serverMessage) throws IOException {
        Gson gson = new Gson();
        String msg =  gson.toJson(serverMessage);
        for (Session c : connections) {
            if (c.isOpen()) {
                if(!c.equals(excludeSession)){
                    c.getRemote().sendString(msg);
                }
            }
        }
    }
}