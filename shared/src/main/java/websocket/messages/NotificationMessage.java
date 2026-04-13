package websocket.messages;

import model.GameData;

public class NotificationMessage extends ServerMessage {
    private String message;

    public NotificationMessage(ServerMessageType type,String message) {
        super(type);
        this.message = message;
    }
    public String getMessage(){
        return message;
    }

}
