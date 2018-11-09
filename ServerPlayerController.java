//
// Artur Wojcik    - awojci5
// Curt Thieme     - cthiem2
// Sean Martinelli - smarti58
//
// CS 342 Project #4 - Battleship
// 11/16/2017
// UIC, Professor Troy
//
// ServerPlayerController:
// This controls interactions between the model and view
// if the program takes the roll of the server.
//

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;

public class ServerPlayerController extends PlayerController
{
    private Model model;
    private ServerSocket serverSocket;
    private BattleShipServer battleShipServer;
    private Socket clientSocket;
    private ObjectOutputStream out;
    private ObjectInputStream in;
    private boolean clientPlayAgain;
    private boolean serverPlayAgain;
    private boolean clientMadeReplayDecision;
    private boolean serverMadeReplayDecision;

    public ServerPlayerController(View view)
    {
        super(view);

        model = new Model();
        serverSocket = null;
        clientSocket = null;
        out = null;
        in = null;

        clientPlayAgain = false;
        serverPlayAgain = false;
        clientMadeReplayDecision = false;
        serverMadeReplayDecision = false;

        setPlayersTurn(true);
        getView().setMoveLabel("Your Turn");

        getView().addOpponentGridButtonListener(new OpponentButtonHandler());
        getView().addPlayerGridButtonListener(new PlayerButtonHandler());
        getView().serverStarted(true);
        getView().addStatisticsListener(new StatisticsButtonHandler());


        battleShipServer = new BattleShipServer();
        battleShipServer.start();

    }

    //Close connections and stop the server
    public synchronized void stopServer()
    {
        try {
            battleShipServer.stopRunning();

            //Make sure server can be stopped
            if(clientSocket != null)
                disconnectFromClient();
            else
                getView().setStatusLabel("Server Stopped.");

            //Reset game state information
            getView().serverStarted(false);
            setGameEnabled(false);
            resetGame();

        } catch (IOException e) {
            getView().setStatusLabel("Could not disconnect from client.");
        }
    }

    //Close connections to client
    private void disconnectFromClient() throws IOException
    {
        in.close();
        out.close();
        clientSocket.close();
        serverSocket.close();

        getView().setStatusLabel("Disconnected from client.     |     " +
                "File > Connect to server\" or \"File > Start server\"" +
                " to start a new game...");
    }

    //Ask the user if they would like to play another game.
    private synchronized void promptForNewGame()
    {
        if(getView().promptUserForNewGame())
            serverPlayAgain = true;
        else
            serverPlayAgain = false;

        serverMadeReplayDecision = true;
        getView().setStatusLabel("Waiting for opponent...");
        checkForNewGame();
    }

    //Check if both the client and server have make a decision about
    //playing a new game.
    private synchronized void checkForNewGame()
    {
        //Make sure the client and server have decided
        if(clientMadeReplayDecision && serverMadeReplayDecision) {

            //Determine the outcome of their decisions
            if(clientPlayAgain && serverPlayAgain) {
                sendMessage(new StartNewGameMessage(true));
                newGame();

            } else if(!clientPlayAgain && serverPlayAgain) {
                stopServer();
                getView().setStatusLabel("Disconnected:  Your opponent does not want to play again.     |     " +
                        "File > Connect to server\" or \"File > Start server\"" +
                        " to start a new game...");

            } else if(clientPlayAgain && !serverPlayAgain) {
                sendMessage(new StartNewGameMessage(false));

            } else {
                stopServer();
            }
        }
    }

    //Reset all game state components
    private synchronized void resetGame()
    {
        //reset GUI components
        getView().reset();
        getView().addOpponentGridButtonListener(new OpponentButtonHandler());
        getView().addPlayerGridButtonListener(new PlayerButtonHandler());

        model = new Model(); //reset the model

        //Reset game state data members
        initializeDataMembers();
        clientPlayAgain = false;
        serverPlayAgain = false;
        serverMadeReplayDecision = false;
        clientMadeReplayDecision = false;
        setPlayersTurn(true);

        getView().setMoveLabel("Your Turn");
    }

