package dtu.master_of_creatures.controllers;

// Project libraries
import dtu.master_of_creatures.models.GameModel;
import dtu.master_of_creatures.models.network.ThreadModel;
import dtu.master_of_creatures.utilities.Constants;
import dtu.master_of_creatures.utilities.enums.GameStates;
import dtu.master_of_creatures.utilities.enums.CardTypes;

// Java libraries
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.List;
import java.util.ArrayList;
import java.io.IOException;

// JavaFX libraries
import javafx.application.Platform;
import javafx.fxml.Initializable;
import javafx.fxml.FXML;
import javafx.scene.layout.GridPane;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;

import javax.swing.*;

public class HostPregameController extends SceneController implements Initializable, ActionListener
{
    // JavaFX
    @FXML
    private TextField player_name;
    @FXML
    private ComboBox<String> turn_time;
    @FXML
    private ComboBox<Integer> blood_points;
    @FXML
    private ComboBox<Integer> hand_size;
    @FXML
    private GridPane deck_grid;
    private Button[] deck_grid_nodes;
    private int deck_grid_rows;
    private int deck_grid_columns;
    private int deck_grid_cells;
    private double deck_grid_cell_size;
    private final List<CardTypes> player_cards;
    @FXML
    private Text cards_chosen;
    private final Timer network_timer;

    // Game data
    private final GameModel game_model;
    private final CardTypes[] card_types_available;

    /**
     * @author Danny (s224774), Mathias (s224273), Maria (s195685), Romel (s215212)
     */
    public HostPregameController()
    {
        game_model = getGameModel();
        game_model.initializeHostModel();

        player_cards = new ArrayList<>();
        card_types_available = CardTypes.values();

        network_timer = new Timer(1000, this); // delay is in milliseconds
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
        turn_time.getItems().addAll("10 seconds", "20 seconds", "30 seconds", "40 seconds", "50 seconds", "60 seconds"); // match setting options
        blood_points.getItems().addAll(0, 1, 2, 3);
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

        turn_time.getSelectionModel().select(2); // indices of the combo-boxes
        blood_points.getSelectionModel().select(0);
        hand_size.getSelectionModel().select(1);
    }

    /**
     * @author Danny (s224774), Mathias (s224273), Maria (s195685), Romel (s215212)
     */
    public void playerClickedOnDeckGrid(Button grid_node)
    {
        if(player_cards.size() < 15)
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
        cards_chosen.setText("Cards chosen: " + player_cards.size() + "/" + 15);
    }

    /**
     * @author Danny (s224774), Carl Emil (s224168), Mathias (s224273), Maria (s195685), Romel (s215212)
     */
    public void finishMatchSetup()
    {
        if(player_cards.size() == 15)
        {
            // Set up game model
            String turn_time_string = turn_time.getSelectionModel().getSelectedItem();
            int turn_time = 0;

            for(int char_index = 0; char_index < turn_time_string.length(); char_index++)
            {
                if(!Character.isDigit(turn_time_string.charAt(char_index)))
                {
                    turn_time = Integer.parseInt(turn_time_string.substring(0, char_index));

                    break;
                }
            }

            final int final_turn_time = turn_time;
            game_model.initializeMatchSettings(1, turn_time, 1, blood_points.getSelectionModel().getSelectedItem(), 15, hand_size.getSelectionModel().getSelectedItem(), true);
            Runnable runnable = () ->{
                game_model.getHost().initializeGameSpace(player_name.getText(), "Waiting for client.", 1, final_turn_time, 1, blood_points.getSelectionModel().getSelectedItem(), 15, hand_size.getSelectionModel().getSelectedItem(), 11,11,false);
                game_model.getHost().updateGameSettings(1, Integer.parseInt(String.valueOf(final_turn_time)), 1, blood_points.getSelectionModel().getSelectedItem(), 15, hand_size.getSelectionModel().getSelectedItem(), "Player 1", "Player 2");
            };

            System.out.println(game_model.getMatchSettings().toString());

            Thread thread = new ThreadModel(runnable);
            thread.start();
            //game_model.getHost().initializeGameSpace(player_name.getText(), "Waiting for client.", round_wins.getSelectionModel().getSelectedItem(), turn_time, health_points.getSelectionModel().getSelectedItem(), blood_points.getSelectionModel().getSelectedItem(), deck_size.getSelectionModel().getSelectedItem(), hand_size.getSelectionModel().getSelectedItem(), deck_size.getSelectionModel().getSelectedItem()+hand_size.getSelectionModel().getSelectedItem(),deck_size.getSelectionModel().getSelectedItem()+hand_size.getSelectionModel().getSelectedItem(),false);

            game_model.initializePlayer(player_name.getText(), player_cards, true);

            game_model.setPlayerReady(true);

            network_timer.start();
        }

        System.out.println("Waiting for client.");
    }

    /**
     * @author Danny (s224774)
     */
    public void quitSetup() throws IOException
    {
        goToMenuScene();
    }

    public void actionPerformed(ActionEvent actionEvent) // gets called every 0.1 seconds
    {
        Runnable initialize_host_parameters = () ->
        {
            try {

                boolean[] result = game_model.getHost().queryPlayerReadyFlag();
                if (result[1])
                {
                    game_model.setOpponentReady(true);

                    Object[] result2 = game_model.getHost().queryPlayer(Constants.PLAYER2);
                    game_model.setOpponentPlayerName((String) result2[0]);
                }
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        };

        Platform.runLater(() -> {
        System.out.println(game_model.getPlayerReady());
        System.out.println(game_model.getOpponentReady());

        if( game_model.getPlayerReady() && game_model.getOpponentReady())
        {
            try
            {
                goToGameScene();
                network_timer.stop();
            }
            catch (IOException e)
            {
                throw new RuntimeException(e);
            }
        }
        else
        {
            System.out.println("No client connected.");
        }
        });
        // Create host thread
        Thread initialize_thread = new ThreadModel(initialize_host_parameters);

        // Start host thread
        initialize_thread.start();
    }
}