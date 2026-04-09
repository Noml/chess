package client.websocket;

import com.google.gson.Gson;
import jakarta.websocket.*;
import websocket.messages.ServerMessage;

import java.io.IOException;
import java.net.URI;

public class WebsocketFacade extends Endpoint {
    private Session session;
    private CMessageHandler messageHandler;

    public WebsocketFacade(String url) throws Exception{
        try {
            url = url.replace("http", "ws");
            URI socketURI = new URI(url + "/ws");
            WebSocketContainer container = ContainerProvider.getWebSocketContainer();
            session = container.connectToServer(this,socketURI);
            session.addMessageHandler(new MessageHandler.Whole<String>() {
                @Override
                public void onMessage(String message) {
                    ServerMessage serverMessage = new Gson().fromJson(message, ServerMessage.class);
                    messageHandler = new CMessageHandler(serverMessage);
                }});
        } catch (Exception e) {
            throw new Exception("Error starting websocket",e);
        }
    }

    public void send(String message) throws IOException {
        session.getBasicRemote().sendText(message);
    }

    @Override
    public void onOpen(Session session, EndpointConfig endpointConfig) {
    }
}
