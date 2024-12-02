package lab2_soap.game;

import lab2_soap.net.GameStateService;

import javax.swing.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class GameFrame extends JFrame {;
    public final GamePanel gamePanel;
    public GameFrame(GameStateService service, char player_char) throws Exception {
        setTitle("VirusWar (Player " + player_char + ")");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                try {
                    service.disconnectPlayer(player_char);
                } catch (Exception ex) {
                    throw new RuntimeException(ex);
                }
            }
        });
        gamePanel = new GamePanel(service, player_char);
        add(gamePanel);
        pack();
        setLocationByPlatform(true);
        setVisible(true);
    }
    public void update() {
        gamePanel.update();
    }
}
