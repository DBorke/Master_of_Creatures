package dtu.master_of_creatures.models;

// Project libraries
import dtu.master_of_creatures.utilities.enums.CommonCardTypes;
import dtu.master_of_creatures.utilities.enums.MythicalCardTypes;

// Java libraries
import java.io.Serializable;

public class CardModel implements Serializable
{
    // Fields
    public CommonCardTypes common_card_type;
    public MythicalCardTypes mythical_card_type;
    public int health;
    public int attack;
    public final int cost;
    public boolean can_attack;

    /**
     * @author Romel (s215212), Danny (s224774)
     */
    public CardModel(CommonCardTypes common_card_type)
    {
        this.common_card_type = common_card_type;

        this.health = common_card_type.getHealth();
        this.attack = common_card_type.getAttack();
        this.cost = common_card_type.getCost();
    }

    /**
     * @author Romel (s215212), Danny (s224774)
     */
    public CardModel(MythicalCardTypes mythical_card_type)
    {
        this.mythical_card_type = mythical_card_type;

        this.health = mythical_card_type.getHealth();
        this.attack = mythical_card_type.getAttack();
        this.cost = mythical_card_type.getCost();
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

    public void setCanAttack(boolean can_attack)
    {
        this.can_attack = can_attack;
    }

    /**
     * @author Romel (s215212)
     */
    public CommonCardTypes getCommonCardType() {
        return common_card_type;
    }

    /**
     * @author Romel (s215212)
     */
    public MythicalCardTypes getMythicalCardType() {
        return mythical_card_type;
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
        return String.format("CardModel{card_type='%s', health=%d, attack=%d, cost=%d}", common_card_type, health, attack, cost);
    }
}
