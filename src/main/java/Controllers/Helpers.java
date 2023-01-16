package Controllers;

import ClientSide.Client;
import Holders.Character;
import Holders.Properties;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.Text;
import org.apache.commons.lang3.SerializationUtils;

import java.util.ArrayList;
import java.util.Objects;


public class Helpers {
    protected static void Kill(Node n){
        n.setVisible(false);
        n.setDisable(true);
    }

    protected static void Revive(Node n){
        n.setVisible(true);
        n.setDisable(false);
    }

    protected static void characterPicked(StackPane character_pane, int number_count) {
        // Make image opaque
        Node image = character_pane.getChildren().get(0);
        image.setStyle("-fx-opacity: 0.5;");

        // Number rank
        Text t = new Text(String.valueOf(number_count));
        t.setFont(Font.font("System", FontPosture.REGULAR, 40));
        character_pane.getChildren().add(t);
        StackPane.setAlignment(t, Pos.CENTER);

        // Character is not able to clicked anymore
        character_pane.setDisable(true);
    }

    protected static int getCharacterIndex(Character c, ArrayList<Character> LIST) {
        for (int i = 0; i < LIST.size(); i++) {
            Character temp = LIST.get(i);
            if (Objects.equals(temp.name, c.name)) {
                return i;
            }
        }
        return -1;
    }

    protected static void prepCharacter(StackPane character_pane, Properties p) {
        // Reset Image size to fit the size of the grid segment and reset the margin
        ImageView view = (ImageView) character_pane.getChildren().get(0);
        view.setFitWidth(p.width);
        view.setFitHeight(p.height);

        StackPane.setMargin(character_pane.getChildren().get(0), new Insets(0, 0, p.bottom_margin_image, 0));

        // Reset text size to fit the size of the grid segment and reset the margin
        Text t = (Text) character_pane.getChildren().get(1);
        t.setFont(Font.font("System", FontPosture.REGULAR, p.name_font_size));

        // Set Margin
        StackPane.setMargin(character_pane.getChildren().get(1), new Insets(0, 0, p.bottom_margin_name, 0));
    }

    protected static void resetCharacters(ArrayList<Character> LIST) {
        for (int i = 0; i < LIST.size(); i++) {
            Character c = LIST.get(i);

            // Set opacity to default
            StackPane character_pane = Client.character_panes.get(c.name);
            Node image = character_pane.getChildren().get(0);
            image.setStyle(null);

            // Remove the number
            try {
                character_pane.getChildren().remove(2);
            } catch (IndexOutOfBoundsException ignored) {
            }

            // Holders.Character is able to clicked
            character_pane.setDisable(false);
        }
    }
}

