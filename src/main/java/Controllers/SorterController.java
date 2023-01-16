package Controllers;

import ClientSide.Client;
import Holders.ArrayAndSorted;
import Holders.Character;
import Holders.Properties;
import Undo.State;
import com.rits.cloning.Cloner;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Stack;
import java.util.concurrent.atomic.AtomicInteger;

import static ClientSide.Client.ALL;
import static Controllers.Helpers.*;


public class SorterController {

    @FXML
    private GridPane grid2;
    Properties props2 = new Properties(500, 500, 80, 40, 40);

    @FXML
    private GridPane grid3;
    Properties props3 = new Properties(300, 450, 100, 55, 40);

    @FXML
    private GridPane grid4;
    Properties props4 = new Properties(275, 450, 100, 60, 35);

    @FXML
    private GridPane grid5;
    Properties props5 = new Properties(200, 450, 100, 60, 30);

    @FXML
    private GridPane grid6;
    Properties props6 = new Properties(300, 200, 50, 20, 30);

    @FXML
    private GridPane grid8;
    Properties props8 = new Properties(250, 200, 50, 30, 25);

    @FXML
    private GridPane grid10;
    Properties props10 = new Properties(200, 250, 50, 15, 22);

    @FXML
    private Text Round;

    @FXML
    private Text Percentage;

    @FXML
    private Text best_to_worst;

    @FXML
    private Text best_from_shown;

    @FXML
    private Button Undo;

    private int sorted_count = 0;

    private int round_count = 1;

    private Stack<State> PreviousStates;

    private Cloner cloner;

    private ArrayList<Character> UNDO_TEMP_UNSORTED;

    private ArrayList<Character> UNDO_TEMP_SORTED;

    protected static ArrayList<ArrayAndSorted> Order;

    @FXML
    private void initialize() {
        // The Undo button
        Undo.setOnMouseClicked(event -> {
            if (PreviousStates.isEmpty()) {
                return;
            }
            // Find the state before the current one
            State s = PreviousStates.pop();

            // Revert values back to the previous round
            sorted_count = s.sorted_count;
            Order = s.Order;

            // Revert character ratings back to the previous round
            UNDO_TEMP_UNSORTED = s.TEMP_UNSORTED;
            UNDO_TEMP_SORTED = s.TEMP_SORTED;

            // Revert round count and percentage
            updateRoundNumberAndPercent(-1, 0);

            // Revert characters who were chosen
            resetCharacters(s.characters);

            // Re-render
            renderCharacters(s.order_count);
        });

        cloner = new Cloner();
        PreviousStates = new Stack<>();
        renderCharacters(0);
    }

