package dtu.master_of_creatures.utilities;

public abstract class Constants
{
    private static final String GAME_VERSION = "v. 0.1";
    private static final int DEFAULT_RESOLUTION_X = 1920;
    private static final int DEFAULT_RESOLUTION_Y = 1080;
    private static final int STARTING_HEALTH_POINTS = 5; // players
    private static final int STARTING_BLOOD_POINTS = 0;
    private static final int STARTING_DECK_SIZE = 15;
    private static final int STARTING_HAND_SIZE = 4;
    private static final int STARTING_CARDS_TOTAL = STARTING_DECK_SIZE + STARTING_HAND_SIZE;
    private static final int STANDARD_TURN_TIME = 60;

    /////////////////////////
    //////// getters ////////
    /////////////////////////

    /**
     * @author Danny (s224774)
     */
    public static String getGameVersion()
    {
        return GAME_VERSION;
    }

    /**
     * @author Danny (s224774)
     */
    public static int getDefaultResolutionX()
    {
        return DEFAULT_RESOLUTION_X;
    }

    /**
     * @author Danny (s224774)
     */
    public static int getDefaultResolutionY()
    {
        return DEFAULT_RESOLUTION_Y;
    }

    /**
     * @author Danny (s224774), Carl Emil (s224168), Mathias (s224273)
     */
    public static int getStartingHealthPoints()
    {
        return STARTING_HEALTH_POINTS;
    }

    /**
     * @author Danny (s224774), Carl Emil (s224168), Mathias (s224273)
     */
    public static int getStartingBloodPoints()
    {
        return STARTING_BLOOD_POINTS;
    }

    /**
     * @author Danny (s224774), Carl Emil (s224168), Mathias (s224273)
     */
    public static int getStartingDeckSize()
    {
        return STARTING_DECK_SIZE;
    }

    /**
     * @author Danny (s224774), Carl Emil (s224168), Mathias (s224273)
     */
    public static int getStartingHandSize()
    {
        return STARTING_HAND_SIZE;
    }

    /**
     * @author Danny (s224774), Carl Emil (s224168), Mathias (s224273)
     */
    public static int getStartingCardsTotal()
    {
        return STARTING_CARDS_TOTAL;
    }

    /**
     * @author Danny (s224774), Carl Emil (s224168), Mathias (s224273)
     */
    public static int getStandardTurnTime()
    {
        return STANDARD_TURN_TIME;
    }
}
