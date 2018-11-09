//
// Artur Wojcik    - awojci5
// Curt Thieme     - cthiem2
// Sean Martinelli - smarti58
//
// CS 342 Project #4 - Battleship
// 11/16/2017
// UIC, Professor Troy
//
// View:
// This class is responsible for displaying all information
// to the user and receiving input information from the user.
//

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.BevelBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseListener;
import java.io.IOException;
import java.util.ArrayList;

public class View extends JFrame
{
    private JFrame frame;
    private JPanel playerGrid;
    private JPanel opponentGrid;
    private GridButton[][] playerGridButtons;
    private GridButton[][] opponentGridButtons;
    private JMenu fileMenu;
    private JMenu helpMenu;
    private JMenuItem startServerItem, stopServerItem;
    private JMenuItem connectToServerItem, disconnectFromServerItem;
    private JLabel statusLabel, moveLabel, rotationLabel;
    private ShipButton[] shipButtons;
    private JButton rotateButton;
    private String[][] horizontalShipImages;
    private String[][] verticalShipImages;
    private Timer animationTimer;

    public View()
    {
        playerGridButtons = new GridButton[10][10];
        opponentGridButtons = new GridButton[10][10];
        shipButtons = new ShipButton[5];
        initializeImagesArray();

        //Create new JFrame
        frame = new JFrame("Battleship");
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());
        frame.setSize(800, 400);
        frame.setLocationRelativeTo(null);
        frame.setResizable(false);
        frame.getContentPane().setBackground(new Color(0,0,200));

        //Set up components that will be placed in frame
        SetCrossPlatformLookAndFeel();
        setUpTopInfoPanel();
        setUpCenterPanel();
        setUpPlayerGrid();
        setUpOpponentGrid();
        setUpGridButtons(playerGridButtons, playerGrid);
        setUpGridButtons(opponentGridButtons, opponentGrid);
        setUpMenu();
        SetUpStatusBar();

        grayOutShipButtons();

