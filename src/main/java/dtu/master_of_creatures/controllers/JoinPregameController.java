package dtu.master_of_creatures.controllers;

// Project libraries
import dtu.master_of_creatures.models.GameModel;
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

import javax.swing.*;
import javax.swing.Timer;

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
        game_model.initializeClientModel();

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
        Runnable runnable = () -> {
            List<CommonCardTypes> temp_list = new ArrayList<>();
            temp_list.add(CommonCardTypes.WOLF);

            Object[] settings = game_model.getClient().queryGameSettings();



            game_model.initializeMatchSettings( (Integer) settings[1], (Integer) settings[2], (Integer)  settings[3],(Integer)  settings[4], (Integer) settings[5], (Integer) settings[6], false);

            System.out.println(game_model.getMatchSettings().toString());
            //game_model.initializeMatchSettings( 1,1,1,1,1,1, false);
            game_model.initializePlayer((String) settings[8], temp_list, false); // player 2 string
            game_model.setPlayerReady(true);
        };

        Thread test = new Thread(runnable);
        test.start();

        join_pane.requestFocus(); // exit player name text field
        ready.setDisable(true);



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

    public void actionPerformed(ActionEvent actionEvent) // gets called every 0.1 seconds
    {

        Runnable runnable = () -> {


            try {
                boolean [] result = game_model.getClient().queryPlayerReadyFlag();
                if ( result[0])
                {
                    game_model.getClient().updatePlayerReadyFlag( true , true );
                    game_model.setOpponentReady(true);
                }

            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }

        };




        Platform.runLater(() -> {
            System.out.println(game_model.getPlayerReady());
            System.out.println(game_model.getOpponentReady());

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
}
