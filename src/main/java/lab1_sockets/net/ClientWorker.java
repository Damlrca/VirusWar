package lab1_sockets.net;

import lab1_sockets.game.GameFrame;
import lab1_sockets.game.GameState;
import lab1_sockets.game.GameStatus;

import javax.swing.*;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class ClientWorker {
    private final GameState gameState;
    private final Socket socket;
    private final DataInputStream dis;
    private final DataOutputStream dos;
    private final GameFrame gameFrame;
    private final char player_char;
    ClientWorker(GameState gameState, Socket socket, DataInputStream dis, DataOutputStream dos, char player_char, GameFrame gameFrame) {
        this.gameState = gameState;
        this.socket = socket;
        this.dis = dis;
        this.dos = dos;
        this.player_char = player_char;
        this.gameFrame = gameFrame;
        gameFrame.gamePanel.clientWorker = this;

        Thread clientWorkerThread = new Thread(() -> {
            try {
                run();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
        clientWorkerThread.setDaemon(true);
        clientWorkerThread.start();
    }

    private void run() throws Exception {
        try {
            while (true) {
                String s = dis.readUTF();
                System.out.println(s);
                if (s.equals("sendGameState")) {
                    receiveGameState();
                }
                else {
                    System.out.println("unkonwn " + s);
                }
                gameFrame.refresh();
            }
        } catch (IOException e) {
            System.out.println("Server disconnected ???");
        }
    }

    public void receiveGameState() throws IOException {
        String gameTable = dis.readUTF();
        for (int i = 0; i < gameState.SIZE * gameState.SIZE; i++) {
            gameState.gameTable[i] = gameTable.charAt(i);
        }
        for (int i = 0; i < gameState.SIZE * gameState.SIZE; i++) {
            System.out.print(gameState.gameTable[i]);
            if ((i + 1) % gameState.SIZE == 0) {
                System.out.println();
            }
        }
        //System.out.println(gameState.gameTable);

        gameState.gameStatus = GameStatus.valueOf(dis.readUTF());
        gameState.turn_player = dis.readChar();
        gameState.moves_cnt = dis.readInt();
        System.out.println(gameState.gameStatus + " " + gameState.turn_player + " " + gameState.moves_cnt);
    }
    public void tryMakeMove(int x, int y) throws IOException {
        dos.writeUTF("tryMakeMove");
        dos.writeInt(x);
        dos.writeInt(y);
    }

    public void tryResetGameState() throws IOException {
        dos.writeUTF("resetGameState");
    }
}
