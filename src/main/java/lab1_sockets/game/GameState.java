package lab1_sockets.game;

import lab1_sockets.net.ServerWorker;

import java.util.ArrayList;

public class GameState {
    public int playerXid = -1, playerOid = -1;
    public char[] gameTable;
    public GameStatus gameStatus;
    public char turn_player;
    public int moves_cnt;
    private final ArrayList<ServerWorker> serverWorkers = new ArrayList<>();

    private int xy_to_indx(int x, int y) {
        return x * 10 + y;
    }

    public GameState() throws Exception {
        reset();
    }

    private void reset() throws Exception {
        gameTable = new char[100];
        for (int i = 0; i < 100; i++) {
            gameTable[i] = '.';
        }
        if (playerOid == -1 || playerXid == -1) {
            gameStatus = GameStatus.WAIT;
        }
        else {
            gameStatus = GameStatus.ONGOING;
        }
        turn_player = 'x';
        moves_cnt = 3;
        sendGameState();
    }

    public synchronized char connectPlayer(ServerWorker serverWorker) throws Exception {
        int playerPort = serverWorker.socket.getPort();
        if (playerXid == playerPort || playerOid == playerPort) {
            return ' ';
        }
        if (playerXid == -1) {
            playerXid = playerPort;
            serverWorkers.add(serverWorker);
            if (playerXid != -1 && playerOid != -1) {
                gameStatus = GameStatus.ONGOING;
            }
            return 'x';
        }
        if (playerOid == -1) {
            playerOid = playerPort;
            serverWorkers.add(serverWorker);
            if (playerXid != -1 && playerOid != -1) {
                gameStatus = GameStatus.ONGOING;
            }
            return 'o';
        }
        return ' ';
    }

    public synchronized void disconnectPlayer(ServerWorker serverWorker) throws Exception {
        int playerPort = serverWorker.socket.getPort();
        if (playerXid == playerPort) {
            playerXid = -1;
            serverWorkers.remove(serverWorker);
            reset();
        }
        if (playerOid == playerPort) {
            playerOid = -1;
            serverWorkers.remove(serverWorker);
            reset();
        }
    }

    private final int[] dx = {-1, -1, -1, 0, 0, 1, 1, 1};
    private final int[] dy = {-1, 0, 1, -1, 1, -1, 0, 1};
    private void dfsX(boolean[][] res, int i, int j) {
        if (res[i][j]) return;
        res[i][j] = true;
        for (int k = 0; k < 8; k++) {
            int I = i + dx[k];
            int J = j + dy[k];
            if (0 <= I && I < 10 && 0 <= J && J < 10) {
                if (gameTable[xy_to_indx(i, j)] == 'x') {
                    if (gameTable[xy_to_indx(I, J)] != 'O') {
                        dfsX(res, I, J);
                    }
                }
                if (gameTable[xy_to_indx(i, j)] == 'X') {
                    if (gameTable[xy_to_indx(I, J)] != 'O' &&
                            gameTable[xy_to_indx(I, J)] != 'x') {
                        dfsX(res, I, J);
                    }
                }
            }
        }
    }
    private boolean[][] calcMovableX() {
        boolean[][] res = new boolean[10][10];
        for (int i = 0; i < 10; i++) {
            for (int j = 0; j < 10; j++) {
                res[i][j] = false;
            }
        }
        for (int i = 0; i < 10; i++) {
            for (int j = 0; j < 10; j++) {
                if (gameTable[xy_to_indx(i, j)] == 'x') {
                    dfsX(res, i, j);
                }
            }
        }
        if (gameTable[xy_to_indx(0, 0)] == '.') {
            res[0][0] = true;
        }
        for (int i = 0; i < 10; i++) {
            for (int j = 0; j < 10; j++) {
                res[i][j] = res[i][j] && (gameTable[xy_to_indx(i, j)] == '.' ||
                        gameTable[xy_to_indx(i, j)] == 'o');
            }
        }
        return res;
    }

