package dtu.master_of_creatures.models;

// Project libraries
import dtu.master_of_creatures.controllers.GameController;
import dtu.master_of_creatures.utilities.enums.GameStates;
import dtu.master_of_creatures.utilities.enums.PhaseTypes;
import dtu.master_of_creatures.utilities.enums.CardTypes;

// Java libraries
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.Timer;
import java.util.List;
import java.util.Random;

public class GameModel implements ActionListener
{
    private static GameStates game_state;
    private static PhaseTypes phase_type;
    private BoardModel board_model;
    private final PlayerModel[] players;
    private PlayerModel current_player;
    private final int[] round_wins;
    private PlayerModel round_winning_player;
    private PlayerModel match_winning_player;
    private final Timer game_timer;
    private int turn_time; // reset each turn
    private int round_time; // reset each round
    private int match_time; // reset each match

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
    }

    public void createPlayer(String player_name, List<CardTypes> cards_chosen, boolean is_host)
    {
        /*
        if(is_host)
        {
            players[0] = new PlayerModel(player_name, cards_chosen);
        }
        else
        {
            players[1] = new PlayerModel(player_name, cards_chosen);
        }
        */
    }

    /**
     * @author Danny (s224774), Carl Emil (s224168), Mathias (s224273)
     */
    public void resetGameForNextRound()
    {
        board_model = new BoardModel(this);

        players[0].resetPlayerForNextRound();
        players[1].resetPlayerForNextRound();
    }

    /**
     * @author Danny (s224774), Carl Emil (s224168), Mathias (s224273)
     */
    public void startNewRound()
    {
        round_time = 0;

        // startTurn(); needs to be commented until controller logic is implemented
        game_timer.start();
    }

    /**
     * @author Danny (s224774), Carl Emil (s224168), Mathias (s224273)
     */
    public void startTurn()
    {
        turn_time = 0;

        nextPlayer();
        current_player.resetTurnDamageDone();

        phase_type = PhaseTypes.PLAYING_PHASE;
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

    public void playChosenCard()
    {
        // code for playing a card (called through GameController)
    }

    public void sacrificeChosenCards()
    {
        // code for sacrificing cards (called through GameController)
    }

    public void gambleWithChosenCards(List<CardTypes> cards_gambled_with)
    {
        // code for gambling with cards (called through GameController)
    }

    /**
     * @author Danny (s224774), Carl Emil (s224168), Mathias (s224273)
     */
    public void endTurn()
    {
        phase_type = PhaseTypes.ATTACK_PHASE;

        performPostTurnAttacks();

        checkRoundMatchOver();

        if(game_state != GameStates.GAME_HALFTIME && game_state != GameStates.GAME_OVER)
        {
            startTurn();
        }
    }

    public void performPostTurnAttacks()
    {
        // code for making viable creatures attack each other and the player after each turn
    }

    /**
     * @author Danny (s224774)
     */
    public void addRewardCards(PlayerModel player, List<CardTypes> cards_chosen)
    {
        for(CardTypes card_chosen : cards_chosen)
        {
            player.addToDeck(card_chosen, false); // to starting deck?
        }
    }

    /**
     * @author Danny (s224774), Carl Emil (s224168), Mathias (s224273)
     */
    public void checkRoundMatchOver()
    {
        PlayerModel opposing_player = current_player == players[0] ? players[1] : players[0];

        if(opposing_player.getHealthPoints() <= 0 || (current_player.getCardsRemaining() == 0 && opposing_player.getCardsRemaining() == 0))
        {
            decideRoundWinner(opposing_player);

            round_wins[round_winning_player == players[0] ? 0 : 1]++;

            if(round_wins[0] != 3 && round_wins[1] != 3)
            {
                game_state = GameStates.GAME_HALFTIME;
            }
            else // a player has gotten 3 wins
            {
                decideMatchWinner();

                game_state = GameStates.GAME_OVER;

                game_timer.stop();
            }
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
     * @author Danny (s224774)
     */
    public void actionPerformed(ActionEvent actionEvent) // gets called every second
    {
        // Increase time variables
        turn_time++;
        round_time++;
        match_time++;
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

    public PlayerModel getCurrentPlayer() {return current_player;}

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
}
