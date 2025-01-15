package dtu.master_of_creatures.controllers;

// Project libraries
import dtu.master_of_creatures.models.GameModel;
import dtu.master_of_creatures.utilities.enums.GameStates;
import dtu.master_of_creatures.utilities.enums.CardTypes;

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
    private GridPane deck_grid;
    private int deck_grid_rows;
    private int deck_grid_columns;
    private int deck_grid_cells;
    private double grid_cell_size;
    private Button[] deck_grid_nodes;
    private final List<CardTypes> player_1_cards;
    private final List<CardTypes> player_2_cards; // temp
    @FXML
    private TextField player_1_name;
    @FXML
    private TextField player_2_name; // temp
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
    private Text cards_chosen;
    @FXML
    private Button sound_button;

    @FXML
    private Button player_selector; // temp
    private int selecting_player = 1; // temp

    // Game data
    private final GameModel game_model;
    private final CardTypes[] cards_available;

    /**
     * @author Danny (s224774), Mathias (s224273), Maria (s195685), Romel (s215212)
     */
    public HostPregameController()
    {
        player_1_cards = new ArrayList<>();
        player_2_cards = new ArrayList<>(); // temp

        game_model = getGameModel();
        cards_available = CardTypes.values();
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
        grid_cell_size =  deck_grid.getPrefWidth(); // assuming grid cell symmetry
        deck_grid_nodes = new Button[deck_grid_cells];

        // Add and set up GUI option elements
        round_wins.getItems().addAll(1, 2, 3, 4, 5); // match setting options
        turn_time.getItems().addAll("30 seconds", "60 seconds", "90 seconds", "120 seconds", "Unlimited");
        health_points.getItems().addAll(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);
        blood_points.getItems().addAll(0, 1, 2, 3);
        deck_size.getItems().addAll(5, 10, 15, 20, 25);
        hand_size.getItems().addAll(3, 4, 5, 6, 7, 8, 9, 10);
        defaultMatchSettings();

        // Initialize deck grid contents
        initializeDeckGrid();
        initializeCardsInDeckGrid();
    }

    /**
     * @author Danny (s224774), Carl Emil (s224168), Mathias (s224273), Maria (s195685), Romel (s215212)
     */
    public void defaultMatchSettings()
    {
        player_1_name.setText("Player 1");
        player_2_name.setText("Player 2"); // temp

        round_wins.getSelectionModel().select(2); // indices of the combo-boxes
        turn_time.getSelectionModel().select(1);
        health_points.getSelectionModel().select(4);
        blood_points.getSelectionModel().select(0);
        deck_size.getSelectionModel().select(2);
        deck_size_selected = deck_size.getSelectionModel().getSelectedItem(); // store for later use
        hand_size.getSelectionModel().select(1);
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
                grid_node.setPrefSize(grid_cell_size, grid_cell_size);
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
            if(deck_grid_index > (cards_available.length - 1))
            {
                break;
            }

            deck_grid_nodes[deck_grid_index].setText(cards_available[deck_grid_index].name());
        }
    }

    /**
     * @author Danny (s224774), Mathias (s224273), Maria (s195685), Romel (s215212)
     */
    public void playerClickedOnDeckGrid(Button grid_node)
    {
        if((selecting_player == 1 && player_1_cards.size() < deck_size_selected) || (selecting_player == 2 && player_2_cards.size() < deck_size_selected)) // temp
        {
            int grid_node_row = GridPane.getRowIndex(grid_node);
            int grid_node_column = GridPane.getColumnIndex(grid_node);
            int index = convertRowColumnToGridIndex(grid_node_row, grid_node_column);

            if(selecting_player == 1)
            {
                player_1_cards.add(cards_available[index]);
            }
            else
            {
                player_2_cards.add(cards_available[index]); // temp
            }

            updateCardsChosenCount();
        }
    }

    /**
     * @author Danny (s224774), Mathias (s224273), Maria (s195685), Romel (s215212)
     */
    public int convertRowColumnToGridIndex(int row_to_convert, int column_to_convert)
    {
        return (row_to_convert * 4) + column_to_convert;
    }

    /**
     * @author Danny (s224774), Mathias (s224273), Maria (s195685), Romel (s215212)
     */
    public void updateCardsChosenLimit()
    {
        deck_size_selected = deck_size.getSelectionModel().getSelectedItem();

        // Flush selected cards
        player_1_cards.clear();
        player_2_cards.clear(); // temp

        cards_chosen.setText("Cards chosen: " + 0 + "/" + deck_size_selected);
    }

    /**
     * @author Danny (s224774), Mathias (s224273), Maria (s195685), Romel (s215212)
     */
    public void updateCardsChosenCount()
    {
        deck_size_selected = deck_size.getSelectionModel().getSelectedItem();

        cards_chosen.setText("Cards chosen: " + (selecting_player == 1 ? player_1_cards.size() : player_2_cards.size()) + "/" + deck_size_selected);
    }

    /**
     * @author Danny (s224774), Carl Emil (s224168), Mathias (s224273), Maria (s195685), Romel (s215212)
     */
    public void startGame() throws IOException
    {
        if(player_1_cards.size() == deck_size_selected && player_2_cards.size() == deck_size_selected) // temp
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

            game_model.initializeGame(round_wins.getSelectionModel().getSelectedItem(), turn_time);

            game_model.initializePlayer(player_1_name.getText(), health_points.getSelectionModel().getSelectedItem(), blood_points.getSelectionModel().getSelectedItem(), deck_size.getSelectionModel().getSelectedItem(), hand_size.getSelectionModel().getSelectedItem(), player_1_cards, true);
            game_model.initializePlayer(player_2_name.getText(), health_points.getSelectionModel().getSelectedItem(), blood_points.getSelectionModel().getSelectedItem(), deck_size.getSelectionModel().getSelectedItem(), hand_size.getSelectionModel().getSelectedItem(), player_2_cards, false); // temp

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

    public void changeDeckBuildingPlayer() // temp
    {
        selecting_player = selecting_player == 1 ? 2 : 1;

        player_selector.setText("Player " + selecting_player);

        updateCardsChosenCount();
    }
}