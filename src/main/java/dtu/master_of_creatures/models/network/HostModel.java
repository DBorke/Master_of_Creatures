package dtu.master_of_creatures.models.network;

// Project libraries
import dtu.master_of_creatures.models.CardModel;
import dtu.master_of_creatures.utilities.enums.GameStates;
import static dtu.master_of_creatures.utilities.enums.GameStates.GAME_ACTIVE;
import static dtu.master_of_creatures.utilities.Constants.*;

// Java libraries
import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;

// Other libraries
import org.jspace.*;

public class HostModel
{
    private final SpaceRepository repository;
    private final SequentialSpace player1Field;
    private final SequentialSpace player2Field;
    private final SequentialSpace players;
    private final SequentialSpace creatureAttackedFlag;
    private final SequentialSpace playerAttackedFlag;
    private final SequentialSpace game;
    private final SequentialSpace lock;
    private final SequentialSpace gameSettings;
    private final SequentialSpace playerReady;
    private final SequentialSpace winner;
    private static final Logger logger = Logger.getLogger(HostModel.class.getName());

    public HostModel(String uri)
    {
        // Initialize repository and spaces
        repository = new SpaceRepository();
        player1Field = new SequentialSpace();
        player2Field = new SequentialSpace();
        players = new SequentialSpace();
        creatureAttackedFlag = new SequentialSpace();
        playerAttackedFlag = new SequentialSpace();
        lock = new SequentialSpace();
        game = new SequentialSpace();
        gameSettings = new SequentialSpace();
        playerReady = new SequentialSpace();
        winner = new SequentialSpace();

        // Add spaces to the repository
        repository.add(PLAYER1_FIELD, player1Field);
        repository.add(PLAYER2_FIELD, player2Field);
        repository.add(PLAYERS, players);
        repository.add(CREATURE_ATTACK_FLAG, creatureAttackedFlag);
        repository.add(PLAYER_ATTACK_FLAG, playerAttackedFlag);
        repository.add(LOCK, lock);
        repository.add(GAME, game);
        repository.add(GAME_SETTINGS, gameSettings);
        repository.add(PLAYER_READY, playerReady);
        repository.add(WINNER, winner);

        // Add repository gate for network access
        repository.addGate(uri);
        logger.info("Repository gate added at " + uri);
    }

    public void initializeGameSpace(String player1name, String player2name, int round_wins, int turn_time, int health_points, int blood_points, int deck_size, int hand_size, int player1remaining_cards, int player2remaining_cards, boolean turn_ends)
    {
        try
        {
            // Initialize attack flags
            initializeFlag(creatureAttackedFlag, CREATURE_ATTACK_FLAG, false, false, false);
            initializeFlag(playerAttackedFlag, PLAYER_ATTACK_FLAG, false, false);

            // Initialize player fields
            initializePlayerField(player1Field);
            initializePlayerField(player2Field);

            // Add players
            initializePlayers(players, PLAYER1, player1name, health_points, player1remaining_cards);
            initializePlayers(players, PLAYER2,"Waiting for player 2", health_points, player2remaining_cards);

            // Initialize game
            initializeGame(game, turn_ends, 0, true, GAME_ACTIVE);

            initializeGame_Settings(gameSettings, round_wins, turn_time, health_points, blood_points, deck_size, hand_size, player1name, "Waiting for player 2");
            logger.info("Game space initialized successfully." + gameSettings);

            initializePlayerReady();

            try
            {
                initializeLock();
            }
            catch (InterruptedException e)
            {
                Thread.currentThread().interrupt();
                logger.severe("Failed to initialize lock: " + e.getMessage());
            }

            logger.info("Game space initialized successfully.");
        }
        catch (InterruptedException e)
        {
            Thread.currentThread().interrupt();
            logger.severe("Initialization interrupted: " + e.getMessage());
        }
    }

    private void initializePlayers(SequentialSpace space, String player, String player_name, int player_health, int player_cards_remaining) throws InterruptedException
    {
        if (space.queryp(new ActualField(player)) == null)
        {
            space.put(player, player_name, player_health, player_cards_remaining);
        }
    }

    private void initializeFlag(SequentialSpace space, String flagName, Object... values) throws InterruptedException
    {
        if (space.queryp(new ActualField(flagName)) == null)
        {
            space.put(flagName, values);
        }
    }