    private void renderCharacters(int order_count) {
        // If the order count is the size of all AofAs combined, we have reached the end of the sorter. RANK CHARACTERS
        if (order_count == Order.size()) {
            try {
                FXMLLoader TierListLoader = new FXMLLoader(Client.class.getResource("/scenes/tier_list.fxml"));
                Client.tier_list = new Scene(TierListLoader.load(), 1280, 720);
                Client.start.setScene(Client.tier_list);
                return;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        ArrayAndSorted AAndS = Order.get(order_count);

        // If the AofA is the lowest level
        if (AAndS.UNSORTED.get(0) instanceof ArrayList) {
            ArrayList<Character> UNSORTED = (ArrayList) AAndS.UNSORTED.get(0);
            // If the unsorted array has more than 1 character
            if (UNSORTED.size() > 1) {
                BestToWorst(UNSORTED, AAndS, order_count);
            }
            // If the unsorted array has less than 1 character, we don't need to sort anything
            else {
                AAndS.SORTED.add(UNSORTED.remove(0));
                updateRoundNumberAndPercent(0, 1);
                renderCharacters(order_count + 1);
            }
        }
        // If the AofA has other AofAs inside it, we need to sort all the AofAs into one
        else {
            BestFromShown(AAndS, order_count);
        }
    }

    private void BestToWorst(ArrayList<Character> UNSORTED, ArrayAndSorted AAndS, int order_count) {
        // If sorting more than two figures at once, change top text
        if (ALL.split_number > 2) {
            Revive(best_to_worst);
            Kill(best_from_shown);
        }

        // Get the initial number count, the grid, the properties for the panes
        AtomicInteger number_count = new AtomicInteger(1);
        Object[] o = getGridAndProps(UNSORTED.size());
        GridPane g = (GridPane) o[0];
        g.getChildren().clear();
        Properties p = (Properties) o[1];

        // Create temporary arrays to hold the sorted characters
        ArrayList<Character> TEMP_UNSORTED;
        ArrayList<Character> TEMP_SORTED;

        // If the user undoed, retrieve their past progress
        if (UNDO_TEMP_UNSORTED != null) {
            TEMP_UNSORTED = UNDO_TEMP_UNSORTED;
            TEMP_SORTED = UNDO_TEMP_SORTED;

            // Reset characters to base and then revert the characters they already chose to being picked
            resetCharacters(TEMP_SORTED);
            for (int i = 0; i < TEMP_SORTED.size(); i++) {
                characterPicked(Client.character_panes.get(TEMP_SORTED.get(i).name), number_count.get());
                number_count.getAndIncrement();
            }

            UNDO_TEMP_UNSORTED = null;
            UNDO_TEMP_SORTED = null;
        }
        // If the user didn't undo, initialize new temporary arrays to hold characters
        else {
            TEMP_UNSORTED = new ArrayList<>(UNSORTED);
            TEMP_SORTED = new ArrayList<>();
        }

        // Render every character onto the screen
        for (int i = 0, count = 0; i < g.getRowConstraints().size() && count < UNSORTED.size(); i++) {
            for (int j = 0; j < g.getColumnConstraints().size() && count < UNSORTED.size(); j++, count++) {
                Character c = UNSORTED.get(count);
                StackPane character_pane = Client.character_panes.get(c.name);
                prepCharacter(character_pane, p);

                character_pane.setOnMouseClicked(event -> {
                    State s = new State(order_count, sorted_count, cloner.deepClone(Order));

                    s.TEMP_UNSORTED = cloner.deepClone(TEMP_UNSORTED);
                    s.TEMP_SORTED = cloner.deepClone(TEMP_SORTED);

                    if (event.getButton() == MouseButton.PRIMARY) {
                        s.characters.add(cloner.deepClone(c));
                        characterPicked(character_pane, number_count.get());

                        // Add to temp sorted array and remove from temp unsorted array
                        TEMP_SORTED.add(TEMP_UNSORTED.remove(getCharacterIndex(c, TEMP_UNSORTED)));

                        updateRoundNumberAndPercent(1, 1);
                        number_count.getAndIncrement();
                    }
                    else if (event.getButton() == MouseButton.SECONDARY) {
                        g.getChildren().remove(character_pane);
                        removeCharacterFromSorter(c);

                        TEMP_UNSORTED.remove(getCharacterIndex(c, TEMP_UNSORTED));

                        updateRoundNumberAndPercent(1, c.amount_shown);
                    }
                    PreviousStates.add(s);

                    if (TEMP_UNSORTED.size() == 1) {
                        TEMP_SORTED.add(TEMP_UNSORTED.remove(0));

                        for (int t = 0; t < TEMP_SORTED.size(); t++) {
                            UNSORTED.remove(0);
                            TEMP_SORTED.get(t).amount_shown--;
                            AAndS.SORTED.add(TEMP_SORTED.get(t));
                        }

                        updateRoundNumberAndPercent(0, 1);
                        resetCharacters(AAndS.SORTED);
                        renderCharacters(order_count + 1);
                    }
                });

                g.add(character_pane, j, i);
            }
        }
    }

    private void BestFromShown(ArrayAndSorted AofA, int order_count) {
        if (ALL.split_number > 2) {
            Kill(best_to_worst);
            Revive(best_from_shown);
        }

        Object[] o = getGridAndProps(AofA.UNSORTED.size());
        GridPane g = (GridPane) o[0];
        g.getChildren().clear();
        Properties p = (Properties) o[1];


        for (int i = 0, count = 0; i < g.getRowConstraints().size(); i++) {
            for (int j = 0; j < g.getColumnConstraints().size(); j++, count++) {
                ArrayAndSorted a = (ArrayAndSorted) AofA.UNSORTED.get(count);
                Character c = a.SORTED.get(0);
                StackPane character_pane = Client.character_panes.get(c.name);
                prepCharacter(character_pane, p);

                int finalCount = count;

                character_pane.setOnMouseClicked(event -> {
                    State s = new State(order_count, sorted_count, cloner.deepClone(Order));
                    PreviousStates.add(s);

                    // If character is chosen out of the bunch
                    if (event.getButton() == MouseButton.PRIMARY) {
                        AofA.SORTED.add(a.SORTED.remove(0));
                        updateRoundNumberAndPercent(1, 1);
                        c.amount_shown = c.amount_shown - 1;
                    }
                    // If user wants to remove character from Sorter
                    else if (event.getButton() == MouseButton.SECONDARY) {
                        removeCharacterFromSorter(c);
                        updateRoundNumberAndPercent(1, c.amount_shown);
                    }


                    if (a.SORTED.isEmpty()) {
                        AofA.UNSORTED.remove(finalCount);
                    }
                    if (AofA.UNSORTED.size() == 1) {
                        ArrayAndSorted last = (ArrayAndSorted) AofA.UNSORTED.get(0);
                        AofA.SORTED.addAll(last.SORTED);

                        updateRoundNumberAndPercent(0, last.SORTED.size());
                        for (int k = 0; k < last.SORTED.size(); k++) {
                            last.SORTED.get(k).amount_shown--;
                        }
                        last.SORTED.clear();
                        renderCharacters(order_count + 1);
                    } else {
                        BestFromShown(AofA, order_count);
                    }
                });
                
                g.add(character_pane, j, i);
            }
        }
    }

    private void removeCharacterFromSorter(Character c) {
        for (int i = 0; i < Order.size(); i++) {
            Object o = Order.get(i).UNSORTED.get(0);
            // Remove character from UNSORTED array if there
            if (o instanceof ArrayList) {
                ArrayList<Character> a = (ArrayList<Character>) o;
                int index = getCharacterIndex(c, a);
                if (index != -1) {
                    a.remove(index);
                    break;
                }
            }

            // Remove character from SORTED array if there
            ArrayList<Character> sorted = Order.get(i).SORTED;
            int index = getCharacterIndex(c, sorted);
            if (index != -1) {
                sorted.remove(index);
                break;
            }
        }
    }

    private void updateRoundNumberAndPercent(int round_increment, int sort_increment) {
        round_count += round_increment;
        //Round.setText("ROUND " + round_count);

        sorted_count += sort_increment;
        //System.out.println(sorted_count + " out of " + ALL.SORTED_COUNT);

        Round.setText("ROUND " + round_count);
        Percentage.setText((int) Math.floor((double) sorted_count / ALL.SORTED_COUNT * 100) + "%");

    }

    private Object[] getGridAndProps(int size) {
        Kill(grid2);
        Kill(grid3);
        Kill(grid4);
        Kill(grid5);
        Kill(grid6);
        Kill(grid8);
        Kill(grid10);

        GridPane g;
        Properties p;
        switch (size) {
            case 2:
                g = grid2;
                p = props2;
                break;
            case 3:
                g = grid3;
                p = props3;
                break;
            case 4:
                g = grid4;
                p = props4;
                break;
            case 5:
                g = grid5;
                p = props5;
                break;
            case 6:
                g = grid6;
                p = props6;
                break;
            case 7:
            case 8:
                g = grid8;
                p = props8;
                break;
            default:
                g = grid10;
                p = props10;
                break;
        }
        Revive(g);
        return new Object[]{g, p};
    }
}
