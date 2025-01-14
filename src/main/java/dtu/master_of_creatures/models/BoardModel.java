/*
 * Packages
 */
package dtu.master_of_creatures.models;

/*
 * Imports for javascript
 */
import dtu.master_of_creatures.utilities.enums.PhaseTypes;

import java.util.ArrayList;
import java.util.List;
import java.util.Arrays;


/*
 * Board Model Logistics / Groundwork
 */
public class BoardModel
{
    /**
     * @author Romel (s215212) Maria (s195685)
     */
    
    // Fields
    private final List<CardModel> player1Lanes;
    private final List<CardModel> player2Lanes;
    // Game data
    private final GameModel game_model;

    // Constructor
    public BoardModel(GameModel game_model)
    {
        this.player1Lanes = new ArrayList<>(Arrays.asList(null, null, null));
        this.player2Lanes = new ArrayList<>(Arrays.asList(null, null, null));
        this.game_model = game_model;
    }

    // Getters
    public List<CardModel> getPlayer1Lanes()
    {
        return player1Lanes;
    }

    public List<CardModel> getPlayer2Lanes()
    {
        return player2Lanes;
    }


    // Summon a creature in a specific lane
    public void summonCreature(CardModel creature, int lane)
    {
        // assuming the name of game model object is game
        if (game_model.getPhaseType() != PhaseTypes.PLAYING_PHASE) // will change depending on the phase names. "SUMMON" is the planning/sacrifice/playing phase.
        {
            throw new IllegalStateException("You can only summon creatures during the summon phase.");
        }

        if (lane < 0 || lane > 2) 
        {
            throw new IllegalArgumentException("Lane must be 0, 1, or 2.");
        }

        List<CardModel> currentPlayerLanes = game_model.getCurrentPlayer() == game_model.getPlayers()[0] ? player1Lanes : player2Lanes;

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
