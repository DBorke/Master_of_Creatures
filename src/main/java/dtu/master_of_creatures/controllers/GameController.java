package dtu.master_of_creatures.controllers;

// Project libraries
import dtu.master_of_creatures.MasterOfCreaturesApp;
import dtu.master_of_creatures.models.GameModel;
import dtu.master_of_creatures.models.BoardModel;
import dtu.master_of_creatures.models.PlayerModel;
import dtu.master_of_creatures.models.CardModel;
import dtu.master_of_creatures.utilities.enums.GameStates;
import dtu.master_of_creatures.utilities.enums.CommonCardTypes;

// Java libraries
import java.net.URL;
import java.util.ResourceBundle;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
import java.io.IOException;

// JavaFX libraries
import javafx.fxml.Initializable;
import javafx.fxml.FXML;
import javafx.event.ActionEvent;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.text.Text;
import javafx.scene.paint.Color;

public class GameController extends SceneController implements Initializable
{
    // JavaFX
    @FXML
    private Text turn_time;
    @FXML
    private Text player_name;
    @FXML
    private Text player_health;
    @FXML
    private Text player_blood;
    @FXML
    private Text player_remain_deck;
    private List<Button> player_hand_list;
    @FXML
    private Button player_hand_1;
    @FXML
    private Button player_hand_2;
    @FXML
    private Button player_hand_3;
    @FXML
    private Button player_hand_4;
    @FXML
    private Button player_hand_5;
    private List<ImageView> player_hand_image_list;
    @FXML
    private ImageView player_hand_image_1;
    @FXML
    private ImageView player_hand_image_2;
    @FXML
    private ImageView player_hand_image_3;
    @FXML
    private ImageView player_hand_image_4;
    @FXML
    private ImageView player_hand_image_5;
    private List<Button> player_field_list;
    @FXML
    private Button player_field_1;
    @FXML
    private Button player_field_2;
    @FXML
    private Button player_field_3;
    private List<ImageView> player_field_image_list;
    @FXML
    private ImageView player_field_image_1;
    @FXML
    private ImageView player_field_image_2;
    @FXML
    private ImageView player_field_image_3;
    @FXML
    private Button player_draw;
    @FXML
    private Button player_scroll_left;
    @FXML
    private Button player_scroll_right;
    @FXML
    private Text opponent_name;
    @FXML
    private Text opponent_health;
    @FXML
    private Text opponent_remain_deck;
    private List<ImageView> opponent_field_image_list;
    @FXML
    private ImageView opponent_field_image_1;
    @FXML
    private ImageView opponent_field_image_2;
    @FXML
    private ImageView opponent_field_image_3;
    @FXML
    private Button sound_button;
    private CardModel selected_card;
    private int hand_slot_index;

    // Game data
    private final GameModel game_model;
    private final BoardModel board_model;
    private final PlayerModel player;
    private final String opponent_player_name;
    private final int opponent_player_number;
    private int opponent_player_health;
    private int opponent_cards_remaining;
    private int current_player_number;
    private final HashMap<String, Integer> match_settings;

    /**
     * @author Danny (s224774), Maria (s195685)
     */
    public GameController()
    {
        game_model = getGameModel();
        game_model.setGameController(this);

        board_model = game_model.getBoardModel();

        // Fetch player information
        player = game_model.getPlayer();
        opponent_player_name = game_model.getOpponentPlayerName();
        opponent_player_number = game_model.getOpponentPlayerNumber();
        opponent_player_health = game_model.getOpponentPlayerHealth();
        opponent_cards_remaining = game_model.getOpponentCardsRemaining();

        match_settings = game_model.getMatchSettings();
    }

