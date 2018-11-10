//
// Artur Wojcik    - awojci5
// Curt Thieme     - cthiem2
// Sean Martinelli - smarti58
//
// CS 342 Project #4 - Battleship
// 11/16/2017
// UIC, Professor Troy
//
// NewGameRequestMessage:
// This is used to exchange information between the server
// and client about whether or not the user would like to
// play again.
//

import java.io.Serializable;

public class NewGameRequestMessage implements Serializable
{
    private boolean requestingNewGame;

    public NewGameRequestMessage(boolean requestingNewGame)
    {
        this.requestingNewGame = requestingNewGame;
    }

    public boolean isRequestingNewGame()
    {
        return requestingNewGame;
    }
}
