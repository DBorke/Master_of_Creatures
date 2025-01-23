package dtu.master_of_creatures.controllers;

// Project libraries
import dtu.master_of_creatures.models.GameModel;
import dtu.master_of_creatures.utilities.Constants;
import dtu.master_of_creatures.utilities.enums.GameStates;
import dtu.master_of_creatures.utilities.enums.CommonCardTypes;

// Java libraries
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.URL;
import java.util.*;
import java.io.IOException;

// JavaFX libraries
import javafx.application.Platform;
import javafx.fxml.Initializable;
import javafx.fxml.FXML;
import javafx.scene.layout.AnchorPane;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Text;
import javax.swing.Timer;

public class JoinPregameController extends SceneController implements Initializable, ActionListener
{
    // JavaFX
    @FXML
    private AnchorPane join_pane;
    @FXML
    private TextField player_name;
    @FXML
    private GridPane deck_grid;
    private Button[] deck_grid_nodes;
    private int deck_grid_rows;
    private int deck_grid_columns;
    private int deck_grid_cells;
    private double deck_grid_cell_size;
    private final List<CommonCardTypes> player_cards;
    @FXML
    private Text cards_chosen;
    @FXML
    private Button ready;
    private final Timer network_timer;
    @FXML
    private Button sound;

    // Game data
    private final GameModel game_model;
    private final CommonCardTypes[] card_types_available;


    /**
     * @author Danny (s224774)
     */
    public JoinPregameController()
    {
        game_model = getGameModel();
        game_model.initializeClientModel();

        player_cards = new ArrayList<>();
        card_types_available = CommonCardTypes.values();

        network_timer = new Timer(1000, this); // delay is in milliseconds

    }
    /**
     * @author Danny (s224774)
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
        defaultMatchSettings();

        // Initialize deck grid and deck grid contents
        initializeDeckGrid();
        initializeCardsInDeckGrid();

        if(!getSoundUnmuted())
        {
            sound.setText("Sound Off");
        }
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
        player_name.setText("Player 2");
    }

    public void clearChosenDeck()
    {
        player_cards.clear();

        updateCardsChosenCount();
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
    public void ready()
    {
        if(player_cards.size() == 15)
        {
            Runnable runnable = () -> {
                Object[] settings = game_model.getClient().queryGameSettings();

                game_model.initializeMatchSettings( (Integer) settings[1], (Integer) settings[2], (Integer)  settings[3],(Integer)  settings[4], (Integer) settings[5], (Integer) settings[6], false);
                game_model.initializePlayer(player_name.getText(), player_cards, false); // player 2 string

                game_model.getClient().initialUpdatePlayer(Constants.PLAYER2, player_name.getText(), game_model.getPlayer().getHealthPoints(), game_model.getPlayer().getCardsRemaining());
            };

            Thread test = new Thread(runnable);
            test.start();

            join_pane.requestFocus(); // exit player name text field
            ready.setDisable(true);

            network_timer.start();
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
     * @author Danny (s224774), Romel (s215212)
     */
    public void actionPerformed(ActionEvent actionEvent) // gets called every 0.1 seconds
    {

        Runnable runnable = () -> {


            try {
                boolean [] result = game_model.getClient().queryPlayerReadyFlag();
                if ( result[0])
                {
                    game_model.getClient().updatePlayerReadyFlag( true , true );
                    game_model.setOpponentReady(true);

                    Object[] result2 = game_model.getClient().queryPlayer(Constants.PLAYER1);
                    game_model.setOpponentPlayerName((String) result2[0]);
                }

            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        };

        Platform.runLater(() -> {
        if(game_model.getPlayerReady() && game_model.getOpponentReady())
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
            System.out.println("No host connected.");
        }
        });

        Thread thread = new Thread(runnable);
        thread.start();
    }

    /**
     * @author Danny (s224774), Carl Emil (s224168), Mathias (s224273)
     */
    public void muteUnmuteSound()
    {
        super.muteUnmuteSound();

        sound.setText(getSoundUnmuted() ? "Sound On" : "Sound Off");
    }
}
