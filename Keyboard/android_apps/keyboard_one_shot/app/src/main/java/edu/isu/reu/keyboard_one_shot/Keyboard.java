package edu.isu.reu.keyboard_one_shot;

import java.util.ArrayList;
import java.util.List;

/**
 * represents a keyboard
 * returns the key for a given x,y coordinate
 *
 * NOTE nothing is currently done with separation between keys
 */
public class Keyboard {
    // used to store the x,y offset of the keyboard on the screen
    // these are only used externally
    public int x_offset, y_offset;

    List<Key> key_list;
    int keyboard_width, keyboard_height, key_separation_x, key_separation_y;

    /**
     * initialize the keyboard
     * define the locations of all the keys given the length, width, and key separation
     *
     * length = length of the keyboard
     * width = width of the keyboard
     * key_weparation = the amount of separation between keys on the keyboard
     *
     * the upper left corner of the keyboard is 0,0
     */
    public Keyboard(int keyboard_width, int keyboard_height, int key_separation_x, int key_separation_y){
        this.keyboard_width = keyboard_width;
        this.keyboard_height = keyboard_height;
        this.key_separation_x = key_separation_x;
        this.key_separation_y = key_separation_y;
        this.key_list = new ArrayList<>();

        // create first row
        for(int i=0; i<10; i++){
            int x = keyboard_width / 10 * i;
            int y = 0;
            int key_width = keyboard_width / 10;
            int key_height = keyboard_height / 4;

            switch(i){
                case 0:
                    this.key_list.add(new Key(x, y, key_width, key_height, 'q'));
                case 1:
                    this.key_list.add(new Key(x, y, key_width, key_height, 'w'));
                case 2:
                    this.key_list.add(new Key(x, y, key_width, key_height, 'e'));
                case 3:
                    this.key_list.add(new Key(x, y, key_width, key_height, 'r'));
                case 4:
                    this.key_list.add(new Key(x, y, key_width, key_height, 't'));
                case 5:
                    this.key_list.add(new Key(x, y, key_width, key_height, 'y'));
                case 6:
                    this.key_list.add(new Key(x, y, key_width, key_height, 'u'));
                case 7:
                    this.key_list.add(new Key(x, y, key_width, key_height, 'i'));
                case 8:
                    this.key_list.add(new Key(x, y, key_width, key_height, 'o'));
                case 9:
                    this.key_list.add(new Key(x, y, key_width, key_height, 'p'));
            }

        }

        // create second row
        for(int i=0; i<9; i++){
            // want to offset by half a key for x ( keyboard_width / 20 )
            int x = (keyboard_width / 10 * i) + (keyboard_width / 20);
            int y = keyboard_height / 4;
            int key_width = keyboard_width / 10;
            int key_height = keyboard_height / 4;

            switch(i){
                case 0:
                    this.key_list.add(new Key(x, y, key_width, key_height, 'a'));
                case 1:
                    this.key_list.add(new Key(x, y, key_width, key_height, 's'));
                case 2:
                    this.key_list.add(new Key(x, y, key_width, key_height, 'd'));
                case 3:
                    this.key_list.add(new Key(x, y, key_width, key_height, 'f'));
                case 4:
                    this.key_list.add(new Key(x, y, key_width, key_height, 'g'));
                case 5:
                    this.key_list.add(new Key(x, y, key_width, key_height, 'h'));
                case 6:
                    this.key_list.add(new Key(x, y, key_width, key_height, 'j'));
                case 7:
                    this.key_list.add(new Key(x, y, key_width, key_height, 'k'));
                case 8:
                    this.key_list.add(new Key(x, y, key_width, key_height, 'l'));
            }

        }

        // create third row
        // this row is different because it has shift and backspace keys
        //TODO implement shift and backspace
        for(int i=0; i<7; i++){
            // want to offset by 1.5 keys for x ( keyboard_width / 20 * 3)
            int x = (keyboard_width / 10 * i) + (keyboard_width / 20 * 3);
            // third row is 2 key heights down
            int y = keyboard_height / 4 * 2;
            int key_width = keyboard_width / 10;
            int key_height = keyboard_height / 4;

            switch(i){
                case 0:
                    this.key_list.add(new Key(x, y, key_width, key_height, 'z'));
                case 1:
                    this.key_list.add(new Key(x, y, key_width, key_height, 'x'));
                case 2:
                    this.key_list.add(new Key(x, y, key_width, key_height, 'c'));
                case 3:
                    this.key_list.add(new Key(x, y, key_width, key_height, 'v'));
                case 4:
                    this.key_list.add(new Key(x, y, key_width, key_height, 'b'));
                case 5:
                    this.key_list.add(new Key(x, y, key_width, key_height, 'n'));
                case 6:
                    this.key_list.add(new Key(x, y, key_width, key_height, 'm'));
            }

        }

        // create 4th row
        //TODO implement other keys
        // the only key implemented here right now is space
        // want to offset by 2.5 keys for space ( keyboard_width / 20 * 5)
        int x = (keyboard_width / 10) + (keyboard_width / 20 * 5);
        // fourth row is 3 key heights down
        int y = keyboard_height / 4 * 3;
        // space is 5 keys wide
        int key_width = keyboard_width / 10 * 5;
        int key_height = keyboard_height / 4;

        this.key_list.add(new Key(x, y, key_width, key_height, ' '));
    }

    /**
     * return the character associated with this coordinate pair
     */
    public char get_character(int x, int y){
        // for every element in key_list
        for(Key key : this.key_list){
            // test if this key contains the x,y coordinate
            if(key.contains_coordinate(x, y)){
                // it does contain it, so return the character associated with this key
                return key.get_character();
            }
        }

        // no character was found return error character
        return '#';
    }

    public int get_height(){
        return this.keyboard_height;
    }

    public int get_width(){
        return this.keyboard_width;
    }
}
