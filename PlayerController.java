//
// Artur Wojcik    - awojci5
// Curt Thieme     - cthiem2
// Sean Martinelli - smarti58
//
// CS 342 Project #4 - Battleship
// 11/16/2017
// UIC, Professor Troy
//
// PlayerController:
// This is the base controller class that can be inherited
// by ServerPlayerController or ClientPlayerController
// depending on which roll the application takes.
//

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;

public class PlayerController
{
    private View view;
    private boolean isPlayersTurn;
    private String selectedShipName;
    private int selectedShipSize;
    private boolean isHorizontal;
    private boolean gameHasStarted;
    private boolean gameEnabled;

    public PlayerController(View view)
    {
        this.view = view;
        initializeDataMembers();

        view.addBoatsToGridButtonListener(new PlayerBoatsHandler());
        view.addRotateButtonActionListner(new RotateButtonHandler());
        view.addMouseRollOverListener(new mouseRollOverListener());
    }

    public void setPlayersTurn(boolean value)
    {
        isPlayersTurn = value;
    }

    public boolean isPlayersTurn()
    {
        return isPlayersTurn;
    }

    public View getView()
    {
        return view;
    }

    public boolean gameHasStarted()
    {
        return gameHasStarted;
    }

    //Change game state data members once game has started
    public synchronized void startGame()
    {
        gameHasStarted = true;
        view.startGame();
    }

    //Display the result of the last strike by the opponent in the status bar
    public void DisplayStrikeMessage(Coordinate coordinate, boolean wasHit)
    {
        int col = coordinate.getColumn() + 1;
        char row = (char)(coordinate.getRow() + 65);

        //Display appropriate hit/miss message
        if(wasHit)
            getView().setStatusLabel("Your opponent hit one of your ships at:  " + row + col);
        else
            getView().setStatusLabel("Your opponent missed at:  " + row + col);
    }

    //Returns true if the ship to be placed is horizontal
    public boolean isHorizontal()
    {
        return isHorizontal;
    }

    public String getSelectedShipName()
    {
        return selectedShipName;
    }

    public int getSelectedShipSize()
    {
        return selectedShipSize;
    }

    //Sets the data members to their initial state for a new game
    public void initializeDataMembers()
    {
        selectedShipName = "cruiser";
        selectedShipSize = 2;
        gameHasStarted = false;
        gameEnabled = false;

        isPlayersTurn = true;
        isHorizontal = true;
    }

    //Disable and enable the game
    public synchronized void setGameEnabled(boolean value)
    {
        gameEnabled = value;

        //set ship buttons based on game state
        if(gameEnabled)
            view.selectShip("cruiser");
        else
            view.grayOutShipButtons();
    }

    //Returns true if the game is currently enabled
    public boolean gameEnabled()
    {
        return gameEnabled;
    }

    //Swap the players turn label
    public synchronized void changePlayerTurn()
    {
        //Change label on GUI
        if(view.getMoveLabel().equals("Your Turn"))
            view.setMoveLabel("Opponent's Turn");
        else
            view.setMoveLabel("Your Turn");

        //Change player turn state data member
        isPlayersTurn = !isPlayersTurn;
    }

    //Create and return a ship to be placed on the board
    public Ship buildShip(Coordinate coord)
    {
        int row = coord.getRow();
        int col = coord.getColumn();
        ArrayList<Coordinate> shipCoords = new ArrayList<Coordinate>();

        if(isHorizontal())
        {
            for(int i = 0; i < getSelectedShipSize(); i++)
            {
                shipCoords.add(new Coordinate(row, col+i));
            }
        }
        else
        {
            for(int i = 0; i < getSelectedShipSize(); i++)
            {
                shipCoords.add(new Coordinate(row+i, col));
            }
        }

        Ship retShip = new Ship(getSelectedShipName(), shipCoords);
        return retShip;
    }

    //Allow the user to select the ship to place on the board
    private class PlayerBoatsHandler implements ActionListener
    {
        @Override
        public void actionPerformed(ActionEvent e) {
            if(gameEnabled && !gameHasStarted) {
                ShipButton shipButton = (ShipButton) e.getSource();
                selectedShipName = shipButton.getBoatName();
                selectedShipSize = shipButton.getShipSize();
                view.selectShip(shipButton.getBoatName());
            }
        }
    }

    //Allow the user to rotate the ship to place on the board
    private class RotateButtonHandler implements ActionListener
    {
        @Override
        public void actionPerformed(ActionEvent e) {
            if(gameEnabled && !gameHasStarted) {
                isHorizontal = !isHorizontal;
                view.switchRotationLabel();
            }
        }
    }

    //Detect mouse roll over to highlight player grid pieces where ship will go
    private class mouseRollOverListener implements MouseListener
    {
        @Override
        public void mouseClicked(MouseEvent e) {}

        @Override
        public void mousePressed(MouseEvent e) {}

        @Override
        public void mouseReleased(MouseEvent e) {}

        @Override
        public void mouseEntered(MouseEvent e)
        {
            //place boarder around where the ship will go on the board
            if(gameEnabled && !gameHasStarted) {
                GridButton button = (GridButton) e.getSource();
                Ship ship = buildShip(button.getCoordinate());
                view.applyBorderToShipCoords(ship);
            }
        }

        @Override
        public void mouseExited(MouseEvent e)
        {
            //remove the boarder from the grid buttons
            if(gameEnabled) {
                GridButton button = (GridButton) e.getSource();
                Ship ship = buildShip(button.getCoordinate());
                view.removeBorderFromShipCoords(ship);
            }
        }
    }
}
