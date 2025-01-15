package dtu.master_of_creatures.utilities;

/*
Constants related to match settings options are chosen in the HostPregameController, to keep the code simple.
*/

public abstract class Constants
{
    private static final String GAME_VERSION = "v. 0.12";
    private static final int DEFAULT_RESOLUTION_X = 1920;
    private static final int DEFAULT_RESOLUTION_Y = 1080;

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
}
