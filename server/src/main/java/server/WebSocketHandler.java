package server;

import io.javalin.websocket.*;
import org.jetbrains.annotations.NotNull;

public class WebSocketHandler implements WsConnectHandler, WsMessageHandler, WsCloseHandler {
    @Override
    public void handleClose(@NotNull WsCloseContext wsCloseContext) throws Exception {
        System.out.println("Websocket closed");
    }

    @Override
    public void handleConnect(@NotNull WsConnectContext wsConnectContext) throws Exception {
        wsConnectContext.enableAutomaticPings();
        System.out.println("Websocket connected");
    }

    @Override
    public void handleMessage(@NotNull WsMessageContext wsMessageContext) throws Exception {
        System.out.println("WS Response: " + wsMessageContext.message());
    }


}
