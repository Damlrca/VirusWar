package lab1_sockets.game;

import javax.swing.*;

public class GameFrame extends JFrame {
    private final GameState gameState;
    public final GamePanel gamePanel;
    private final char player_char;
    public GameFrame(GameState gameState, char player_char) {
        setTitle("VirusWar (Player " + player_char + ")");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        this.gameState = gameState;
        this.player_char = player_char;
        gamePanel = new GamePanel(gameState);
        add(gamePanel);
        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }
    public void refresh() {
        gamePanel.refresh();
    }
}
