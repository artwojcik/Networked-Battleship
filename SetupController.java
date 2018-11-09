//
// Artur Wojcik    - awojci5
// Curt Thieme     - cthiem2
// Sean Martinelli - smarti58
//
// CS 342 Project #4 - Battleship
// 11/16/2017
// UIC, Professor Troy
//
// SetupController:
// This is the main controller that initially controls
// interactions between the user and the view.  Once the
// program becomes either the server or the client, control
// is passed to a new dedicated controller for the roll.
//

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class SetupController
{
    private View view;
    private ServerPlayerController serverPlayerController;
    private ClientPlayerController clientPlayerController;

    public SetupController(View view)
    {
        this.view = view;
        view.addConnectionButtonListener(new ConnectionButtonHandler());
        view.addHelpMenuListeners(new HelpMenuButtonHandler());
    }

    //Allow the user to connect to a server
    private void connectToServer()
    {
        clientPlayerController = new ClientPlayerController(view);
    }

    //Allow the user to start a new server
    private void startServer()
    {
        view.setStatusLabel("Starting server...");
        serverPlayerController = new ServerPlayerController(view);
    }

    //React to the user pressing the help menu buttons
    private class HelpMenuButtonHandler implements ActionListener
    {
        @Override
        public void actionPerformed(ActionEvent e) {
            if(e.getActionCommand().equals("About"))
                view.displayAboutMessage();
            else if(e.getActionCommand().equals("How to play"))
                view.displayHelpMessage();
            else if(e.getActionCommand().equals("Connection info"))
                view.displayConnectionHelpMessage();

        }
    }

    //React to the user pressing one of the connection buttons
    private class ConnectionButtonHandler implements ActionListener
    {
        @Override
        public void actionPerformed(ActionEvent e)
        {
            if(e.getActionCommand().equals("Connect to server"))
                connectToServer();
            else if(e.getActionCommand().equals("Disconnect from server"))
                clientPlayerController.disconnectFromServer();
            else if(e.getActionCommand().equals("Start server"))
                startServer();
            else if(e.getActionCommand().equals("Stop server"))
                serverPlayerController.stopServer();
            else if(e.getActionCommand().equals("Quit"))
                System.exit(0);
        }
    }
}
