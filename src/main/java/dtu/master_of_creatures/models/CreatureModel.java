package dtu.master_of_creatures.models;

import dtu.master_of_creatures.utilities.enums.CreatureTypes;
import eu.hansolo.tilesfx.addons.Switch;

/**
 * @author Romel (s215212) Maria (s195685)
 */

public class CreatureModel
{
    // Fields
    private final CreatureTypes creature_type;
    private int health;
    private int attack;
    private int cost;

    // Constructor
    public CreatureModel(CreatureTypes creature_type) { // Set up the specific creature
        this.creature_type = creature_type;

        switch(creature_type) { // Set the health of the creature
            case RABBIT, WOLF-> this.health = 2;
            case DRAGON -> this.health = 10;
            default -> throw new IllegalArgumentException("Invalid creature type");
        }

        switch (creature_type) { // Set the attack of the creature
            case RABBIT -> this.attack = 0;
            case WOLF -> this.attack = 5;
            case DRAGON -> this.attack = 10;
            default -> throw new IllegalArgumentException("Invalid creature type");
        }

        switch (creature_type) { // Set the cost of the creature
            case RABBIT -> this.cost = 0;
            case WOLF -> this.cost = 2;
            case DRAGON -> this.cost = 4;
            default-> throw new IllegalArgumentException("Invalid creature type");
        }

    }

    // Getters and Setters
    public CreatureTypes getCreatureType() {
        return creature_type;
    }

    public int getHealth() {
        return health;
    }

    public void setHealth(int health) {
        this.health = health;
    }

    public int getAttack() {
        return attack;
    }

    public void setAttack(int attack) {
        this.attack = attack;
    }

    public int getCost() {
        return cost;
    }

    public void setCost(int cost) {
        this.cost = cost;
    }

    @Override
    public String toString() {
        return String.format("CreatureModel{creature_type='%s', health=%d, attack=%d, cost=%d}", creature_type, health, attack, cost);
    }
}

