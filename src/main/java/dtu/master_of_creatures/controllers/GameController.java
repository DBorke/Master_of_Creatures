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
import java.util.*;
import java.io.IOException;

// JavaFX libraries
import dtu.master_of_creatures.utilities.enums.MythicalCardTypes;
import javafx.fxml.Initializable;
import javafx.fxml.FXML;
import javafx.event.ActionEvent;
import javafx.scene.control.Button;
import javafx.scene.control.ToggleButton;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.shape.Rectangle;
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
    private Text p1_current_player;
    @FXML
    private Text p2_current_player;
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
    @FXML
    private Button player_hand_6;
    @FXML
    private Button player_hand_7;
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
    @FXML
    private ImageView player_hand_image_6;
    @FXML
    private ImageView player_hand_image_7;
    private List<Button> player_field_list;
    @FXML
    private Button player_field_1;
    @FXML
    private Button player_field_2;
    @FXML
    private Button player_field_3;
    @FXML
    private Button player_field_4;
    private List<ImageView> player_field_image_list;
    @FXML
    private ImageView player_field_image_1;
    @FXML
    private ImageView player_field_image_2;
    @FXML
    private ImageView player_field_image_3;
    @FXML
    private ImageView player_field_image_4;
    @FXML
    private Button player_draw;
    @FXML
    private ToggleButton player_sacrifice;
    @FXML
    private ToggleButton player_gamble;
    @FXML
    private Text opponent_name;
    private List<ImageView> opponent_field_image_list;
    @FXML
    private ImageView opponent_field_image_1;
    @FXML
    private ImageView opponent_field_image_2;
    @FXML
    private ImageView opponent_field_image_3;
    @FXML
    private ImageView opponent_field_image_4;
    @FXML
    private Rectangle match_over_screen;
    @FXML
    private Text won_lost;
    private CardModel selected_card;
    private int hand_slot_index;
    @FXML
    private Button sound;

    // Game data
    private final GameModel game_model;
    private final BoardModel board_model;
    private final PlayerModel player;
    private final String opponent_player_name;
    private int current_player_number;
    private final HashMap<String, Integer> match_settings;

    /**
     * @author Danny (s224774), Maria (s195685)
     */
    public GameController() {
        game_model = getGameModel();
        game_model.setGameController(this);

        board_model = game_model.getBoardModel();

        // Fetch player information
        player = game_model.getPlayer();
        opponent_player_name = game_model.getOpponentPlayerName();

        match_settings = game_model.getMatchSettings();
    }

    /**
     * @author Danny (s224774), Maria (s195685), Mathias (s224273), Romel (s215212)
     */
    public void initialize(URL url, ResourceBundle resource_bundle) {
        handleTurnTimeUI(match_settings.get("time limit"));

        // Initialize data structures for player
        player_hand_list = new ArrayList<>(List.of(player_hand_1, player_hand_2, player_hand_3, player_hand_4, player_hand_5, player_hand_6, player_hand_7));
        player_hand_image_list = new ArrayList<>(List.of(player_hand_image_1, player_hand_image_2, player_hand_image_3, player_hand_image_4, player_hand_image_5, player_hand_image_6, player_hand_image_7));
        player_field_list = new ArrayList<>(List.of(player_field_1, player_field_2, player_field_3, player_field_4));
        player_field_image_list = new ArrayList<>(List.of(player_field_image_1, player_field_image_2, player_field_image_3, player_field_image_4));

        // Initialize data structures for opponent
        opponent_field_image_list = new ArrayList<>(List.of(opponent_field_image_1, opponent_field_image_2, opponent_field_image_3, opponent_field_image_4));

        game_model.startNewRound();
        game_model.setGameState(GameStates.GAME_ACTIVE);

        if(!getSoundUnmuted())
        {
            sound.setText("Sound Off");
        }
    }

    /**
     * @author Maria (s195685), Danny (s224774), Mathias (s224273), Romel (s215212)
     */
    public void handlePlayerInfoUIs()
    {
        // Update UI information for player
        p1_current_player.setText(current_player_number == player.getPlayerNumber() ? "(current player)" : "");
        player_name.setText(player.getPlayerName());
        player_blood.setText("" + player.getBloodPoints());
        player_remain_deck.setText("" + player.getCurrentDeck().size());

        // Update UI information for opponent
        p2_current_player.setText(current_player_number != player.getPlayerNumber() ? "(current player)" : "");
        opponent_name.setText(opponent_player_name);
    }

    /**
     * @author Maria (s195685), Danny (s224774)
     */
    public void handlePlayerCardUIs(boolean update_player) {
        if (update_player) {
            updatePlayerHandImages();

            updatePlayerFieldImages(true);
        } else {
            updateOpponentFieldImages(false);
        }
    }

    /**
     * @author Danny (s224774), Maria (s195685), Carl Emil (s224168), Mathias (s224273), Romel (s215212)
     */
    public void handlePlayerButtons()
    {
        player_draw.setDisable(current_player_number != player.getPlayerNumber());
        player_sacrifice.setDisable(current_player_number != player.getPlayerNumber());
        player_gamble.setDisable(current_player_number != player.getPlayerNumber());

        selected_card = null; // reset selection
    }

    /**
     * @author Danny (s224774), Maria (s195685), Mathias (s224273), Romel (s215212)
     */
    public void handleTurnTimeUI(int turn_time_remaining) {
        turn_time.setText("Turn time left: " + turn_time_remaining);

        // Change text color depending on time left
        if (turn_time_remaining > 10) {
            if (turn_time.getFill().equals(Color.RED)) {
                turn_time.setFill(Color.WHITE); // default color
            }
        } else // time is running out
        {
            turn_time.setFill(Color.RED);
        }
    }

    /**
     * @author Danny (s224774), Maria (s195685), Carl Emil (s224168), Mathias (s224273), Romel (s215212)
     */
    public void playerClickedOnCard(ActionEvent event) {
        if (player.getPlayerNumber() == current_player_number && game_model.getTurnActive()) {
            GameStates game_state = game_model.getGameState();

            if (game_state != GameStates.GAME_OVER) {
                Button slot = (Button) event.getSource();
                int slot_index;

                slot_index = player_hand_list.indexOf(slot);

                if (player.getInSacrificeMode()) {
                    // Get the card in the field at the clicked slot index
                    selected_card = player.getCardsInHand().get(slot_index);

                    // Check if a card exists in the clicked slot
                    if (selected_card != null) {
                        // Trigger the sacrifice

                        game_model.sacrificeCardForBloodPoints(selected_card);
                    }
                }
                else if(player.getInGambleMode())
                {
                    // Get the card in the field at the clicked slot index
                    selected_card = player.getCardsInHand().get(slot_index);

                    // Check if a card exists in the clicked slot
                    if (selected_card != null) {
                        // Trigger the sacrifice

                        game_model.gambleCardForMythicalCard(selected_card);
                    }
                }

                if (player.getCardsInHand().size() > slot_index) {
                    hand_slot_index = slot_index;

                    selected_card = player.getCardsInHand().get(slot_index);
                }
            }
        }
    }

    /**
     * @author Danny (s224774), Maria (s195685), Carl Emil (s224168), Mathias (s224273), Romel (s215212)
     */
    public void playerClickedOnField(ActionEvent event) {
        if (player.getPlayerNumber() == current_player_number && game_model.getTurnActive()) {
            GameStates game_state = game_model.getGameState();

            if (game_state != GameStates.GAME_OVER) {
                if (selected_card != null) // make sure a card is selected from hand
                {
                    Button slot = (Button) event.getSource();
                    int slot_index;

                    slot_index = player_field_list.indexOf(slot);

                    if (game_model.playChosenCard(hand_slot_index, slot_index)) {
                        selected_card = null; // reset card selection
                    }
                }
            }
        }
    }

    /**
     * @author Danny (s224774), Maria (s195685), Carl Emil (s224168), Mathias (s224273), Romel (s215212)
     */
    public void playerDrewFromDeck() {
        game_model.drawFromDeck();

        updatePlayerHandImages();

        player_draw.setDisable(true);
    }

    public void playerSacrificedChosenCard()
    {
        // Change sacrifice status
        if (!player.getInSacrificeMode()) {
            player_sacrifice.setText("Sacrifice: On");
            player.setInSacrificeMode(true);
            player_sacrifice.setSelected(true);

            player_gamble.setText("Gamble: off"); // reset
            player.setInGambleMode(false);
            player_gamble.setSelected(false);
        }
        else
        {
            player_sacrifice.setText("Sacrifice: off");
            player.setInSacrificeMode(false);
            player_sacrifice.setSelected(false);
        }
    }

    public void playerGambledChosenCard()
    {
        // Change gamble status
        if (!player.getInGambleMode())
        {
            player_gamble.setText("Gamble: on");
            player.setInGambleMode(true);
            player_gamble.setSelected(true);

            player_sacrifice.setText("Sacrifice: off"); // reset
            player.setInSacrificeMode(false);
            player_sacrifice.setSelected(false);
        }
        else
        {
            player_gamble.setText("Gamble: off");
            player.setInGambleMode(false);
            player_gamble.setSelected(false);
        }
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
                updateCardImage(player_hand.get(hand_index).getCommonCardType(), player_hand.get(hand_index).getMythicalCardType(), hand_index, true, true);
            }
            else // empty slot
            {
                updateCardImage(null, null, hand_index, true, true);
            }
        }
    }

    /**
     * @author Danny (s224774), Maria (s195685)
     */
    public void updatePlayerFieldImages(boolean update_player) // updated for both players at a time
    {
        CardModel[] player_field_cards = player.getPlayerNumber() == 0 ? board_model.getPlayer1Lanes() : board_model.getPlayer2Lanes();
        CardModel player_field_card;

        for(int field_index = 0; field_index < player_field_cards.length; field_index++)
        {
            player_field_card = player_field_cards[field_index];

            updateCardImage((player_field_card != null ? player_field_card.getCommonCardType() : null), (player_field_card != null ? player_field_card.getMythicalCardType() : null), field_index, false, update_player);
        }
    }

    /**
     * @author Danny (s224774), Maria (s195685)
     */
    public void updateOpponentFieldImages(boolean update_player) // updated for both players at a time
    {
        CardModel[] opponent_field_cards = player.getPlayerNumber() == 0 ? board_model.getPlayer2Lanes() : board_model.getPlayer1Lanes();
        CardModel opponent_field_card;

        for(int field_index = 0; field_index < opponent_field_cards.length; field_index++)
        {
            opponent_field_card = opponent_field_cards[field_index];

            updateCardImage((opponent_field_card != null ? opponent_field_card.getCommonCardType() : null), (opponent_field_card != null ? opponent_field_card.getMythicalCardType() : null), field_index, false, update_player);
        }
    }

    /**
     * @author Danny (s224774), Maria (s195685)
     */
    public void updateCardImage(CommonCardTypes common_card_type, MythicalCardTypes mythical_card_type, int slot_index, boolean in_hand, boolean update_player)
    {
        ImageView slot_image = getSlotImage(slot_index, in_hand, update_player);

        if(common_card_type != null)
        {
            slot_image.setImage(new Image(String.valueOf(MasterOfCreaturesApp.class.getResource("media/images/" + common_card_type.name().toLowerCase() + ".png"))));
        }
        else if(mythical_card_type != null)
        {
            slot_image.setImage(new Image(String.valueOf(MasterOfCreaturesApp.class.getResource("media/images/" + mythical_card_type.name().toLowerCase() + ".png"))));
        }
        else
        {
            if(in_hand)
            {
                slot_image.setImage(null);
            }
            else
            {
                if(update_player)
                {
                    slot_image.setImage(new Image(String.valueOf(MasterOfCreaturesApp.class.getResource("media/images/card_back_1.png"))));
                }
                else
                {
                    slot_image.setImage(new Image(String.valueOf(MasterOfCreaturesApp.class.getResource("media/images/card_back_2.png"))));
                }
            }
        }
    }

    /**
     * @author Danny (s224774), Maria (s195685)
     */
    private ImageView getSlotImage(int slot_index, boolean in_hand, boolean update_player)
    {
        ImageView slot_image;

        if(in_hand)
        {
            slot_image = player_hand_image_list.get(slot_index);
        }
        else
        {
            if(update_player)
            {
                slot_image = player_field_image_list.get(slot_index);
            }
            else
            {
                slot_image = opponent_field_image_list.get(slot_index);
            }
        }

        return slot_image;
    }

    public void playerHasWon(int match_winning_player)
    {
        match_over_screen.setVisible(true);
        match_over_screen.setDisable(false);
        won_lost.setVisible(true);
        won_lost.setDisable(false);

        won_lost.setText(player.getPlayerNumber() == match_winning_player ? "You won the match!" : "You lost the match...");
    }

    /**
     * @author Danny (s224774)
     */
    public void quitGame() throws IOException
    {
        game_model.setGameState(GameStates.GAME_QUIT);

        goToMenuScene();
    }

    public void muteUnmuteSound()
    {
        super.muteUnmuteSound();

        sound.setText(getSoundUnmuted() ? "Sound On" : "Sound Off");
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
