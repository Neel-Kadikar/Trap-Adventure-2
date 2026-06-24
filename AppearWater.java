// AppearWater.java
// Neel Kadikar and Arsal Gazi
// This class creates a secret water path that appears when the player enters a certain trigger area. This is used in level 6.
// It works by creating two rects, a trigger and a path block. When the player enters the trigger rect, the path block becomes active and is drawn on the screen.
// The path blocks are drawn on top of blocks that are simply brick images, not solid brick blocks. Our checking for the boy is different for these two, as he can move
// through the images but not the solid blocks. So, the path is drawn on top of the "ghost bricks", and the player is then able to complete the level by swimming through the water path.


//Import the necessary libraries
import java.awt.*;
import java.io.File;

import javax.sound.sampled.*;
import javax.swing.*;
import java.util.*;

//Main class - appearWater. handles the triggers, blocks, and hiding of the hidden water path.
class AppearWater {

    private ArrayList<Rectangle> triggers; //arraylist for the triggers of the path blocks. Player touches trigger, path appears
    private ArrayList<Rectangle> pathBlocks; //arraylist for the path blocks rects 
    private ArrayList<Boolean> active; //arraylist to help track active path blocks. uses true and false for active inactive
    private Image water;
    private Clip sound;
    private String currentSoundFile = "";
    private boolean soundLoaded = false;

    //Constructor - AppearWater. Adds all the hidden path blocks for the level 6 and level 7. Works by having a trigger, and when the boy enters that 
    //trigger rect, the path appears. Otherwise, it is invisible.
    public AppearWater(int lvl) {
        triggers = new ArrayList<>(); //arraylists to handles the triggers and path blocks
        pathBlocks = new ArrayList<>();
        active = new ArrayList<>();
        water = new ImageIcon("Sprites/appearWater.png").getImage();

        //Main use - in  level 6. Creates the hidden water path necessary to complete the level.
        if (lvl == 6) {
            loadSound();
            addTrap(new Rectangle(495,110, 55, 55),new Rectangle(495,93, 55, 55)); 
            addTrap(new Rectangle(495,110, 55, 55),new Rectangle(440,93, 55, 55)); 
            addTrap(new Rectangle(495,110, 55, 55),new Rectangle(385,93, 55, 55)); 
            addTrap(new Rectangle(495,110, 55, 55),new Rectangle(385,38, 55, 55)); 
            addTrap(new Rectangle(495,110, 55, 55),new Rectangle(330,38, 55, 55)); 
            addTrap(new Rectangle(495,110, 55, 55),new Rectangle(275,38, 55, 55)); 
            addTrap(new Rectangle(495,110, 55, 55),new Rectangle(220,38, 55, 55)); 
            addTrap(new Rectangle(495,110, 55, 55),new Rectangle(165,38, 55, 55)); 
            addTrap(new Rectangle(495,110, 55, 55),new Rectangle(110,38, 55, 55)); 
            addTrap(new Rectangle(495,110, 55, 55),new Rectangle(55,38, 55, 55)); 
            addTrap(new Rectangle(495,110, 55, 55),new Rectangle(0,38, 55, 55));
            addTrap(new Rectangle(495,110, 55, 55),new Rectangle(385,93, 55, 55)); 
            addTrap(new Rectangle(495,110, 55, 55),new Rectangle(330,93, 55, 55)); 
            addTrap(new Rectangle(495,110, 55, 55),new Rectangle(275,93, 55, 55)); 
            addTrap(new Rectangle(495,110, 55, 55),new Rectangle(220,93, 55, 55)); 
            addTrap(new Rectangle(495,110, 55, 55),new Rectangle(165,93, 55, 55)); 
            addTrap(new Rectangle(495,110, 55, 55),new Rectangle(110,93, 55, 55)); 
        }

        //Creates the transition blocks for level 7
        if(lvl == 7){ 
            addTrap(new Rectangle(1210,38, 55, 55),new Rectangle(1210,38, 55, 55)); 
            addTrap(new Rectangle(1210,38, 55, 55),new Rectangle(1210 + 55,38, 55, 55));
            addTrap(new Rectangle(1210,38, 55, 55),new Rectangle(1210 - 55,38, 55, 55));
        }

    }

    //Method - addTrap. Takes in two parameters, the trigger for the path and the path block itself. If the player collides with the trigger the path becomes active
    private void addTrap(Rectangle trigger, Rectangle path) {
        triggers.add(trigger); //add the triggers, pathblock, and add a spot to the active arraylist to help track it.
        pathBlocks.add(path);
        active.add(false);
    }

    //Method - update. If the boy intersects the trigger rect, set it to active.
    public void update(Rectangle boyRect) {
        for (int i = 0; i < triggers.size(); i++) { //check each trigger
            if (active.get(i) == false && triggers.get(i).intersects(boyRect)) {
                active.set(i, true); //set the respective position to active in active arraylist
                playSound();
            }
        }
    }

    //Method getActivePathBlocks - simply returns all the path blocks that are current active via a for loop, and checking the place in active.
    public ArrayList<Rectangle> getActivePathBlocks() {
        ArrayList<Rectangle> list = new ArrayList<>();
        for (int i = 0; i < pathBlocks.size(); i++) { //go through all pathblocks
            if (active.get(i) == true) {
                list.add(pathBlocks.get(i)); //add it to the list to be returned if it is active
            }
        }
        return list;
    }

    //Method - loadSound. Given by Mr. McKenzie
    private void loadSound(){
        try{
            AudioInputStream audioStream = AudioSystem.getAudioInputStream(new File("Sounds/Secret.wav").getAbsoluteFile());
            sound = AudioSystem.getClip();
            sound.open(audioStream);
            soundLoaded = true;
        } catch(Exception e){
            soundLoaded = false;
        }
    }

    //Method - playSound. Given by Mr. McKenzie
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

    //Method - draw. Goes through each path block, if it's actives, draws it.
    public void draw(Graphics g) {
        for (int i = 0; i < pathBlocks.size(); i++) {
            if (active.get(i)) {
                Rectangle r = pathBlocks.get(i);
                
                g.drawImage(water,r.x,r.y,55,55,null);
            }
        }
    }
}