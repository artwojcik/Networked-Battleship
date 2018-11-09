//
// Artur Wojcik    - awojci5
// Curt Thieme     - cthiem2
// Sean Martinelli - smarti58
//
// CS 342 Project #4 - Battleship
// 11/16/2017
// UIC, Professor Troy
//
// ShipStatusMessage:
// This allows the server to send a message to the client
// to let the client know if their ship placement was
// successful.
//

import java.io.Serializable;

public class ShipStatusMessage implements Serializable
{
    private Ship ship;
    private boolean placementWasSuccessful;

    public ShipStatusMessage(Ship ship, boolean status)
    {
        this.ship = ship;
        placementWasSuccessful = status;
    }

    //Allow the recipient of the message to determine if their ship placement was successful
    public boolean placementWasSuccessful()
    {
        return placementWasSuccessful;
    }

    public Ship getShip()
    {
        return ship;
    }
}
