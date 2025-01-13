/*
 * Packages
 */
package dtu.master_of_creatures.models;

/*
 * Imports
 */
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

    // Enums for turn phases
    public enum TurnPhase 
    {
        SUMMON,
        ATTACK
    }

    // Fields
    private List<CreatureModel> player1Lanes;
    private List<CreatureModel> player2Lanes;
    private int player1Health;
    private int player2Health;
    private int currentTurn; // 1 for Player 1, 2 for Player 2
    private TurnPhase currentPhase;

    // Constructor
    public BoardModel(int initialHealth) 
    {
        this.player1Lanes = new ArrayList<>(Arrays.asList(null, null, null));
        this.player2Lanes = new ArrayList<>(Arrays.asList(null, null, null));
        this.player1Health = initialHealth;
        this.player2Health = initialHealth;
        this.currentTurn = 1; // Player 1 starts
        this.currentPhase = TurnPhase.SUMMON; // Start in summon phase
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

    public int getPlayer1Health() 
    {
        return player1Health;
    }

    public int getPlayer2Health() 
    {
        return player2Health;
    }

    public int getCurrentTurn() 
    {
        return currentTurn;
    }

    public TurnPhase getCurrentPhase() 
    {
        return currentPhase;
    }

    // Summon a creature in a specific lane
    public void summonCreature(CreatureModel creature, int lane) 
    {
        if (currentPhase != TurnPhase.SUMMON) 
        {
            throw new IllegalStateException("You can only summon creatures during the summon phase.");
        }

        if (lane < 0 || lane > 2) 
        {
            throw new IllegalArgumentException("Lane must be 0, 1, or 2.");
        }

        List<CreatureModel> currentPlayerLanes = currentTurn == 1 ? player1Lanes : player2Lanes;

        if (currentPlayerLanes.get(lane) != null) 
        {
            throw new IllegalStateException("Lane is already occupied.");
        }

        currentPlayerLanes.set(lane, creature);
    }

    // Attack logic
    public void attackCreature(int attackingLane, int defendingLane) 
    {
        if (currentPhase != TurnPhase.ATTACK) 
        {
            throw new IllegalStateException("You can only attack during the attack phase.");
        }

        if (attackingLane < 0 || attackingLane > 2 || defendingLane < 0 || defendingLane > 2) 
        {
            throw new IllegalArgumentException("Lane must be 0, 1, or 2.");
        }

        List<CreatureModel> attackerLanes = currentTurn == 1 ? player1Lanes : player2Lanes;
        List<CreatureModel> defenderLanes = currentTurn == 1 ? player2Lanes : player1Lanes;

        CreatureModel attacker = attackerLanes.get(attackingLane);
        if (attacker == null) 
        {
            throw new IllegalStateException("No attacker in the specified lane.");
        }

        CreatureModel defender = defenderLanes.get(defendingLane);
        if (defender != null) 
        {
            // Attack the opposing creature
            defender.setHealth(defender.getHealth() - attacker.getAttack());
            if (defender.getHealth() <= 0) 
            {
                defenderLanes.set(defendingLane, null); // Remove defeated creature
            }
        } else 
        {
            // Attack the opposing player directly
            if (currentTurn == 1) 
            {
                player2Health -= attacker.getAttack();
            } else 
            {
                player1Health -= attacker.getAttack();
            }
        }
    }

    // Transition to the next phase or next player's turn
    public void endPhase() 
    {
        if (currentPhase == TurnPhase.SUMMON) 
        {
            currentPhase = TurnPhase.ATTACK;
        } else if (currentPhase == TurnPhase.ATTACK) 
        {
            // Switch turn to the other player
            currentTurn = currentTurn == 1 ? 2 : 1;
            currentPhase = TurnPhase.SUMMON;
        }
    }

    @Override
    public String toString() 
    {
        return String.format(
                "BoardModel{player1Lanes=%s, player2Lanes=%s, player1Health=%d, player2Health=%d, currentTurn=%d, currentPhase=%s}",
                player1Lanes, player2Lanes, player1Health, player2Health, currentTurn, currentPhase
        );
    }
}
