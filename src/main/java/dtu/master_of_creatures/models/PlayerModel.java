package dtu.master_of_creatures.models;

// Project libraries
import dtu.master_of_creatures.utilities.enums.CommonCardTypes;
import dtu.master_of_creatures.utilities.enums.MythicalCardTypes;

// Java libraries
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
import java.util.Random;

public class PlayerModel
{
    private final String player_name;
    private final int player_number;
    private final int health_points;
    private int blood_points;
    private final List<CardModel> current_deck;
    private final List<CardModel> cards_in_hand;
    private int cards_remaining;
    private boolean in_sacrifice_mode;
    private boolean in_gamble_mode;
    private final HashMap<String, Integer> match_settings;

    /**
     * @author Danny (s224774), Carl Emil (s224168), Mathias (s224273), Maria (s195685), Romel (s215212)
     */
    public PlayerModel(String player_name, int player_number, List<CommonCardTypes> cards_chosen, GameModel game_model)
    {
        // Set up game related variables
        this.match_settings = game_model.getMatchSettings();

        // Set up player stats
        this.player_name = player_name;
        this.player_number = player_number;
        health_points = match_settings.get("health points");
        blood_points = match_settings.get("blood points");

        // Set up card related variables
        current_deck = new ArrayList<>();
        cards_in_hand = new ArrayList<>();

        createDecks(cards_chosen);
        createHand();

        cards_remaining = match_settings.get("deck size");;
    }

    /**
     * @author Danny (s224774), Carl Emil (s224168), Mathias (s224273)
     */
    private void createDecks(List<CommonCardTypes> cards_chosen)
    {
        for(CommonCardTypes card_chosen : cards_chosen)
        {
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
    public void changeBloodPoints(int blood_change)
    {
        blood_points += blood_change;
    }

    /**
     * @author Danny (s224774), Carl Emil (s224168), Mathias (s224273)
     */
    public void addToHand(MythicalCardTypes card_to_add)
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

    /////////////////////////
    // setters and getters //
    /////////////////////////

    /**
     * @author Carl Emil (s224168), Mathias (s224273)
     */
    public void setInSacrificeMode(boolean inSacrificeMode) {
        this.in_sacrifice_mode = inSacrificeMode;
    }

    /**
     * @author Carl Emil (s224168), Mathias (s224273), Danny (s224774)
     */
    public void setInGambleMode(boolean in_gamble_mode) {
        this.in_gamble_mode = in_gamble_mode;
    }

    /**
     * @author Danny (s224774), Carl Emil (s224168), Mathias (s224273)
     */
    public String getPlayerName()
    {
        return player_name;
    }

    /**
     * @author Danny (s224774)
     */
    public int getPlayerNumber()
    {
        return player_number;
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
    public int getCardsRemaining()
    {
        return cards_remaining;
    }

    /**
     * @author Carl Emil (s224168), Mathias (s224273)
     */
    public boolean getInSacrificeMode() {
        return in_sacrifice_mode;
    }

    public boolean getInGambleMode() {
        return in_gamble_mode;
    }
}
