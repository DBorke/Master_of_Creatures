package dtu.master_of_creatures.models;

// Project libraries
import dtu.master_of_creatures.utilities.enums.CardTypes;

public class CardModel
{
    private final CardTypes card_type;

    public CardModel(CardTypes card_type)
    {
        this.card_type = card_type;
    }
}
