package dtu.master_of_creatures.controllers;

// Project libraries
import dtu.master_of_creatures.MasterOfCreaturesApp;
import dtu.master_of_creatures.models.GameModel;
import dtu.master_of_creatures.models.BoardModel;
import dtu.master_of_creatures.models.PlayerModel;
import dtu.master_of_creatures.models.CardModel;
import dtu.master_of_creatures.utilities.enums.GameStates;
import dtu.master_of_creatures.utilities.enums.CardTypes;

// Java libraries
import java.net.URL;
import java.util.ResourceBundle;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.List;
import java.io.IOException;

// JavaFX libraries
import javafx.fxml.Initializable;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.text.Text;
import javafx.scene.paint.Color;

public class GameController extends SceneController implements Initializable
{
    // JavaFX
    @FXML
    private Button sound_button;
    @FXML
    private Text turn_time;
    @FXML
    private Text p1_name;
    @FXML
    private Text p1_health;
    @FXML
    private Text p1_blood_points;
    @FXML
    private Text p1_remain_deck;
    @FXML
    private Text p1_remain_hand;
    private List<Button> p1_hand_list;
    @FXML
    private Button p1_hand_1;
    @FXML
    private Button p1_hand_2;
    @FXML
    private Button p1_hand_3;
    @FXML
    private Button p1_hand_4;
    @FXML
    private Button p1_hand_5;
    private List<ImageView> p1_hand_image_list;
    @FXML
    private ImageView p1_hand_image_1;
    @FXML
    private ImageView p1_hand_image_2;
    @FXML
    private ImageView p1_hand_image_3;
    @FXML
    private ImageView p1_hand_image_4;
    @FXML
    private ImageView p1_hand_image_5;
    private List<Button> p1_field_list;
    @FXML
    private Button p1_field_1;
    @FXML
    private Button p1_field_2;
    @FXML
    private Button p1_field_3;
    private List<ImageView> p1_field_image_list;
    @FXML
    private ImageView p1_field_image_1;
    @FXML
    private ImageView p1_field_image_2;
    @FXML
    private ImageView p1_field_image_3;
    @FXML
    private Button p1_scroll_left;
    @FXML
    private Button p1_scroll_right;
    @FXML
    private Button p1_draw;
    @FXML
    private Text p2_name;
    @FXML
    private Text p2_health;
    @FXML
    private Text p2_blood_points;
    @FXML
    private Text p2_remain_deck;
    @FXML
    private Text p2_remain_hand;
    private List<Button> p2_hand_list;
    @FXML
    private Button p2_hand_1;
    @FXML
    private Button p2_hand_2;
    @FXML
    private Button p2_hand_3;
    @FXML
    private Button p2_hand_4;
    @FXML
    private Button p2_hand_5;
    private List<ImageView> p2_hand_image_list;
    @FXML
    private ImageView p2_hand_image_1;
    @FXML
    private ImageView p2_hand_image_2;
    @FXML
    private ImageView p2_hand_image_3;
    @FXML
    private ImageView p2_hand_image_4;
    @FXML
    private ImageView p2_hand_image_5;
    private List<Button> p2_field_list;
    @FXML
    private Button p2_field_1;
    @FXML
    private Button p2_field_2;
    @FXML
    private Button p2_field_3;
    private List<ImageView> p2_field_image_list;
    @FXML
    private ImageView p2_field_image_1;
    @FXML
    private ImageView p2_field_image_2;
    @FXML
    private ImageView p2_field_image_3;
    @FXML
    private Button p2_scroll_left;
    @FXML
    private Button p2_scroll_right;
    @FXML
    private Button p2_draw;

    private int hand_slot_index;
    private CardModel selected_card;

    // Game data
    private final GameModel game_model;
    private final BoardModel board_model;
    private final PlayerModel player_1;
    private final PlayerModel player_2; // temp
    private PlayerModel current_player;
    private final HashMap<String, Integer> match_settings;

