package dtu.master_of_creatures.utilities.enums;

public enum CreatureTypes
{   RABBIT(2, 0, 0),
    WOLF(1, 1, 1), // Health, Attack, Cost
    DRAGON(10, 10, 4);

    /*
    DOE,
    COYOTE,
    BADGER,
    SHARK,
    DEER,
    HAWK,
    GOAT */


    private final int health;
    private final int attack;
    private final int cost;

    // Enum constructor
    CreatureTypes(int health, int attack, int cost) {
        this.health = health;
        this.attack = attack;
        this.cost = cost;
    }

    // Getters
    public int getHealth() {
        return health;
    }

    public int getAttack() {
        return attack;
    }

    public int getCost() {
        return cost;
    }
    

}
