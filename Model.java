//
// Artur Wojcik    - awojci5
// Curt Thieme     - cthiem2
// Sean Martinelli - smarti58
//
// CS 342 Project #4 - Battleship
// 11/16/2017
// UIC, Professor Troy
//
// Model:
// This is used to keep track of data related to each
// player's current ships on the board.
//

import java.util.ArrayList;

public class Model
{
    private ArrayList<Ship> clientShips;
    private ArrayList<Ship> serverShips;
    private int clientNumShotsMissed;
    private int serverNumShotsMissed;
    private int clientNumShotsHit;
    private int serverNumShotsHit;

    public Model()
    {
        clientShips = new ArrayList<Ship>();
        serverShips = new ArrayList<Ship>();

        clientNumShotsMissed = 0;
        clientNumShotsMissed = 0;
    }

    //Strike a player's ship. Return true if strike was successful, false if not.
    private boolean strike(Coordinate coordinate, ArrayList<Ship> player)
    {
        //Loop through ships to see if strike was successful
        for(Ship ship : player)
            if(ship.strike(coordinate))
                return true;

        return false;
    }

    //Strike a client ship. Return true if strike was successful, false if not.
    public boolean clientStrike(Coordinate coordinate)
    {
        if(strike(coordinate, clientShips)) {
            ++serverNumShotsHit;
            return true;
        } else {
            ++serverNumShotsMissed;
            return false;
        }
    }

    //Strike a server ship. Return true if strike was successful, false if not.
    public boolean serverStrike(Coordinate coordinate)
    {
        if(strike(coordinate, serverShips)) {
            ++clientNumShotsHit;
            return true;
        } else {
            ++clientNumShotsMissed;
            return false;
        }
    }

    //Determine if a set of coordinate are valid.  Return true if they are, false if not.
    private boolean coordsAreValid(ArrayList<Coordinate> coordinates)
    {
        //Check each coordinate
        for(Coordinate coord : coordinates)
        {
            //get coordinate row and column
            int row = coord.getRow();
            int col = coord.getColumn();

            //check row
            if(row < 0 || row > 9)
                return false;

            //check column
            if(col < 0 || col > 9)
                return false;
        }

        return true;
    }

    //Add a player's ship to their board.  Return true if add was successful, false if not.
    private boolean addShip(Ship shipToAdd, ArrayList<Ship> player)
    {
        ArrayList<Coordinate> shipToAddCoords = shipToAdd.getCoords();

        //Check for valid coordinates
        if(!coordsAreValid(shipToAddCoords))
            return false;

        //Check for duplicate coordinates
        for(Coordinate coord : shipToAddCoords) {
            for(Ship ship : player) {
                if(ship.containsCoord(coord))
                    return false;
            }
        }

        player.add(shipToAdd);

        return true;
    }

    //Add a client ship to their board.  Return true if add was successful, false if not.
    public boolean addClientShip(Ship shipToAdd)
    {
        //check for duplicate ship
        for(Ship ship : clientShips)
            if(ship.getName().equals(shipToAdd.getName()))
                return false;

        return addShip(shipToAdd, clientShips);
    }

    //Add a server ship to their board.  Return true if add was successful, false if not.
    public boolean addServerShip(Ship shipToAdd)
    {
        //check for duplicate ship
        for(Ship ship : serverShips)
            if(ship.getName().equals(shipToAdd.getName()))
                return false;

        return addShip(shipToAdd, serverShips);
    }

    //Check if a player has won. Return true if they have, false if not.
    private boolean checkForWin(ArrayList<Ship> player)
    {
        //Check if the player has any ships that are not sunk
        for(Ship ship : player)
            if(!ship.isSunk())
                return false;

        return true;
    }

    //Check if the server has won. Return true if they have, false if not.
    public boolean checkServerForWin()
    {
        return checkForWin(clientShips);
    }

    //Check if the client has won. Return true if they have, false if not.
    public boolean checkClientForWin()
    {
        return checkForWin(serverShips);
    }

    //Check if the server has five ships on the board. Return true if they do, false if not.
    public boolean serverIsReady()
    {
        return (serverShips.size() == 5);
    }

    //Check if the client has five ships on the board. Return true if they do, false if not.
    public boolean clientIsReady()
    {
        return (clientShips.size() == 5);
    }

    //Returns the number of successful hits the client has
    public int getClientNumShotsHit()
    {
        return clientNumShotsHit;
    }

    //Returns the number of successful hits the server has
    public int getServerNumShotsHit()
    {
        return serverNumShotsHit;
    }

    //Returns the number of unsuccessful shots the client has made
    public int getClientNumShotsMissed()
    {
        return clientNumShotsMissed;
    }

    //Returns the number of unsuccessful shots the server has made
    public int getServerNumShotsMissed()
    {
        return serverNumShotsMissed;
    }

    //Returns the percentage of hits the server has
    public float getServerPercentHit()
    {
        int totalShotsFired = serverNumShotsHit + serverNumShotsMissed;

        if(totalShotsFired != 0)
            return (serverNumShotsHit / (float)totalShotsFired) * 100;
        else
            return (float)0;
    }

    //Returns the percentage of hits the client has
    public float getClientPercentHit()
    {
        int totalShotsFired = clientNumShotsHit + clientNumShotsMissed;

        if(totalShotsFired != 0)
            return (clientNumShotsHit / (float)totalShotsFired) * 100;
        else
            return (float)0;
    }

    public int getServerTotalShotsFired()
    {
        return serverNumShotsHit + serverNumShotsMissed;
    }

    public int getClientTotalShotsFired()
    {
        return clientNumShotsHit + clientNumShotsMissed;
    }

    //Returns the number of hits the server needs to win the game
    public int getServerNumHitsForWin()
    {
        int hitsForWin = 0;

        for(Ship ship : clientShips)
            hitsForWin += ship.getNumNotHit();

        return  hitsForWin;
    }

    //Returns the number of hits the client needs to win the game
    public int getClientNumHitsForWin()
    {
        int hitsForWin = 0;

        for(Ship ship : serverShips)
            hitsForWin += ship.getNumNotHit();

        return  hitsForWin;
    }

}
