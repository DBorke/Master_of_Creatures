/*
 * Packages
 */
package dtu.master_of_creatures.models;

/*
 * Imports for javascript
 */
import java.util.ArrayList;
import java.util.List;
import java.util.Arrays;
import dtu.master_of_creatures.models.GameModel; // we're assuming the game model is instantiated here, until proven otherwise :)
import dtu.master_of_creatures.utilities.enums.Phases;

/*
 * Board Model Logistics / Groundwork
 */
public class BoardModel
{
    /**
     * @author Romel (s215212) Maria (s195685)
     */
    
    // Fields
    private List<CreatureModel> player1Lanes;
    private List<CreatureModel> player2Lanes;

    // Constructor
    public BoardModel(int initialHealth) 
    {
        this.player1Lanes = new ArrayList<>(Arrays.asList(null, null, null));
        this.player2Lanes = new ArrayList<>(Arrays.asList(null, null, null));
    }

    // Getters
    public List<CreatureModel> getPlayer1Lanes() 
    {
        return player1Lanes;
    }

    public List<CreatureModel> getPlayer2Lanes() 
    {
        return player2Lanes;
    }


    // Summon a creature in a specific lane
    public void summonCreature(CreatureModel creature, int lane) 
    {
        // assuming the name of game model object is game
        if (GameModel.game.getCurrentPhase != Phases.SUMMON) // will change depending on the phase names. "SUMMON" is the planning/sacrifice/playing phase.
        {
            throw new IllegalStateException("You can only summon creatures during the summon phase.");
        }

        if (lane < 0 || lane > 2) 
        {
            throw new IllegalArgumentException("Lane must be 0, 1, or 2.");
        }

        List<CreatureModel> currentPlayerLanes = GameModel.game.getCurrentTurn == 1 ? player1Lanes : player2Lanes; // assuming the name of the game model object is game

        if (currentPlayerLanes.get(lane) != null) 
        {
            throw new IllegalStateException("Lane is already occupied.");
        }

        currentPlayerLanes.set(lane, creature);
    }


    @Override
    public String toString() 
    {
        return String.format(
                "BoardModel{player1Lanes=%s, player2Lanes=%s}",
                player1Lanes, player2Lanes
        );
    }
}
