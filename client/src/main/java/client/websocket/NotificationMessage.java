package client.websocket;

public record NotificationMessage(websocket.messages.ServerMessage.ServerMessageType messageType,String message) {
}
