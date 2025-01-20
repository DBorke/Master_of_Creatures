package dtu.master_of_creatures.controllers;

// Project libraries
import dtu.master_of_creatures.models.GameModel;
import dtu.master_of_creatures.utilities.enums.GameStates;
import dtu.master_of_creatures.utilities.enums.CommonCardTypes;

// Java libraries
import java.net.URL;
import java.util.ResourceBundle;
import java.util.List;
import java.util.ArrayList;
import java.io.IOException;

// JavaFX libraries
import javafx.fxml.Initializable;
import javafx.fxml.FXML;
import javafx.scene.layout.GridPane;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;

public class HostPregameController extends SceneController implements Initializable
{
    // JavaFX
    @FXML
    private TextField player_name;
    @FXML
    private Text opponent_name;
    @FXML
    private ComboBox<Integer> round_wins;
    @FXML
    private ComboBox<String> turn_time;
    @FXML
    private ComboBox<Integer> health_points;
    @FXML
    private ComboBox<Integer> blood_points;
    @FXML
    private ComboBox<Integer> deck_size;
    private int deck_size_selected;
    @FXML
    private ComboBox<Integer> hand_size;
    @FXML
    private GridPane deck_grid;
    private Button[] deck_grid_nodes;
    private int deck_grid_rows;
    private int deck_grid_columns;
    private int deck_grid_cells;
    private double deck_grid_cell_size;
    private final List<CommonCardTypes> player_cards;
    @FXML
    private Text cards_chosen_count;
    @FXML
    private Button sound_button;

    // Game data
    private final GameModel game_model;
    private final CommonCardTypes[] card_types_available;
    private boolean opponent_ready;

    /**
     * @author Danny (s224774), Mathias (s224273), Maria (s195685), Romel (s215212)
     */
    public HostPregameController()
    {
        game_model = getGameModel();

        player_cards = new ArrayList<>();
        card_types_available = CommonCardTypes.values();
    }

    /**
     * @author Danny (s224774), Carl Emil (s224168), Mathias (s224273), Maria (s195685), Romel (s215212)
     */
    public void initialize(URL url, ResourceBundle resource_bundle)
    {
        game_model.setGameState(GameStates.GAME_SETUP);

        // Gather information about the deck grid
        deck_grid_rows = deck_grid.getRowCount();
        deck_grid_columns = deck_grid.getColumnCount();
        deck_grid_cells = deck_grid_rows * deck_grid_columns;
        deck_grid_cell_size =  deck_grid.getPrefWidth(); // assuming grid cell symmetry
        deck_grid_nodes = new Button[deck_grid_cells];

        // Add and set up GUI option elements
        round_wins.getItems().addAll(1, 2, 3, 4, 5); // match setting options
        turn_time.getItems().addAll("30 seconds", "60 seconds", "90 seconds", "120 seconds", "Unlimited");
        health_points.getItems().addAll(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);
        blood_points.getItems().addAll(0, 1, 2, 3);
        deck_size.getItems().addAll(5, 10, 15, 20, 25);
        hand_size.getItems().addAll(3, 4, 5, 6, 7, 8, 9, 10);
        defaultMatchSettings();

        // Initialize deck grid and deck grid contents
        initializeDeckGrid();
        initializeCardsInDeckGrid();
    }

    /**
     * @author Danny (s224774), Mathias (s224273), Maria (s195685), Romel (s215212)
     */
    public void initializeDeckGrid()
    {
        for(int row = 0; row < deck_grid_rows; row++)
        {
            for (int column = 0; column < deck_grid_columns; column++)
            {
                // Node setup
                Button grid_node = new Button();
                grid_node.setPrefSize(deck_grid_cell_size, deck_grid_cell_size);
                grid_node.setFocusTraversable(false);

                grid_node.setOnMouseClicked(event -> playerClickedOnDeckGrid(grid_node)); // needed to detect player deck choices

                // Grid setup
                deck_grid.add(grid_node, column, row);
                deck_grid_nodes[convertRowColumnToGridIndex(row, column)] = grid_node;
            }
        }
    }

