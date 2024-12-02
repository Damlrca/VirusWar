package lab2_soap.net;

import lab2_soap.game.GameState;

import javax.xml.ws.Endpoint;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {
    public static final int port = 8080;
    public static final String name = "GameState";

    Server() throws Exception {
        String url = String.format("http://localhost:%d/%s", port, name);
        GameState gameState = new GameState();
        Endpoint.publish(url, gameState);
        System.out.println("Сервер работает: " + url);
    }

    public static void main(String[] args) throws Exception {
        Server server = new Server();
    }
}
