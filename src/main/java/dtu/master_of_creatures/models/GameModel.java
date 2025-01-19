package dtu.master_of_creatures.models;

// Project libraries
import dtu.master_of_creatures.controllers.GameController;
import dtu.master_of_creatures.utilities.enums.GameStates;
import dtu.master_of_creatures.utilities.enums.PhaseTypes;
import dtu.master_of_creatures.utilities.enums.CommonCardTypes;

// Java libraries
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.util.HashMap;
import java.util.List;
import javax.swing.Timer;
import java.util.Random;

public class GameModel implements ActionListener
{
    private static GameStates game_state;
    private static PhaseTypes phase_type;
    private BoardModel board_model;
    private final PlayerModel[] players; // 0 = host, 1 = joining player
    private PlayerModel current_player;
    private boolean turn_active;
    private final int[] round_wins;
    private PlayerModel round_winning_player;
    private PlayerModel match_winning_player;
    private final Timer game_timer;
    private int turn_time; // reset each turn
    private int round_time; // reset each round
    private int match_time; // reset each match (auto)
    private final HashMap<String, Integer> match_settings;

    // App
    private GameController game_controller;

    /**
     * @author Danny (s224774), Carl Emil (s224168), Mathias (s224273)
     */
    public GameModel()
    {
        board_model = new BoardModel(this);
        players = new PlayerModel[2];
        round_wins = new int[2];

        game_timer = new Timer(1000, this); // delay is in milliseconds

        match_settings = new HashMap<>();
    }

    /**
     * @author Danny (s224774), Maria (s195685)
     */
    public void initializeGame(int round_wins_needed, int turn_time_limit, int health_points, int blood_points, int deck_size, int hand_size)
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
        int player_index = is_host ? 0 : 1;

        players[player_index] = new PlayerModel(player_name, player_index, cards_chosen, this);
    }

    /**
     * @author Danny (s224774), Carl Emil (s224168), Mathias (s224273)
     */
    public void resetGameForNextRound()
    {
        board_model = new BoardModel(this);

        players[0].resetPlayerForNextRound();
        players[1].resetPlayerForNextRound(); // temp

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
        current_player.resetTurnDamageDone();

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
        Random randomizer = new Random();

        if(current_player == null)
        {
            current_player = players[randomizer.nextInt(0,2)];
        }
        else
        {
            current_player = current_player == players[0] ? players[1] : players[0];
        }

        game_controller.setCurrentPlayer(current_player);
    }

    /**
     * @author Danny (s224774), Maria (s195685), Carl Emil (s224168), Mathias (s224273), Romel (s215212)
     */
    public void drawFromDeck()
    {
        current_player.drawFromDeck();

        game_controller.handlePlayerInfoUIs();
    }

    /**
     * @author Danny (s224774), Maria (s195685), Carl Emil (s224168), Mathias (s224273), Romel (s215212)
     */
    public boolean playChosenCard(int hand_index, int field_index)
    {
        CardModel card_played = current_player.getCardsInHand().get(hand_index);

        if(board_model.summonCreature(current_player, card_played, field_index))
        {
            current_player.removeFromHand(card_played);

            game_controller.handlePlayerInfoUIs();

            return true;
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

        players[0].updateCardsRemaining();
        players[1].updateCardsRemaining(); // temp

        checkRoundMatchOver(false);
    }

    /**
     * @author Danny (s224774), Carl Emil (s224168), Mathias (s224273), Maria (s195685), Romel (s215212)
     */
    public void performPostTurnAttacks()
    {
        CardModel[] player_1_lanes = board_model.getPlayer1Lanes();
        CardModel[] player_2_lanes = board_model.getPlayer2Lanes();

        PlayerModel attacking_player = current_player == players[0] ? players[0] : players[1];
        PlayerModel attacked_player = current_player == players[0] ? players[1] : players[0];
        CardModel attacking_card;
        CardModel attacked_card;

        int post_attack_health;

        for(int lane_index = 0; lane_index < 3; lane_index++)
        {
            attacking_card = attacking_player == players[0] ? player_1_lanes[lane_index] : player_2_lanes[lane_index];
            attacked_card = attacking_player == players[0] ? player_2_lanes[lane_index] : player_1_lanes[lane_index];

            if(attacking_card != null || attacked_card != null)
            {
                if(attacking_card != null && attacked_card != null)
                {
                    post_attack_health = attacked_card.damageCard(attacking_card.getAttack());

                    if(post_attack_health <= 0) // card dead
                    {
                        board_model.removeCreatureFromField(attacked_player, lane_index);

                        if(post_attack_health < 0) // player damaged
                        {
                            attacked_player.changeHealthPoints(post_attack_health);
                        }
                    }

                    attacking_player.increaseDamageDone(attacking_card.getAttack()); // damage done to both cards and opponent player
                }
                else if(attacking_card != null)
                {
                    attacked_player.changeHealthPoints(-1 * attacking_card.getAttack());

                    attacking_player.increaseDamageDone(attacking_card.getAttack()); // damage done to player
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
        PlayerModel opposing_player = current_player == players[0] ? players[1] : players[0];

        if(!player_conceded_round)
        {
            if(opposing_player.getHealthPoints() <= 0 || (current_player.getCardsRemaining() == 0 && opposing_player.getCardsRemaining() == 0))
            {
                decideRoundWinner(opposing_player);

                round_wins[round_winning_player == players[0] ? 0 : 1]++;

                if(round_wins[0] != 3 && round_wins[1] != 3)
                {
                    game_state = GameStates.GAME_HALFTIME;
                }
                else // a player has gotten the required amount of wins
                {
                    decideMatchWinner();

                    game_state = GameStates.GAME_OVER;

                    game_timer.stop();
                }
            }
        }
        else // current player has conceded the round
        {
            round_winning_player = opposing_player;

            game_state = GameStates.GAME_HALFTIME;
        }
    }

    /**
     * @author Danny (s224774), Carl Emil (s224168), Mathias (s224273)
     */
    private void decideRoundWinner(PlayerModel opposing_player)
    {
        if(current_player.getHealthPoints() > opposing_player.getHealthPoints())
        {
            round_winning_player = current_player;
        }
        else if(current_player.getHealthPoints() < opposing_player.getHealthPoints())
        {
            round_winning_player = opposing_player;
        }
        else // round is a draw
        {
            round_winning_player = null;
        }
    }

    /**
     * @author Danny (s224774), Carl Emil (s224168), Mathias (s224273)
     */
    private void decideMatchWinner()
    {
        match_winning_player = round_wins[0] == 3 ? players[0] : players[1];
    }

    /**
     * @author Danny (s224774), Maria (s195685), Carl Emil (s224168), Mathias (s224273), Romel (s215212)
     */
    public void actionPerformed(ActionEvent actionEvent) // gets called every second
    {
        if(game_state != GameStates.GAME_HALFTIME && game_state != GameStates.GAME_OVER)
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

    /**
     * @author Danny (s224774)
     */
    public void setGameController(GameController game_controller)
    {
        this.game_controller = game_controller;
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
    public PlayerModel[] getPlayers()
    {
        return players;
    }

    /**
     * @author Maria (s195685), Danny (s224774), Mathias (s224273), Romel (s215212)
     */
    public PlayerModel getCurrentPlayer()
    {
        return current_player;
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
    public PlayerModel getRoundWinningPlayer()
    {
        return round_winning_player;
    }

    /**
     * @author Danny (s224774)
     */
    public PlayerModel getMatchWinningPlayer()
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
}
