package lab2_soap.net;

import lab2_soap.game.GameFrame;

import javax.swing.*;
import javax.xml.namespace.QName;
import javax.xml.ws.Service;
import java.net.URL;

public class Client {
    public static final int port = 8080;
    public static final String name = "GameState";

    GameStateService server;

    public Client() throws Exception {
        URL url = new URL(String.format("http://localhost:%d/%s&wsdl", port, name));
        QName qname = new QName("http://game.lab2_soap/", "GameStateService");
        Service service = Service.create(url, qname);
        server = service.getPort(GameStateService.class);

        char playerCode = server.connectPlayer();
        if (playerCode == ' ') {
            System.out.println("Two players already connected");
            return;
        }

        GameFrame myFrame = new GameFrame(server, playerCode);

        Thread updateThread = new Thread(() -> {
            while (!Thread.interrupted()) {
                SwingUtilities.invokeLater(() -> myFrame.update());
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        });
        updateThread.setDaemon(true);
        updateThread.start();
    }

    public static void main(String[] args) throws Exception {
        Client client = new Client();
    }
}
