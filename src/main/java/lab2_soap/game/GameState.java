package lab2_soap.game;

import lab2_soap.net.GameStateService;

import javax.jws.WebService;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;

@XmlAccessorType(XmlAccessType.FIELD)
@WebService(endpointInterface = "lab2_soap.net.GameStateService")
public class GameState implements GameStateService {
    public final int TABLE_SIZE = 10;
    public boolean playerXconnected = false, playerOconnected = false;
    public char[] gameTable;
    public GameStatus gameStatus;
    public char turn_player;
    public int moves_cnt;

    public int xy_to_indx(int x, int y) {
        return x * TABLE_SIZE + y;
    }

    public GameState() throws Exception {
        reset();
    }

    private void reset() throws Exception {
        gameTable = new char[TABLE_SIZE * TABLE_SIZE];
        for (int i = 0; i < TABLE_SIZE * TABLE_SIZE; i++) {
            gameTable[i] = '.';
        }
        if (!playerXconnected || !playerOconnected) {
            gameStatus = GameStatus.WAIT;
        }
        else {
            gameStatus = GameStatus.ONGOING;
        }
        turn_player = 'x';
        moves_cnt = 3;
    }

    @Override
    public synchronized char connectPlayer() throws Exception {
        if (playerXconnected && playerOconnected) {
            return ' ';
        }
        if (playerXconnected == false) {
            playerXconnected = true;
            if (playerXconnected && playerOconnected) {
                gameStatus = GameStatus.ONGOING;
            }
            return 'x';
        }
        if (playerOconnected == false) {
            playerOconnected = true;
            if (playerXconnected && playerOconnected) {
                gameStatus = GameStatus.ONGOING;
            }
            return 'o';
        }
        return ' ';
    }
    @Override
    public synchronized void disconnectPlayer(char playerCode) throws Exception {
        if (playerXconnected && playerCode == 'x') {
            playerXconnected = false;
            reset();
        }
        if (playerOconnected && playerCode == 'o') {
            playerOconnected = false;
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
            if (0 <= I && I < TABLE_SIZE && 0 <= J && J < TABLE_SIZE) {
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
        boolean[][] res = new boolean[TABLE_SIZE][TABLE_SIZE];
        for (int i = 0; i < TABLE_SIZE; i++) {
            for (int j = 0; j < TABLE_SIZE; j++) {
                res[i][j] = false;
            }
        }
        for (int i = 0; i < TABLE_SIZE; i++) {
            for (int j = 0; j < TABLE_SIZE; j++) {
                if (gameTable[xy_to_indx(i, j)] == 'x') {
                    dfsX(res, i, j);
                }
            }
        }
        if (gameTable[xy_to_indx(0, 0)] == '.') {
            res[0][0] = true;
        }
        for (int i = 0; i < TABLE_SIZE; i++) {
            for (int j = 0; j < TABLE_SIZE; j++) {
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
            if (0 <= I && I < TABLE_SIZE && 0 <= J && J < TABLE_SIZE) {
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
        boolean[][] res = new boolean[TABLE_SIZE][TABLE_SIZE];
        for (int i = 0; i < TABLE_SIZE; i++) {
            for (int j = 0; j < TABLE_SIZE; j++) {
                res[i][j] = false;
            }
        }
        for (int i = 0; i < TABLE_SIZE; i++) {
            for (int j = 0; j < TABLE_SIZE; j++) {
                if (gameTable[xy_to_indx(i, j)] == 'o') {
                    dfsO(res, i, j);
                }
            }
        }
        if (gameTable[xy_to_indx(TABLE_SIZE - 1, TABLE_SIZE - 1)] == '.') {
            res[TABLE_SIZE - 1][TABLE_SIZE - 1] = true;
        }
        for (int i = 0; i < TABLE_SIZE; i++) {
            for (int j = 0; j < TABLE_SIZE; j++) {
                res[i][j] = res[i][j] && (gameTable[xy_to_indx(i, j)] == '.' ||
                        gameTable[xy_to_indx(i, j)] == 'x');
            }
        }
        return res;
    }
    @Override
    public synchronized void tryMakeMove(char playerCode, int x, int y) throws Exception {
        if (gameStatus != GameStatus.ONGOING || turn_player != playerCode) {
            return;
        }
        if (playerCode == 'x') {
            boolean[][] movable = calcMovableX();
            if (movable[x][y]) {
                if (gameTable[xy_to_indx(x, y)] == '.') {
                    gameTable[xy_to_indx(x, y)] = 'x';
                } else if (gameTable[xy_to_indx(x, y)] == 'o') {
                    gameTable[xy_to_indx(x, y)] = 'X';
                }
            } else {
                return;
            }
        }
        if (playerCode == 'o') {
            boolean[][] movable = calcMovableO();
            if (movable[x][y]) {
                if (gameTable[xy_to_indx(x, y)] == '.') {
                    gameTable[xy_to_indx(x, y)] = 'o';
                } else if (gameTable[xy_to_indx(x, y)] == 'x') {
                    gameTable[xy_to_indx(x, y)] = 'O';
                }
            } else {
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
        for (int i = 0; i < TABLE_SIZE; i++) {
            for (int j = 0; j < TABLE_SIZE; j++) {
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
    }
    @Override
    public synchronized GameState getGameState() throws Exception {
        return this;
    }
    @Override
    public synchronized void resetGameState() throws Exception {
        if (gameStatus == GameStatus.XWIN || gameStatus == GameStatus.OWIN) {
            reset();
        }
    }
}