        frame.setVisible(true);
    }

    //Add an action listener for the buttons that handle connection
    public void addConnectionButtonListener(ActionListener listener)
    {
        //Add action listener to each file menu item
        for(int i=0; i<fileMenu.getItemCount(); ++i)
            fileMenu.getItem(i).addActionListener(listener);
    }

    //Add action listener for help menu
    public void addHelpMenuListeners(ActionListener listener){
        for (int i=0; i < helpMenu.getItemCount(); i++ ){
            helpMenu.getItem(i).addActionListener(listener);
        }
    }

    //add action listener for statistics menu item
    public void addStatisticsListener(ActionListener listener){
        helpMenu.getItem(2).addActionListener(listener);
    }

    //Add an action listener for each opponent grid button
    public void addOpponentGridButtonListener(ActionListener listener)
    {
        for(int i = 0; i < opponentGridButtons.length; ++i)
            for(int j = 0; j < opponentGridButtons[i].length; ++j)
                opponentGridButtons[i][j].addActionListener(listener);
    }

    //Add an action listener for each player grid button
    public void addPlayerGridButtonListener(ActionListener listener)
    {
        for(int i = 0; i < playerGridButtons.length; ++i)
            for(int j = 0; j < playerGridButtons[i].length; ++j)
                playerGridButtons[i][j].addActionListener(listener);
    }

    //Add an action listener for the add ship buttons in the center of the window
    public void addBoatsToGridButtonListener(ActionListener listener)
    {
        for(ShipButton but: shipButtons)
            but.addActionListener(listener);
    }

    //Add an action listener for the rotate button
    public void addRotateButtonActionListner(ActionListener listener){
        rotateButton.addActionListener(listener);
    }

    //Add a mouse rollover listener to each player grid button
    public void addMouseRollOverListener(MouseListener listener)
    {
        for(int i = 0; i < playerGridButtons.length; ++i)
            for(int j = 0; j < playerGridButtons[i].length; ++j)
                playerGridButtons[i][j].addMouseListener(listener);

    }

    public String PromptForServerIP()
    {
        return JOptionPane.showInputDialog(frame,"Enter the IP Address of the server");
    }

    public String PromptForServerPort()
    {
        return JOptionPane.showInputDialog(frame,"Enter the port number");
    }

    //Allow the status label to be changed to a custom message
    public synchronized void setStatusLabel(String message)
    {
        statusLabel.setText(message);
    }

    //Allow the move label to be set and make it the appropriate color.
    public void setMoveLabel(String message)
    {
        moveLabel.setText(message);

        //Set correct color
        if(moveLabel.getText().equals("Your Turn"))
            moveLabel.setForeground(Color.green);
        else
            moveLabel.setForeground(Color.red);
    }

    public String getMoveLabel()
    {
        return moveLabel.getText();
    }

    //Add a ship in the correct orientation to the player's board
    public void AddShip(Ship shipToAdd, boolean isHorizontal)
    {
        String[] images;
        ArrayList<Coordinate> shipCoords;

        //Select correct image array based on orientation
        if(isHorizontal) {
            if (shipToAdd.getName().equals("cruiser"))
                images = horizontalShipImages[0];
            else if (shipToAdd.getName().equals("destroyer"))
                images = horizontalShipImages[1];
            else if (shipToAdd.getName().equals("submarine"))
                images = horizontalShipImages[2];
            else if (shipToAdd.getName().equals("battleship"))
                images = horizontalShipImages[3];
            else
                images = horizontalShipImages[4];
        } else {
            if (shipToAdd.getName().equals("cruiser"))
                images = verticalShipImages[0];
            else if (shipToAdd.getName().equals("destroyer"))
                images = verticalShipImages[1];
            else if (shipToAdd.getName().equals("submarine"))
                images = verticalShipImages[2];
            else if (shipToAdd.getName().equals("battleship"))
                images = verticalShipImages[3];
            else
                images = verticalShipImages[4];
        }

        shipCoords = shipToAdd.getCoords();

        //Set the image for each coordinate of the ship
        for(int i = 0; i < shipCoords.size(); ++i) {
            int row = shipCoords.get(i).getRow();
            int col = shipCoords.get(i).getColumn();

            try { //Add image to piece
                Image img = ImageIO.read(getClass().getResource(images[i]));
                Image scaledImg = img.getScaledInstance(27,27,Image.SCALE_DEFAULT);
                playerGridButtons[row][col].setIcon(new ImageIcon(scaledImg));
                playerGridButtons[row][col].setImageFileName(images[i]);
            } catch (Exception ex) {
                System.err.println(ex + "Cannot find: Icons/" + images[i]);
            }
        }
    }

    //Update appropriate components when the game starts.
    public void startGame()
    {
        moveLabel.setVisible(true);
        startGameAnimation();
        grayOutShipButtons();
        statusLabel.setText("The game has started!");
    }

    //Update the opponent's board based on the coordinates of the strike
    public void opponentStrike(Coordinate coordinate, boolean hit)
    {
        String newImageName;

        //get row and column of strike
        int row = coordinate.getRow();
        int column = coordinate.getColumn();

        opponentGridButtons[row][column].setHasBeenStruck(true);

        //Determine if the strike was a hit or a miss
        if(hit)
            newImageName = "Icons/batt103.gif";
        else
            newImageName = "Icons/batt102.gif";

        //Add image to piece
        try {
            Image img = ImageIO.read(getClass().getResource(newImageName));
            Image scaledImg = img.getScaledInstance(27,27,Image.SCALE_DEFAULT);
            opponentGridButtons[row][column].setIcon(new ImageIcon(scaledImg));
        } catch (Exception ex) {
            System.err.println(ex + "Cannot find: " + newImageName);
        }
    }

    //Update the player's board based on the coordinates of the strike
    public void playerStrike(Coordinate coordinate, boolean hit)
    {
        //get row and column of strike
        int row = coordinate.getRow();
        int column = coordinate.getColumn();

        String imageFileName = playerGridButtons[row][column].getImageFileName();
        String newImageName;

        //Determine if the strike was a hit or a miss
        if(hit)
            newImageName = "hit_" + imageFileName;
        else
            newImageName = "batt102.gif";

        //Add image to piece
        try {
            Image img = ImageIO.read(getClass().getResource("Icons/" + newImageName));
            Image scaledImg = img.getScaledInstance(27,27,Image.SCALE_DEFAULT);
            playerGridButtons[row][column].setIcon(new ImageIcon(scaledImg));
        } catch (Exception ex) {
            System.err.println(ex + "Cannot find: Icons/" + newImageName);
        }
    }

    public void DisplayMessage(String message, String title)
    {
        JOptionPane.showMessageDialog(frame, message, title, JOptionPane.PLAIN_MESSAGE);
    }

    //Ask the user if they would like to play another game
    public boolean promptUserForNewGame()
    {
        //Get response from user
        int userResponse = JOptionPane.showConfirmDialog(frame,
                "Do you want to play another game?",
                "New Game", JOptionPane.YES_NO_OPTION);

        return userResponse == JOptionPane.YES_OPTION;
    }

    //Reset the GUI to get it ready for another game
    public void reset()
    {
        moveLabel.setVisible(false);
        rotationLabel.setText("Placing ships: Horizontally");
        grayOutShipButtons();

        //Reset player and opponent grid buttons
        for(int i = 0; i < 10; ++i) {
            for(int j = 0; j < 10; ++j) {
                try { //Add image to piece
                    Image img = ImageIO.read(getClass().getResource("Icons/batt100.gif"));
                    Image scaledImg = img.getScaledInstance(27,27,Image.SCALE_DEFAULT);
                    playerGridButtons[i][j].setIcon(new ImageIcon(scaledImg));
                    playerGridButtons[i][j].setHasBeenStruck(false);
                    playerGridButtons[i][j].setBorderPainted(false);
                    opponentGridButtons[i][j].setIcon(new ImageIcon(scaledImg));
                    opponentGridButtons[i][j].setHasBeenStruck(false);
                } catch (Exception ex) {
                    System.err.println(ex + "Cannot find: Icons/batt100.gif");
                }
            }
        }
    }

    //Highlight the ship specified by name
    public void selectShip(String name)
    {
        //Find the shipButton that has the name specified and highlight it
        for(ShipButton shipButton : shipButtons)
        {
            if(shipButton.getBoatName().equals(name))
                shipButton.select();
            else
                shipButton.deselect();
        }
    }

    //switch the orientation specified by the rotationLabel
    public void switchRotationLabel()
    {
        if(rotationLabel.getText().equals("Placing ships: Horizontally"))
            rotationLabel.setText("Placing ships: Vertically");
        else
            rotationLabel.setText("Placing ships: Horizontally");
    }

    //Apply a boarder to each player grid button at the
    //coordinates specified by the ship.
    public void applyBorderToShipCoords(Ship ship)
    {
        ArrayList<Coordinate> shipCoords = ship.getCoords();

        for(Coordinate coord : shipCoords)
        {
            int row = coord.getRow();
            int col = coord.getColumn();

            //Make sure coordinate is valid
            if(row >= 0 && row <= 9 && col >= 0 && col <= 9){
                playerGridButtons[row][col].setBorder(BorderFactory.createLineBorder(Color.red, 2));
                playerGridButtons[row][col].setBorderPainted(true);
            }
        }
    }

    //Remove a boarder to each player grid button at the
    //coordinates specified by the ship.
    public void removeBorderFromShipCoords(Ship ship)
    {
        ArrayList<Coordinate> shipCoords = ship.getCoords();

        for(Coordinate coord : shipCoords) {
            int row = coord.getRow();
            int col = coord.getColumn();

            //Make sure the coordinate is valid.
            if(row >= 0 && row <= 9 && col >= 0 && col <= 9) {
                playerGridButtons[row][col].setBorderPainted(false);
            }
        }
    }

    //Change the images for the ship buttons to their gray versions
    public void grayOutShipButtons()
    {
        for(ShipButton button : shipButtons)
            button.grayOut();
    }

    //Disable/enable certain menu items once the server is connected to
    public void connectedToServer(boolean status)
    {
        connectToServerItem.setVisible(!status);
        disconnectFromServerItem.setVisible(status);
        startServerItem.setEnabled(!status);
    }

    //Disable/enable certain menu items once the server is started
    public void serverStarted(boolean status)
    {
        startServerItem.setVisible(!status);
        stopServerItem.setVisible(status);
        connectToServerItem.setEnabled(!status);
    }

    public void displayAboutMessage()
    {
        JOptionPane.showMessageDialog(frame,
                "BattleShip Project 4 for CS 342\n" +
                "Date: 11/16/2017\n"+
                "Authors:\n" +
                "Sean Martinelli - smarti58\n" +
                "Curt Thieme     - cthiem2\n"+
                "Artur Wojcik    - awojci5", "About", JOptionPane.PLAIN_MESSAGE);
    }

    public void displayHelpMessage()
    {
        JOptionPane.showMessageDialog(frame,
                "Battleship is a game of strategy; your goal is to place the\n"+
                        "five ships on the board in such a way that the other player doesnt\n"+
                        "know where you placed the ships. Once all the ships are on the board\n"+
                        "you each take turn trying to hit the other player by clicking on the\n"+
                        "one of the buttons on your attack side of the board. Your side of the board is\n"+
                        "one the left where your ships are. the attack side is on the right, thats the \n"+
                        "side you click on. If the spot turns red, you hit a ship! Keep going until all\n"+
                        "ships are destroyed. There are 5 ships total as seen in center of board of sizes\n"+
                        "2,3,3,4,5 on grid space.", "How To Play", JOptionPane.PLAIN_MESSAGE);
    }

    public void displayConnectionHelpMessage()
    {
            JOptionPane.showMessageDialog(frame,
                    "To connect to another server, go to File -> Connect To Server\n"+
                            "You will be prompted for the servers IP adress, you you have to get\n"+
                            "that from the other player if you are the client. Enter the IP adress;\n"+
                            "after that you will be prompted for the port number. If connection was\n"+
                            "successful, it will show on the bottom of the screen.", "How to Connect",
                            JOptionPane.PLAIN_MESSAGE);
    }

    //Set up the components that make up the top info panel
    private void setUpTopInfoPanel()
    {
        //Create a font to be used for the labels
        Font labelFont = new Font("Arial", Font.BOLD, 18);

        //Create top panel
        JPanel topInfoPanel = new JPanel();
        topInfoPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
        topInfoPanel.setPreferredSize(new Dimension(800,30));
        topInfoPanel.setOpaque(false);

        //Create "Your Board" label
        JPanel topInfoPaneLeft = new JPanel();
        topInfoPaneLeft.setLayout(new FlowLayout(FlowLayout.CENTER));
        topInfoPaneLeft.setPreferredSize(new Dimension(220,30));
        JLabel opponentLabel = new JLabel("Your Board");
        opponentLabel.setForeground(Color.white);
        opponentLabel.setFont(labelFont);
        topInfoPaneLeft.add(opponentLabel);
        topInfoPaneLeft.setOpaque(false);
        topInfoPanel.add(topInfoPaneLeft);

        //Create label that will show who's turn it is
        JPanel topInfoPanelCenter = new JPanel();
        topInfoPanelCenter.setLayout(new FlowLayout(FlowLayout.CENTER));
        topInfoPanelCenter.setPreferredSize(new Dimension(230,30));
        moveLabel = new JLabel("");
        moveLabel.setForeground(Color.white);
        moveLabel.setFont(labelFont);
        moveLabel.setVerticalAlignment(SwingConstants.TOP);
        moveLabel.setVisible(false);
        topInfoPanelCenter.add(moveLabel);
        topInfoPanelCenter.setOpaque(false);
        topInfoPanel.add(topInfoPanelCenter);

        //Create "Opponent's Board" label
        JPanel topInfoPanelRight = new JPanel();
        topInfoPanelRight.setLayout(new FlowLayout(FlowLayout.CENTER));
        topInfoPanelRight.setPreferredSize(new Dimension(250,30));
        JLabel playerLabel = new JLabel("Opponent's Board");
        playerLabel.setForeground(Color.white);
        playerLabel.setFont(labelFont);
        topInfoPanelRight.add(playerLabel);
        topInfoPanelRight.setOpaque(false);
        topInfoPanel.add(topInfoPanelRight);

        frame.add(topInfoPanel, BorderLayout.NORTH);
    }

    //Set up the components that make up the center panel
    private void setUpCenterPanel()
    {
        //Create center panel
        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new GridLayout(8,1));
        centerPanel.setPreferredSize(new Dimension(200,300));
        centerPanel.setOpaque(false);

        //Create ship buttons
        shipButtons[0] = new ShipButton("cruiser", 2, "ship1.png", 54, 27);
        shipButtons[1] = new ShipButton("destroyer", 3, "ship2.png", 81, 27);
        shipButtons[2] = new ShipButton("submarine", 3, "ship3.png", 81, 27);
        shipButtons[3] = new ShipButton("battleship", 4, "ship4.png", 108, 27);
        shipButtons[4] = new ShipButton("aircraft", 5, "ship5.png", 134, 27);

        //Set button appearance
        for(ShipButton button : shipButtons) {
            button.setOpaque(false);
            button.setContentAreaFilled(false);
            button.setBorderPainted(false);
            button.setFocusPainted(false);
        }

        setUpRotateButton();

        //Create rotation label
        rotationLabel = new JLabel("Placing ships: Horizontally");
        rotationLabel.setForeground(Color.white);
        rotationLabel.setHorizontalAlignment(SwingConstants.CENTER);
        rotationLabel.setVerticalAlignment(SwingConstants.CENTER);

        //Add buttons
        centerPanel.add(new JLabel(" "));
        for(ShipButton but: shipButtons)
        {
            centerPanel.add(but);
        }
        centerPanel.add(rotateButton);
        centerPanel.add(rotationLabel);

        frame.add(centerPanel, BorderLayout.CENTER);
    }

    //Create player grid to hold player grid buttons
    private void setUpPlayerGrid()
    {
        //Create main grid
        playerGrid = new JPanel();
        playerGrid.setLayout(new GridLayout(11,11,1, 1));
        playerGrid.setPreferredSize(new Dimension(300,300));
        playerGrid.setOpaque(false);

        frame.add(playerGrid, BorderLayout.WEST);
    }

    //Create opponent grid to hold opponent grid buttons
    private void setUpOpponentGrid()
    {
        //Create main grid
        opponentGrid = new JPanel();
        opponentGrid.setLayout(new GridLayout(11,11,1, 1));
        opponentGrid.setPreferredSize(new Dimension(300,300));
        opponentGrid.setOpaque(false);

        frame.add(opponentGrid, BorderLayout.EAST);
    }

    //Set up the buttons that make up the player and opponent grid
    private void setUpGridButtons(GridButton[][] gridButtons, JPanel grid)
    {
        if(gridButtons == null)
            return;

        createGridButtons(gridButtons);
        addButtonsToGrid(gridButtons, grid);
    }

    //Create the buttons that make up the player and opponent grid
    private void createGridButtons(GridButton[][] gridButtons)
    {
        //Create buttons
        for(int i = 0; i < gridButtons.length; ++i) {
            for(int j = 0; j < gridButtons[i].length; ++j)
            {
                gridButtons[i][j] = new GridButton(new Coordinate(i, j), "Icons/batt100.gif");
                gridButtons[i][j].setFocusPainted(false);
                gridButtons[i][j].setBorder(BorderFactory.createEmptyBorder());
                gridButtons[i][j].setContentAreaFilled(false);

                try { //Add image to piece
                    Image img = ImageIO.read(getClass().getResource("Icons/batt100.gif"));
                    Image scaledImg = img.getScaledInstance(27,27,Image.SCALE_DEFAULT);
                    gridButtons[i][j].setIcon(new ImageIcon(scaledImg));
                } catch (Exception ex) {
                    System.err.println(ex + "Cannot find: Icons/batt100.gif");
                }
            }
        }
    }

    //Create a menu bar and add components to it
    private void setUpMenu()
    {
        //Menu bar
        JMenuBar menuBar = new JMenuBar();

        //Create Menus
        createMenu(menuBar);

        frame.setJMenuBar(menuBar);
    }

    // Set up the status bar at the bottom of the page.  This will allow
    // messages to be displayed to the user.
    private void SetUpStatusBar()
    {
        //Create statusBar
        JPanel statusBar = new JPanel();
        statusBar.setLayout(new FlowLayout(FlowLayout.LEFT));
        statusBar.setPreferredSize(new Dimension(0, 30));
        statusBar.setBackground(Color.darkGray);
        statusBar.setBorder(new BevelBorder(BevelBorder.LOWERED));

        //Create statusLabel
        statusLabel = new JLabel("\"File > Connect to server\" or \"File > Start server\" to begin...");
        statusLabel.setForeground(Color.white);
        statusLabel.setFont(new Font("Arial", Font.BOLD, 11));
        statusBar.add(statusLabel);

        frame.add(statusBar, BorderLayout.SOUTH);
    }

    //Add grid buttons to the player and opponent grids
    private void addButtonsToGrid(GridButton[][] gridButtons, JPanel grid)
    {
        //Add buttons to grid
        for(int i = 0; i < 11; ++i) {
            for(int j = 0 ; j < 11; ++j) {

                //Add row heading number headings
                if(i == 0) {
                    JLabel rowHeading;
                    if(j == 0)
                        rowHeading = new JLabel("");
                    else
                        rowHeading = new JLabel(Integer.toString(j));

                    rowHeading.setForeground(Color.white);
                    rowHeading.setHorizontalAlignment(SwingConstants.CENTER);
                    rowHeading.setVerticalAlignment(SwingConstants.CENTER);
                    grid.add(rowHeading);

                } else {

                    //Add column letter heading
                    if(j == 0) {
                        JLabel columnHeading = new JLabel(Character.toString((char)(i+64)));
                        columnHeading.setHorizontalAlignment(SwingConstants.CENTER);
                        columnHeading.setVerticalAlignment(SwingConstants.CENTER);
                        columnHeading.setForeground(Color.white);
                        grid.add(columnHeading);

                    //Add grid button
                    } else {
                        grid.add(gridButtons[i - 1][j - 1]);
                    }
                }
            }
        }
    }

    //Create the rotate ship button
    private void setUpRotateButton()
    {
        try {
            Image img = ImageIO.read(getClass().getResource("Icons/rotateButton.png"));
            rotateButton = new JButton(new ImageIcon(img));
            rotateButton.setOpaque(false);
            rotateButton.setContentAreaFilled(false);
            rotateButton.setBorderPainted(false);
            rotateButton.setFocusPainted(false);
        } catch (IOException e) {
            System.err.println("Cannot find Icons/rotateButton.png");
        }
    }

    // Set up the File menu and all of the menu items that
    // go with it then add it to menuBar
    private void createMenu(JMenuBar menuBar)
    {
        // set up File menu
        fileMenu = new JMenu( "File" );
        fileMenu.setMnemonic( 'F' );
        helpMenu = new JMenu("Help");
        helpMenu.setMnemonic('H');

        //set up New Game File menu item
        startServerItem = new JMenuItem("Start server");
        startServerItem.setMnemonic('S');
        fileMenu.add(startServerItem);

        //set how to play
        JMenuItem howPlay = new JMenuItem("How to play");
        helpMenu.add(howPlay);

        //set connection info
        JMenuItem howConnect = new JMenuItem("Connection info");
        helpMenu.add(howConnect);


        //set staistic info
        JMenuItem statistics = new JMenuItem("Statistics");
        helpMenu.add(statistics);

        //set about info
        JMenuItem about = new JMenuItem("About");
        about.setMnemonic('A');
        helpMenu.add(about);

        //set up New Game File menu item
        stopServerItem = new JMenuItem("Stop server");
        stopServerItem.setVisible(false);
        fileMenu.add(stopServerItem);

        //set up New Game File menu item
        connectToServerItem = new JMenuItem("Connect to server");
        connectToServerItem.setMnemonic('C');
        fileMenu.add(connectToServerItem);

        //set up New Game File menu item
        disconnectFromServerItem = new JMenuItem("Disconnect from server");
        disconnectFromServerItem.setVisible(false);
        fileMenu.add(disconnectFromServerItem);

        // set up About File menu item
        JMenuItem quitItem = new JMenuItem("Quit");
        quitItem.setMnemonic('Q');
        fileMenu.add(quitItem);

        menuBar.add(fileMenu);
        menuBar.add(helpMenu);
    }

    //Make sure the look of the program is the same on different platforms
    private void SetCrossPlatformLookAndFeel()
    {
        // Set cross-platform look and feel
        try {
            UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
        }
        catch (Exception e) {
            // Just continue using default look and feel
        }
    }

    //Initialize the arrays that hold the image names of the ship images
    private void initializeImagesArray()
    {
        horizontalShipImages = new String[][] {
                {"Icons/batt1.gif", "Icons/batt5.gif"},
                {"Icons/batt1.gif", "Icons/batt3.gif", "Icons/batt5.gif"},
                {"Icons/batt1.gif", "Icons/batt4.gif", "Icons/batt5.gif"},
                {"Icons/batt1.gif", "Icons/batt2.gif", "Icons/batt3.gif", "Icons/batt5.gif"},
                {"Icons/batt1.gif", "Icons/batt2.gif", "Icons/batt3.gif", "Icons/batt4.gif", "Icons/batt5.gif"}
        };

        verticalShipImages = new String[][] {
                {"Icons/batt6.gif", "Icons/batt10.gif"},
                {"Icons/batt6.gif", "Icons/batt8.gif", "Icons/batt10.gif"},
                {"Icons/batt6.gif", "Icons/batt9.gif", "Icons/batt10.gif"},
                {"Icons/batt6.gif", "Icons/batt7.gif", "Icons/batt8.gif", "Icons/batt10.gif"},
                {"Icons/batt6.gif", "Icons/batt7.gif", "Icons/batt8.gif", "Icons/batt9.gif", "Icons/batt10.gif"}
        };
    }

    // Make the background color flash red and then fades back to blue.
    private void startGameAnimation()
    {
        //If a timer is running make sure not to start another one
        if(animationTimer != null && animationTimer.isRunning())
            return;

        //Create fade timer
        animationTimer = new Timer(20, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                //Get current background and foreground colors
                Color frameBackgroundColor = frame.getContentPane().getBackground();

                //Check if the timer should stop
                if(frameBackgroundColor.getRed() == 0) {
                    animationTimer.stop();
                    return;
                }

                //Update colors
                frame.getContentPane().setBackground(new Color(
                        frameBackgroundColor.getRed()-5,
                        0,
                        frameBackgroundColor.getBlue()+5));
            }
        });

        //Set initial color
        frame.getContentPane().setBackground(new Color(200, 0, 0));
        animationTimer.start();
    }
}
