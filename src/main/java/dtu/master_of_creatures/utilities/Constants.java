package dtu.master_of_creatures.utilities;

public abstract class Constants
{
    private static final String GAME_VERSION = "v. 0.01";
    private static final int DEFAULT_RESOLUTION_X = 1920;
    private static final int DEFAULT_RESOLUTION_Y = 1080;
    private static final int STARTING_PLAYER_HP = 5;
    private static final int STARTING_BLOOD_POINTS = 0;
    private static final int STARTING_DECK_SIZE = 15;
    private static final int STARTING_HAND_SIZE = 4;

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

    public static int getStartingPlayerHp()
    {
        return STARTING_PLAYER_HP;
    }

    public static int getStartingBloodPoints()
    {
        return STARTING_BLOOD_POINTS;
    }

    public static int getStartingDeckSize()
    {
        return STARTING_DECK_SIZE;
    }

    public static int getStartingHandSize()
    {
        return STARTING_HAND_SIZE;
    }
}
