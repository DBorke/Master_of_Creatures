package dtu.master_of_creatures.models;

// Project libraries
import dtu.master_of_creatures.controllers.GameController;
import dtu.master_of_creatures.models.network.ClientModel;
import dtu.master_of_creatures.models.network.HostModel;
import dtu.master_of_creatures.utilities.Constants;
import dtu.master_of_creatures.utilities.enums.GameStates;
import dtu.master_of_creatures.utilities.enums.CommonCardTypes;
import dtu.master_of_creatures.utilities.enums.MythicalCardTypes;

// Java libraries
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import javax.swing.Timer;

public class GameModel implements ActionListener
{
    private static GameStates game_state;
    private BoardModel board_model;
    private PlayerModel player;
    private boolean player_ready = true;
    private String opponent_player_name;
    private int opponent_player_number;
    private boolean[] opponent_field_flags;
    private boolean opponent_ready;
    private int current_player_number;
    private boolean turn_active;
    private int match_winning_player;
    private final Timer game_timer;
    private int turn_time;
    private final HashMap<String, Integer> match_settings;
    private HostModel host;
    private ClientModel client;
    private final String uri = "tcp://localhost:8080/?keep";

    // App
    private GameController game_controller;

    /**
     * @author Danny (s224774), Carl Emil (s224168), Mathias (s224273)
     */

    public GameModel()
    {
        board_model = new BoardModel(this);

        opponent_field_flags = new boolean[4];

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
        opponent_player_number = is_host ? 1 : 0;

        player = new PlayerModel(player_name, player_number, cards_chosen, this);
    }

    public void initializeHostModel()
    {
        host = new HostModel(uri);
    }

