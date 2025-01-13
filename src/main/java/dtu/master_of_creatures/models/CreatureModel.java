package dtu.master_of_creatures.models;

// Project libraries
import dtu.master_of_creatures.utilities.enums.CreatureTypes;

public class CreatureModel
{
    private final CreatureTypes creature_type;

    public CreatureModel(CreatureTypes creature_type)
    {
        this.creature_type = creature_type;
    }
}
