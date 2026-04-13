package client.websocket;

import client.ServerMessageHandler;
import com.google.gson.Gson;
import jakarta.websocket.*;
import websocket.commands.UserGameCommand;
import websocket.messages.ServerMessage;

import java.io.IOException;
import java.net.URI;

public class WebsocketFacade extends Endpoint {
    private Session session;
    private Gson gson;

    public WebsocketFacade(String url, ServerMessageHandler s) throws Exception{
        try {
            url = url.replace("http", "ws");
            URI socketURI = new URI(url + "/ws");
            gson = new Gson();
            WebSocketContainer container = ContainerProvider.getWebSocketContainer();
            session = container.connectToServer(this,socketURI);
            session.addMessageHandler(new MessageHandler.Whole<String>() {
                @Override
                public void onMessage(String message) {
                    ServerMessage serverMessage = new Gson().fromJson(message, ServerMessage.class);
                    s.notify(serverMessage,message);
                }});
        } catch (Exception e) {
            throw new Exception("Error starting websocket: "+ e.getMessage(),e);
        }
    }

    public void send(UserGameCommand command) throws IOException {
        session.getBasicRemote().sendText(gson.toJson(command));
    }

    @Override
    public void onOpen(Session session, EndpointConfig endpointConfig) {
    }
}
