package client.websocket;

import model.GameData;

public record LoadGameMessage (websocket.messages.ServerMessage.ServerMessageType messageType, GameData game)
{ }
