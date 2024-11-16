package lab1_sockets.game;

import javax.swing.*;

public class GameFrame extends JFrame {;
    public final GamePanel gamePanel;
    public GameFrame(GameState gameState, char player_char) {
        setTitle("VirusWar (Player " + player_char + ")");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        gamePanel = new GamePanel(gameState);
        add(gamePanel);
        pack();
        setLocationByPlatform(true);
        setVisible(true);
    }
    public void update() {
        gamePanel.update();
    }
}
