/*
Shurikens.java
Neel Kadikar, Arsal Gazi
This program handles the shurikens for the level in which the player acquires the sub. These chase the player and add difficulty to the game. They function
by checking the player's x and y values and adjusting their values to get closer to the players. These are only found in level 8.
*/


//Import necessary libraries
import java.awt.*;
import javax.swing.*;
import java.util.*;

//Main class - Shurikens. Houses necessary methods and initializes necessary arraylists for the shurikens, like triggers, active, and shurikens
class Shurikens {

    private ArrayList<Rectangle> triggers; //triggers - activate them when the player is in this rect
    private ArrayList<Rectangle> shurikens; 
    private ArrayList<Boolean> active;
    private Image shuriken;
    private int shurikenSpeed = 1;



    //Constructor method - shurikens. Creates the arraylists, and adds the traps of the level.
    public Shurikens(int lvl) {
        triggers = new ArrayList<>();
        shurikens = new ArrayList<>();
        active = new ArrayList<>();
        shuriken = new ImageIcon("Sprites/shuriken.png").getImage();
        //add all the traps in level 8
        if (lvl == 8) {
            addTrap(new Rectangle(0, 225, 385, 385), new Rectangle(165, 390, 55, 55));
            addTrap(new Rectangle(0, 315, 385, 385), new Rectangle(165, 480, 55, 55));

            
            addTrap(new Rectangle(110, 225, 385, 385), new Rectangle(275, 390, 55, 55));
            addTrap(new Rectangle(110, 315, 385, 385), new Rectangle(275, 480, 55, 55));

            
            addTrap(new Rectangle(220, 225, 385, 385), new Rectangle(385, 390, 55, 55));
            addTrap(new Rectangle(220, 315, 385, 385), new Rectangle(385, 480, 55, 55));

            
            addTrap(new Rectangle(330, 225, 385, 385), new Rectangle(495, 390, 55, 55));
            addTrap(new Rectangle(330, 315, 385, 385), new Rectangle(495, 480, 55, 55));

            
            addTrap(new Rectangle(440, 225, 385, 385), new Rectangle(605, 390, 55, 55));
            addTrap(new Rectangle(440, 315, 385, 385), new Rectangle(605, 480, 55, 55));

            
            addTrap(new Rectangle(550, 225, 385, 385), new Rectangle(715, 390, 55, 55));
            addTrap(new Rectangle(550, 315, 385, 385), new Rectangle(715, 480, 55, 55));
            

            
        }
    }

    //Method - add trap. Takes in the two rects, one for the trigger (when to active the shuriken) and one for the shuriken itself.
    private void addTrap(Rectangle trigger, Rectangle shuriken) {
        triggers.add(trigger); //add the trigger, shuriken, and its spot in value
        shurikens.add(shuriken);
        active.add(false);
    }

    //Method - update. Responsible for spawning new shurikens based on a time delay
    // also for checking for if the boy is in a trigger, and moving active shurikens toward the boy
    public void update(Rectangle boyRect, int lvl){
    

    // activate shurikens if boy touches their trigger
    for (int i = 0; i < triggers.size(); i++) {
        if (!active.get(i) && triggers.get(i).intersects(boyRect)){
            active.set(i, true);
        }
    }

    // move active shurikens toward boy
    for (int i = 0; i < shurikens.size(); i++) {
        if (active.get(i) == true) {
            
            Rectangle r = shurikens.get(i);

            int originalX = r.x;
            int originalY = r.y;
            //move in the appropriate direction depending on if the boy is to the left, right, up, or down

            if (r.y < boyRect.y){ 
                r.y += shurikenSpeed;
            }
            else {
                r.y -= shurikenSpeed;
            }

            if (r.x < boyRect.x){
                r.x += shurikenSpeed;
            }
            else{

                r.x -= shurikenSpeed;
            }
        
      
            //Prevents shurikens from overlapping with each other - they should not stack
            for (int j = 0; j < shurikens.size(); j++) {
            if (i == j || !active.get(j)){
                continue;
            }

            //stop the movement if they do collide
            if (r.intersects(shurikens.get(j))) {
                r.x = originalX;
                r.y = originalY;
                break;
            }
        }
        }
    }
    }

    //Method - getactiveshurikens. Return the active shurikens
    public ArrayList<Rectangle> getActiveShurikens() {
        ArrayList<Rectangle> list = new ArrayList<>();
        for (int i = 0; i < shurikens.size(); i++) {
            if (active.get(i) == true) {
                list.add(shurikens.get(i));
            }
        }
        return list;
    }

    //Method - getShurikens. returns active and inactive shurikens in a new arraylist
    public ArrayList<Rectangle> getShurikens() {
        ArrayList<Rectangle> allShurikens = new ArrayList<>();
        for (int i = 0; i < shurikens.size(); i++) {
                allShurikens.add(shurikens.get(i));
        }
        return allShurikens;
    }

    //Method - draw. Draws the shurikens.
    public void draw(Graphics g){
        for (int i = 0; i < shurikens.size(); i++) {
                Rectangle r = shurikens.get(i);
                
                g.drawImage(shuriken,r.x,r.y,55,55,null);
        }
    }
}