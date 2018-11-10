//
// Artur Wojcik    - awojci5
// Curt Thieme     - cthiem2
// Sean Martinelli - smarti58
//
// CS 342 Project #4 - Battleship
// 11/16/2017
// UIC, Professor Troy
//
// Coordinate:
// Used to store row and column info about a location
// on the board.
//

import java.io.Serializable;

public class Coordinate implements Serializable
{
    private int row;
    private int column;

    public Coordinate(int row, int column)
    {
        this.row = row;
        this.column = column;
    }

    public int getRow() {return row;}

    public int getColumn() {return column;}

    //Returns true if the coordinate passed to the method
    //is equal to the this coordinate, false if not
    public boolean equals(Coordinate coordinate)
    {
        int row = coordinate.getRow();
        int col = coordinate.getColumn();

        if(this.row == row && this.column == col)
            return true;
        else
            return false;
    }
}