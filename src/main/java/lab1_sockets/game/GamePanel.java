package lab1_sockets.game;

import lab1_sockets.net.Client;
import lab1_sockets.net.ClientWorker;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;

public class GamePanel extends JPanel {

    private static final int CELL_SIZE = 45;
    private final GameState gameState;
    private final JPanel gameArea;
    private final JButton resetButton;
    private final JPanel buttonArea;
    private final JLabel stateLabel;
    public ClientWorker clientWorker = null;
    public GamePanel(GameState gameState) {
        this.gameState = gameState;

        gameArea = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                drawCells(g);
            }
        };
        gameArea.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                handleClick(e.getX(), e.getY());
            }
        });

        buttonArea = new JPanel();
        stateLabel = new JLabel("stateLabel");
        buttonArea.add(stateLabel);
        resetButton = new JButton("reset");
        buttonArea.add(resetButton);
        resetButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                handleReset();
            }
        });

        setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
        buttonArea.setPreferredSize(new Dimension(gameState.SIZE * CELL_SIZE, 35));
        add(buttonArea);
        gameArea.setPreferredSize(new Dimension(gameState.SIZE * CELL_SIZE, gameState.SIZE * CELL_SIZE));
        add(gameArea);
    }
    public void handleClick(int x, int y) {
        x /= CELL_SIZE;
        y /= CELL_SIZE;
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
    public void refresh() {
        gameArea.repaint();
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
        for (int x = 0; x < gameState.SIZE; x++) {
            for (int y = 0; y < gameState.SIZE; y++) {
                drawCell(g, x, y, gameState.gameTable[gameState.xy_to_indx(x, y)]);
            }
        }
    }
    public void drawCell(Graphics g, int x, int y, char c) {
        if ((x + y) % 2 == 1) {
            g.setColor(Color.GRAY);
        } else {
            g.setColor(Color.LIGHT_GRAY);
        }
        g.fillRect(x * CELL_SIZE, y * CELL_SIZE, CELL_SIZE, CELL_SIZE);
        int x1 = x * CELL_SIZE + 5;
        int x2 = (x + 1) * CELL_SIZE - 5;
        int y1 = y * CELL_SIZE + 5;
        int y2 = (y + 1) * CELL_SIZE - 5;
        switch (c) {
            case 'x': {
                g.setColor(Color.BLUE);
                g.drawLine(x1, y1, x2, y2);
                g.drawLine(x1, y2, x2, y1);
                break;
            }
            case 'X': {
                g.setColor(Color.BLUE);
                g.fillOval(x1, y1, CELL_SIZE - 10, CELL_SIZE - 10);
                g.drawLine(x1, y1, x2, y2);
                g.drawLine(x1, y2, x2, y1);
                break;
            }
            case 'o': {
                g.setColor(Color.RED);
                g.drawOval(x1, y1, CELL_SIZE - 10, CELL_SIZE - 10);
                break;
            }
            case 'O': {
                g.setColor(Color.RED);
                g.drawLine(x1, y1, x2, y2);
                g.drawLine(x1, y2, x2, y1);
                g.fillOval(x1, y1, CELL_SIZE - 10, CELL_SIZE - 10);
                break;
            }
        }
    }
}
