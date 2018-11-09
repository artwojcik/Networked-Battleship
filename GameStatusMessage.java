//
// Artur Wojcik    - awojci5
// Curt Thieme     - cthiem2
// Sean Martinelli - smarti58
//
// CS 342 Project #4 - Battleship
// 11/16/2017
// UIC, Professor Troy
//
// GameStatusMessage:
// Used to send information about the status of the game
// from the server to the client.
//

import java.io.Serializable;

public class GameStatusMessage implements Serializable
{
    private boolean gameHasStarted;
    private boolean gameOver;
    private boolean serverIsWinner;

    public GameStatusMessage(boolean gameOver, boolean gameHasStarted, boolean serverIsWinner)
    {
        this.gameHasStarted = gameHasStarted;
        this.gameOver = gameOver;
        this.serverIsWinner = serverIsWinner;
    }

    //Allows the recipient to check if the game has started
    public boolean gameHasStarted()
    {
        return gameHasStarted;
    }

    //Allows the recipient to check if the game is over
    public boolean gameOver()
    {
        return gameOver;
    }

    //Allows the recipient to check who has won
    public boolean serverIsWinner()
    {
        return serverIsWinner;
    }
}