    /**
     * @author Danny (s224774), Maria (s195685)
     */
    public GameController()
    {
        game_model = getGameModel();
        game_model.setGameController(this);

        board_model = game_model.getBoardModel();

        PlayerModel[] players = game_model.getPlayers();
        player_1 = players[0];
        player_2 = players[1]; // temp

        match_settings = game_model.getMatchSettings();
    }

    /**
     * @author Danny (s224774), Maria (s195685), Mathias (s224273), Romel (s215212)
     */
    public void initialize(URL url, ResourceBundle resource_bundle)
    {
        handleTurnTimeUI(match_settings.get("time limit"));

        p1_hand_list = new ArrayList<>(List.of(p1_hand_1, p1_hand_2, p1_hand_3, p1_hand_4, p1_hand_5));
        p1_hand_image_list = new ArrayList<>(List.of(p1_hand_image_1, p1_hand_image_2, p1_hand_image_3, p1_hand_image_4, p1_hand_image_5));
        p1_field_list = new ArrayList<>(List.of(p1_field_1, p1_field_2, p1_field_3));
        p1_field_image_list = new ArrayList<>(List.of(p1_field_image_1, p1_field_image_2, p1_field_image_3));
        p2_hand_list = new ArrayList<>(List.of(p2_hand_1, p2_hand_2, p2_hand_3, p2_hand_4, p2_hand_5));
        p2_hand_image_list = new ArrayList<>(List.of(p2_hand_image_1, p2_hand_image_2, p2_hand_image_3, p2_hand_image_4, p2_hand_image_5));
        p2_field_list = new ArrayList<>(List.of(p2_field_1, p2_field_2, p2_field_3));
        p2_field_image_list = new ArrayList<>(List.of(p2_field_image_1, p2_field_image_2, p2_field_image_3));

        game_model.startNewRound();
        game_model.setGameState(GameStates.GAME_ACTIVE);
    }

    /**
     * @author Maria (s195685), Danny (s224774), Mathias (s224273), Romel (s215212)
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
     * @author Maria (s195685), Danny (s224774), Mathias (s224273), Romel (s215212)
     */
    public void handlePlayerInfoUIs()
    {
        // Update UI information for player 1
        p1_name.setText(current_player == player_1 ? player_1.getPlayerName() + " (current player)" : player_1.getPlayerName());
        p1_health.setText("Health: " + player_1.getHealthPoints());
        p1_blood_points.setText("Blood points: " + player_1.getBloodPoints());
        p1_remain_deck.setText("Remaining in deck: " + player_1.getCurrentDeck().size());
        p1_remain_hand.setText("Remaining in hand: " + player_1.getCardsInHand().size());

        // Update UI information for player 2 (temp)
        p2_name.setText(current_player == player_2 ? player_2.getPlayerName() + " (current player)" : player_2.getPlayerName());
        p2_health.setText("Health: " + player_2.getHealthPoints());
        p2_blood_points.setText("Blood points: " + player_2.getBloodPoints());
        p2_remain_deck.setText("Remaining in deck: " + player_2.getCurrentDeck().size());
        p2_remain_hand.setText("Remaining in hand: " + player_2.getCardsInHand().size());
    }

    /**
     * @author Maria (s195685), Danny
     */
    public void handlePlayerCardUIs()
    {
        if(current_player != null) // subsequent turns
        {
            updatePlayerHandImages(current_player);
            updatePlayersFieldImages();
        }
        else // first turn
        {
            updatePlayerHandImages(player_1);
            updatePlayerHandImages(player_2);
        }
    }

    public void handlePlayerButtons()
    {
        if(current_player == player_1)
        {
            p1_draw.setDisable(false);
            p2_draw.setDisable(true);
        }
        else
        {
            p1_draw.setDisable(true);
            p2_draw.setDisable(false);
        }

        selected_card = null;
    }

