package dtu.master_of_creatures.models.network;

// Project libraries
import dtu.master_of_creatures.models.CardModel;
import dtu.master_of_creatures.utilities.enums.GameStates;
import static dtu.master_of_creatures.utilities.Constants.*;

// Java libraries
import java.util.List;
import java.util.Arrays;
import java.io.IOException;
import java.util.logging.Logger;

// Other libraries
import org.jspace.*;

public class ClientModel
{
    private final RemoteSpace player1Field;
    private final RemoteSpace player2Field;
    private final RemoteSpace players;
    private final RemoteSpace creatureAttackedFlag;
    private final RemoteSpace playerAttackedFlag;
    private final RemoteSpace game;
    private final RemoteSpace lock;
    private final RemoteSpace gameSettings;
    private final RemoteSpace playerReady;
    private static final Logger logger = Logger.getLogger(ClientModel.class.getName());

    public ClientModel(String uri) throws IOException
    {
        // Connect to the remote spaces
        player1Field = new RemoteSpace("tcp://localhost:8080/" + PLAYER1_FIELD + "?keep");
        player2Field = new RemoteSpace("tcp://localhost:8080/" + PLAYER2_FIELD + "?keep");
        players = new RemoteSpace("tcp://localhost:8080/" + PLAYERS + "?keep");// why players?
        creatureAttackedFlag = new RemoteSpace("tcp://localhost:8080/"+ CREATURE_ATTACK_FLAG + "?keep");
        playerAttackedFlag = new RemoteSpace("tcp://localhost:8080/"+ PLAYER_ATTACK_FLAG + "?keep");
        lock = new RemoteSpace("tcp://localhost:8080/" + LOCK + "?keep");
        game = new RemoteSpace("tcp://localhost:8080/" + GAME + "?keep");
        gameSettings = new RemoteSpace("tcp://localhost:8080/" + GAME_SETTINGS + "?keep");
        playerReady = new RemoteSpace("tcp://localhost:8080/" + PLAYER_READY + "?keep");

        logger.info("Client connected to spaces at " + uri);

        // Add a delay to ensure synchronization with the host
        try
        {
            Thread.sleep(1000); // Adjust the delay as needed
        }
        catch (InterruptedException e)
        {
            Thread.currentThread().interrupt();
            logger.severe("Failed to wait for synchronization: " + e.getMessage());
        }
    }

    public CardModel queryCard(String playerField, int lane) throws InterruptedException
    {
        RemoteSpace field = getPlayerField(playerField);
        logger.info("Before querying card");
        Object[] result = field.queryp(new ActualField(lane), new FormalField(CardModel.class));

        if (result == null)
        {
            logger.warning("No card found in " + playerField + " lane " + lane);

            return null;
        }
        else
        {
            CardModel card = (CardModel) result[1];
            logger.info("Card found: " + card);

            return card;
        }
    }

    public boolean[] queryPlayerReadyFlag() throws InterruptedException
    {
        logger.info("Thread " + Thread.currentThread().getName() + " is querying player ready flag");
        Object[] result = playerReady.queryp(new ActualField(PLAYER_READY), new FormalField(Boolean.class), new FormalField(Boolean.class));
        return new boolean[]{(boolean) result[1], (boolean) result[2]};
    }

    public void updatePlayerReadyFlag(boolean player1_ready, boolean player2_ready) throws InterruptedException
    {
        logger.info("Thread " + Thread.currentThread().getName() + " is updating player ready flag");
        Object[] existingPlayerReady = playerReady.getp(new ActualField(PLAYER_READY), new FormalField(Boolean.class), new FormalField(Boolean.class));

        if (existingPlayerReady == null)
        {
            logger.warning("No existing player ready found.");
        }
        else
        {
            logger.info("Existing player ready removed.");
        }

        playerReady.put(PLAYER_READY, player1_ready, player2_ready);
        logger.info("Player ready updated.");
    }

    public Object[] queryGameSettings() {
        logger.info("Before querying game settings");
        Object[] result = null;
        try {
            result = gameSettings.query(new ActualField(GAME_SETTINGS), new FormalField(Integer.class), new FormalField(Integer.class), new FormalField(Integer.class), new FormalField(Integer.class), new FormalField(Integer.class), new FormalField(Integer.class), new FormalField(String.class), new FormalField(String.class));
        } catch (InterruptedException e) {
            logger.warning("Failed to query game settings: " + e.getMessage());
        }
        return result;
    }

