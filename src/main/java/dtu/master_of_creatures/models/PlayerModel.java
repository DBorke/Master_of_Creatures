package dtu.master_of_creatures.models;

// Project libraries
import dtu.master_of_creatures.utilities.enums.CardTypes;

// Java libraries
import java.util.List;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

public class PlayerModel
{
    private final String player_name;
    private int health_points;
    private int blood_points;
    private final List<CardModel> starting_deck;
    private List<CardModel> current_deck;
    private final List<CardModel> cards_in_hand;
    private CardModel[] cards_in_fields;
    private int cards_remaining;
    private int turn_damage_done;
    private int round_damage_done;
    private int match_damage_done;
    private final HashMap<String, Integer> match_settings;

    /**
     * @author Danny (s224774), Carl Emil (s224168), Mathias (s224273), Maria (s195685), Romel (s215212)
     */
    public PlayerModel(String player_name, List<CardTypes> cards_chosen, HashMap<String, Integer> match_settings)
    {
        // Set up player stats
        this.player_name = player_name;
        health_points = match_settings.get("health points");
        blood_points = match_settings.get("blood points");
        turn_damage_done = 0;
        round_damage_done = 0;
        match_damage_done = 0;

        // Set up game related variables
        this.match_settings = match_settings;

        // Set up card related variables
        starting_deck = new ArrayList<>();
        current_deck = new ArrayList<>();
        cards_in_hand = new ArrayList<>();
        cards_in_fields = new CardModel[3];

        createDecks(cards_chosen);
        createHand();

        cards_remaining = match_settings.get("deck size");;
    }

    /**
     * @author Danny (s224774), Carl Emil (s224168), Mathias (s224273), Maria (s195685), Romel (s215212)
     */
    public void resetPlayerForNextRound()
    {
        // Reset player stats
        health_points = match_settings.get("health points");
        blood_points = match_settings.get("blood points");
        round_damage_done = 0;

        // Reset card related variables
        current_deck = new ArrayList<>(starting_deck);
        cards_in_hand.clear();
        cards_in_fields = new CardModel[3];

        createHand();

        cards_remaining = starting_deck.size() + match_settings.get("hand size");
    }

    /**
     * @author Danny (s224774), Carl Emil (s224168), Mathias (s224273)
     */
    private void createDecks(List<CardTypes> cards_chosen)
    {
        for(CardTypes card_chosen : cards_chosen)
        {
            starting_deck.add(new CardModel(card_chosen));
            current_deck.add(new CardModel(card_chosen));
        }
    }

    /**
     * @author Danny (s224774), Carl Emil (s224168), Mathias (s224273), Maria (s195685), Romel (s215212)
     */
    private void createHand()
    {
        Random randomizer = new Random();
        int card_index;

        while(cards_in_hand.size() != match_settings.get("hand size") && !current_deck.isEmpty())
        {
            card_index = randomizer.nextInt(0, current_deck.size());

            cards_in_hand.add(current_deck.get(card_index));
            current_deck.remove(card_index);
        }
    }

    /**
     * @author Danny (s224774), Carl Emil (s224168), Mathias (s224273), Maria (s195685), Romel (s215212)
     */
    public void drawFromDeck()
    {
        Random randomizer = new Random();

        int card_index = randomizer.nextInt(0, current_deck.size());

        cards_in_hand.add(current_deck.get(card_index));
        current_deck.remove(card_index);
    }

    /**
     * @author Danny (s224774), Carl Emil (s224168), Mathias (s224273)
     */
    public void changeHealthPoints(int health_change)
    {
        health_points += health_change;
    }

    /**
     * @author Danny (s224774), Carl Emil (s224168), Mathias (s224273)
     */
    public void changeBloodPoints(int blood_change)
    {
        blood_points += blood_change;
    }

    /**
     * @author Danny (s224774), Carl Emil (s224168), Mathias (s224273)
     */
    public void addToDeck(CardTypes card_to_add, boolean to_starting_deck)
    {
        if(to_starting_deck)
        {
            starting_deck.add(new CardModel(card_to_add));
        }
        else
        {
           current_deck.add(new CardModel(card_to_add));
        }
    }