    /**
     * @author Danny (s224774), Maria (s195685)
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

                if(current_player == player_1)
                {
                    slot_index = p1_hand_list.indexOf(slot);

                    if(slot_index != -1) // make sure card is in player 1's hand
                    {
                        if(player_1.getCardsInHand().size() > slot_index)
                        {
                            hand_slot_index = slot_index;

                            selected_card = player_1.getCardsInHand().get(slot_index);
                        }
                    }
                }
                else // player 2
                {
                    slot_index = p2_hand_list.indexOf(slot);

                    if(slot_index != -1) // make sure card is in player 2's hand
                    {
                        if(player_2.getCardsInHand().size() > slot_index)
                        {
                            hand_slot_index = slot_index;

                            selected_card = player_2.getCardsInHand().get(slot_index);
                        }
                    }
                }
            }
        }
    }

    /**
     * @author Danny (s224774), Maria (s195685)
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

                    if(current_player == player_1)
                    {
                        slot_index = p1_field_list.indexOf(slot);
                    }
                    else // player 2
                    {
                        slot_index = p2_field_list.indexOf(slot);
                    }

                    if(slot_index != -1) // make sure the field belongs to the current player
                    {
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

    public void playerDrewFromDeck()
    {
        game_model.drawFromDeck();

        updatePlayerHandImages(current_player);

        if(current_player == player_1)
        {
            p1_draw.setDisable(true);
        }
        else
        {
            p2_draw.setDisable(true);
        }
    }

    /**
     * @author Danny (s224774), Maria (s195685)
     */
    public void updatePlayerHandImages(PlayerModel player) // updated for one player at a time
    {
        List<CardModel> player_hand = player.getCardsInHand();
        int player_card_count = player_hand.size();

        for(int hand_index = 0; hand_index < p1_hand_list.size(); hand_index++)
        {
            if(hand_index < player_card_count)
            {
                updateCardImage(player, player_hand.get(hand_index).getCardType(), hand_index, true);
            }
            else // empty slot
            {
                updateCardImage(player, null, hand_index, true);
            }
        }
    }

    /**
     * @author Danny (s224774), Maria (s195685)
     */
    public void updatePlayersFieldImages() // updated for both players at a time
    {
        CardModel[] p1_field_cards = board_model.getPlayer1Lanes();
        CardModel p1_card_in_field;

        CardModel[] p2_field_cards = board_model.getPlayer2Lanes();
        CardModel p2_card_in_field;

        for(int field_index = 0; field_index < p1_field_cards.length; field_index++)
        {
            p1_card_in_field = p1_field_cards[field_index];
            p2_card_in_field = p2_field_cards[field_index];

            updateCardImage(player_1, (p1_card_in_field != null ? p1_card_in_field.getCardType() : null), field_index, false);
            updateCardImage(player_2, (p2_card_in_field != null ? p2_card_in_field.getCardType() : null), field_index, false);
        }
    }

    /**
     * @author Danny (s224774), Maria (s195685)
     */
    public void updateCardImage(PlayerModel player, CardTypes card_type, int slot_index, boolean in_hand)
    {
        ImageView slot_image;

        if(player == player_1)
        {
            if(in_hand)
            {
                slot_image = p1_hand_image_list.get(slot_index);
            }
            else
            {
                slot_image = p1_field_image_list.get(slot_index);
            }
        }
        else
        {
            if(in_hand)
            {
                slot_image = p2_hand_image_list.get(slot_index);
            }
            else
            {
                slot_image = p2_field_image_list.get(slot_index);
            }
        }

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

    /**
     * @author Danny (s224774)
     */
    public void muteSound()
    {
        super.muteSound();

        sound_button.setText(getSoundUnmuted() ? "Sound On" : "Sound Off");
    }

    /////////////////////////
    //////// setters ////////
    /////////////////////////

    /**
     * @author Danny (s224774)
     */
    public void setCurrentPlayer(PlayerModel current_player)
    {
        this.current_player = current_player;
    }
}
