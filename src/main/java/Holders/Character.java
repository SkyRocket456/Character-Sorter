package Holders;

import javafx.scene.layout.StackPane;

public class Character {
    public String name;
    public String imageURL;
    public int amount_shown;

    public Character(String name, String imageURL){
        this.name = name;
        this.imageURL = imageURL;
        this.amount_shown = 0;
    }
}
