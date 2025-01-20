package dtu.master_of_creatures.controllers;

// Project libraries
import dtu.master_of_creatures.models.GameModel;
import dtu.master_of_creatures.utilities.enums.GameStates;
import dtu.master_of_creatures.utilities.enums.CommonCardTypes;

// Java libraries
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.List;
import java.util.ArrayList;
import java.io.IOException;

// JavaFX libraries
import javafx.fxml.Initializable;
import javafx.fxml.FXML;
import javafx.scene.layout.AnchorPane;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;

import javax.swing.*;

public class JoinPregameController extends SceneController implements Initializable, ActionListener
{
    // JavaFX
    @FXML
    private AnchorPane join_pane;
    @FXML
    private TextField player_name;
    @FXML
    private Button ready;
    @FXML
    private Button sound_button;
    private final Timer network_timer;

    // Game data
    private final GameModel game_model;

    /**
     * @author Danny (s224774)
     */
    public JoinPregameController()
    {
        game_model = getGameModel();

        network_timer = new Timer(1000, this); // delay is in milliseconds
    }
    /**
     * @author Danny (s224774)
     */
    public void initialize(URL url, ResourceBundle resource_bundle)
    {
        game_model.setGameState(GameStates.GAME_SETUP);

        defaultMatchSettings();
    }

    /**
     * @author Danny (s224774), Carl Emil (s224168), Mathias (s224273), Maria (s195685), Romel (s215212)
     */
    public void defaultMatchSettings()
    {
        player_name.setText("Player 2");
    }

    /**
     * @author Danny (s224774), Carl Emil (s224168), Mathias (s224273), Maria (s195685), Romel (s215212)
     */
    public void ready()
    {
        List<CommonCardTypes> temp_list = new ArrayList<>();
        temp_list.add(CommonCardTypes.WOLF);

        game_model.initializePlayer(player_name.getText(), temp_list, false);

        join_pane.requestFocus(); // exit player name text field
        ready.setDisable(true);

        game_model.setOpponentReady(true);

        network_timer.start();

        // test if repository is ready
    }

    /**
     * @author Danny (s224774), Carl Emil (s224168), Mathias (s224273), Maria (s195685), Romel (s215212)
     */
    public void unready()
    {
        ready.setDisable(false);
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

    public void actionPerformed(ActionEvent actionEvent) // gets called every 0.1 seconds
    {
        if(game_model.getPlayerReady() && game_model.getOpponentReady())
        {
            try
            {
                goToGameScene();
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
    }
}
