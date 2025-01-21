package dtu.master_of_creatures.models;

// Project libraries
import dtu.master_of_creatures.controllers.GameController;
import dtu.master_of_creatures.controllers.HostPregameController;
import dtu.master_of_creatures.models.network.ClientModel;
import dtu.master_of_creatures.models.network.HostModel;
import dtu.master_of_creatures.utilities.Constants;
import dtu.master_of_creatures.utilities.enums.GameStates;
import dtu.master_of_creatures.utilities.enums.CommonCardTypes;

// Java libraries
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import javax.swing.Timer;
import java.util.Random;
import java.util.logging.Logger;

public class GameModel implements ActionListener
{
    private static GameStates game_state;
    private BoardModel board_model;
    private PlayerModel player;
    private boolean player_ready = true;
    private boolean opponent_ready;
    private String opponent_player_name;
    private int opponent_player_number;
    private int opponent_player_health;
    private int opponent_cards_remaining;
    private int current_player_number;
    private boolean turn_active;
    private final int[] round_wins;
    private int round_winning_player;
    private int match_winning_player;
    private final Timer game_timer;
    private int turn_time;
    private final HashMap<String, Integer> match_settings;
    private HostModel host;
    private ClientModel client;
    private final String uri = "tcp://localhost:8080/?keep";
    private static final Logger logger = Logger.getLogger(GameModel.class.getName());

    // App
    private GameController game_controller;
    private HostPregameController host_pregame_controller;

    /**
     * @author Danny (s224774), Carl Emil (s224168), Mathias (s224273)
     */

    public GameModel()
    {
        board_model = new BoardModel(this);
        round_wins = new int[2];

        game_timer = new Timer(1000, this); // delay is in milliseconds
        match_settings = new HashMap<>();
    }

    /**
     * @author Danny (s224774), Maria (s195685)
     */
    public void initializeMatchSettings(int round_wins_needed, int turn_time_limit, int health_points, int blood_points, int deck_size, int hand_size, boolean is_host)
    {
        match_settings.put("round wins", round_wins_needed);
        match_settings.put("time limit", turn_time_limit);
        match_settings.put("health points", health_points);
        match_settings.put("blood points", blood_points);
        match_settings.put("deck size", deck_size);
        match_settings.put("hand size", hand_size);
    }

    /**
     * @author Danny (s224774), Maria (s195685)
     */
    public void initializePlayer(String player_name, List<CommonCardTypes> cards_chosen, boolean is_host)
    {
        int player_number = is_host ? 0 : 1;

        player = new PlayerModel(player_name, player_number, cards_chosen, this);

        opponent_player_number = player_number;
        opponent_player_health = match_settings.get("health points");
        opponent_cards_remaining = match_settings.get("deck size") - match_settings.get("hand size");
    }

    public void initializeHostModel()
    {
        host = new HostModel(uri);
    }

