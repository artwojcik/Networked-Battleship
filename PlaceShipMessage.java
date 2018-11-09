//
// Artur Wojcik    - awojci5
// Curt Thieme     - cthiem2
// Sean Martinelli - smarti58
//
// CS 342 Project #4 - Battleship
// 11/16/2017
// UIC, Professor Troy
//
// PlaceShipMessage:
// This allows the client to send information to the
// server about the ship it would like to place on the board.
//

import java.io.Serializable;

public class PlaceShipMessage implements Serializable
{
    private Ship shipToPlace;

    public PlaceShipMessage(Ship shipToPlace)
    {
        this.shipToPlace = shipToPlace;
    }

    public Ship getShipToPlace()
    {
        return shipToPlace;
    }
}
