package server;

import io.javalin.*;
import server.Handlers.*;

public class Server {

    private final Javalin javalin;

    public Server() {
        javalin = Javalin.create(config -> config.staticFiles.add("web"));
        javalin.post("/user", new RegisterHandler())
                .delete("/db", new ClearHandler())
                .post("/session", new LoginHandler())
                .delete("/session", new LogoutHandler())
                .get("/game", new GameHandler())
                .post("/game", new GameHandler())
                .put("/game", new GameHandler())
                .start(8080);
    }

    public int run(int desiredPort) {
        javalin.start(desiredPort);
        return javalin.port();
    }

    public void stop() {
        javalin.stop();
    }
}
