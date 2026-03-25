package client;

import com.google.gson.Gson;
import model.GameData;
import service.requests.JoinRequest;
import service.requests.LoginRequest;
import service.requests.LogoutRequest;
import service.requests.RegisterRequest;
import service.results.CreateGameResult;
import service.results.LoginResult;
import service.results.RegisterResult;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.HashMap;

public class ServerFacade {
    private final HttpClient client = HttpClient.newHttpClient();
    private final String serverUrl;
    record Request(String authToken, String gameName){}
    record JoinGameResult(GameData gameData) {}

    public ServerFacade(String serverUrl) {
        this.serverUrl = serverUrl;
    }

    public RegisterResult register(RegisterRequest request) throws Exception{
        HttpRequest builtReq = buildRequest("POST","/user",request);
        var response = sendRequest(builtReq);
        return handleResponse(response,RegisterResult.class);
    }

    public LoginResult login(LoginRequest request) throws Exception{
        HttpRequest builtReq = buildRequest("POST","/session",request);
        var response = sendRequest(builtReq);
        return handleResponse(response,LoginResult.class);
    }

    public void logout(LogoutRequest request) throws Exception{
        HttpRequest builtReq = buildRequest("DELETE","/session",request);
        var response = sendRequest(builtReq);
        handleResponse(response,null);
    }

    public JoinGameResult joinGame(JoinRequest request, String authToken) throws Exception{
        var HTTPReq = HttpRequest.newBuilder()
                .uri(URI.create(serverUrl + "/game"))
                .method("PUT", makeRequestBody(request));
        HTTPReq.setHeader("authorization",authToken);
        HTTPReq.setHeader("Content-Type", "application/json");
        HttpRequest builtReq = HTTPReq.build();
        var response = sendRequest(builtReq);
        return handleResponse(response,JoinGameResult.class);
    }

    public ArrayList<GameData> listGames(String authToken) throws Exception{
        record Temp(String authorization){}
        record ListedGames(ArrayList<GameData> games) {}
        var HTTPReq = HttpRequest.newBuilder()
                .uri(URI.create(serverUrl + "/game"))
                .method("GET", makeRequestBody(new Temp(authToken)));
        HTTPReq.setHeader("authorization",authToken);
        HTTPReq.setHeader("Content-Type", "application/json");
        HttpRequest builtReq = HTTPReq.build();
        var response = sendRequest(builtReq);
        return handleResponse(response,ListedGames.class).games();
    }

    public CreateGameResult createGame(String authToken, String gameName) throws Exception{
        HttpRequest builtReq = buildRequest("POST","/game",new Request(authToken,gameName));
        var response = sendRequest(builtReq);
        return handleResponse(response,CreateGameResult.class);
    }

    public void clear() throws Exception{
        HttpRequest builtReq = buildRequest("DELETE","/db",null);
        var response = sendRequest(builtReq);
        handleResponse(response,null);
    }

    private HttpRequest buildRequest(String method, String path, Object body) {
        var request = HttpRequest.newBuilder()
                .uri(URI.create(serverUrl + path))
                .method(method, makeRequestBody(body));
        if (body != null) {
            if(body.getClass() == LogoutRequest.class){
                request.setHeader("authorization",((LogoutRequest) body).authToken());
            } else if (body.getClass() == Request.class) {
                request.setHeader("authorization",((Request) body).authToken());
            }
            request.setHeader("Content-Type", "application/json");
        }
        return request.build();
    }

    private HttpRequest.BodyPublisher makeRequestBody(Object request) {
        if (request != null) {
            return HttpRequest.BodyPublishers.ofString(new Gson().toJson(request));
        } else {
            return HttpRequest.BodyPublishers.noBody();
        }
    }

    private HttpResponse<String> sendRequest(HttpRequest request) throws Exception {
        return client.send(request, HttpResponse.BodyHandlers.ofString());
    }

    private <T> T handleResponse(HttpResponse<String> response, Class<T> responseClass) throws Exception {
        var status = response.statusCode();
        if (status != 200) {
            var body = response.body();
            if (body != null) {
                throw new Exception(new Gson().fromJson(body, HashMap.class).get("message").toString());
            }
            throw new Exception("other failure: " + status);
        }
        if (responseClass != null) {
            return new Gson().fromJson(response.body(), responseClass);
        }
        return null;
    }

}
