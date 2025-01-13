package dtu.master_of_creatures.utilities;

public abstract class Constants
{
    private static final String GAME_VERSION = "v. 0.01";
    private static final int DEFAULT_RESOLUTION_X = 1920;
    private static final int DEFAULT_RESOLUTION_Y = 1080;

    /////////////////////////
    //////// getters ////////
    /////////////////////////

    public static String getGameVersion()
    {
        return GAME_VERSION;
    }

    public static int getDefaultResolutionX()
    {
        return DEFAULT_RESOLUTION_X;
    }

    public static int getDefaultResolutionY()
    {
        return DEFAULT_RESOLUTION_Y;
    }
}
