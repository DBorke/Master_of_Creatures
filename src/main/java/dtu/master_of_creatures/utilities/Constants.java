package dtu.master_of_creatures.utilities;

/*
Constants related to match settings are chosen in the HostPregameController, to keep the code simple.
*/

public abstract class Constants
{
    public static final String GAME_SETTINGS = "game_settings"; ;
    private static final String GAME_VERSION = "v. 0.35";
    private static final int APP_RESOLUTION_X = 1920;
    private static final int APP_RESOLUTION_Y = 1080;
    public static final String PLAYER1_FIELD = "player1_field";
    public static final String PLAYER2_FIELD = "player2_field";
    public static final String PLAYERS = "players";
    public static final String PLAYER1 = "player1";
    public static final String PLAYER2 = "player2";
    public static final String CREATURE_ATTACK_FLAG = "AttackFlag";
    public static final String PLAYER_ATTACK_FLAG = "PlayerAttackFlag";
    public static final String LOCK = "LOCKED";
    public static final String GAME = "game";
    public static final String PLAYER_READY = "player_ready";

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
    public static int getAppResolutionX()
    {
        return APP_RESOLUTION_X;
    }

    /**
     * @author Danny (s224774)
     */
    public static int getAppResolutionY()
    {
        return APP_RESOLUTION_Y;
    }
}