    private void dfsO(boolean[][] res, int i, int j) {
        if (res[i][j]) return;
        res[i][j] = true;
        for (int k = 0; k < 8; k++) {
            int I = i + dx[k];
            int J = j + dy[k];
            if (0 <= I && I < 10 && 0 <= J && J < 10) {
                if (gameTable[xy_to_indx(i, j)] == 'o') {
                    if (gameTable[xy_to_indx(I, J)] != 'X') {
                        dfsO(res, I, J);
                    }
                }
                if (gameTable[xy_to_indx(i, j)] == 'O') {
                    if (gameTable[xy_to_indx(I, J)] != 'X' &&
                            gameTable[xy_to_indx(I, J)] != 'o') {
                        dfsO(res, I, J);
                    }
                }
            }
        }
    }
    private boolean[][] calcMovableO() {
        boolean[][] res = new boolean[10][10];
        for (int i = 0; i < 10; i++) {
            for (int j = 0; j < 10; j++) {
                res[i][j] = false;
            }
        }
        for (int i = 0; i < 10; i++) {
            for (int j = 0; j < 10; j++) {
                if (gameTable[xy_to_indx(i, j)] == 'o') {
                    dfsO(res, i, j);
                }
            }
        }
        if (gameTable[xy_to_indx(9, 9)] == '.') {
            res[9][9] = true;
        }
        for (int i = 0; i < 10; i++) {
            for (int j = 0; j < 10; j++) {
                res[i][j] = res[i][j] && (gameTable[xy_to_indx(i, j)] == '.' ||
                        gameTable[xy_to_indx(i, j)] == 'x');
            }
        }
        return res;
    }
    public synchronized void tryMakeMove(ServerWorker serverWorker, int x, int y) throws Exception {
        int playerPort = serverWorker.socket.getPort();
        if (playerPort != playerXid && playerPort != playerOid) {
            return;
        }
        if (gameStatus != GameStatus.ONGOING) {
            return;
        }
        if (turn_player == 'x' && playerPort == playerOid) {
            return;
        }
        if (turn_player == 'o' && playerPort == playerXid) {
            return;
        }
        if (playerPort == playerXid) {
            boolean[][] movable = calcMovableX();
            if (movable[x][y]) {
                if (gameTable[xy_to_indx(x, y)] == '.') {
                    gameTable[xy_to_indx(x, y)] = 'x';
                } else if (gameTable[xy_to_indx(x, y)] == 'o') {
                    gameTable[xy_to_indx(x, y)] = 'X';
                }
            }
            else {
                return;
            }
        }
        if (playerPort == playerOid) {
            boolean[][] movable = calcMovableO();
            if (movable[x][y]) {
                if (gameTable[xy_to_indx(x, y)] == '.') {
                    gameTable[xy_to_indx(x, y)] = 'o';
                }
                else if (gameTable[xy_to_indx(x, y)] == 'x') {
                    gameTable[xy_to_indx(x, y)] = 'O';
                }
            }
            else {
                return;
            }
        }
        moves_cnt--;
        if (moves_cnt == 0) {
            moves_cnt = 3;
            turn_player = (char) ('x' + 'o' - turn_player);
        }
        boolean[][] movable;
        if (turn_player == 'x') {
            movable = calcMovableX();
        }
        else {
            movable = calcMovableO();
        }
        int cnt = 0;
        for (int i = 0; i < 10; i++) {
            for (int j = 0; j < 10; j++) {
                if (movable[i][j]) {
                    cnt++;
                }
            }
        }
        if (cnt == 0) {
            if (turn_player == 'x') {
                gameStatus = GameStatus.OWIN;
            }
            else {
                gameStatus = GameStatus.XWIN;
            }
        }
        sendGameState();
    }

    public synchronized void sendGameState() throws Exception {
        for (ServerWorker o : serverWorkers) {
            o.sendGameState();
        }
    }

    public synchronized void resetGameState() throws Exception {
        if (gameStatus == GameStatus.XWIN || gameStatus == GameStatus.OWIN) {
            reset();
        }
    }
}
