package Controllers;

import ClientSide.Client;
import Holders.Character;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.Text;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import static Controllers.Helpers.Kill;
import static Controllers.Helpers.Revive;


public class TierListController {

    @FXML
    private AnchorPane top10pane;
    @FXML
    private GridPane top10grid;
    @FXML
    private Button showTheRest;

    @FXML
    private ScrollPane therestpane;
    @FXML
    private GridPane the_rest_grid;
    @FXML
    private Button showTop10;
    @FXML
    private Button Download;

    @FXML
    private void initialize() {
        showTheRest.setOnMouseClicked(event -> {
            Kill(top10pane);
            Kill(showTheRest);
            Revive(therestpane);
            Revive(showTop10);
        });

        showTop10.setOnMouseClicked(event -> {
            Kill(therestpane);
            Kill(showTop10);
            Revive(top10pane);
            Revive(showTheRest);
        });

        ArrayList<Character> TIER_LIST = SorterController.Order.get(SorterController.Order.size() - 1).SORTED;

        int count = 0;
        for (int i = 0; i < 5 && count < TIER_LIST.size(); i++) {
            for (int j = 0; j < 2 && count < TIER_LIST.size(); j++, count++) {
                Character c = TIER_LIST.get(count);
                top10grid.add(generateTop10Pane(count+1, c), j, i);
            }
        }

        for(int i = 0; count < TIER_LIST.size(); i++, count++){
            Character c = TIER_LIST.get(count);
            the_rest_grid.addRow(i, generateRankPane(count+1, c));
        }

        Download.setOnMouseClicked(event -> {
            try {
                FileWriter myWriter = new FileWriter("list.txt");
                for(int i = 0; i < TIER_LIST.size(); i++){
                    myWriter.write((i + 1) + ". " + TIER_LIST.get(i).name + "\n");
                }
                myWriter.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    private AnchorPane generateTop10Pane(int count, Character c) {
        AnchorPane character_pane = new AnchorPane();
        character_pane.setPrefSize(500, 128);

        // Create number pane
        StackPane number_pane = new StackPane();
        number_pane.setStyle("-fx-border-color: black;");
        number_pane.setPrefSize(100, 128);
        number_pane.setLayoutX(0);
        number_pane.setLayoutY(0);

        Text number = new Text(String.valueOf(count));
        number.setFont(Font.font("System", FontPosture.REGULAR, 60));
        number_pane.getChildren().add(number);

        character_pane.getChildren().add(number_pane);

        // Put imageview
        StackPane image_pane = new StackPane();
        image_pane.setPrefSize(150, 128);
        image_pane.setLayoutX(100);
        image_pane.setLayoutY(0);

        ImageView view = (ImageView) Client.character_panes.get(c.name).getChildren().get(0);
        view.setFitWidth(150);
        view.setFitHeight(120);

        image_pane.getChildren().add(view);
        StackPane.setAlignment(view, Pos.CENTER);
        StackPane.setMargin(view, new Insets(0, 0, 0, 0));

        character_pane.getChildren().add(image_pane);

        // Name of character
        StackPane name_pane = new StackPane();
        name_pane.setPrefSize(250, 128);
        name_pane.setLayoutX(250);
        name_pane.setLayoutY(0);

        Text name = new Text(String.valueOf(c.name));
        name.setFont(Font.font("System", FontPosture.REGULAR, 26));

        name_pane.getChildren().add(name);
        StackPane.setAlignment(name, Pos.CENTER_LEFT);
        StackPane.setMargin(name, new Insets(0, 0, 0, 20));

        character_pane.getChildren().add(name_pane);

        return character_pane;
    }

    private AnchorPane generateRankPane(int count, Character c) {
        AnchorPane rank_pane = new AnchorPane();
        rank_pane.setPrefSize(1000, 32);

        // Create number pane
        StackPane number_pane = new StackPane();
        number_pane.setStyle("-fx-border-color: black;");
        number_pane.setPrefSize(50, 32);
        number_pane.setLayoutX(0);
        number_pane.setLayoutY(0);

        Text number = new Text(String.valueOf(count));
        number.setFont(Font.font("System", FontPosture.REGULAR, 20));
        number_pane.getChildren().add(number);

        rank_pane.getChildren().add(number_pane);
        StackPane.setMargin(number, new Insets(0, 0, 2, 0));


        // Name of character
        StackPane name_pane = new StackPane();
        name_pane.setPrefSize(950, 32);
        name_pane.setLayoutX(50);
        name_pane.setLayoutY(0);

        Text name = new Text(String.valueOf(c.name));
        name.setFont(Font.font("System", FontPosture.REGULAR, 20));

        name_pane.getChildren().add(name);
        StackPane.setAlignment(name, Pos.CENTER_LEFT);
        StackPane.setMargin(name, new Insets(0, 0, 2, 20));

        rank_pane.getChildren().add(name_pane);

        return rank_pane;
    }
}
