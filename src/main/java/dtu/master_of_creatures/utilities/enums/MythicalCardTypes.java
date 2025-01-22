package dtu.master_of_creatures.utilities.enums;

public enum MythicalCardTypes
{
    // Card health, attack, cost
    HYDRA(6, 7, 7),
    DRAGON(5, 6, 6),
    FENRIR(5,5,5),
    OOZE(8, 2, 5),
    PHEONIX(5,5,4),
    BISHOPFISH(6,2,4),
    MOTHMAN(4,6,5);

    private final int health;
    private final int attack;
    private final int cost;

    /**
     * Enum constructor
     * @author Romel (s215212)
     */
    MythicalCardTypes(int health, int attack, int cost)
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
