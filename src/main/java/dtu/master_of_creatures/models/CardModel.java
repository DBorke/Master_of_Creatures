package dtu.master_of_creatures.models;
import dtu.master_of_creatures.utilities.enums.CardTypes;


public class CardModel {
    // Fields
    private final CardTypes card_type;
    private final int health;
    private final int attack;

    private final int cost;

    //Constructor
    public CardModel(CardTypes card_type)
    {
        this.card_type = card_type;
        this.health = card_type.getHealth();
        this.attack = card_type.getAttack();
        this.cost = card_type.getCost();
    }

    // Getters and Setters
    public CardTypes getCreatureType() {
        return card_type;
    }


    public int getHealth() {
        return health;
    }


    public int getAttack() {
        return attack;
    }



    public int getCost() {
        return cost;
    }

    @Override
    public String toString() {
        return String.format("CreatureModel{creature_type='%s', health=%d, attack=%d, cost=%d}", card_type, health, attack, cost);
    }



}
