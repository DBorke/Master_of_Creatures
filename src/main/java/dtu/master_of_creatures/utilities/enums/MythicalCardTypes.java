package dtu.master_of_creatures.utilities.enums;

public enum MythicalCardTypes
{
    // Health, attack, cost
    DRAGON(5, 7, 3),
    HYDRA(4,5,3),
    BISHOPFISH(7,2,2),
    PHEONIX(3,4,2),
    SHESHESHEN(8,1,2),
    MOTHMAN(3,6,2);

    private final int health;
    private final int attack;
    private final int cost;

    /**
     * Enum constructor
     * @author Maria (s195685)
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
     * @author Maria (s195685)
     */
    public int getHealth()
    {
        return health;
    }

    /**
     * @author Maria (s195685)
     */
    public int getAttack()
    {
        return attack;
    }

    /**
     * @author Maria (s195685)
     */
    public int getCost()
    {
        return cost;
    }
}
