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

    private final Map<Integer,Set<Session>> connectionMap = new HashMap<>();

    public void add(Session session, int gameID) {
        connectionMap.computeIfAbsent(gameID, k -> new HashSet<>()).add(session);
    }

    public void remove(Session session,int gameID) throws Exception {
        try {
            var connections = connectionMap.get(gameID);
            if(connections != null){
                connections.remove(session);
                if(connections.isEmpty()){
                    connectionMap.remove(gameID);
                }
            }
        } catch (Exception e) {
            throw new Exception("Session not found in map of sessions",e);
        }
    }

    public void broadcast(Session excludeSession, Notification notification, int gameID) throws IOException {
        Gson gson = new Gson();
        var connections = connectionMap.get(gameID);
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

    public void broadcast(Session excludeSession, ServerMessage serverMessage,int gameID) throws IOException {
        Gson gson = new Gson();
        var connections = connectionMap.get(gameID);
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