//
// Artur Wojcik    - awojci5
// Curt Thieme     - cthiem2
// Sean Martinelli - smarti58
//
// CS 342 Project #4 - Battleship
// 11/16/2017
// UIC, Professor Troy
//
// Battleship:
// Creates a view and the initial controller to start the game.
//

public class Battleship
{
    public static void main(String[] args)
    {
        View view = new View();
        new SetupController(view);
    }
}