    /**
     * @author Danny (s224774), Maria (s195685), Mathias (s224273), Romel (s215212)
     */
    public void initialize(URL url, ResourceBundle resource_bundle)
    {
        handleTurnTimeUI(match_settings.get("time limit"));

        // Initialize data structures for player
        player_hand_list = new ArrayList<>(List.of(player_hand_1, player_hand_2, player_hand_3, player_hand_4, player_hand_5));
        player_hand_image_list = new ArrayList<>(List.of(player_hand_image_1, player_hand_image_2, player_hand_image_3, player_hand_image_4, player_hand_image_5));
        player_field_list = new ArrayList<>(List.of(player_field_1, player_field_2, player_field_3));
        player_field_image_list = new ArrayList<>(List.of(player_field_image_1, player_field_image_2, player_field_image_3));

        // Initialize data structures for opponent
        opponent_field_image_list = new ArrayList<>(List.of(opponent_field_image_1, opponent_field_image_2, opponent_field_image_3));

        game_model.startNewRound();
        game_model.setGameState(GameStates.GAME_ACTIVE);
    }

    public void updateOpponentPlayerInfo(int opponent_player_health, int opponent_cards_remaining)
    {
        this.opponent_player_health = opponent_player_health;
        this.opponent_cards_remaining = opponent_cards_remaining;
    }

    /**
     * @author Maria (s195685), Danny (s224774), Mathias (s224273), Romel (s215212)
     */
    public void handlePlayerInfoUIs()
    {
        // Update UI information for player
        player_name.setText(current_player_number == player.getPlayerNumber() ? player.getPlayerName() + " (current player)" : player.getPlayerName());
        player_health.setText("Health: " + player.getHealthPoints());
        player_blood.setText("Blood points: " + player.getBloodPoints());
        player_remain_deck.setText("Remaining in deck: " + player.getCurrentDeck().size());

        // Update UI information for opponent
        opponent_name.setText(current_player_number != player.getPlayerNumber() ? opponent_player_name + " (current player)" : opponent_player_name);
        opponent_health.setText("Health: " + opponent_player_health);
        opponent_remain_deck.setText("Remaining in deck: " + opponent_cards_remaining);
    }

    /**
     * @author Maria (s195685), Danny (s224774)
     */
    public void handlePlayerCardUIs()
    {
        updatePlayerHandImages();

        if(current_player_number != -1) // after the first turn
        {
            updatePlayersFieldImages();
        }
    }

    /**
     * @author Danny (s224774), Maria (s195685), Carl Emil (s224168), Mathias (s224273), Romel (s215212)
     */
    public void handlePlayerButtons()
    {
        player_draw.setDisable(current_player_number != player.getPlayerNumber());

        selected_card = null; // reset selection
    }

    /**
     * @author Danny (s224774), Maria (s195685), Mathias (s224273), Romel (s215212)
     */
    public void handleTurnTimeUI(int turn_time_remaining)
    {
        turn_time.setText("Turn time left: " + turn_time_remaining);

        // Change text color depending on time left
        if(turn_time_remaining > 10)
        {
            if(turn_time.getFill().equals(Color.RED))
            {
                turn_time.setFill(Color.BLACK); // default color
            }
        }
        else // time is running out
        {
            turn_time.setFill(Color.RED);
        }
    }

    /**
     * @author Danny (s224774), Maria (s195685), Carl Emil (s224168), Mathias (s224273), Romel (s215212)
     */
    public void playerClickedOnCard(ActionEvent event)
    {
        if(game_model.getTurnActive())
        {
            GameStates game_state = game_model.getGameState();

            if(game_state != GameStates.GAME_HALFTIME && game_state != GameStates.GAME_OVER)
            {
                Button slot = (Button) event.getSource();
                int slot_index;

                if(current_player_number == player.getPlayerNumber())
                {
                    slot_index = player_hand_list.indexOf(slot);

                    if(player.getCardsInHand().size() > slot_index)
                    {
                        hand_slot_index = slot_index;

                        selected_card = player.getCardsInHand().get(slot_index);
                    }
                }
            }
        }
    }

    /**
     * @author Danny (s224774), Maria (s195685), Carl Emil (s224168), Mathias (s224273), Romel (s215212)
     */
    public void playerClickedOnField(ActionEvent event)
    {
        if(game_model.getTurnActive())
        {
            GameStates game_state = game_model.getGameState();

            if(game_state != GameStates.GAME_HALFTIME && game_state != GameStates.GAME_OVER)
            {
                if(selected_card != null) // make sure a card is selected from hand
                {
                    Button slot = (Button) event.getSource();
                    int slot_index;

                    if(current_player_number == player.getPlayerNumber())
                    {
                        slot_index = player_field_list.indexOf(slot);

                        if(game_model.playChosenCard(hand_slot_index, slot_index))
                        {
                            handlePlayerCardUIs();

                            selected_card = null; // reset card selection
                        }
                    }
                }
            }
        }
    }

