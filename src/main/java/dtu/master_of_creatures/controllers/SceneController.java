package dtu.master_of_creatures.controllers;

// Project libraries
import dtu.master_of_creatures.MasterOfCreaturesApp;
import dtu.master_of_creatures.models.GameModel;
import dtu.master_of_creatures.utilities.enums.GameStates;
import dtu.master_of_creatures.utilities.enums.SoundLabels;
import dtu.master_of_creatures.utilities.Constants;

// Java libraries
import java.util.HashMap;
import java.util.Objects;
import java.awt.GraphicsDevice;
import java.io.IOException;

// JavaFX libraries
import javafx.fxml.FXMLLoader;
import javafx.stage.Stage;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.transform.Scale;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

public abstract class SceneController
{
    // App
    private static Stage app_stage;
    private static Scene app_scene;
    private static boolean is_default_resolution;
    private static double scale_factor_x;
    private static double scale_factor_y;
    private static Scale transform_scale;

    // Game data
    public static GameModel game_model;

    // Sound data
    private static boolean sound_unmuted = true;
    private static HashMap<SoundLabels, MediaPlayer> sound_players;

    /**
     * @author Danny (s224774)
     */
    public SceneController()
    {
        if(game_model == null || game_model.getGameState() == GameStates.GAME_QUIT) // for initializing and resetting
        {
            game_model = new GameModel();
        }
    }

    /**
     * @author Danny (s224774)
     */
    public void goToMenuScene() throws IOException
    {
        setAppScene("MenuScene");
    }

    /**
     * @author Danny (s224774)
     */
    public void goToPregameScene() throws IOException
    {
        setAppScene("PregameScene");
    }

    /**
     * @author Danny (s224774)
     */
    public void goToGameScene() throws IOException
    {
        setAppScene("GameScene");
    }

    /**
     * @author Danny (s224774)
     */
    public void gatherScreenInformation(GraphicsDevice screen)
    {
        int default_resolution_x = Constants.getDefaultResolutionX();
        int default_resolution_y = Constants.getDefaultResolutionY();
        int current_resolution_x = screen.getDisplayMode().getWidth();
        int current_resolution_y = screen.getDisplayMode().getHeight();

        is_default_resolution = current_resolution_x == default_resolution_x && current_resolution_y == default_resolution_y;

        if(!is_default_resolution)
        {
            scale_factor_x = current_resolution_x / (double) default_resolution_x;
            scale_factor_y = current_resolution_y / (double) default_resolution_y;

            transform_scale = new Scale(scale_factor_x, scale_factor_y);
            transform_scale.setPivotX(-1.0); // needed for proper scene positioning
            transform_scale.setPivotY(-1.0);
        }
    }

    /**
     * @author Danny (s224774), Mathias (s224273)
     */
    private void scaleScene()
    {
        app_scene.getRoot().setTranslateX(scale_factor_x);
        app_scene.getRoot().setTranslateY(scale_factor_y);
        app_scene.getRoot().getTransforms().setAll(transform_scale);
        app_scene.getRoot().setStyle("-fx-background-color: transparent;"); // make scene background invisible
    }

    /**
     * @author Danny (s224774)
     */
    public void playSoundEffect(SoundLabels sound_label, double volume)
    {
        if(sound_players == null)
        {
            sound_players = new HashMap<>();

            createSoundPlayers();
        }

        if(sound_unmuted)
        {
            MediaPlayer sound_player = sound_players.get(sound_label);

            sound_player.setVolume(volume);
            sound_player.play();
        }
    }

    /**
     * @author Danny (s224774)
     */
    private void createSoundPlayers()
    {
        SoundLabels[] sounds = SoundLabels.values();

        for(SoundLabels sound_label : sounds)
        {
            sound_players.put(sound_label, new MediaPlayer(new Media(String.valueOf(MasterOfCreaturesApp.class.getResource("media/sounds/" + sound_label.name().toLowerCase() + ".mp3")))));
        }
    }

    /**
     * @author Danny (s224774)
     */
    public void stopSoundEffect(SoundLabels sound_label)
    {
        MediaPlayer sound_player = sound_players.get(sound_label);

        if(!sound_player.isMute())
        {
            sound_player.stop();
        }
    }

    /**
     * @author Danny (s224774)
     */
    public void muteSound()
    {
        sound_unmuted = !sound_unmuted;

        if(sound_players != null) // make sure sounds are available
        {
            // Stop all active sound effects
            for(MediaPlayer sound_player : sound_players.values())
            {
                if(!sound_player.isMute())
                {
                    sound_player.setMute(true);
                }
            }
        }
    }

    /////////////////////////
    // setters and getters //
    /////////////////////////

    /**
     * @author Danny (s224774)
     */
    public void setAppStage(Stage stage)
    {
        app_stage = stage;
    }

    /**
     * @author Danny (s224774), Mathias (s224273)
     */
    private void setAppScene(String scene_name) throws IOException
    {
        Parent root = FXMLLoader.load(Objects.requireNonNull(MasterOfCreaturesApp.class.getResource("scenes/" + scene_name + ".fxml")));

        if(app_scene != null)
        {
            app_scene.setRoot(root);
        }
        else
        {
            app_scene = new Scene(root);
        }

        app_stage.setScene(app_scene); // construct scene
        app_stage.show(); // show scene

        if(!is_default_resolution)
        {
            scaleScene(); // scale scene to different screen resolutions
        }
    }

    /**
     * @author Danny (s224774)
     */
    public GameModel getGameModel()
    {
        return game_model;
    }

    /**
     * @author Danny (s224774)
     */
    public boolean getSoundUnmuted()
    {
        return sound_unmuted;
    }
}
