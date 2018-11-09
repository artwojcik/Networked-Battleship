//
// Artur Wojcik    - awojci5
// Curt Thieme     - cthiem2
// Sean Martinelli - smarti58
//
// CS 342 Project #4 - Battleship
// 11/16/2017
// UIC, Professor Troy
//
// ShipButton:
// This button allows stores information about a ship that
// the user can select to place on the board.
//

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.io.IOException;

public class ShipButton extends JButton{

    private String name;
    private int shipSize;
    private String imageFileName;
    private int imageWidth;
    private int imageHeight;

    public ShipButton(String name, int size, String imageFileName, int imageWidth, int imageHeight){
        this.imageFileName = imageFileName;
        this.imageWidth = imageWidth;
        this.imageHeight = imageHeight;
        this.name = name;
        this.shipSize = size;

        deselect(); //ship should start deselected
    }

    public String getBoatName(){
        return name;
    }

    public int getShipSize(){
        return shipSize;
    }

    //Change the button image to the normal deselected version of the ship
    public void deselect()
    {
        try {
            Image image = ImageIO.read(getClass().getResource("Icons/" + imageFileName));
            Image scaledImage = image.getScaledInstance(imageWidth, imageHeight, Image.SCALE_DEFAULT);
            super.setIcon(new ImageIcon(scaledImage));
        } catch (IOException e) {
            System.err.println("Could not find image: Icons/" + imageFileName);
        }
    }

    //Change the button image to the selected version of the ship
    public void select()
    {
        try {
            Image image = ImageIO.read(getClass().getResource("Icons/selected_" + imageFileName));
            Image scaledImage = image.getScaledInstance(imageWidth, imageHeight, Image.SCALE_DEFAULT);
            super.setIcon(new ImageIcon(scaledImage));
        } catch (IOException e) {
            System.err.println("Could not find image: Icons/selected_" + imageFileName);
        }
    }

    //Change the button image to the grayed out version of the ship
    public void grayOut()
    {
        try {
            Image image = ImageIO.read(getClass().getResource("Icons/gray_" + imageFileName));
            Image scaledImage = image.getScaledInstance(imageWidth, imageHeight, Image.SCALE_DEFAULT);
            super.setIcon(new ImageIcon(scaledImage));
        } catch (IOException e) {
            System.err.println("Could not find image: Icons/selected_" + imageFileName);
        }
    }
}
