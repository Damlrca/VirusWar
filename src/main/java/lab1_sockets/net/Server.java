package lab1_sockets.net;

import lab1_sockets.game.GameState;

import java.net.ServerSocket;
import java.net.Socket;

public class Server {
    static int port = 8080;

    Server() throws Exception {
        ServerSocket listenSocket = new ServerSocket(port);
        System.out.println("Server started");
        GameState gameState = new GameState();
        while (true) {
            Socket socket = listenSocket.accept();
            System.out.println("Client connected (" + socket.getPort() + ")");
            ServerWorker serverWorker = new ServerWorker(gameState, socket);
        }
    }

    public static void main(String[] args) throws Exception {
        Server server = new Server();
    }
}