    /**
     * @author Danny (s224774), Mathias (s224273), Maria (s195685), Romel (s215212)
     */
    public void initializeCardsInDeckGrid()
    {
        for(int deck_grid_index = 0; deck_grid_index < deck_grid_cells; deck_grid_index++)
        {
            if(deck_grid_index > (card_types_available.length - 1))
            {
                break;
            }

            deck_grid_nodes[deck_grid_index].setText(card_types_available[deck_grid_index].name());
        }
    }

    /**
     * @author Danny (s224774), Carl Emil (s224168), Mathias (s224273), Maria (s195685), Romel (s215212)
     */
    public void defaultMatchSettings()
    {
        player_name.setText("Player 1");
        opponent_name.setText("Player 2");

        round_wins.getSelectionModel().select(2); // indices of the combo-boxes
        turn_time.getSelectionModel().select(1);
        health_points.getSelectionModel().select(4);
        blood_points.getSelectionModel().select(0);
        deck_size.getSelectionModel().select(2);
        deck_size_selected = deck_size.getSelectionModel().getSelectedItem(); // store for cards chosen feedback
        hand_size.getSelectionModel().select(1);
    }

    /**
     * @author Danny (s224774), Mathias (s224273), Maria (s195685), Romel (s215212)
     */
    public void playerClickedOnDeckGrid(Button grid_node)
    {
        if(player_cards.size() < deck_size_selected)
        {
            int grid_node_row = GridPane.getRowIndex(grid_node);
            int grid_node_column = GridPane.getColumnIndex(grid_node);
            int index = convertRowColumnToGridIndex(grid_node_row, grid_node_column);

            player_cards.add(card_types_available[index]);

            updateCardsChosenCount();
        }
    }

    /**
     * @author Danny (s224774), Mathias (s224273), Maria (s195685), Romel (s215212)
     */
    public void updateCardsChosenCount()
    {
        deck_size_selected = deck_size.getSelectionModel().getSelectedItem();

        cards_chosen_count.setText("Cards chosen: " + player_cards.size() + "/" + deck_size_selected);
    }

    /**
     * @author Danny (s224774), Mathias (s224273), Maria (s195685), Romel (s215212)
     */
    public void updateCardsChosenLimit()
    {
        deck_size_selected = deck_size.getSelectionModel().getSelectedItem();

        player_cards.clear(); // remove all cards from chosen deck

        cards_chosen_count.setText("Cards chosen: " + 0 + "/" + deck_size_selected);
    }

    /**
     * @author Danny (s224774), Carl Emil (s224168), Mathias (s224273), Maria (s195685), Romel (s215212)
     */
    public void startGame() throws IOException
    {
        opponent_ready = true;

        if(player_cards.size() == deck_size_selected && opponent_ready)
        {
            // Set up game model
            String turn_time_string = turn_time.getSelectionModel().getSelectedItem();
            int turn_time = 0;

            if(!turn_time_string.equals("Unlimited"))
            {
                for(int char_index = 0; char_index < turn_time_string.length(); char_index++)
                {
                    if(!Character.isDigit(turn_time_string.charAt(char_index)))
                    {
                        turn_time = Integer.parseInt(turn_time_string.substring(0, char_index));

                        break;
                    }
                }
            }
            else
            {
                turn_time = -1; // infinite
            }

            game_model.initializeGame(round_wins.getSelectionModel().getSelectedItem(), turn_time, health_points.getSelectionModel().getSelectedItem(), blood_points.getSelectionModel().getSelectedItem(), deck_size.getSelectionModel().getSelectedItem(), hand_size.getSelectionModel().getSelectedItem());
            game_model.initializePlayer(player_name.getText(), player_cards, true);

            // Models ready, go to playing scene
            goToGameScene();
        }
    }

    /**
     * @author Danny (s224774)
     */
    public void quitSetup() throws IOException
    {
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

    /**
     * @author Danny (s224774), Mathias (s224273), Maria (s195685), Romel (s215212)
     */
    public int convertRowColumnToGridIndex(int row_to_convert, int column_to_convert)
    {
        return (row_to_convert * 4) + column_to_convert;
    }

    /////////////////////////
    //////// setters ////////
    /////////////////////////

    public void setOpponentName(String opponent_chosen_name)
    {
        opponent_name.setText(opponent_chosen_name);
    }

    public void setOpponentReady(boolean opponent_ready)
    {
        this.opponent_ready = opponent_ready;
    }
}