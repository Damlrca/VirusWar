package lab1_sockets.net;

import lab1_sockets.game.GameState;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class ServerWorker {
    private final GameState gameState;
    public final Socket socket;
    private final DataInputStream dis;
    private final DataOutputStream dos;

    ServerWorker(GameState gameState, Socket socket) throws Exception {
        this.gameState = gameState;
        this.socket = socket;

        dis = new DataInputStream(socket.getInputStream());
        dos = new DataOutputStream(socket.getOutputStream());

        synchronized (this.gameState) {
            char player_char = this.gameState.connectPlayer(this);
            dos.writeChar(player_char);
        }
        
        Thread serverWorkerThread = new Thread(() -> {
            try {
                run();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
        serverWorkerThread.setDaemon(true);
        serverWorkerThread.start();
    }

    private void run() throws Exception {
        try {
            while (true) {
                String msgType = dis.readUTF();
                System.out.println("(" + socket.getPort() + "):" + msgType);
                switch (msgType) {
                    case "getGameState":
                        gameState.sendGameState();
                        break;
                    case "tryMakeMove":
                        int x = dis.readInt();
                        int y = dis.readInt();
                        gameState.tryMakeMove(this, x, y);
                        break;
                    case "resetGameState":
                        gameState.resetGameState();
                        break;
                    default:
                        System.out.println("unknown msgType");
                        break;
                }
            }
        } catch (IOException e) {
            System.out.println("Client disconnected (" + socket.getPort() + ")");
            gameState.disconnectPlayer(this);
        }
    }

    public void sendGameState() throws Exception {
        dos.writeUTF("sendGameState");
        dos.writeUTF(new String(gameState.gameTable));
        dos.writeUTF(gameState.gameStatus.name());
        dos.writeChar(gameState.turn_player);
        dos.writeInt(gameState.moves_cnt);
    }

}
