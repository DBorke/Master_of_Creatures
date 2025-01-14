package dtu.master_of_creatures.utilities;

public abstract class Constants
{
    private static final String GAME_VERSION = "v. 0.1";
    private static final int DEFAULT_RESOLUTION_X = 1920;
    private static final int DEFAULT_RESOLUTION_Y = 1080;
    private static final int DEFAULT_HEALTH_POINTS = 5; // players
    private static final int DEFAULT_BLOOD_POINTS = 0;
    private static final int DEFAULT_DECK_SIZE = 15;
    private static final int DEFAULT_HAND_SIZE = 2;
    private static final int DEFAULT_TURN_TIME = 60;

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
    public static int getDefaultHealthPoints()
    {
        return DEFAULT_HEALTH_POINTS;
    }

    /**
     * @author Danny (s224774), Carl Emil (s224168), Mathias (s224273)
     */
    public static int getDefaultBloodPoints()
    {
        return DEFAULT_BLOOD_POINTS;
    }

    /**
     * @author Danny (s224774), Carl Emil (s224168), Mathias (s224273)
     */
    public static int getDefaultDeckSize()
    {
        return DEFAULT_DECK_SIZE;
    }

    /**
     * @author Danny (s224774), Carl Emil (s224168), Mathias (s224273)
     */
    public static int getDefaultHandSize()
    {
        return DEFAULT_HAND_SIZE;
    }

    /**
     * @author Danny (s224774), Carl Emil (s224168), Mathias (s224273)
     */
    public static int getDefaultTurnTime()
    {
        return DEFAULT_TURN_TIME;
    }
}
