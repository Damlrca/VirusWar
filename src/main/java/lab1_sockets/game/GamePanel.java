package lab1_sockets.game;

import lab1_sockets.net.ClientWorker;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;

public class GamePanel extends JPanel {
    private static final int RECT_SIZE = 45;
    private final GameState gameState;
    private final JPanel gameTablePanel;
    private final JButton resetButton;
    private final JLabel stateLabel;
    public ClientWorker clientWorker = null;
    public GamePanel(GameState gameState) {
        this.gameState = gameState;

        gameTablePanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                drawCells(g);
            }
        };
        gameTablePanel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                handleClick(e.getX(), e.getY());
            }
        });

        JPanel bottomPanel = new JPanel();
        stateLabel = new JLabel("stateLabel");
        bottomPanel.add(stateLabel);
        resetButton = new JButton("reset");
        bottomPanel.add(resetButton);
        resetButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                handleReset();
            }
        });

        setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
        bottomPanel.setPreferredSize(new Dimension(gameState.TABLE_SIZE * RECT_SIZE, 35));
        add(bottomPanel);
        gameTablePanel.setPreferredSize(new Dimension(gameState.TABLE_SIZE * RECT_SIZE, gameState.TABLE_SIZE * RECT_SIZE));
        add(gameTablePanel);
    }
    public void handleClick(int x, int y) {
        x /= RECT_SIZE;
        y /= RECT_SIZE;
        if (clientWorker != null) {
            try {
                clientWorker.tryMakeMove(x, y);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
    public void handleReset() {
        if (clientWorker != null) {
            try {
                clientWorker.tryResetGameState();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
    public void update() {
        gameTablePanel.repaint();
        resetButton.setEnabled(gameState.gameStatus == GameStatus.XWIN ||
                gameState.gameStatus == GameStatus.OWIN);
        String stateLabelText = "";
        switch (gameState.gameStatus) {
            case WAIT:
                stateLabelText = "Waiting another player";
                break;
            case ONGOING:
                stateLabelText = "Turn of player " + gameState.turn_player + "(" + gameState.moves_cnt + "/3)";
                break;
            case XWIN:
                stateLabelText = "Player X is winner!";
                break;
            case OWIN:
                stateLabelText = "Player O is winner!";
                break;
        }
        stateLabel.setText(stateLabelText);
    }
    public void drawCells(Graphics g) {
        for (int x = 0; x < gameState.TABLE_SIZE; x++) {
            for (int y = 0; y < gameState.TABLE_SIZE; y++) {
                drawCell(g, x, y, gameState.gameTable[gameState.xy_to_indx(x, y)]);
            }
        }
    }
    public void drawCell(Graphics g, int x, int y, char c) {
        ((Graphics2D) g).setStroke(new BasicStroke(3));
        ((Graphics2D) g).setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        if ((x + y) % 2 == 1) {
            g.setColor(Color.GRAY);
        } else {
            g.setColor(Color.LIGHT_GRAY);
        }
        g.fillRect(x * RECT_SIZE, y * RECT_SIZE, RECT_SIZE, RECT_SIZE);
        int x1 = x * RECT_SIZE + 5;
        int x2 = (x + 1) * RECT_SIZE - 5;
        int y1 = y * RECT_SIZE + 5;
        int y2 = (y + 1) * RECT_SIZE - 5;
        switch (c) {
            case 'x': {
                g.setColor(Color.BLUE);
                g.drawLine(x1, y1, x2, y2);
                g.drawLine(x1, y2, x2, y1);
                break;
            }
            case 'X': {
                g.setColor(Color.BLUE);
                g.fillOval(x1, y1, RECT_SIZE - 10, RECT_SIZE - 10);
                g.drawLine(x1, y1, x2, y2);
                g.drawLine(x1, y2, x2, y1);
                break;
            }
            case 'o': {
                g.setColor(Color.RED);
                g.drawOval(x1, y1, RECT_SIZE - 10, RECT_SIZE - 10);
                break;
            }
            case 'O': {
                g.setColor(Color.RED);
                g.drawLine(x1, y1, x2, y2);
                g.drawLine(x1, y2, x2, y1);
                g.fillOval(x1, y1, RECT_SIZE - 10, RECT_SIZE - 10);
                break;
            }
        }
    }
}
