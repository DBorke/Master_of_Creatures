package dtu.master_of_creatures.models;
import dtu.master_of_creatures.utilities.enums.CreatureTypes;
import eu.hansolo.tilesfx.addons.Switch;

/**
 * @author Romel (s215212) Maria (s195685)
 */

public class CardModel
{   // Fields
    private final CreatureTypes creature_type;
    private final int health;
    private final int attack;
    private final int cost;

    //Constructor
    public CardModel(CreatureTypes creature_type)
    {
        this.creature_type = creature_type;
        this.health = creature_type.getHealth();
        this.attack = creature_type.getAttack();
        this.cost = creature_type.getCost();
    }
}

    // Getters and Setters
    public CreatureTypes getCreatureType() {
        return creature_type;
    }


public int getHealth() {
        return health;
    }


    public int getAttack() {
        return attack;
    }



    public int getCost() {
        return cost;
    }



    @Override
    public String toString() {
        return String.format("CreatureModel{creature_type='%s', health=%d, attack=%d, cost=%d}", creature_type, health, attack, cost);
    }
}

