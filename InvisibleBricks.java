/*
InvisibleBricks.java
Neel Kadikar, Arsal Gazi
This program handles the invisible bricks in level 9. These prevent the player from entering back into the sub.
They work through the use of a trigger. If the player is in the trigger, the blocks are active and cannot be collided with.
 */

//import libraries
import java.awt.*;
import java.io.File;

import javax.sound.sampled.*;
import javax.swing.*;
import java.util.*;

//main class invisible bricks. Handles all the invisble bricks, their locations, triggers, and active status
class InvisibleBricks {

    private ArrayList<Rectangle> triggers; //arraylists for triggers and bricks
    private ArrayList<Rectangle> invisibleBricks;
    private ArrayList<Boolean> active;
    private Image invisibleBrick; //The image is in the sprites folder, it's just invisible. (joke)
    private Clip sound;
    private boolean soundLoaded = false;

    //Constructor - invisiblebricks. Adds the traps and their triggers, along with the images and arraylists necessary.
    public InvisibleBricks(int lvl) {
        triggers = new ArrayList<>();
        invisibleBricks = new ArrayList<>();
        active = new ArrayList<>();
        invisibleBrick = new ImageIcon("Sprites/invisibleBrick.png").getImage();


        if (lvl == 9) { //one one large brick in level 9 to prevent going back to the submarine
            addTrap(new Rectangle(990, 0, 400, 720),new Rectangle(880,0, 110, 720), false);
        }

        
        loadSound();
    }

    //Method - addtrap. adds the trigger and actual rectangle of the brick
    private void addTrap(Rectangle trigger, Rectangle invisibleBrickRectangle, boolean up) {
        triggers.add(trigger);
        invisibleBricks.add(invisibleBrickRectangle);
        active.add(false);
    }

    // updates the brick to be active and not allow the boy to go through if he is in the trigger rect
    public void update(Rectangle boyRect) {
        for (int i = 0; i < triggers.size(); i++) {
            if (active.get(i) == false && triggers.get(i).intersects(boyRect)) {
                playSound();
                active.set(i, true);
            }
        }
    }

    //Method to get the invisible bricks.
    public ArrayList<Rectangle> getActiveInvisibleBricks() {
        ArrayList<Rectangle> list = new ArrayList<>();
        for (int i = 0; i < invisibleBricks.size(); i++) {
            if (active.get(i) == true) {
                list.add(invisibleBricks.get(i));
            }
        }
        return list;
    }

    //Sound method - given by Mr. McKenzie
    private void loadSound(){
        try{
            AudioInputStream audioStream = AudioSystem.getAudioInputStream(new File("Sounds/Splash.wav").getAbsoluteFile());
            sound = AudioSystem.getClip();
            sound.open(audioStream);
            soundLoaded = true;
        } catch(Exception e){
            soundLoaded = false;
        }
    }

    //Sound method - given by Mr. McKenzie
    private void playSound(){
        if(soundLoaded == false){
          return;
        }
        if(sound.isRunning() == true){
            sound.stop();
        }
        sound.setFramePosition(0);
        sound.start();
    }

    //Draw. Draws in all the invisible bricks. Not much drawing going on here
    public void draw(Graphics g) {
        for (int i = 0; i < invisibleBricks.size(); i++) {
             if (active.get(i) == true) {
                Rectangle r = invisibleBricks.get(i);

                    g.drawImage(invisibleBrick, r.x, r.y, 55, 36, null);
                
            }
        }
    }
}