    //Start a new game
    private synchronized void newGame()
    {
        resetGame();
        getView().setStatusLabel("Place your ships on your board.");
        setGameEnabled(true);
    }

    //Allows the user to strike the opponent's board
    private class OpponentButtonHandler implements ActionListener
    {
        @Override
        public void actionPerformed(ActionEvent e)
        {
            GridButton button = (GridButton)e.getSource();

            //Only allow a strike if the game has started and the location has not already been struck
            if(gameHasStarted() && isPlayersTurn() && !button.hasBeenStruck())
            {
                changePlayerTurn();

                //Determine if strike was a hit or miss
                if(model.clientStrike(button.getCoordinate()))
                {
                    //Update view and send a message to the client with results of strike
                    getView().opponentStrike(button.getCoordinate(), true);
                    sendMessage(new StrikeMessage(button.getCoordinate(), true, false));

                    //Check if the client won
                    if(model.checkServerForWin()) {
                        sendMessage(new GameStatusMessage(true, true, true));
                        getView().setStatusLabel("Game over!  You Won!");
                        getView().DisplayMessage("You Won!  You sunk all of your" +
                                " opponent's ships!", "You Won!");
                        promptForNewGame();
                    }
                } else {
                    getView().opponentStrike(button.getCoordinate(), false);
                    sendMessage(new StrikeMessage(button.getCoordinate(), false, false));
                }
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
            if(gameEnabled() && !gameHasStarted()) {

                //Build ship to place on board
                GridButton button = (GridButton) e.getSource();
                Coordinate coord = button.getCoordinate();
                Ship shipToAdd = buildShip(coord);

                addServerShip(shipToAdd);
            }
        }
    }

    private class StatisticsButtonHandler implements ActionListener
    {
        @Override
        public void actionPerformed(ActionEvent e)
        {
            if(gameHasStarted())
                DisplayStatistics();
        }
    }

    private void DisplayStatistics()
    {
        String message = "Number of Shots Fired: " + model.getServerTotalShotsFired() + "\n"
                + "Number of Hits: " + model.getServerNumShotsHit() + "\n"
                + "Number of Misses: " + model.getServerNumShotsMissed() + "\n"
                + "Percent Hit: " + model.getServerPercentHit() + "%\n"
                + "Number of Hits to Win: " + model.getServerNumHitsForWin() + "\n";

        getView().DisplayMessage(message, "Game Statistics");
    }

    //Check if both the server and client have placed all fo their ships on the board
    private synchronized void checkReadyStatus()
    {
        if (model.serverIsReady() && model.clientIsReady()) {
            startGame();
            sendMessage(new GameStatusMessage(false, true, false));
        }
    }

    //Add a client ship to their board then send the client a message
    //to let it know if adding the shi was successful.
    private void addClientShip(Ship ship)
    {
        if(model.addClientShip(ship)) {
            sendMessage(new ShipStatusMessage(ship, true));
            checkReadyStatus();
        } else {
            sendMessage(new ShipStatusMessage(ship, false));
        }
    }

    //Add a server ship to the board
    private void addServerShip(Ship ship)
    {
        //Try to add server's ship
        if (model.addServerShip(ship)) {
            getView().AddShip(ship, isHorizontal());
            checkReadyStatus();
        }
    }

    //Send a message to the client
    private synchronized void sendMessage(Object message)
    {
        try {
            out.writeObject(message);
            out.flush();
        } catch (IOException e) {
            System.err.println("Cannot send message to client: " + e.toString());
        }
    }