    public void updateGameSettings(int round_wins, int turn_time, int health_points, int blood_points, int deck_size, int hand_size, String player1name, String player2name)
    {
        try
        {
            logger.info("Updating game settings...");
            Object[] existingGameSettings = gameSettings.getp(new ActualField(GAME_SETTINGS), new FormalField(Integer.class), new FormalField(Integer.class), new FormalField(Integer.class), new FormalField(Integer.class), new FormalField(Integer.class), new FormalField(Integer.class), new FormalField(String.class), new FormalField(String.class));

            if (existingGameSettings == null)
            {
                logger.warning("No existing game settings found.");
            }
            else
            {
                logger.info("Existing game settings removed.");
            }

            gameSettings.put(GAME_SETTINGS, round_wins, turn_time, health_points, blood_points, deck_size, hand_size, player1name, player2name);
            logger.info("Game settings updated.");
        }
        catch (InterruptedException e)
        {
            Thread.currentThread().interrupt();
            logger.severe("Failed to update game settings: " + e.getMessage());
        }
    }

    private Object queryPlayerReady() throws InterruptedException
    {
        logger.info("Querying player ready space...");
        Object[] result = playerReady.getp(new ActualField(PLAYER_READY), new FormalField(Boolean.class), new FormalField(Boolean.class));
        return result;
    }

    private void updatePlayerReady(boolean player1_ready, boolean player2_ready) throws InterruptedException
    {
        logger.info("Updating player ready space...");
        Object[] existingPlayerReady = playerReady.getp(new ActualField(PLAYER_READY), new FormalField(Boolean.class), new FormalField(Boolean.class));

        if (existingPlayerReady == null)
        {
            logger.warning("No existing player ready found.");
        }
        else
        {
            logger.info("Existing player ready removed.");
        }

        playerReady.put(PLAYER_READY, player1_ready, player2_ready);
        logger.info("Player ready updated.");
    }


    public synchronized Object[] queryPlayer(String playerName) throws InterruptedException
    {
        logger.info("Before querying player: " + playerName);
        Object[] result = players.queryp(new ActualField(playerName), new FormalField(String.class), new FormalField(Integer.class), new FormalField(Integer.class));

        if (result == null)
        {
            logger.warning("No player found: " + playerName);

            return new Object[]{ "No one",-1, -1};
        }

        logger.info("After querying player: " + playerName);
        logger.info(result[1] + " found: health " + result[2] + ", number of cards remaining " + result[3]);

        return new Object[]{result[1], result[2], result[3]};
    }

    public void queryAllPlayers()
    {
        try
        {
            logger.info("Querying all players in the space:");
            // Query for all tuples with the specified format
            List<Object[]> allPlayers = players.queryAll(
                new FormalField(String.class), // Player identifier (String)
                new FormalField(Integer.class), // Health (int)
                new FormalField(Integer.class)  // Cards remaining (int)
            );

            // Check if the list is empty
            if (allPlayers.isEmpty())
            {
                logger.info("No players found in the space.");
            }
            else
            {
                // Process and log each tuple
                for (Object[] playerTuple : allPlayers)
                {
                    if (playerTuple.length == 3 &&
                        playerTuple[0] instanceof String &&
                        playerTuple[1] instanceof Integer &&
                        playerTuple[2] instanceof Integer)
                    {

                        String playerName = (String) playerTuple[0];
                        int health = (Integer) playerTuple[1];
                        int cardsRemaining = (Integer) playerTuple[2];

                        logger.info(String.format("Player in space: %s, health: %d, cards remaining: %d", playerName, health, cardsRemaining));
                    }
                    else
                    {
                        // Log a warning if the tuple structure is unexpected
                        logger.warning("Unexpected tuple format in space: " + Arrays.toString(playerTuple));
                    }
                }
            }
        }
        catch (Exception e)
        {
            logger.severe("An unexpected error occurred while querying players: " + e.getMessage());
        }
    }

