package server;

import io.javalin.*;
import server.Handlers.*;
import service.Service;
import service.UserService;

public class Server {
    private Service service;
    private final Javalin javalin;
    private Database db;

    public Server() {
        db = new Database();
        service = new Service(db);
        javalin = Javalin.create(config -> config.staticFiles.add("/web"));
        javalin.post("/user", new RegisterHandler(new UserService(service)))
                .delete("/db", new ClearHandler(service))
                .post("/session", new LoginHandler(new UserService(service)))
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
