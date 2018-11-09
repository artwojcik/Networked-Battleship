//
// Artur Wojcik    - awojci5
// Curt Thieme     - cthiem2
// Sean Martinelli - smarti58
//
// CS 342 Project #4 - Battleship
// 11/16/2017
// UIC, Professor Troy
//
// Ship:
// This stores information about a single ship that
// can be placed or is on the board.
//

import java.io.Serializable;
import java.util.ArrayList;

public class Ship implements Serializable
{
    private String name;
    private ArrayList<Coordinate> shipCoordinates;
    private ArrayList<Coordinate> hitCoordinates;
    private boolean isSunk;

    public Ship(String name, ArrayList<Coordinate> shipCoordinates)
    {
        this.name = name;
        this.isSunk = false;
        this.shipCoordinates = shipCoordinates;
        hitCoordinates = new ArrayList<Coordinate>();
    }

    //Returns the sunk status of the ship
    public boolean isSunk()
    {
        return isSunk;
    }

    //Check if a strike hit or missed the ship.  If the strike was successful
    //true is returned, false if not.
    public boolean strike(Coordinate coordinate)
    {
        //Check if coordinate was already a hit
        for(Coordinate hitCoord : hitCoordinates)
            if(hitCoord.equals(coordinate))
                return false; //duplicate hit

        //If not already a hit, compare against shipCoordinates
        for(Coordinate shipCoord : shipCoordinates)
            if(shipCoord.equals(coordinate))
            {
                hitCoordinates.add(coordinate);

                //Check if the hit sunk the ship
                if(hitCoordinates.size() == shipCoordinates.size())
                    isSunk = true;

                return true;  //hit successful
            }

        return false;// hit unsuccessful
    }

    //Check if the ship contains the specified coordinate
    public boolean containsCoord(Coordinate coordinateToCheck)
    {
        for(Coordinate coord : shipCoordinates)
            if(coord.equals(coordinateToCheck))
                return true;

        return false;
    }

    public String getName()
    {
        return name;
    }

    public ArrayList<Coordinate> getCoords()
    {
        return shipCoordinates;
    }

    //Returns the number of coordinates for the ship that have not been hit
    public int getNumNotHit()
    {
        return shipCoordinates.size() - hitCoordinates.size();
    }
}
