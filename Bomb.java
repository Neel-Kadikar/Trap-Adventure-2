/* 
Bombs.java
Neel Kadikar, Arsal Gazi
The bombs program handles the creation of bombs. It also handles the delay between spawning bombs. The game will only spawn bombs if the boy is in the submarine
If the bombs collide with a solid object, like traps from water levels, the bomb will dissapear and an explosion will play. In the game, the bombs are used to break 
the breakable bricks and also to add another layer of difficulty to the game as the player must navigate to avoid getting hit by their own bombs.
*/

//Import necessary libraries
import java.awt.*;
import java.io.File;

import javax.sound.sampled.*;
import javax.swing.*;
import java.util.*;


//Main bomb class. Handles all the information for bombs like the dimensions, and the delay between spawning them. Also handles their gravity through fall speed.
class Bomb {
    private ArrayList<Rectangle> bombs;
    private Image bomb;
    private int width, height;
    private int fallSpeed = 10;
    private int spawnWait = 0; // counts updates until next bomb
    private int spawnDelay = 200; //200 ticks between spawning bombs

    //Constructor method - bomb. sets the dimensions and image necessary.
    public Bomb(int lvl) {
        bombs = new ArrayList<>();
        width = 55;
        height = 55;
        bomb = new ImageIcon("Sprites/bomb.png").getImage();
    }

    //Method - spawnBomb. Adds bomb rectangle to the bombRect arraylist
    private void spawnBomb(Rectangle bombRect) {
        bombs.add(bombRect);
    }

    //Get method. Returns the bomb rects
    public ArrayList<Rectangle> getBombs() {
        return bombs;
    }

    //Method - update. Spawns new bombs in under two conditions: the boy is in the submarine, and the spawn delay has been reached.
    public void update(Boy boy) { //take in the boy so we can check his state.
        if (boy.isInSubmarine()) {     //only spawn if he's in the submarine
            spawnWait++;                        
            if (spawnWait >= spawnDelay) {  //update spawnwait every frame, and then spawn a new bomb in if it is reached. Based on the boy's location so it falls from the sub    
                spawnBomb(new Rectangle(boy.getX(), boy.getY(), width, height)); 
                spawnWait = 0;                   
            }
        }

        for (int i = 0; i < bombs.size(); i++) { //bring the bombs down via their fallspeed.
            Rectangle r = bombs.get(i);
            if (r.y < 1920){
                r.y += fallSpeed;
            }            

        }
        
        
    }

    //Method - remove bomb. Removes bombs, used for exploded bombs
    public void removeBomb(Rectangle removeBombRect){
        bombs.remove(removeBombRect);
    }

    //Get method - getRect. Returns the bomb rect
    public Rectangle getRect(){
        if (bombs.size()>0){
            Rectangle bombRect = bombs.get(0);
            return bombRect;    
        }
        else{
            return (new Rectangle (0,0,0,0));
        }
    }

    //Draw Method. Draws in all the bombs that are active
    public void draw(Graphics g) {
        for (int i = 0; i < bombs.size(); i++) {
            Rectangle r = bombs.get(i);
            g.drawImage(bomb, r.x, r.y, 55, 55, null);
        }
    }
}
