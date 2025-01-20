package dtu.master_of_creatures.models;

// Project libraries
import dtu.master_of_creatures.controllers.GameController;
import dtu.master_of_creatures.controllers.HostPregameController;
import dtu.master_of_creatures.models.network.ClientModel;
import dtu.master_of_creatures.models.network.HostModel;
import dtu.master_of_creatures.models.network.ThreadModel;
import dtu.master_of_creatures.utilities.enums.GameStates;
import dtu.master_of_creatures.utilities.enums.PhaseTypes;
import dtu.master_of_creatures.utilities.enums.CommonCardTypes;

// Java libraries
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import javax.swing.Timer;
import java.util.Random;
import java.util.logging.Logger;

public class GameModel implements ActionListener
{
    private static GameStates game_state;
    private static PhaseTypes phase_type;
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
    private int round_time;
    private int match_time;
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
    public void initializeGame(int round_wins_needed, int turn_time_limit, int health_points, int blood_points, int deck_size, int hand_size, boolean is_host)
    {
        // Make backup of game related match settings

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

        player = new PlayerModel(player_name, is_host ? 0 : 1, cards_chosen, this);

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

        round_time = 0;
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

        phase_type = PhaseTypes.PLAYING_PHASE;

        turn_active = true;
    }

    /**
     * @author Danny (s224774), Carl Emil (s224168), Mathias (s224273)
     */
    public void nextPlayer()
    {
        if(current_player_number == -1) // first turn
        {
            current_player_number = new Random().nextInt(0,2);
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

            if(board_model.summonCreature(card_played, field_index))
            {
                player.removeFromHand(card_played);

                game_controller.handlePlayerInfoUIs();

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
     * @author Maria (s195685), Danny (s224774), Mathias (s224273), Romel (s215212)
     */
    public void gambleWithChosenCards(List<CommonCardTypes> cards_gambled_with)
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

        phase_type = PhaseTypes.ATTACK_PHASE;

        performPostTurnAttacks();

        player.updateCardsRemaining(); // needed for opponent too

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

                    player.increaseDamageDone(attacking_card.getAttack()); // damage done to both cards and opponent player
                }
                else if(attacking_card != null)
                {
                    // method needed to update opponent player health

                    player.increaseDamageDone(attacking_card.getAttack()); // damage done to player
                }
            }
        }

        game_controller.handlePlayerInfoUIs();
        game_controller.handlePlayerCardUIs();
    }

    /**
     * @author Maria (s195685), Danny (s224774), Mathias (s224273), Romel (s215212)
     */
    public void addRewardCards(PlayerModel player, List<CommonCardTypes> cards_chosen)
    {
        for(CommonCardTypes card_chosen : cards_chosen)
        {
            player.addToDeck(card_chosen, false); // to starting deck?
        }

        game_controller.handlePlayerInfoUIs();
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
            round_time++;
            match_time++;

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

    /**
     * @author Danny (s224774)
     */
    public GameStates getGameState()
    {
        return game_state;
    }

    /**
     * @author Danny (s224774), Carl Emil (s224168), Mathias (s224273), Maria (s195685), Romel (s215212)
     */
    public PhaseTypes getPhaseType()
    {
        return phase_type;
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
     * @author Danny (s224774)
     */
    public int getRoundTime()
    {
        return round_time;
    }

    /**
     * @author Danny (s224774)
     */
    public int getMatchTime()
    {
        return match_time;
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
