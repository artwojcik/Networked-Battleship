//
// Artur Wojcik    - awojci5
// Curt Thieme     - cthiem2
// Sean Martinelli - smarti58
//
// CS 342 Project #4 - Battleship
// 11/16/2017
// UIC, Professor Troy
//
// StatisticsMessage:
// This message allows the server to provide the client
// with statistics information about the game.
//

import java.io.Serializable;

public class StatisticsMessage implements Serializable
{
    private int shotsMissed;
    private int shotsHit;
    private int totalShotsFired;
    private int hitsToWin;
    private float percentHitToMiss;

    //All data members are set when the message is created and then cannot be changed.
    public StatisticsMessage(int shotsMissed, int shotsHit, int totalShotsFired,int hitsToWin, float percentHitToMiss)
    {
       this.shotsMissed = shotsMissed;
       this.shotsHit = shotsHit;
       this.totalShotsFired = totalShotsFired;
       this.hitsToWin = hitsToWin;
       this.percentHitToMiss = percentHitToMiss;
    }

    public int getShotsMissed()
    {
      return shotsMissed;
    }
    
    public int getShotsHit()
    {
      return shotsHit;
    }
    
    public int getTotalShotsFired()
    {
      return totalShotsFired;
    }
    
    public int getHitsToWin()
    {
      return hitsToWin;
    }
    
    public float getPercentHitToMiss()
    {
      return percentHitToMiss;
    }
}