    //Process a strike message
    private void handleStrikeMessage(StrikeMessage message)
    {
        //Determine if strike was successful and send the result to the client
        if(model.serverStrike(message.getCoordinate()))
        {
            getView().playerStrike(message.getCoordinate(), true);
            sendMessage(new StrikeMessage(message.getCoordinate(), true, true));
            DisplayStrikeMessage(message.getCoordinate(), true);
        }
        else
        {
            getView().playerStrike(message.getCoordinate(), false);
            sendMessage(new StrikeMessage(message.getCoordinate(), false, true));
            DisplayStrikeMessage(message.getCoordinate(), false);
        }

        changePlayerTurn();

        //Check if client won
        if(model.checkClientForWin()) {
            sendMessage(new GameStatusMessage(true, true, false));
            getView().setStatusLabel("Game over!  You Lost!");
            getView().DisplayMessage(" You Lost!  All of your ships have been sunk!", "You Lost!");
            promptForNewGame();
        }
    }

    //Process a new game request message
    private void handleNewGameRequestMessage(NewGameRequestMessage message)
    {
        //Check if the client would like to play again
        if(message.isRequestingNewGame())
            clientPlayAgain = true;

        clientMadeReplayDecision = true;
        checkForNewGame();
    }

    private void handleStatisticsMessage(StatisticsMessage message)
    {
        sendMessage(new StatisticsMessage(
                model.getClientNumShotsMissed(),
                model.getClientNumShotsHit(),
                model.getClientTotalShotsFired(),
                model.getClientNumHitsForWin(),
                model.getClientPercentHit())
        );
    }

    //Determine which type of message has been received and process it.
    private synchronized void processMessage(Object message)
    {
        if(message instanceof StrikeMessage)
        {
            StrikeMessage strikeMessage = (StrikeMessage) message;
            handleStrikeMessage(strikeMessage);
        }
        else if(message instanceof PlaceShipMessage)
        {
            PlaceShipMessage placeShipMessage = (PlaceShipMessage) message;
            addClientShip(placeShipMessage.getShipToPlace());
        }
        else if(message instanceof NewGameRequestMessage)
        {
            NewGameRequestMessage newGameRequestMessage = (NewGameRequestMessage) message;
            handleNewGameRequestMessage(newGameRequestMessage);
        }
        else if(message instanceof StatisticsMessage)
        {
            StatisticsMessage statisticsMessage = (StatisticsMessage) message;
            handleStatisticsMessage(statisticsMessage);
        }
    }

    // This private inner class will run on a separate thread and receive
    // and process messages sent by the client.
    private class BattleShipServer extends Thread
    {
        private volatile boolean shouldRun;

        public BattleShipServer() {
            shouldRun = true;
        }

        public void run()
        {
            try {

                //Setup serverSocket
                serverSocket = new ServerSocket(0);
                serverSocket.setSoTimeout(1000);

                getView().setStatusLabel("Waiting for client...    " +
                        "Server IP: " + InetAddress.getLocalHost().getHostAddress() +
                        "    Port: " +
                        serverSocket.getLocalPort());

                //Keep looping until a connection is established
                while (clientSocket == null) {

                    try {
                        clientSocket = serverSocket.accept();
                    } catch (SocketTimeoutException e) {
                        //Loop again if timeout occurs
                    }

                    //Once accept times out, check to see if another attempt should be made
                    if (!shouldRun) {
                        serverSocket.close();
                        return;
                    }
                }

                getView().setStatusLabel("Client accepted.   |   Place your ships on your board.");
                setGameEnabled(true);

                //set up in/out streams
                out = new ObjectOutputStream(clientSocket.getOutputStream());
                in = new ObjectInputStream(clientSocket.getInputStream());

            } catch (IOException e) {
                System.err.println(e.toString());
            }

            while (shouldRun)
                readMessage();
        }

        //Read a message when one arrives from the client
        private void readMessage()
        {
            try {
                processMessage(in.readObject());
            }
            catch (Exception ex)
            {
                getView().setStatusLabel("Lost connection with client");
                shouldRun = false;
                stopServer();
            }
        }

        public void stopRunning() {
            shouldRun = false;
        }
    }
}
