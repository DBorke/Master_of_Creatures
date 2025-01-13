package dtu.master_of_creatures.models;

/**
 * @author Romel (s215212) Maria (s195685)
 */

public class CreatureModel
{
    private final String name;
    private int health;
    private int attack;
    private int cost;

    public CreatureModel(String name, int health, int attack, int cost) {
        if (health < 0 || attack < 0 || cost < 0) {
            throw new IllegalArgumentException("Values cannot be negative.");
        }
        this.name = name;
        this.health = health;
        this.attack = attack;
        this.cost = cost;
    }

    // Getters and Setters
    public String getName() {
        return name;
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
        return String.format("CreatureModel{name='%s', health=%d, attack=%d, cost=%d}", name, health, attack, cost);
    }
}

