package client.websocket;

public record ErrorMessage(websocket.messages.ServerMessage.ServerMessageType messageType, String errorMessage) {
}