    /**
     * @author Danny (s224774), Maria (s195685), Carl Emil (s224168), Mathias (s224273), Romel (s215212)
     */
    public void playerDrewFromDeck()
    {
        game_model.drawFromDeck();

        updatePlayerHandImages();

        player_draw.setDisable(true);
    }

    /**
     * @author Danny (s224774), Maria (s195685)
     */
    public void updatePlayerHandImages() // updated for one player at a time
    {
        List<CardModel> player_hand = player.getCardsInHand();
        int player_card_count = player_hand.size();

        for(int hand_index = 0; hand_index < player_hand_list.size(); hand_index++)
        {
            if(hand_index < player_card_count)
            {
                updateCardImage(player.getPlayerNumber(), player_hand.get(hand_index).getCardType(), hand_index, true);
            }
            else // empty slot
            {
                updateCardImage(player.getPlayerNumber(), null, hand_index, true);
            }
        }
    }

    /**
     * @author Danny (s224774), Maria (s195685)
     */
    public void updatePlayersFieldImages() // updated for both players at a time
    {
        CardModel[] player_field_cards = board_model.getPlayer1Lanes();
        CardModel player_field_card;

        CardModel[] opponent_field_cards = board_model.getPlayer2Lanes();
        CardModel opponent_field_card;

        for(int field_index = 0; field_index < player_field_cards.length; field_index++)
        {
            player_field_card = player_field_cards[field_index];
            opponent_field_card = opponent_field_cards[field_index];

            updateCardImage(player.getPlayerNumber(), (player_field_card != null ? player_field_card.getCardType() : null), field_index, false);
            updateCardImage(opponent_player_number, (opponent_field_card != null ? opponent_field_card.getCardType() : null), field_index, false);
        }
    }

    /**
     * @author Danny (s224774), Maria (s195685)
     */
    public void updateCardImage(int player_number, CommonCardTypes card_type, int slot_index, boolean in_hand)
    {
        ImageView slot_image = getSlotImage(player_number, slot_index, in_hand);

        if(card_type != null)
        {
            slot_image.setImage(new Image(String.valueOf(MasterOfCreaturesApp.class.getResource("media/images/" + card_type.name().toLowerCase() + ".png"))));
        }
        else
        {
            if(in_hand)
            {
                slot_image.setImage(null);
            }
            else
            {
                slot_image.setImage(new Image(String.valueOf(MasterOfCreaturesApp.class.getResource("media/images/card_back.png"))));
            }
        }
    }

    /**
     * @author Danny (s224774), Maria (s195685)
     */
    private ImageView getSlotImage(int player_number, int slot_index, boolean in_hand)
    {
        ImageView slot_image;

        if(player_number == player.getPlayerNumber())
        {
            if(in_hand)
            {
                slot_image = player_hand_image_list.get(slot_index);
            }
            else
            {
                slot_image = player_field_image_list.get(slot_index);
            }
        }
        else
        {
            slot_image = opponent_field_image_list.get(slot_index);
        }

        return slot_image;
    }

    /**
     * @author Maria (s195685), Danny (s224774), Mathias (s224273), Romel (s215212)
     */
    public void playerEndedTurn()
    {
        game_model.endTurn();
    }

    /**
     * @author Maria (s195685), Danny (s224774), Mathias (s224273), Romel (s215212)
     */
    public void playerHasConceded()
    {
        game_model.checkRoundMatchOver(true);
    }

    /**
     * @author Danny (s224774)
     */
    public void quitGame() throws IOException
    {
        game_model.setGameState(GameStates.GAME_QUIT);

        goToMenuScene();
    }

    /////////////////////////
    //////// setters ////////
    /////////////////////////

    /**
     * @author Danny (s224774)
     */
    public void setCurrentPlayerNumber(int current_player_number)
    {
        this.current_player_number = current_player_number;
    }
}
