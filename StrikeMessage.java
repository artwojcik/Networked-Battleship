//
// Artur Wojcik    - awojci5
// Curt Thieme     - cthiem2
// Sean Martinelli - smarti58
//
// CS 342 Project #4 - Battleship
// 11/16/2017
// UIC, Professor Troy
//
// StrikeMessage:
// This message allows the client to specify a position
// that it would like to strike to the server.  It also
// allows the server to send a message back to the client
// to let them know if the strike was a hit or miss.
//

import java.io.Serializable;

public class StrikeMessage implements Serializable
{
    private Coordinate coordinate;
    private boolean wasHit;
    private boolean isStrikeResult;

    public StrikeMessage(Coordinate coordinate, boolean wasHit, boolean isStrikeResult)
    {
        this.coordinate = coordinate;
        this.wasHit = wasHit;
        this.isStrikeResult = isStrikeResult;
    }

    public Coordinate getCoordinate()
    {
        return coordinate;
    }

    //Allow the recipient of the message to check if there was a hit
    public boolean wasHit()
    {
        return wasHit;
    }

    //Allow the recipient of the message to check if this is the results of a hit
    public boolean isStrikeResult()
    {
        return isStrikeResult;
    }
}