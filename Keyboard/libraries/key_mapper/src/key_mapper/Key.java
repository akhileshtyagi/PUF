package key_mapper;

/**
 * TODO list
 * [ ] test contains_coordinate()
 */

/**
 * Represents a key in the keyboard class
 */
public class Key {
    int x, y, width, height;
    char character;

    /**
     * construct a key with x, y, width, height
     *
     * the key can tell whether a pair of x,y are inside the key given x,y
     *
     * the coordinates of the key are relative to the upper left corner of the keyboard
     * in other words the upper left corner of the keyboard is 0,0
     */
    public Key(int x, int y, int width, int height, char character){
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.character = character;
    }

    public char get_character(){
        return this.character;
    }

    /**
     * return true if the key contains the given coordinate
     */
    public boolean contains_coordinate(int x, int y){
        // determine if x,y fit within the bounds
        boolean x_fit = (x >= this.x) && (x <= this.x + this.width);
        boolean y_fit = (y >= this.y) && (y <= this.y + this.height);

        return x_fit && y_fit;
    }
}
