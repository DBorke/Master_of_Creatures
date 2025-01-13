package dtu.master_of_creatures.models;

// Project libraries
import dtu.master_of_creatures.utilities.enums.CreatureTypes;
import dtu.master_of_creatures.utilities.Constants;

// Java libraries
import java.util.List;
import java.util.ArrayList;
import java.util.Random;

public class PlayerModel
{
    private final String player_name;
    private int health_points;
    private int blood_points;
    private final List<CreatureModel> starting_deck;
    private final List<CreatureModel> current_deck;
    private final List<CreatureModel> creatures_in_hand;
    private final CreatureModel[] creatures_in_fields;

    public PlayerModel(String player_name, List<CreatureTypes> creatures_chosen)
    {
        this.player_name = player_name;
        this.health_points = Constants.getStartingPlayerHp();
        this.blood_points = Constants.getStartingBloodPoints();
        starting_deck = new ArrayList<>();
        current_deck = new ArrayList<>();
        creatures_in_hand = new ArrayList<>();
        creatures_in_fields = new CreatureModel[3];

        createDecks(creatures_chosen);
        createHand();
    }

    private void createDecks(List<CreatureTypes> creatures_chosen)
    {
        for(CreatureTypes creature_chosen : creatures_chosen)
        {
            starting_deck.add(new CreatureModel(creature_chosen));
            current_deck.add(new CreatureModel(creature_chosen));
        }
    }

    private void createHand()
    {
        Random randomizer = new Random();
        int creature_index;

        while(creatures_in_hand.size() != Constants.getStartingHandSize())
        {
            creature_index = randomizer.nextInt(0, Constants.getStartingDeckSize());

            creatures_in_hand.add(current_deck.get(creature_index));
            current_deck.remove(creature_index);
        }
    }

    public void drawFromDeck()
    {
        Random randomizer = new Random();

        int creature_index = randomizer.nextInt(0, Constants.getStartingDeckSize());

        creatures_in_hand.add(current_deck.get(creature_index));
        current_deck.remove(creature_index);
    }

    public void changeHealthPoints(int health_change)
    {
        health_points += health_change;
    }

    public void changeBloodPoints(int blood_change)
    {
        blood_points += blood_change;
    }

    public void addToDeck(CreatureTypes creature_to_add, boolean start_deck)
    {
        if(start_deck)
        {
            starting_deck.add(new CreatureModel(creature_to_add));
        }
        else
        {
           current_deck.add(new CreatureModel(creature_to_add));
        }
    }

    public void removeFromDeck(CreatureModel creature_to_remove, boolean start_deck)
    {
        if(start_deck)
        {
            starting_deck.remove(creature_to_remove);
        }
        else
        {
            current_deck.remove(creature_to_remove);
        }
    }

    public void addToHand(CreatureTypes creature_to_add)
    {
        creatures_in_hand.add(new CreatureModel(creature_to_add));
    }

    public void removeFromHand(CreatureModel creature_to_remove)
    {
        creatures_in_hand.remove(creature_to_remove);
    }

    /////////////////////////
    // setters and getters //
    /////////////////////////

    public String getPlayerName()
    {
        return player_name;
    }

    public int getHealthPoints()
    {
        return health_points;
    }

    public int getBloodPoints()
    {
        return blood_points;
    }

    public List<CreatureModel> getStartingDeck()
    {
        return starting_deck;
    }

    public List<CreatureModel> getCurrentDeck()
    {
        return current_deck;
    }

    public List<CreatureModel> getCreaturesInHand()
    {
        return creatures_in_hand;
    }

    public CreatureModel[] getCreaturesInFields()
    {
        return creatures_in_fields;
    }
}
