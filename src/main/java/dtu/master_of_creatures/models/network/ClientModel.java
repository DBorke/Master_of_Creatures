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
    private final RemoteSpace winner;
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
        winner = new RemoteSpace("tcp://localhost:8080/" + WINNER + "?keep");

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

    public void updateWinner(int winning_player) throws InterruptedException
    {
        logger.info("Updating winner space...");
        Object[] existingWinner = winner.getp(new ActualField(WINNER), new FormalField(Integer.class));
        winner.put(WINNER, winning_player);
    }

    public int queryWinner() throws InterruptedException
    {
        logger.info("Querying winner space...");
        Object[] result = winner.queryp(new ActualField(WINNER), new FormalField(Integer.class));
        if (result == null)
        {
            logger.warning("No winner found in the space.");
            return -1;
        }
        return (int) result[1];
    }

    private RemoteSpace getPlayerField(String playerField)
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
}
