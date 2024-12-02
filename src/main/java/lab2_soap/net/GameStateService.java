package lab2_soap.net;

import lab2_soap.game.GameState;

import javax.jws.WebMethod;
import javax.jws.WebService;

@WebService
public interface GameStateService {
    @WebMethod
    public char connectPlayer() throws Exception;
    @WebMethod
    public void disconnectPlayer(char playerCode) throws Exception;
    @WebMethod
    public void tryMakeMove(char playerCode, int x, int y) throws Exception;
    @WebMethod
    public GameState getGameState() throws Exception;
    @WebMethod
    public void resetGameState() throws Exception;
}