    private void initializePlayerField(SequentialSpace field) throws InterruptedException
    {
        for (int i = 0; i < 3; i++)
        {
            if (field.queryp(new ActualField(i)) == null)
            {
                field.put(i, null);
            }
        }
    }

    private  void initializeGame(SequentialSpace game, boolean turn_ends, int round_wins, boolean turn_active, GameStates game_state) throws InterruptedException
    {
        if (game.queryp(new ActualField(GAME)) == null)
        {
            game.put(GAME, turn_ends, round_wins, turn_active, game_state);
        }
    }

    private void initializeGame_Settings(SequentialSpace gameSettings, int round_wins, int turn_time, int health_points, int blood_points, int deck_size, int hand_size, String player1name, String player2name) throws InterruptedException
    {
        if (game.queryp(new ActualField(GAME_SETTINGS)) == null) {
            gameSettings.put(GAME_SETTINGS, round_wins, turn_time, health_points, blood_points, deck_size, hand_size, player1name, player2name);
        }
    }

    private void initializePlayerReady() throws InterruptedException
    {
        if (playerReady.queryp(new ActualField(PLAYER_READY)) == null)
        {
            playerReady.put(PLAYER_READY, true, false);
        }
    }

    private void initializeLock() throws InterruptedException
    {
        logger.info("Initializing lock space...");

        if (lock.queryp(new ActualField(LOCK)) == null)
        {
            lock.put(LOCK, -1);
            logger.info("Lock space initialized with LOCK value.");
        }
    }

    private void initializeWinner() throws InterruptedException
    {
        if (winner.queryp(new ActualField(WINNER)) == null)
        {
            winner.put(WINNER, -1);
        }
    }

    public void updateWinner(int winning_player) throws InterruptedException
    {
        logger.info("Updating winner space...");
        Object[] existingWinner = winner.getp(new ActualField(WINNER), new FormalField(Integer.class));
        winner.put(WINNER, winning_player);
        logger.info("Winner updated to player " + winning_player);
    }

    public int queryWinner()
    {
        logger.info("Querying winneing player...");
        Object[] result = winner.queryp(new ActualField(WINNER), new FormalField(Integer.class));
        if (result == null)
        {
            logger.warning("No winner found in the space.");
            return -1;
        }
        return (int) result[1];
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

    public Object[] queryGameSettings() throws InterruptedException {
        logger.info("Before querying game settings");
        Object[] result = gameSettings.queryp(new ActualField(GAME_SETTINGS), new FormalField(Integer.class), new FormalField(Integer.class), new FormalField(Integer.class), new FormalField(Integer.class), new FormalField(Integer.class), new FormalField(Integer.class), new FormalField(String.class), new FormalField(String.class));
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

    public CardModel queryCard(String playerField, int lane)
    {
        SequentialSpace field = getPlayerField(playerField);
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

    public synchronized Object[] queryPlayer(String playerName)
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

    public boolean[] queryCreatureAttackFlag(int lane)
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

    public boolean[] queryPlayerAttackFlag(int player) //player 1 or 2
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

    public void queryGame()
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
            SequentialSpace field = getPlayerField(playerField);
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

    public void updateCreatureAttackFlag(int lane, boolean state)
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

    public void updatePlayerAttackFlag(int player, boolean state)
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

    private SequentialSpace getPlayerField(String playerField)
    {
        return playerField.equals(PLAYER1_FIELD) ? player1Field : player2Field;
    }

    public void updateCurrentPlayer(int current_player) throws InterruptedException
    {
        logger.info("Updating winner space...");
        Object[] previous_player = lock.getp(new ActualField(LOCK), new FormalField(Integer.class));
        lock.put(LOCK, current_player);
        logger.info("Current player updated to player " + current_player);
    }

    public int queryCurrentPlayer()
    {
        logger.info("Querying current player player...");
        Object[] result = lock.queryp(new ActualField(LOCK), new FormalField(Integer.class));
        if (result == null)
        {
            logger.warning("No winner found in the space.");
            return -1;
        }
        return (int) result[1];
    }


    public void shutdown()
    {
        try
        {
            repository.shutDown();
            logger.info("Repository shut down successfully.");
        }
        catch (Exception e)
        {
            logger.severe("Error during repository shutdown: " + e.getMessage());
        }
    }
}
