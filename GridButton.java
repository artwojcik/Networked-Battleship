//
// Artur Wojcik    - awojci5
// Curt Thieme     - cthiem2
// Sean Martinelli - smarti58
//
// CS 342 Project #4 - Battleship
// 11/16/2017
// UIC, Professor Troy
//
// GridButton:
// An extension of JButton that contains information about
// the buttons position on the board and whether or not it
// has been hit.
//

import javax.swing.*;

public class GridButton extends JButton
{
    private Coordinate coordinate;
    private String imageFileName;
    private boolean hasBeenStruck;

    public GridButton(Coordinate coordinate, String imageFileName)
    {
        this.coordinate = coordinate;
        this.imageFileName = imageFileName;
        hasBeenStruck = false;
    }

    public void setImageFileName(String name)
    {
        imageFileName = name;
    }

    //Returns the part of the file name after the "/" char
    public String getImageFileName()
    {
        String[] stringParts = imageFileName.split("/");
        return stringParts[1];
    }

    public Coordinate getCoordinate()
    {
        return coordinate;
    }

    public void setHasBeenStruck(boolean value)
    {
        hasBeenStruck = value;
    }

    public boolean hasBeenStruck()
    {
        return hasBeenStruck;
    }
}