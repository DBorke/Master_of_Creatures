package dtu.master_of_creatures.models;

// Project libraries
import dtu.master_of_creatures.utilities.enums.CommonCardTypes;

// Java libraries
import java.io.Serializable;

public class CardModel implements Serializable
{
    // Fields
    private final CommonCardTypes card_type;
    private int health;
    private int attack;
    private final int cost;

    /**
     * @author Romel (s215212), Danny (s224774)
     */
    public CardModel(CommonCardTypes card_type)
    {
        this.card_type = card_type;
        this.health = card_type.getHealth();
        this.attack = card_type.getAttack();
        this.cost = card_type.getCost();
    }

    /**
     * @author Danny (s224774), Carl Emil (s224168), Mathias (s224273)
     */
    public int damageCard(int damage_done)
    {
        health -= damage_done;

        return health;
    }

    /////////////////////////
    // setters and getters //
    /////////////////////////

    /**
     * @author Romel (s215212)
     */
    public CommonCardTypes getCardType() {
        return card_type;
    }

    /**
     * @author Romel (s215212)
     */
    public int getHealth() {
        return health;
    }

    /**
     * @author Romel (s215212)
     */
    public int getAttack() {
        return attack;
    }

    /**
     * @author Romel (s215212)
     */
    public int getCost() {
        return cost;
    }

    /**
     * @author Romel (s215212)
     */
    @Override
    public String toString() {
        return String.format("CardModel{card_type='%s', health=%d, attack=%d, cost=%d}", card_type, health, attack, cost);
    }
}
