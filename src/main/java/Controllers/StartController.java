package Controllers;

import ClientSide.Client;
import Holders.ArrayAndSorted;
import Holders.Character;
import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvException;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.Slider;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;

import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Hashtable;
import java.util.List;

import static ClientSide.Client.ALL;

// TODO when user does not input a number
public class StartController {

    @FXML
    private Button start;

    @FXML
    private Text maxSort;

    @FXML
    private Slider slider;

    @FXML
    private ProgressBar LoadingBar;

    @FXML
    private Text LoadingText;

    @FXML
    private void initialize() {
        Task<Void> LoadSorter = new Task<Void>() {
            @Override
            public Void call() throws IOException, CsvException {
                start.setDisable(true);
                slider.setDisable(true);

                LoadingText.setText("Loading character from CSV...");
                ArrayList<Character> A = new ArrayList<>();
                FileReader filereader = new FileReader("./characters.csv");
                CSVReader csvReader = new CSVReader(filereader);
                csvReader.skip(1);

                List<String[]> list = csvReader.readAll();
                int character_count = list.size();

                for(int i = 0; i < 10000; i++){
                    Collections.shuffle(list);
                }

                while (!list.isEmpty()) {
                    String[] s = list.remove(0);
                    Character c = new Character(s[0], s[1]);
                    A.add(c);
                }

                LoadingText.setText("Initializing sorter setup...");
                ALL.Setup(A, (int) slider.getValue());

                LoadingText.setText("Loading character images...");
                SorterController.Order = new ArrayList();
                Client.character_panes = new Hashtable<>();
                generatePanesAndOrder(ALL.Characters, character_count);

                return null;
            }
        };

        LoadSorter.setOnSucceeded(e -> {
            try {
                FXMLLoader SorterLoader = new FXMLLoader(Client.class.getResource("/scenes/sorter.fxml"));
                Client.sorter = new Scene(SorterLoader.load(), 1280, 720);
                Client.start.setScene(Client.sorter);
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        });

        start.setOnMouseClicked(event -> {
            new Thread(LoadSorter).start();
        });

        slider.valueProperty().addListener((ov, old_val, new_val) -> {
            slider.setValue(new_val.intValue());
            maxSort.setText(String.valueOf(new_val.intValue()));
        });
    }

    private void generatePanesAndOrder(ArrayAndSorted Characters, int character_count) {
        ArrayList UNSORTED = Characters.UNSORTED;
        for (int i = 0; i < UNSORTED.size(); i++) {
            Object o = UNSORTED.get(i);
            if (o instanceof ArrayList) {
                ArrayList<Character> a = (ArrayList<Character>) o;
                for (int j = 0; j < a.size(); j++) {
                    Character c = a.get(j);
                    generatePane(c);
                    LoadingBar.setProgress(LoadingBar.getProgress() + (1.0 / character_count));
                }
            } else {
                ArrayAndSorted a = (ArrayAndSorted) UNSORTED.get(i);
                generatePanesAndOrder(a, character_count);
            }
        }
        SorterController.Order.add(Characters);
    }

    private void generatePane(Character c) {
        // Pane for character stuff
        StackPane character_pane = new StackPane();

        // Character picture
        Image character_image = new Image(c.imageURL);
        ImageView productImageView = new ImageView(character_image);
        productImageView.setPreserveRatio(true);
        productImageView.setSmooth(true);

        character_pane.getChildren().add(productImageView);
        StackPane.setAlignment(productImageView, Pos.CENTER);

        // Character name
        Text t = new Text(c.name);
        character_pane.getChildren().add(t);
        StackPane.setAlignment(t, Pos.BOTTOM_CENTER);

        // Border
        character_pane.setStyle("-fx-border-color: black; -fx-stroke-width: 5;");

        Client.character_panes.put(c.name, character_pane);
    }
}
