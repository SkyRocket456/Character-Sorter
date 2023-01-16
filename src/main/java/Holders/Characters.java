package Holders;

import java.util.ArrayList;

public class Characters {
    public int SORTED_COUNT;
    public int split_number;
    public ArrayAndSorted Characters; // Create an ArrayList object

    public void Setup(ArrayList<Character> ALL, int split_num) {
        this.split_number = split_num;
        this.SORTED_COUNT = 0;
        Characters = Split(ALL, 1);
    }

    private ArrayAndSorted Split(ArrayList<Character> UNSORTED, int level) {
        ArrayAndSorted Characters = new ArrayAndSorted();

        // Find the factor to do Modulo by
        int mod_factor = split_number;

        while (mod_factor * split_number < UNSORTED.size()) {
            mod_factor = mod_factor * split_number;
        }

        // Get the factor to the split the unsorted array by
        int split_factor = UNSORTED.size() % mod_factor;

        // If the number does not divide cleanly
        if (split_factor != 0 && UNSORTED.size() > split_number) {
            ArrayList<Character> first_half = new ArrayList<>();
            int length_first_half = UNSORTED.size() - split_factor;
            for (int i = 0; i < length_first_half; i++) {
                first_half.add(UNSORTED.get(i));
                UNSORTED.get(i).amount_shown++;
            }
            Characters.UNSORTED.add(Split(first_half, level+1));

            ArrayList<Character> second_half = new ArrayList<>();
            for (int i = length_first_half; i < UNSORTED.size(); i++) {
                second_half.add(UNSORTED.get(i));
                UNSORTED.get(i).amount_shown++;
            }
            Characters.UNSORTED.add(Split(second_half, level+1));
        }
        // If the number's digit length is greater than 2
        else if (UNSORTED.size() > split_number) {
            int num_of_arrays = UNSORTED.size() / mod_factor;
            int array_size = UNSORTED.size() / num_of_arrays;

            for (int i = 0, h = 0; i < num_of_arrays; i++) {
                ArrayList<Character> temp = new ArrayList<>();
                for (int j = 0; j < array_size; j++, h++) {
                    temp.add(UNSORTED.get(h));
                    UNSORTED.get(h).amount_shown++;
                }
                Characters.UNSORTED.add(Split(temp, level+1));
            }
        } else {
            Characters.UNSORTED.add(UNSORTED);
            for(int i = 0; i < UNSORTED.size(); i++){
                UNSORTED.get(i).amount_shown++;
            }
        }
        SORTED_COUNT += UNSORTED.size();
        return Characters;
    }
}
