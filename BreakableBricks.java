/* 
BreakableBricks.java
Neel Kadikar, Arsal Gazi
This program handles the breakable bricks in levels with the submarine after receiving it. The breakable bricks can be broken by the bombs dropped by the submarine
They work by becoming inactive after being caught in the bomb explosion radius, and then dissapear
They also work in that the bomb will dissapear after colliding with them.

*/

//Import necessary libraries
import java.awt.*;
import javax.swing.*;
import java.util.*;

//Main class - BreakableBricks. Creates the necessary arraylists for breakable bricks like active, breakableBricks. Houses all the necessary methods.
class BreakableBricks {
    private ArrayList<Rectangle> breakableBricks; //arraylist for current breakable bricks
    private ArrayList<Boolean> active; //to track which bricks to keep and remove
    private Image brick;

    //Constructor - breakable bricks. Creates all the necessary breakable bricks for the levels after the sub is collected.
    public BreakableBricks(int lvl) {
        breakableBricks = new ArrayList<>();
        active = new ArrayList<>();
        brick = new ImageIcon("Sprites/brick.png").getImage();
        //add all the bricks of level 9
        if (lvl==9){
            addTrap(new Rectangle(220, 495 - 17, 55, 55));
            addTrap(new Rectangle(220, 495 + 55 - 17, 55, 55));
            addTrap(new Rectangle(220, 495 + 2*55 - 17, 55, 55));
            addTrap(new Rectangle(220, 495 + 3*55 - 17, 55, 55));
            
           
            addTrap(new Rectangle(825, 550 -17, 55, 55));
            addTrap(new Rectangle(825, 605 -17, 55, 55));
            addTrap(new Rectangle(825, 605 -17+55, 55, 55));

            addTrap(new Rectangle(715, 215 -12 + 3*55, 55, 55));
            addTrap(new Rectangle(770, 215 -12 + 3*55, 55, 55));
        }

    }

    //Method - addTrap. Takes in the rect for the breakable brick and adds it to active
    private void addTrap(Rectangle breakableBrick) {
    
        breakableBricks.add(breakableBrick);
        active.add(true);
    }

    //Update - sets it to inactive (false in the arraylist) if it is in contact with the bomb
    public void update(Rectangle bombRect) {
        for (int i = 0; i < breakableBricks.size(); i++){
            if (active.get(i) == true && breakableBricks.get(i).intersects(bombRect)) {
                active.set(i, false);
            }
        }
    }

    //getActiveBreakableBricks - simply returns the active (not broken) breakable bricks
    public ArrayList<Rectangle> getActiveBreakableBricks() {
        ArrayList<Rectangle> list = new ArrayList<>();
        for (int i = 0; i < breakableBricks.size(); i++) {
            if (active.get(i) == true) {
                list.add(breakableBricks.get(i)); //add it to the list if it's active
            }
        }
        return list;
    }

    //Method - draw. Draws in all the active breakable bricks with same dimensions as a regular brick. They should blend in!
    public void draw(Graphics g) {
        for (int i = 0; i < breakableBricks.size(); i++) {
            if (active.get(i)) {
                Rectangle r = breakableBricks.get(i);
                
                g.drawImage(brick,r.x,r.y,55,55,null);
            }
        }
    }
}