package client.websocket;
import com.google.gson.Gson;
import model.GameData;
import websocket.messages.ServerMessage;

public class CMessageHandler {
    public CMessageHandler(ServerMessage serverMessage){
        ServerMessage.ServerMessageType messageType = serverMessage.getServerMessageType();
        Gson gson = new Gson();
        switch (messageType){
            case ERROR:
                ErrorMessage message = gson.fromJson(serverMessage.getServerMessage(), ErrorMessage.class);
                System.out.println(message);
                break;
            case LOAD_GAME:
                LoadGameMessage gameData = gson.fromJson(serverMessage.getServerMessage(), LoadGameMessage.class);
                break;
            case NOTIFICATION:
                NotificationMessage nMessage = gson.fromJson(serverMessage.getServerMessage(), NotificationMessage.class);
                System.out.println(nMessage);
                break;
        }
    }
}
