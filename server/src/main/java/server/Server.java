package server;

import io.javalin.*;
import server.Handlers.*;
import service.Service;
import service.UserService;

public class Server {
    private Service service;
    private final Javalin javalin;
    public Database db = new Database();

    public Server(Service service){
        this();
        this.service = service;
    }

    public Server() {
        javalin = Javalin.create(config -> config.staticFiles.add("/web"));
        javalin.post("/user", new RegisterHandler((UserService) service))
                .delete("/db", new ClearHandler(service))
                .post("/session", new LoginHandler(service))
                .delete("/session", new LogoutHandler(service))
                .get("/game", new GameHandler(service))
                .post("/game", new GameHandler(service))
                .put("/game", new GameHandler(service));
    }

    public int run(int desiredPort) {
        javalin.start(desiredPort);
        return javalin.port();
    }

    public void stop() {
        javalin.stop();
    }
}