    public boolean[] queryCreatureAttackFlag(int lane) throws InterruptedException
    {
        logger.info("Thread " + Thread.currentThread().getName() + " is querying creature attack flag for lane " + lane);
        Object[] result = creatureAttackedFlag.queryp(new ActualField(CREATURE_ATTACK_FLAG), new FormalField(Boolean.class), new FormalField(Boolean.class), new FormalField(Boolean.class));

        if (result == null)
        {
            logger.warning("No CreatureAttackFlag ");

            return new boolean[]{false, false, false};
        }
        logger.info("After querying CreatureAttackFlag ");
        logger.info("Thread " + Thread.currentThread().getName() + " queried creature attack flag for lane " + lane + " with result: " + Arrays.toString(result));

        return new boolean[]{(boolean) result[1], (boolean) result[2], (boolean) result[3]};
    }

    public boolean[] queryPlayerAttackFlag(int player) throws InterruptedException //player 1 or 2
    {
        logger.info("Thread " + Thread.currentThread().getName() + " is querying player attack flag for player " + player);
        Object[] result = playerAttackedFlag.queryp(new ActualField(PLAYER_ATTACK_FLAG), new FormalField(Boolean.class), new FormalField(Boolean.class));

        if (result == null)
        {
            logger.warning("No PlayerAttackFlag ");

            return new boolean[]{false, false};
        }

        logger.info("After querying PlayerAttackFlag ");
        logger.info("Thread " + Thread.currentThread().getName() + " queried player attack flag for player " + player + " with result: " + Arrays.toString(result));

        return new boolean[]{(boolean) result[1], (boolean) result[2]};
    }

    public void queryGame() throws InterruptedException
    {
        logger.info("Querying game space...");
        Object[] result = game.queryp(new ActualField(GAME), new FormalField(Boolean.class), new FormalField(Integer.class), new FormalField(Boolean.class), new FormalField(GameStates.class));

        if (result == null)
        {
            logger.warning("No game found in the space.");
        }
        else
        {
            logger.info("Game found in the space.");
        }
    }

    public void updateCard(String playerField, int lane, CardModel card)
    {
        try
        {
            RemoteSpace field = getPlayerField(playerField);
            logger.info("Before updating card");
            Object[] existingCard = field.getp(new ActualField(lane), new FormalField(CardModel.class));

            if (existingCard == null)
            {
                logger.warning("No existing card found in " + playerField + " lane " + lane);
            }
            else
            {
                logger.info("Existing card removed from " + playerField + " lane " + lane);
            }

            field.put(lane, card);
            logger.info(String.format("Updated %s lane %d with card: %s", playerField, lane, card));
        }
        catch (InterruptedException e)
        {
            Thread.currentThread().interrupt();
            logger.severe("Failed to update card: " + e.getMessage());
        }
    }

    public synchronized void initialUpdatePlayer(String playerName, String update_player_name, int health, int cardsRemaining)
    {
        try
        {
            logger.info("Before updating player: " + playerName);

            // Attempt to remove the existing player tuple
            Object[] existingPlayer = players.getp(new ActualField(playerName),new FormalField(String.class), new FormalField(Integer.class), new FormalField(Integer.class));

            if (existingPlayer == null)
            {
                logger.warning("No existing player found: " + playerName);
            }
            else
            {
                logger.info("Existing player removed: " + playerName);
            }

            // Add the updated player tuple
            players.put(playerName, update_player_name , health, cardsRemaining);
            logger.info("Updated player: " + playerName);
        }
        catch (InterruptedException e)
        {
            Thread.currentThread().interrupt();
            logger.severe("Failed to update player: " + e.getMessage());
        }
    }

    public synchronized void updatePlayer(String playerName,  int health, int cardsRemaining)
    {
        try
        {
            logger.info("Before updating player: " + playerName);

            // Attempt to remove the existing player tuple
            Object[] existingPlayer = players.getp(new ActualField(playerName),new FormalField(String.class), new FormalField(Integer.class), new FormalField(Integer.class));

            if (existingPlayer == null)
            {
                logger.warning("No existing player found: " + playerName);
            }
            else
            {
                logger.info("Existing player removed: " + playerName);
            }

            // Add the updated player tuple
            players.put(playerName, existingPlayer[1] , health, cardsRemaining);
            logger.info("Updated player: " + playerName);
        }
        catch (InterruptedException e)
        {
            Thread.currentThread().interrupt();
            logger.severe("Failed to update player: " + e.getMessage());
        }
    }