    public void initializeClientModel()
    {
        try {
            client = new ClientModel(uri);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    /**
     * @author Danny (s224774), Carl Emil (s224168), Mathias (s224273)
     */
    public void resetGameForNextRound()
    {
        board_model = new BoardModel(this);

        player.resetPlayerForNextRound();
    }

    /**
     * @author Danny (s224774), Maria (s195685), Carl Emil (s224168), Mathias (s224273), Romel (s215212)
     */
    public void startNewRound()
    {
        if(game_state == GameStates.GAME_HALFTIME)
        {
            resetGameForNextRound();

            game_state = GameStates.GAME_ACTIVE;
        }

        current_player_number = -1; // no current player
        round_winning_player = -1; // no round winner

        game_controller.handlePlayerInfoUIs();
        game_controller.handlePlayerCardUIs();

        startTurn();
        game_timer.start();
    }

    /**
     * @author Danny (s224774), Maria (s195685), Carl Emil (s224168), Mathias (s224273), Romel (s215212)
     */
    public void startTurn()
    {
        turn_time = match_settings.get("time limit");

        nextPlayer();

        game_controller.handlePlayerInfoUIs();
        game_controller.handlePlayerButtons();

        turn_active = true;
    }

    /**
     * @author Danny (s224774), Carl Emil (s224168), Mathias (s224273)
     */
    public void nextPlayer()
    {
        if(current_player_number == -1) // first turn
        {
            current_player_number = 0;
        }
        else // subsequent turns
        {
            current_player_number = current_player_number == 0 ? 1 : 0;
        }

        game_controller.setCurrentPlayerNumber(current_player_number);
    }

    /**
     * @author Danny (s224774), Maria (s195685), Carl Emil (s224168), Mathias (s224273), Romel (s215212)
     */
    public void drawFromDeck()
    {
        if(current_player_number == player.getPlayerNumber())
        {
            player.drawFromDeck();
        }

        game_controller.handlePlayerInfoUIs();
    }

    /**
     * @author Danny (s224774), Maria (s195685), Carl Emil (s224168), Mathias (s224273), Romel (s215212)
     */
    public boolean playChosenCard(int hand_index, int field_index)
    {
        if(current_player_number == player.getPlayerNumber())
        {
            CardModel card_played = player.getCardsInHand().get(hand_index);

            if(board_model.summonCreature(card_played, field_index, false))
            {
                player.removeFromHand(card_played);

                game_controller.handlePlayerInfoUIs();

                // Update own player fields on the network
                Runnable runnable = () ->
                {
                    if(current_player_number == 0)
                    {
                        host.updateCard(Constants.PLAYER1_FIELD, field_index, card_played);
                    }
                    else
                    {
                        client.updateCard(Constants.PLAYER2_FIELD, field_index, card_played);
                    }
                };

                Thread place_thread = new Thread(runnable);
                place_thread.start();

                return true;
            }
        }

        return false;
    }

    /**
     * @author Maria (s195685), Danny (s224774), Mathias (s224273), Romel (s215212)
     */
    public void sacrificeChosenCards()
    {
        game_controller.handlePlayerInfoUIs();
    }

    /**
     * @author Danny (s224774), Carl Emil (s224168), Mathias (s224273)
     */
    public void endTurn()
    {
        turn_active = false;

        if(turn_time > 0)
        {
            turn_time = 0;

            game_controller.handleTurnTimeUI(turn_time);
        }

        performPostTurnAttacks();

        // Update opposing player fields on the network
        if(player.getPlayerNumber() != current_player_number)
        {
            Runnable runnable = () ->
            {
                if(player.getPlayerNumber() != current_player_number && player.getPlayerNumber() == 0)
                {
                    System.out.println("for host: " + player.getPlayerNumber());
                    System.out.println("for host: " + current_player_number);

                    board_model.summonCreature(host.queryCard(Constants.PLAYER2_FIELD, 0), 0, true);
                    board_model.summonCreature(host.queryCard(Constants.PLAYER2_FIELD, 1), 1, true);
                    board_model.summonCreature(host.queryCard(Constants.PLAYER2_FIELD, 2), 2, true);
                    board_model.summonCreature(host.queryCard(Constants.PLAYER2_FIELD, 3), 3, true);
                }
                else if(player.getPlayerNumber() != current_player_number && player.getPlayerNumber() == 1)
                {
                    try
                    {
                        System.out.println("for client: " + player.getPlayerNumber());
                        System.out.println("for client: " + current_player_number);

                        board_model.summonCreature(client.queryCard(Constants.PLAYER1_FIELD, 0), 0, true);
                        board_model.summonCreature(client.queryCard(Constants.PLAYER1_FIELD, 1), 1, true);
                        board_model.summonCreature(client.queryCard(Constants.PLAYER1_FIELD, 2), 2, true);
                        board_model.summonCreature(client.queryCard(Constants.PLAYER1_FIELD, 3), 3, true);

                    }
                    catch (InterruptedException e)
                    {
                        throw new RuntimeException(e);
                    }
                }
            };

            Thread place_thread = new Thread(runnable);
            place_thread.start();
        }

        game_controller.handlePlayerInfoUIs();
        game_controller.handlePlayerCardUIs();

        System.out.println(player.getPlayerNumber() + " :" + Arrays.toString(board_model.getPlayer1Lanes()));
        System.out.println(player.getPlayerNumber() + " :" + Arrays.toString(board_model.getPlayer2Lanes()));

        checkRoundMatchOver(false);
    }

    /**
     * @author Danny (s224774), Carl Emil (s224168), Mathias (s224273), Maria (s195685), Romel (s215212)
     */
    public void performPostTurnAttacks()
    {
        CardModel[] player_1_lanes = board_model.getPlayer1Lanes();
        CardModel[] player_2_lanes = board_model.getPlayer2Lanes();

        CardModel attacking_card;
        CardModel attacked_card;

        int post_attack_health;

        for(int lane_index = 0; lane_index < 3; lane_index++)
        {
            attacking_card = current_player_number == 0 ? player_1_lanes[lane_index] : player_2_lanes[lane_index];
            attacked_card = current_player_number == 0 ? player_2_lanes[lane_index] : player_1_lanes[lane_index];

            if(attacking_card != null || attacked_card != null)
            {
                if(attacking_card != null && attacked_card != null)
                {
                    post_attack_health = attacked_card.damageCard(attacking_card.getAttack());

                    if(post_attack_health <= 0) // card dead
                    {
                        board_model.removeCreatureFromField(opponent_player_number, lane_index);

                        if(post_attack_health < 0) // player damaged
                        {
                            // method needed to update opponent player health
                        }
                    }
                }
                else if(attacking_card != null)
                {
                    // method needed to update opponent player health
                }
            }
        }
    }

    /**
     * @author Danny (s224774), Carl Emil (s224168), Mathias (s224273), Maria (s195685), Romel (s215212)
     */
    public void checkRoundMatchOver(boolean player_conceded_round)
    {
        int round_wins_needed = match_settings.get("round wins");

        if(!player_conceded_round)
        {
            if(opponent_player_health <= 0 || (player.getCardsRemaining() == 0 && opponent_cards_remaining == 0))
            {
                decideRoundWinner();

                round_wins[round_winning_player == player.getPlayerNumber() ? 0 : 1]++;

                if(round_wins[0] != round_wins_needed && round_wins[1] != round_wins_needed)
                {
                    game_state = GameStates.GAME_HALFTIME;
                }
                else // a player has gotten the required amount of wins
                {
                    decideMatchWinner();

                    game_timer.stop();

                    game_state = GameStates.GAME_OVER;
                }
            }
        }
        else // current player has conceded the round
        {
            round_winning_player = opponent_player_number;

            round_wins[opponent_player_number]++;

            game_state = GameStates.GAME_HALFTIME;
        }
    }

    /**
     * @author Danny (s224774), Carl Emil (s224168), Mathias (s224273)
     */
    private void decideRoundWinner()
    {
        if(player.getHealthPoints() > opponent_player_health)
        {
            round_winning_player = player.getPlayerNumber();
        }
        else if(player.getHealthPoints() < opponent_player_health)
        {
            round_winning_player = opponent_player_number;
        }
        else // round is a draw
        {
            round_winning_player = -1;
        }
    }

    /**
     * @author Danny (s224774), Carl Emil (s224168), Mathias (s224273)
     */
    private void decideMatchWinner()
    {
        match_winning_player = round_wins[0] == match_settings.get("round wins") ? 0 : 1;
    }

    /**
     * @author Danny (s224774), Maria (s195685), Carl Emil (s224168), Mathias (s224273), Romel (s215212)
     */
    public void actionPerformed(ActionEvent actionEvent) // gets called every second
    {
        if(game_state == GameStates.GAME_ACTIVE)
        {
            turn_time--; // time remaining, therefore subtraction

            if(turn_time >= 0)
            {
                game_controller.handleTurnTimeUI(turn_time);
            }
            else if(turn_time == -2 && turn_active) // slight delay to attack, make sure turn has not already ended
            {
                endTurn();
            }
            else if(turn_time == -5) // delay to give time between turns
            {
                startTurn();

                game_controller.handleTurnTimeUI(turn_time);
            }
        }
    }

    /////////////////////////
    // setters and getters //
    /////////////////////////

    /**
     * @author Danny (s224774)
     */
    public void setGameState(GameStates game_state)
    {
        GameModel.game_state = game_state;
    }

    public void setHostPregameController(HostPregameController host_pregame_controller)
    {
        this.host_pregame_controller = host_pregame_controller;
    }

    /**
     * @author Danny (s224774)
     */
    public void setGameController(GameController game_controller)
    {
        this.game_controller = game_controller;
    }

    public void setPlayerReady(boolean player_ready)
    {
        this.player_ready = player_ready;
    }

    public void setOpponentReady(boolean opponent_ready)
    {
        this.opponent_ready = opponent_ready;
    }

    public void setOpponentPlayerHealth(int opponent_player_health)
    {
        this.opponent_player_health = opponent_player_health;
    }

    public void setOpponentCardRemaining(int opponent_cards_remaining)
    {
        this.opponent_cards_remaining = opponent_cards_remaining;
    }

    public void setOpponentPlayerName(String opponent_player_name)
    {
        this.opponent_player_name = opponent_player_name;
    }

    /**
     * @author Danny (s224774)
     */
    public GameStates getGameState()
    {
        return game_state;
    }

    /**
     * @author Danny (s224774), Carl Emil (s224168), Mathias (s224273)
     */
    public BoardModel getBoardModel()
    {
        return board_model;
    }

    /**
     * @author Danny (s224774), Carl Emil (s224168), Mathias (s224273)
     */
    public PlayerModel getPlayer()
    {
        return player;
    }

    public boolean getPlayerReady()
    {
        return player_ready;
    }

    public boolean getOpponentReady()
    {
        return opponent_ready;
    }

    /**
     * @author Danny (s224774)
     */
    public String getOpponentPlayerName()
    {
        return opponent_player_name;
    }

    /**
     * @author Danny (s224774)
     */
    public int getOpponentPlayerNumber()
    {
        return opponent_player_number;
    }

    /**
     * @author Danny (s224774)
     */
    public int getOpponentPlayerHealth()
    {
        return opponent_player_health;
    }

    /**
     * @author Danny (s224774)
     */
    public int getOpponentCardsRemaining()
    {
        return opponent_cards_remaining;
    }

    public int getCurrentPlayerNumber()
    {
        return current_player_number;
    }

    /**
     * @author Danny (s224774), Carl Emil (s224168), Mathias (s224273), Maria (s195685), Romel (s215212)
     */
    public boolean getTurnActive()
    {
        return turn_active;
    }

    /**
     * @author Danny (s224774)
     */
    public int getRoundWinningPlayer()
    {
        return round_winning_player;
    }

    /**
     * @author Danny (s224774)
     */
    public int getMatchWinningPlayer()
    {
        return match_winning_player;
    }

    /**
     * @author Danny (s224774)
     */
    public int getTurnTime()
    {
        return turn_time;
    }

    /**
     * @author Danny (s224774), Maria (s195685)
     */
    public HashMap<String, Integer> getMatchSettings()
    {
        return match_settings;
    }

    public HostModel getHost()
    {
        return host;
    }

    public ClientModel getClient()
    {
        return client;
    }
}
