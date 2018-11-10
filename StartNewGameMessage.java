//
// Artur Wojcik    - awojci5
// Curt Thieme     - cthiem2
// Sean Martinelli - smarti58
//
// CS 342 Project #4 - Battleship
// 11/16/2017
// UIC, Professor Troy
//
// StartNewGameMessage:
// This message allows the server to tell the client
// to start a new game.
//

import java.io.Serializable;

public class StartNewGameMessage implements Serializable
{
    private boolean shouldStartNewGame;

    public StartNewGameMessage(boolean shouldStartNewGame)
    {
        this.shouldStartNewGame = shouldStartNewGame;
    }

    //Allow the recipient of the message to check if they should start a new game
    public boolean shouldStartNewGame()
    {
        return shouldStartNewGame;
    }
}
