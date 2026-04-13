package client;

import websocket.messages.ServerMessage;

public interface ServerMessageHandler {
    void notify(ServerMessage s, String message);
}
