/*
Submarine.java
Neel Kadikar, Arsal Gazi
This program is for the submarine that the player needs to collide with to change their move. When the player first sees the submarine, they must enter its
rect to change from swim to submarine mode. It also appears when the player dismounts the submarine. However, the player is unable to enter the the 
submarine second time around due to invisible Bricks. 
*/

//Import necessary libraries
import java.awt.*;
import javax.swing.*;
import java.util.*;


//Main class - Submarine. This handles the dimensions of the submarine, and booleans like whether it has been mounted or not, and if it should be visible or not
class Submarine {
    private Image submarine;
    private int x, y, width, height; //submarine dimensions
    private boolean mounted, visible;    

    //Constructor - submarine. Handles the scenarios and locations of the submarine in levels 8 and 9. Handles where to draw it and the unique sprite
    public Submarine(int lvl) {
        mounted = false;

        // for level 8 - before it is collected
        if (lvl == 8){
            x = 622;
            y = 50;
            width = 77;
            height = 63;
            submarine = new ImageIcon("Sprites/sub.png").getImage();
        }

        //For level 9 - when the boy dismounts
        if(lvl == 9){
            x = 900;
            y = 423 - 63 + 8;
            width = 77;
            height = 63;
            submarine = new ImageIcon("Sprites/dismount.png").getImage();
        }

        

    }

    //Method - getSubRect. returns the sub rect
    public Rectangle getSubRect(){
        return new Rectangle(x, y, width, height);
    }

    //Method - set mounted. sets the boolean mounted.
    public void setMounted(boolean m){
        mounted = m;
    }

    //Method - setvisible. sets the boolean for visible
    public void setVisible(boolean v) {
        visible = v;
    }

    //Method - clear. Simply clears the subway by setting its dimensions to 0 all around
    public void clear(){
        x = 0;
        y = 0;
        width = 0;
        height = 0;
    }

    
    //Method - draw. Does not draw the submarine if the boy has collected it and is now in it, and draws the dismount if he has done so.
    public void draw(Graphics g, Boy boy) {
        if (boy.isInSubmarine()){
            return;
        }
        if (boy.hasDismounted()){
            return;
        }

        g.drawImage(submarine, x, y, null);
    }
}
