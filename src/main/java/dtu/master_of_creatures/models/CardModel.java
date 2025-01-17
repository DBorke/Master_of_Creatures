package dtu.master_of_creatures.models;

import dtu.master_of_creatures.utilities.enums.CardTypes;

public class CardModel
{
    // Fields
    private final CardTypes card_type;
    private final int health;
    private final int attack;

    private final int cost;

    /**
     * @author Romel (s215212), Danny (s224774)
     */
    public CardModel(CardTypes card_type)
    {
        this.card_type = card_type;
        this.health = card_type.getHealth();
        this.attack = card_type.getAttack();
        this.cost = card_type.getCost();
    }

    /////////////////////////
    // setters and getters //
    /////////////////////////

    /**
     * @author Romel (s215212)
     */
    public CardTypes getCardType() {
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
