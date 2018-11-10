//
// Artur Wojcik    - awojci5
// Curt Thieme     - cthiem2
// Sean Martinelli - smarti58
//
// CS 342 Project #4 - Battleship
// 11/16/2017
// UIC, Professor Troy
//
// ClientPlayerController:
// This controls interactions between server and the view if the
// program takes the roll of the client.
//

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.*;


public class ClientPlayerController extends PlayerController
{
    private Socket sock;
    private ObjectOutputStream out;
    private ObjectInputStream in;
    private BattleShipClient battleShipClient;

    public ClientPlayerController(View view)
    {
        super(view);

        sock = null;
        out = null;
        in = null;

        setPlayersTurn(false);
        getView().setMoveLabel("Opponent's Turn");
        getView().setStatusLabel("");

        getView().addOpponentGridButtonListener(new OpponentButtonHandler());
        getView().addPlayerGridButtonListener(new PlayerButtonHandler());
        getView().addStatisticsListener(new StatisticsButtonHandler());

        battleShipClient = new BattleShipClient();
        battleShipClient.start();
    }

    //Allows the user to strike the opponent's board
    private class OpponentButtonHandler implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e)
        {
            GridButton button = (GridButton) e.getSource();

            //Only allow a strike if the game has started and the location has not already been struck
            if (gameHasStarted() && isPlayersTurn() && !button.hasBeenStruck())
            {
                //Let server know which position was struck
                sendMessage(new StrikeMessage(button.getCoordinate(), false, false));

                changePlayerTurn();
            }
        }
    }

    //Allows the user to place ships on their board
    private class PlayerButtonHandler implements ActionListener
    {
        @Override
        public void actionPerformed(ActionEvent e)
        {
            //Only allow ships to be placed before the game has started
            if(gameEnabled() && !gameHasStarted())
            {
                //Build ship to place on board
                GridButton button = (GridButton) e.getSource();
                Coordinate coord = button.getCoordinate();
                Ship shipToAdd = buildShip(coord);

                //Let server know where to place ship
                sendMessage(new PlaceShipMessage(shipToAdd));
            }
        }
    }

    //Allows the user to view statistics about the current game
    private class StatisticsButtonHandler implements ActionListener
    {
        @Override
        public void actionPerformed(ActionEvent e)
        {
            if(gameHasStarted())
                sendMessage(new StatisticsMessage(0,0,0,0,0));
        }
    }

    //Send a message to the server
    private synchronized void sendMessage(Object message)
    {
        try {
            out.writeObject(message);
            out.flush();
        } catch (IOException e) {
            System.err.println("Cannot send message to server: " + e.toString());
        }
    }

    //Ask the user if they would like to play another game and send
    //their answer to the server.
    private void promptForNewGame()
    {
        if(getView().promptUserForNewGame()) {
            sendMessage(new NewGameRequestMessage(true));
        } else {
            sendMessage(new NewGameRequestMessage(false));
        }

        getView().setStatusLabel("Waiting for opponent...");
    }

    // Close the connection to the server and reset the GUI.
    public synchronized void disconnectFromServer()
    {
        try {
            //close connections
            battleShipClient.stopClient();
            in.close();
            out.close();
            sock.close();

            //Update game state information
            getView().connectedToServer(false);
            setGameEnabled(false);
            resetGame();
            getView().setStatusLabel("Disconnected from server.     |     " +
                    "File > Connect to server\" or \"File > Start server\"" +
                    " to start a new game...");

        } catch (IOException e) {
            getView().setStatusLabel("Could not disconnect from server.");
        }
    }

    //Start a new game
    private void newGame()
    {
        resetGame();
        getView().setStatusLabel("Place your ships on your board.");
        setGameEnabled(true);
    }

    //Reset all game state components
    private void resetGame()
    {
        //Reset view
        getView().reset();
        getView().addOpponentGridButtonListener(new OpponentButtonHandler());
        getView().addPlayerGridButtonListener(new PlayerButtonHandler());

        //reset data members
        initializeDataMembers();
        setPlayersTurn(false);

        getView().setMoveLabel("Opponent's Turn");
    }

    //Process a strike message from the server
    private void handleStrikeMessage(StrikeMessage message)
    {
        //Determine if the message is the results from last strike or an actual strike
        if(message.isStrikeResult())
        {
            //Update opponent's board
            if(message.wasHit())
                getView().opponentStrike(message.getCoordinate(), true);
            else
                getView().opponentStrike(message.getCoordinate(), false);
        }
        else
        {
            //update player's board
            if(message.wasHit()) {
                getView().playerStrike(message.getCoordinate(), true);
                DisplayStrikeMessage(message.getCoordinate(), true);
            } else {
                getView().playerStrike(message.getCoordinate(), false);
                DisplayStrikeMessage(message.getCoordinate(), false);
            }

            changePlayerTurn();
        }
    }

    //Process a ship status message from the server
    private void handleShipStatusMessage(ShipStatusMessage message)
    {
        if(message.placementWasSuccessful())
            getView().AddShip(message.getShip(), isHorizontal());
    }

    //Process a game status message from the server
    private void handleGameStatusMessage(GameStatusMessage message)
    {
        //Determine if the game has started
        if(message.gameHasStarted() && !message.gameOver()) {
            startGame();
        }

        //Determine if the game is over
        if(message.gameHasStarted() && message.gameOver())
        {
            //Display win/lose message to the user
            if(message.serverIsWinner()) {
                getView().setStatusLabel("Game over!  You Lost!");
                getView().DisplayMessage(" You Lost!  All of your ships have been sunk!", "You Lost!");
            } else {
                getView().setStatusLabel("Game over!  You Won!");
                getView().DisplayMessage("You Won!  You sunk all of your opponent's ships!", "You Won!");
            }

            promptForNewGame();
        }
    }

    //Process a start new game message
    private void handleStartNewGameMessage(StartNewGameMessage message)
    {
        //Determine if a new game should be started or if the connection should be terminated
        if(message.shouldStartNewGame()) {
            newGame();
        } else {
            disconnectFromServer();
            getView().setStatusLabel("Disconnected:  Your opponent does not want to play again.     |     " +
                    "File > Connect to server\" or \"File > Start server\"" +
                    " to start a new game...");
        }

    }

    //Process a statistics message from the server
    private void handleStatisticsMessage(StatisticsMessage message)
    {
        String statsString = "Number of Shots Fired: " + message.getTotalShotsFired() + "\n"
                + "Number of Hits: " + message.getShotsHit() + "\n"
                + "Number of Misses: " + message.getShotsMissed() + "\n"
                + "Percent Hit: " + message.getPercentHitToMiss() + "%\n"
                + "Number of Hits to Win: " + message.getHitsToWin() + "\n";

        getView().DisplayMessage(statsString, "Game Statistics");
    }

    //Determine which type of message has been received and process it.
    private void processMessage(Object message)
    {
        //Determine message type and process
        if(message instanceof StrikeMessage)
        {
            StrikeMessage strikeMessage = (StrikeMessage) message;
            handleStrikeMessage(strikeMessage);
        }
        else if(message instanceof ShipStatusMessage)
        {
            ShipStatusMessage shipStatusMessage = (ShipStatusMessage) message;
            handleShipStatusMessage(shipStatusMessage);
        }
        else if(message instanceof GameStatusMessage)
        {
            GameStatusMessage gameStatusMessage = (GameStatusMessage) message;
            handleGameStatusMessage(gameStatusMessage);
        }
        else if(message instanceof StartNewGameMessage)
        {
            StartNewGameMessage startNewGameMessage = (StartNewGameMessage) message;
            handleStartNewGameMessage(startNewGameMessage);
        }
        else if(message instanceof StatisticsMessage)
        {
            StatisticsMessage statisticsMessage = (StatisticsMessage) message;
            handleStatisticsMessage(statisticsMessage);
        }
    }

    // This private inner class will run on a separate thread and receive
    // and process messages sent by the server.
    private class BattleShipClient extends Thread
    {
        private volatile boolean shouldRun;

        public BattleShipClient() {
            shouldRun = true;
        }

        public void run()
        {
            try
            {
                //Get IP address and port number from the user
                String ipAddress = getView().PromptForServerIP();
                int portNum = Integer.parseInt(getView().PromptForServerPort());

                getView().setStatusLabel("Connecting to server...");

                sock = new Socket();

                //Connect to the server
                try {
                    sock.connect(new InetSocketAddress(ipAddress, portNum), 5000);
                    out = new ObjectOutputStream(sock.getOutputStream());
                    in = new ObjectInputStream(sock.getInputStream());

                    getView().connectedToServer(true);

                    getView().setStatusLabel("Connected to server.   |   Place your ships on your board.");
                    setGameEnabled(true);

                //Handle exceptions related to connecting to the server
                } catch (SocketTimeoutException e) {
                    getView().setStatusLabel("Cannot connect to server at " + ipAddress +
                            " on port " + portNum + ".  Make sure the" +
                            " IP address and port number are correct.");
                    return;
                } catch (ConnectException e) {
                    getView().setStatusLabel("Connection refused by " + ipAddress +
                            " on port " + portNum + ".  Make sure a Battleship " +
                            "server is running on that machine.");
                    return;
                } catch (UnknownHostException e) {
                    getView().setStatusLabel("Invalid IP address.");
                    return;
                } catch (IllegalArgumentException e) {
                    getView().setStatusLabel("Invalid port number.");
                    return;
                }

            //Handle exceptions related to the port number
            } catch (NumberFormatException e) {
                getView().setStatusLabel("Invalid port number.");
                return;
            }  catch (IOException e) {
                getView().setStatusLabel("Could not connect to server.");
                return;
            }

            //Make sure the socket is connected before proceeding
            if(!sock.isConnected())
                return;

            while(shouldRun)
                readMessage();
        }

        //Read a message when one arrives from the server
        private void readMessage()
        {
            try {
                processMessage(in.readObject());
            }
            catch (Exception ex)
            {
                getView().setStatusLabel("Lost connection with server");
                shouldRun = false;
                disconnectFromServer();
            }
        }

        public void stopClient() {
            shouldRun = false;
        }
    }
}