    public void initializeClientModel()
    {
        try
        {
            client = new ClientModel(uri);
        }
        catch (IOException e)
        {
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
        current_player_number = 0; // no current player
        match_winning_player = -1; // no winner

        game_controller.handlePlayerInfoUIs();
        game_controller.handlePlayerCardUIs(true);

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

        allowsCardsToAttack();

        game_controller.handlePlayerInfoUIs();
        game_controller.handlePlayerButtons();

        turn_active = true;
    }

    /**
     * @author Danny (s224774), Carl Emil (s224168), Mathias (s224273)
     */
    public void nextPlayer()
    {
        current_player_number = current_player_number == 0 ? 1 : 0;



        game_controller.setCurrentPlayerNumber(current_player_number);

        Runnable runnable = () ->
        {
            if (player.getPlayerNumber() == 0)
            {
                try {
                    host.updateCurrentPlayer(1);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
            else if (player.getPlayerNumber() == 1)
            {
                try {
                    client.updateCurrentPlayer(0);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        };

        new Thread(runnable).start();

    }

    public void allowsCardsToAttack()
    {
        CardModel[] player_cards = player.getPlayerNumber() == 0 ? board_model.getPlayer1Lanes() : board_model.getPlayer2Lanes();

        for(CardModel card : player_cards)
        {
            if(card != null)
            {
                card.setCanAttack(true);
            }
        }
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

    public void sacrificeCardForBloodPoints(CardModel cardInHand)
    {
        // Update blood points for the current player
        player.changeBloodPoints(cardInHand.getCost());
        game_controller.handlePlayerInfoUIs();

        // Remove the creature from the field for the current player
        player.removeFromHand(cardInHand);

        // Update the UI for player
        game_controller.handlePlayerCardUIs(true);
    }

    public void gambleCardForMythicalCard(CardModel card_in_hand)
    {
        player.removeFromHand(card_in_hand);

        if(card_in_hand.getCost() != 0)
        {
            Random randomizer = new Random();
            int max_probability_range = 9 - card_in_hand.cost;

            if(randomizer.nextInt(0, max_probability_range) == 0)
            {
                MythicalCardTypes[] mythical_card_types = MythicalCardTypes.values();

                player.addToHand(mythical_card_types[randomizer.nextInt(0, mythical_card_types.length)]);
            }
        }

        game_controller.handlePlayerCardUIs(true);
    }

    /**
     * @author Danny (s224774), Maria (s195685), Carl Emil (s224168), Mathias (s224273), Romel (s215212)
     */
    public boolean playChosenCard(int hand_index, int field_index)
    {
        if(current_player_number == player.getPlayerNumber())
        {
            CardModel card_played = player.getCardsInHand().get(hand_index);
            int card_cost = card_played.cost;

            if(player.getBloodPoints() >= card_cost)
            {
                if(board_model.summonCreature(card_played, field_index, false))
                {
                    player.removeFromHand(card_played);

                    player.changeBloodPoints(-1 * card_cost);

                    game_controller.handlePlayerInfoUIs();
                    game_controller.handlePlayerCardUIs(true);

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
        }

        return false;
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

        System.out.println(player.getPlayerNumber() + " :" + Arrays.toString(board_model.getPlayer1Lanes()));
        System.out.println(player.getPlayerNumber() + " :" + Arrays.toString(board_model.getPlayer2Lanes()));

        checkMatchOver(false);
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

        for(int lane_index = 0; lane_index < 4; lane_index++)
        {
            attacking_card = current_player_number == 0 ? player_1_lanes[lane_index] : player_2_lanes[lane_index];
            attacked_card = current_player_number == 0 ? player_2_lanes[lane_index] : player_1_lanes[lane_index];

            if(attacking_card != null && attacking_card.can_attack && attacking_card.getAttack() > 0)
            {
                if(attacked_card != null)
                {
                    post_attack_health = attacked_card.damageCard(attacking_card.getAttack());

                    if(post_attack_health <= 0) // card dead
                    {
                        board_model.removeCreatureFromField(opponent_player_number, lane_index, player.getPlayerNumber() != 0);

                        final int lane_index_2 = lane_index;

                        System.out.println("Attacking card: " + attacking_card);
                        System.out.println("Attacked card: " + attacked_card);

                        // Update own player fields on the network
                        Runnable runnable = () ->
                        {
                            if(opponent_player_number == 0)
                            {
                                host.updateCard(Constants.PLAYER1_FIELD, lane_index_2, null);
                            }
                            else
                            {
                                client.updateCard(Constants.PLAYER2_FIELD, lane_index_2, null);
                            }

                            opponent_field_flags[lane_index_2] = false;
                        };

                        Thread place_thread = new Thread(runnable);
                        place_thread.start();

                        System.out.println("FUCK THIS PIECE OF SHIT!!!!!!!!!!!!!!!");

                        game_controller.handlePlayerCardUIs(true);
                        game_controller.handlePlayerCardUIs(false);
                    }
                }
                else
                {
                    match_winning_player = current_player_number;

                    Runnable runnable = () ->
                    {
                        if (current_player_number == 0){
                            try {
                                getHost().updateWinner(0);
                            } catch (InterruptedException e) {
                                throw new RuntimeException(e);
                            }

                        } else if (current_player_number == 1){
                            try {
                                getClient().updateWinner(1);
                            } catch (InterruptedException e) {
                                throw new RuntimeException(e);
                            }
                        }};
                    Thread thread = new Thread(runnable);
                    thread.start();
                }
            }
        }
    }

    /**
     * @author Danny (s224774), Carl Emil (s224168), Mathias (s224273), Maria (s195685), Romel (s215212)
     */
    public void checkMatchOver(boolean player_conceded_round)
    {
        if(!player_conceded_round)
        {
            if(match_winning_player != -1)
            {
                game_controller.playerHasWon(match_winning_player);
                game_state = GameStates.GAME_OVER;

                System.out.println("DET HER ER DEN FUCKING VINDER AF LORTE SPILLET: " + match_winning_player);
            }
        }
        else // current player has conceded the round
        {
            match_winning_player = opponent_player_number;

            game_controller.playerHasWon(match_winning_player);
            game_state = GameStates.GAME_OVER;
        }
    }

    /**
     * @author Danny (s224774), Maria (s195685), Carl Emil (s224168), Mathias (s224273), Romel (s215212)
     */
    public void actionPerformed(ActionEvent actionEvent) // gets called every second
    {
        if(game_state == GameStates.GAME_ACTIVE)
        {
            turn_time--; // time remaining, therefore subtraction

            // Update opposing player fields on the network
            if(player.getPlayerNumber() != current_player_number)
            {
                Runnable runnable = () ->
                {
                    if(player.getPlayerNumber() != current_player_number && player.getPlayerNumber() == 0)
                    {
                        System.out.println("for host: " + player.getPlayerNumber());
                        System.out.println("for host: " + current_player_number);

                        CardModel queried_card = host.queryCard(Constants.PLAYER2_FIELD, 0);

                        if(queried_card != null && !opponent_field_flags[0])
                        {
                            board_model.summonCreature(queried_card, 0, true);

                            opponent_field_flags[0] = true;
                        }

                        queried_card = host.queryCard(Constants.PLAYER2_FIELD, 1);

                        if(queried_card != null && !opponent_field_flags[1])
                        {
                            board_model.summonCreature(queried_card, 1, true);

                            opponent_field_flags[1] = true;
                        }

                        queried_card = host.queryCard(Constants.PLAYER2_FIELD, 2);

                        if(queried_card != null && !opponent_field_flags[2])
                        {
                            board_model.summonCreature(queried_card, 2, true);

                            opponent_field_flags[2] = true;
                        }

                        queried_card = host.queryCard(Constants.PLAYER2_FIELD, 3);

                        if(queried_card != null && !opponent_field_flags[3])
                        {
                            board_model.summonCreature(queried_card, 3, true);

                            opponent_field_flags[3] = true;
                        }

                        if (getHost().queryCurrentPlayer() != current_player_number)
                        {
                            System.out.println("Opposing playuer's turn is over");
                            endTurn();
                        }


                        if (getHost().queryWinner() == 1)
                        {
                            System.out.println("Opposing playuer's won");

                            match_winning_player = 1;

                            game_controller.playerHasWon(match_winning_player);
                            game_state = GameStates.GAME_OVER;
                        }


                    }
                    else if(player.getPlayerNumber() != current_player_number && player.getPlayerNumber() == 1)
                    {
                        try
                        {
                            System.out.println("for client: " + player.getPlayerNumber());
                            System.out.println("for client: " + current_player_number);

                            CardModel queried_card = client.queryCard(Constants.PLAYER1_FIELD, 0);

                            if(queried_card != null && !opponent_field_flags[0])
                            {
                                board_model.summonCreature(queried_card, 0, true);

                                opponent_field_flags[0] = true;
                            }

                            queried_card = client.queryCard(Constants.PLAYER1_FIELD, 1);

                            if(queried_card != null && !opponent_field_flags[1])
                            {
                                board_model.summonCreature(queried_card, 1, true);

                                opponent_field_flags[1] = true;
                            }

                            queried_card = client.queryCard(Constants.PLAYER1_FIELD, 2);

                            if(queried_card != null && !opponent_field_flags[2])
                            {
                                board_model.summonCreature(queried_card, 2, true);

                                opponent_field_flags[2] = true;
                            }

                            queried_card = client.queryCard(Constants.PLAYER1_FIELD, 3);

                            if(queried_card != null && !opponent_field_flags[3])
                            {
                                board_model.summonCreature(queried_card, 3, true);

                                opponent_field_flags[3] = true;
                            }

                            System.out.println("Winner:" + getClient().queryWinner());

                            if (getClient().queryWinner() == 0)
                            {
                                System.out.println("Opposing playuer's won");

                                match_winning_player = 0;

                                game_controller.playerHasWon(match_winning_player);
                                game_state = GameStates.GAME_OVER;
                            }


                            if (getClient().queryCurrentPlayer() != current_player_number)
                            {
                                System.out.println("Opposing playuer's turn is over");
                                endTurn();
                            }
                        }
                        catch (InterruptedException e)
                        {
                            throw new RuntimeException(e);
                        }
                    }
                };

                Thread place_thread = new Thread(runnable);
                place_thread.start();

                game_controller.handlePlayerCardUIs(false);
            }

            if(turn_time >= 0)
            {
                game_controller.handleTurnTimeUI(turn_time);
            }
            else if(turn_time == -1 && turn_active) // slight delay to attack, make sure turn has not already ended
            {
                endTurn();
            }
            else if(turn_time == -3) // delay to give time between turns
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

    public void setPlayerReady(boolean player_ready)
    {
        this.player_ready = player_ready;
    }

    public void setOpponentReady(boolean opponent_ready)
    {
        this.opponent_ready = opponent_ready;
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