    /**
     * @author Danny (s224774), Carl Emil (s224168), Mathias (s224273)
     */
    public void removeFromDeck(CardModel card_to_remove, boolean from_starting_deck)
    {
        if(from_starting_deck)
        {
            starting_deck.remove(card_to_remove);
        }
        else
        {
            current_deck.remove(card_to_remove);
        }
    }

    /**
     * @author Danny (s224774), Carl Emil (s224168), Mathias (s224273)
     */
    public void addToHand(CardTypes card_to_add, boolean from_deck)
    {
        cards_in_hand.add(new CardModel(card_to_add));
    }

    /**
     * @author Danny (s224774), Carl Emil (s224168), Mathias (s224273)
     */
    public void removeFromHand(CardModel card_to_remove)
    {
        cards_in_hand.remove(card_to_remove);
    }

    /**
     * @author Danny (s224774), Carl Emil (s224168), Mathias (s224273)
     */
    public boolean placeCardInField(int hand_index, int field_index)
    {
        if(cards_in_fields[field_index] == null)
        {
            CardModel card_to_place = cards_in_hand.get(hand_index);

            cards_in_fields[field_index] = card_to_place;
            removeFromHand(card_to_place);

            return true;
        }

        return false;
    }

    /**
     * @author Danny (s224774), Carl Emil (s224168), Mathias (s224273)
     */
    public void removeCardFromField(int field_position)
    {
        cards_in_fields[field_position] = null;
    }

    /**
     * @author Danny (s224774), Carl Emil (s224168), Mathias (s224273), Maria (s195685), Romel (s215212)
     */
    public void updateCardsRemaining()
    {
        cards_remaining = current_deck.size() + cards_in_hand.size();

        for(int card_index = 0; card_index < cards_in_fields.length; card_index++)
        {
            if(cards_in_fields[0] != null)
            {
                cards_remaining++;
            }
        }
    }

    /**
     * @author Danny (s224774)
     */
    public void increaseDamageDone(int damage_done)
    {
        turn_damage_done += damage_done;
        round_damage_done += damage_done;
        match_damage_done += damage_done;
    }

    /**
     * @author Danny (s224774)
     */
    public void resetTurnDamageDone()
    {
        turn_damage_done = 0;
    }

    /////////////////////////
    // setters and getters //
    /////////////////////////

    /**
     * @author Danny (s224774), Carl Emil (s224168), Mathias (s224273)
     */
    public String getPlayerName()
    {
        return player_name;
    }

    /**
     * @author Danny (s224774), Carl Emil (s224168), Mathias (s224273)
     */
    public int getHealthPoints()
    {
        return health_points;
    }

    /**
     * @author Danny (s224774), Carl Emil (s224168), Mathias (s224273)
     */
    public int getBloodPoints()
    {
        return blood_points;
    }

    /**
     * @author Danny (s224774), Carl Emil (s224168), Mathias (s224273)
     */
    public List<CardModel> getStartingDeck()
    {
        return starting_deck;
    }

    /**
     * @author Danny (s224774), Carl Emil (s224168), Mathias (s224273)
     */
    public List<CardModel> getCurrentDeck()
    {
        return current_deck;
    }

    /**
     * @author Danny (s224774), Carl Emil (s224168), Mathias (s224273)
     */
    public List<CardModel> getCardsInHand()
    {
        return cards_in_hand;
    }

    /**
     * @author Danny (s224774), Carl Emil (s224168), Mathias (s224273)
     */
    public CardModel[] getCardsInFields()
    {
        return cards_in_fields;
    }

    /**
     * @author Danny (s224774), Carl Emil (s224168), Mathias (s224273)
     */
    public int getCardsRemaining()
    {
        return cards_remaining;
    }

    /**
     * @author Danny (s224774)
     */
    public int getTurnDamageDone()
    {
        return turn_damage_done;
    }

    /**
     * @author Danny (s224774)
     */
    public int getRoundDamageDone()
    {
        return round_damage_done;
    }

    /**
     * @author Danny (s224774)
     */
    public int getMatchDamageDone()
    {
        return match_damage_done;
    }
}
