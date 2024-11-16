package lab1_sockets.net;

import lab1_sockets.game.GameState;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.Socket;
import java.util.Scanner;

public class Client {
    static String host = "localhost";
    static int port = 8080;

    final DataInputStream dis;
    final DataOutputStream dos;

    public Client() throws Exception {
        Socket socket = new Socket(host, port);
        System.out.println("Client started");

        dis = new DataInputStream(socket.getInputStream());
        dos = new DataOutputStream(socket.getOutputStream());

        GameState gameState = new GameState();

        char player_char = dis.readChar();

        ClientWorker clientWorker = new ClientWorker(gameState, socket, dis, dos, player_char);

        if (player_char == 'x' || player_char == 'o') {
            System.out.println("Connected to server");
            System.out.println("Player " + player_char);
            dos.writeUTF("getGameState");
            while (true) {
                Scanner scanner = new Scanner(System.in);
                int x = scanner.nextInt();
                int y = scanner.nextInt();
                clientWorker.tryMakeMove(x, y);
            }
        }
        else {
            System.out.println("Two players already connected");
        }
    }

    public static void main(String[] args) throws Exception {
        Client client = new Client();
    }
}