    public void updateCreatureAttackFlag(int lane, boolean state) throws InterruptedException
    {
        logger.info("Thread " + Thread.currentThread().getName() + " is updating creature attack flag for lane " + lane + " to state " + state);
        Object[] result = creatureAttackedFlag.getp(new ActualField(CREATURE_ATTACK_FLAG), new FormalField(Boolean.class), new FormalField(Boolean.class), new FormalField(Boolean.class));
        boolean[] flags = {false, false, false};

        if (result == null)
        {
            logger.warning("No existing CreatureAttackFlag, initializing new flag.");
        }
        else
        {
            logger.info("Existing CreatureAttackFlag  " + Arrays.toString(new boolean[]{(boolean) result[1], (boolean) result[2], (boolean) result[3]}));
            flags[0] = (boolean) result[1];
            flags[1] = (boolean) result[2];
            flags[2] = (boolean) result[3];
        }

        // Update the flag for the specified lane
        flags[lane] = state;

        // Add the updated flag tuple
        try
        {
            creatureAttackedFlag.put(CREATURE_ATTACK_FLAG, flags[0], flags[1], flags[2]);
        }
        catch (InterruptedException e)
        {
            throw new RuntimeException(e);
        }

        logger.info("Updated CreatureAttackFlag to " + Arrays.toString(flags));
        logger.info("Thread " + Thread.currentThread().getName() + " updated creature attack flag for lane " + lane + " to state " + state);
    }

    public void updatePlayerAttackFlag(int player, boolean state) throws InterruptedException
    {
        logger.info("Thread " + Thread.currentThread().getName() + " is updating player attack flag for player" + player + " to state " + state);
        Object[] result = playerAttackedFlag.getp(new ActualField(PLAYER_ATTACK_FLAG), new FormalField(Boolean.class), new FormalField(Boolean.class));
        boolean[] flags = {false, false};

        if (result == null)
        {
            logger.warning("No existing playerAttackedFlag, initializing new flag.");
        }
        else
        {
            logger.info("Existing playerAttackedFlag  " + Arrays.toString(new boolean[]{(boolean) result[1], (boolean) result[2]}));
            flags[0] = (boolean) result[1];
            flags[1] = (boolean) result[2];
        }

        // Update the flag for the specified player
        flags[player] = state;

        // Add the updated flag tuple
        try
        {
            playerAttackedFlag.put(PLAYER_ATTACK_FLAG, flags[0], flags[1]);
        }
        catch (InterruptedException e)
        {
            throw new RuntimeException(e);
        }

        logger.info("Updated playerAttackedFlag to " + Arrays.toString(flags));
        logger.info("Thread " + Thread.currentThread().getName() + " updated player attack flag for player" + player + " to state " + state);
    }

    public void updateGame(boolean turn_ends, int round_wins, boolean turn_active, GameStates game_state)
    {
        try
        {
            logger.info("Updating game space...");
            Object[] existingGame = game.getp(new ActualField(GAME), new FormalField(Boolean.class), new FormalField(Integer.class), new FormalField(Boolean.class), new FormalField(GameStates.class));

            if (existingGame == null)
            {
                logger.warning("No existing game found.");
            }
            else
            {
                logger.info("Existing game removed.");
            }

            game.put(GAME, turn_ends, round_wins, turn_active, game_state);
            logger.info("Game updated.");
        }
        catch (InterruptedException e)
        {
            Thread.currentThread().interrupt();
            logger.severe("Failed to update game: " + e.getMessage());
        }
    }

    private RemoteSpace getPlayerField(String playerField)
    {
        return playerField.equals(PLAYER1_FIELD) ? player1Field : player2Field;
    }

    public void releaseLock()
    {
        try
        {
            logger.info("Attempting to release lock");
            lock.put(LOCK);
            logger.info("Lock successfully released");
            Thread.sleep(200); // Small delay to ensure synchronization
        }
        catch (InterruptedException e)
        {
            Thread.currentThread().interrupt();
            logger.severe("Failed to release lock: " + e.getMessage());
        }
    }

    public Object getLock()
    {
        try
        {
            logger.info("Attempting to acquire lock");
            Object locked = lock.get(new ActualField(LOCK));
            if (locked == null)
            {
                logger.warning("Failed to acquire lock");
                return null;
            }
            logger.info("Lock successfully acquired");
            return locked;
        }
        catch (InterruptedException e)
        {
            Thread.currentThread().interrupt();
            logger.severe("Failed to acquire lock: " + e.getMessage());
            return null;
        }
    }
}
