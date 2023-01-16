package Undo;

import Holders.ArrayAndSorted;
import Holders.Character;
import java.util.ArrayList;

public class State {
    public int order_count;
    public int sorted_count;
    public ArrayList<ArrayAndSorted> Order;
    public ArrayList<Character> characters;
    public ArrayList<Character> TEMP_UNSORTED;
    public ArrayList<Character> TEMP_SORTED;

    public State(int order_count, int sorted_count, ArrayList<ArrayAndSorted> Order){
        this.order_count = order_count;
        this.sorted_count = sorted_count;
        this.Order = Order;
        this.characters = new ArrayList<>();
    }
}
