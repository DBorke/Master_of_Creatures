package dtu.master_of_creatures.models;

// Java libraries
import java.util.Arrays;

public class BoardModel
{
    // Fields
    private final CardModel[] player_1_lanes;
    private final CardModel[] player_2_lanes;

    // Game data
    private final GameModel game_model;

    /**
     * Constructor
     * @author Maria (s195685), Danny (s224774)
     */
    public BoardModel(GameModel game_model)
    {
        player_1_lanes = new CardModel[4];
        player_2_lanes = new CardModel[4];

        this.game_model = game_model;
    }

    /**
     * Summon a creature in a specific lane
     * @author Maria (s195685), Danny (s224774)
     */
    public boolean summonCreature(CardModel creature, int lane, boolean update_opponent) // can only be called for the local player
    {
        if (lane < 0 || lane > 3)
        {
            throw new IllegalArgumentException("Lane must be either 0, 1, 2 or 3.");
        }

        CardModel[] player_lanes;

        if(update_opponent)
        {
            player_lanes = game_model.getPlayer().getPlayerNumber() == 0 ? player_2_lanes : player_1_lanes;
        }
        else
        {
            player_lanes = game_model.getPlayer().getPlayerNumber() == 0 ? player_1_lanes : player_2_lanes;
        }

        if (player_lanes[lane] != null)
        {
            throw new IllegalStateException("Lane is already occupied.");
        }

        System.out.println("Summon creature: " + game_model.getPlayer().getPlayerNumber());
        System.out.println("Summon creature: " + game_model.getCurrentPlayerNumber());

        player_lanes[lane] = creature;

        return true;
    }

    /**
     * Remove a creature from a specific lane.
     * @author Danny (s224774)
     */
    public void removeCreatureFromField(int player_number, int lane, boolean update_opponent) // can be called for both the local and remote player
    {
        if(lane >= 0 && lane <= 3)
        {
            CardModel[] player_lanes;

            if(update_opponent)
            {
                player_lanes = game_model.getPlayer().getPlayerNumber() == 0 ? player_2_lanes : player_1_lanes;
            }
            else
            {
                player_lanes = game_model.getPlayer().getPlayerNumber() == 0 ? player_1_lanes : player_2_lanes;
            }

            if(player_lanes[lane] != null)
            {
                player_lanes[lane] = null;
            }
        }
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
