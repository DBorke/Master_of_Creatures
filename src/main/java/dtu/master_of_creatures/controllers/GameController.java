package dtu.master_of_creatures.controllers;

// Project libraries
import dtu.master_of_creatures.MasterOfCreaturesApp;
import dtu.master_of_creatures.models.CardModel;
import dtu.master_of_creatures.models.GameModel;
import dtu.master_of_creatures.models.PlayerModel;
import dtu.master_of_creatures.utilities.enums.CardTypes;
import dtu.master_of_creatures.utilities.enums.GameStates;

// Java libraries
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.ResourceBundle;
import java.io.IOException;

// JavaFX libraries
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;

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
    private List<ImageView> p1_hand_list;
    @FXML
    private ImageView p1_hand_1;
    @FXML
    private ImageView p1_hand_2;
    @FXML
    private ImageView p1_hand_3;
    @FXML
    private ImageView p1_hand_4;
    @FXML
    private ImageView p1_hand_5;
    @FXML
    private ImageView p1_field_1;
    @FXML
    private ImageView p1_field_2;
    @FXML
    private ImageView p1_field_3;
    @FXML
    private Button p1_scroll_left;
    @FXML
    private Button p1_scroll_right;
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
    private List<ImageView> p2_hand_list;
    @FXML
    private ImageView p2_hand_1;
    @FXML
    private ImageView p2_hand_2;
    @FXML
    private ImageView p2_hand_3;
    @FXML
    private ImageView p2_hand_4;
    @FXML
    private ImageView p2_hand_5;
    @FXML
    private ImageView p2_field_1;
    @FXML
    private ImageView p2_field_2;
    @FXML
    private ImageView p2_field_3;
    @FXML
    private Button p2_scroll_left;
    @FXML
    private Button p2_scroll_right;
    @FXML
    private ImageView selected_card;

    // Game data
    private final GameModel game_model;
    private PlayerModel current_player;

    /**
     * @author Danny (s224774)
     */
    public GameController()
    {
        game_model = getGameModel();
        game_model.setGameController(this);
    }

    /**
     * @author Danny (s224774), Maria (s195685), Mathias (s224273), Romel (s215212)
     */
    public void initialize(URL url, ResourceBundle resource_bundle)
    {
        handleTurnTimeUI(game_model.getTurnTimeLimit());

        p1_hand_list = new ArrayList<>(List.of(p1_hand_1, p1_hand_2, p1_hand_3, p1_hand_4, p1_hand_5));
        p2_hand_list = new ArrayList<>(List.of(p2_hand_1, p2_hand_2, p2_hand_3, p2_hand_4, p2_hand_5));

        game_model.startNewRound();
        game_model.setGameState(GameStates.GAME_ACTIVE);
    }

    public void initializePlayerHand(PlayerModel[] players)
    {
        PlayerModel player_1 = players[0];
        PlayerModel player_2 = players[1];

        List<CardModel> p1_hand = player_1.getCardsInHand();
        List<CardModel> p2_hand = player_2.getCardsInHand();

        int hand_index = 0;
        for (CardModel card : p1_hand)
        {
            if (card.getCreatureType() == CardTypes.RABBIT)
            {
                p1_hand_list.get(hand_index).setImage(new Image(String.valueOf(MasterOfCreaturesApp.class.getResource("media/images/rabbit.png"))));

            }
            else if (card.getCreatureType() == CardTypes.WOLF)
            {
                p1_hand_list.get(hand_index).setImage(new Image(String.valueOf(MasterOfCreaturesApp.class.getResource("media/images/wolf.png"))));
            }
            else if (card.getCreatureType() == CardTypes.DRAGON)
            {
                p1_hand_list.get(hand_index).setImage(new Image(String.valueOf(MasterOfCreaturesApp.class.getResource("media/images/dragon.png"))));
            }
            else
            {
                p1_hand_list.get(hand_index).setImage(new Image(String.valueOf(MasterOfCreaturesApp.class.getResource("media/images/filler.png"))));
            }
            hand_index++;
        }

        hand_index = 0;
        for (CardModel card : p2_hand)
        {
            if (card.getCreatureType() == CardTypes.RABBIT)
            {
                p2_hand_list.get(hand_index).setImage(new Image(String.valueOf(MasterOfCreaturesApp.class.getResource("media/images/rabbit.png"))));
            }
            else if (card.getCreatureType() == CardTypes.WOLF)
            {
                p2_hand_list.get(hand_index).setImage(new Image(String.valueOf(MasterOfCreaturesApp.class.getResource("media/images/wolf.png"))));
            }
            else if (card.getCreatureType() == CardTypes.DRAGON)
            {
                p2_hand_list.get(hand_index).setImage(new Image(String.valueOf(MasterOfCreaturesApp.class.getResource("media/images/dragon.png"))));
            }
            else
            {
                p2_hand_list.get(hand_index).setImage(new Image(String.valueOf(MasterOfCreaturesApp.class.getResource("media/images/filler.png"))));
            }
            hand_index++;
        }

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
    public void handlePlayerInfoUIs(PlayerModel[] players)
    {
        PlayerModel player_1 = players[0];
        PlayerModel player_2 = players[1];

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

    public void playerSelectedCard(ActionEvent event)
    {
        selected_card = (ImageView) event.getSource();

    }

    public void playerPlacedCard1(ActionEvent event)
    {

        ImageView field = (ImageView) event.getSource();

        if (selected_card != null)
        {
            field.setImage(selected_card.getImage());
            current_player.placeCardInField(current_player.getCardsInHand().get(p1_hand_list.indexOf(selected_card)),0);
        }

    }

    public void playerPlacedCard2(ActionEvent event)
    {

        ImageView field = (ImageView) event.getSource();

        if (selected_card != null)
        {
            field.setImage(selected_card.getImage());
            current_player.placeCardInField(current_player.getCardsInHand().get(p1_hand_list.indexOf(selected_card)),1);
        }

    }

    public void playerPlacedCard3(ActionEvent event)
    {

        ImageView field = (ImageView) event.getSource();

        if (selected_card != null)
        {
            field.setImage(selected_card.getImage());
            current_player.placeCardInField(current_player.getCardsInHand().get(p1_hand_list.indexOf(selected_card)),2);
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
