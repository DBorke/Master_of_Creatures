package dtu.master_of_creatures.utilities.enums;

public enum CommonCardTypes
{
    // Health, attack, cost
    RABBIT(1, 0, 0),
    LAMB(2, 0, 0),
    RAT(1,1,1),
    WOLF(2, 3, 2),
    STOAT(2,1,1),
    DEER(3,2,2),
    COYOTE(1,2,1),
    BADGER(2,3,2),
    FOX(1,1,1),
    SHARK(2,4,3),
    HORSE(3,2,2),
    CAT(2,1,1),
    HAWK(3,2,2),
    GOAT(2,2,1),
    SPARROW(1,2,1),
    BEAR(3,3,3),
    LIZARD(2,1,1),
    TOAD(2,1,1),
    SNAKE(1,3,2),
    COCKROACH(4,1,2),
    PRONGHORN_BEETLE(2,1,1),
    BEE(1,1,1),
    PRAYING_MANTIS(1,2,1),
    PORCUPINE(2,1,1),
    ANT(2,1,1),
    BAT(1,2,1),
    OPOSSUM(1,1,1),
    TORTOISE(4,1,2),
    BLOODHOUND(2,2,2),
    VULTURE(2,3,2),
    HIPPOPOTAMUS(4, 4, 4),
    TIGER(3, 4, 3),
    MOSQUITO(1, 3, 1),
    ELEPHANT(4, 2, 3),
    PANDA(4, 0, 2),
    CROCODILE(3, 2, 3);

    private final int health;
    private final int attack;
    private final int cost;

    /**
     * Enum constructor
     * @author Romel (s215212)
     */
    CommonCardTypes(int health, int attack, int cost)
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
