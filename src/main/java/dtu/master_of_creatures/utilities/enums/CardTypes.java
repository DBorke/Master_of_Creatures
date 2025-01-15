package dtu.master_of_creatures.utilities.enums;

public enum CardTypes
{
    RABBIT(2, 0, 0),
    WOLF(1, 1, 1), // Health, Attack, Cost
    DRAGON(10, 10, 4);

    /* to be done
    DOE,
    COYOTE,
    BADGER,
    SHARK,
    DEER,
    HAWK,
    GOAT
    */

    private final int health;
    private final int attack;
    private final int cost;

    /**
     * Enum constructor
     * @author Romel (s215212)
     */
    CardTypes(int health, int attack, int cost)
    {
        this.health = health;
        this.attack = attack;
        this.cost = cost;
    }

    /////////////////////////
    //////// getters ////////
    /////////////////////////

    /**
     * @author Romel (s215212)
     */
    public int getHealth()
    {
        return health;
    }

    /**
     * @author Romel (s215212)
     */
    public int getAttack()
    {
        return attack;
    }

    /**
     * @author Romel (s215212)
     */
    public int getCost()
    {
        return cost;
    }
}
