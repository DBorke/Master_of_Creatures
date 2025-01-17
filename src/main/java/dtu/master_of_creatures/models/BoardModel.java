/*
 * Packages
 */
package dtu.master_of_creatures.models;

/*
 * Imports for java
 */
import dtu.master_of_creatures.utilities.enums.PhaseTypes;

import java.util.Arrays;

/*
 * Board Model Logistics / Groundwork
 */
public class BoardModel
{
    // Fields
    private final CardModel[] player_1_lanes;
    private final CardModel[] player_2_lanes;

    // Game data
    private final GameModel game_model;

    /**
     * Constructor
     * @author Maria (s195685)
     */
    public BoardModel(GameModel game_model)
    {
        player_1_lanes = new CardModel[3];
        player_2_lanes = new CardModel[3];

        this.game_model = game_model;
    }

    /**
     * Summon a creature in a specific lane
     * @author Maria (s195685), Danny (s224774)
     */
    public boolean addRemoveCreatureFromField(CardModel creature, int lane)
    {
        if (game_model.getPhaseType() != PhaseTypes.PLAYING_PHASE) // will change depending on the phase names. "SUMMON" is the planning/sacrifice/playing phase.
        {
            throw new IllegalStateException("You can only summon creatures during the summon phase.");
        }

        if (lane < 0 || lane > 2)
        {
            throw new IllegalArgumentException("Lane must be 0, 1, or 2.");
        }

        CardModel[] current_player_lanes = game_model.getCurrentPlayer() == game_model.getPlayers()[0] ? player_1_lanes : player_2_lanes;

        if (current_player_lanes[lane] != null)
        {
            throw new IllegalStateException("Lane is already occupied.");
        }

        current_player_lanes[lane] = creature;

        return true;
    }

    /**
     * @author Maria (s195685), Romel (s215212)
     */
    @Override
    public String toString()
    {
        return String.format(
            "BoardModel{player_1_lanes=%s, player_2_lanes=%s}",
            Arrays.toString(player_1_lanes), Arrays.toString(player_2_lanes)
        );
    }

    /////////////////////////
    //////// getters ////////
    /////////////////////////

    /**
     * @author Maria (s195685)
     */
    public CardModel[] getPlayer1Lanes()
    {
        return player_1_lanes;
    }

    /**
     * @author Maria (s195685)
     */
    public CardModel[] getPlayer2Lanes()
    {
        return player_2_lanes;
    }
}
