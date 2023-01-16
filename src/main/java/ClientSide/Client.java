package ClientSide;

import Holders.Characters;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import java.util.Hashtable;

public class Client extends Application {
    public static Characters ALL = new Characters();
    public static Stage start;
    public static Scene sorter;
    public static Scene tier_list;
    public static Hashtable<String, StackPane> character_panes;

    public static void main(String[] args) {
        launch();
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        Client.start = primaryStage;
        primaryStage.setTitle("Sorter");
        FXMLLoader StartLoader = new FXMLLoader(Client.class.getResource("/scenes/start.fxml"));
        Scene Start = new Scene(StartLoader.load(), 1280, 720);
        primaryStage.setScene(Start);
        primaryStage.show();
        primaryStage.setResizable(false);
    }
}

