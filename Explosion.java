/*
Explosions.java
Neel Kadikar, Arsal Gazi

This class handles the explosion effects from the bombs of the submarine. It handles the spawning of the explosions at the right occurences (when a bomb lands on something),
the drawing of the explosion gifs, the playing of the sound, and the rects. The collisions between the explosion and other objects, like the boy, shurikens, or bricks
are checked in other classes. When the explosion occurs, it is dependent on the bomb's location to keep a cohesive game look. Additionally, the explosions
are limited in how long they are visible by the clearDelay. The clear wait gets larger every tick, and once it reaches the clear delay number, the explosions are cleared.
This avoids keeping them visible for too long to keep a natural look, as an explosion would not repeat itself multiple times.


In the game itself, the bombs and their explosions serve as a tool and a trap. If the player gets caught in the explosion, they will die. 
However, they can use it to their advantage to break certain walls to advance in levels.
*/


// import the necessary libraries

import java.awt.*;
import java.io.File;

import javax.sound.sampled.*;
import javax.swing.*;
import java.util.*;

//Class - explosion
//This is where the rects, image, dimensions, and more are handled for the explosion. It handles all the necessary methods for explode, with the 
//Delay and it's clearing in the draw method being particularly important for resetting the explosions and their graphics.
class Explosion {
    private ArrayList<Rectangle> explosions;
    //private ArrayList<Boolean> active;
    private Image explosion;
    private int width, height;
    //private int fallSpeed = 10;
    private int clearWait = 0; 
    private int clearDelay = 50;

    private Clip sound;
    private String currentSoundFile = "";
    private boolean soundLoaded = false;
    private Rectangle drawR;
    
    //Constructor method - explosion.
    //initializes the explosions arraylist, the gif for the explosion, and establishes the width and height.
    public Explosion(int lvl) {
        explosions = new ArrayList<>();
        explosion = new ImageIcon("Sprites/explosion.gif").getImage(); //actually creates the arraylist
        
        width = 400;
        height = 400;
        loadSound();

    }

    //Method - getExplosions. returns explosions
    public ArrayList<Rectangle> getExplosions() {
        return explosions;
    }
    

    // Method - explode. Adds the explosion to the explosions arraylist
    public void explode(Rectangle bombRect){
        playSound();
        explosion.flush();
        explosion = new ImageIcon("Sprites/explosion.gif").getImage();
        explosions.add(new Rectangle(bombRect.x, bombRect.y, width, height));   
    }

    //Method - remove explosion. Removes explosion from the arraylist.
    public void removeExplosion(Rectangle removeExplosionRect){
        explosions.remove(removeExplosionRect);
    }

    //Get method - returns the width
    public int getWidth(){
        return width;
    }
    
    //Get method - returns the height
    public int getHeight(){
        return height;
    }

    //Get method - gets the rect for explosions
    public Rectangle getRect(){
        if (explosions.size()>0){
            Rectangle explosionRect = explosions.get(0);
            return explosionRect;    
        }
        else{
            return (new Rectangle (0,0,0,0));
        }
    }

    //Method - loads the sounds. From Mr. McKenzie's given file
    private void loadSound(){
        try{
            AudioInputStream audioStream = AudioSystem.getAudioInputStream(new File("Sounds/Explode.wav").getAbsoluteFile());
            sound = AudioSystem.getClip();
            sound.open(audioStream);
            soundLoaded = true;
        } catch(Exception e){
            soundLoaded = false;
        }
    }

    //Method - plays the sounds. From Mr. McKenzie's file
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

    //Method - draw. Draws in all the explosions and handles the delay for clearing the explosions' gifs.
    public void draw(Graphics g) {
        if(explosions.size() > 0){ //don't draw if there are no explosions active
          drawR = explosions.get(0);
        }
        if(drawR != null){
          g.drawImage(explosion, drawR.x-drawR.width/2, drawR.y-drawR.height/2, drawR.width, drawR.height, null);
        }
        
        clearWait++; //increase clearWait every tick
        if(clearWait>clearDelay){ //if the clear delay has been reached, clear out the explosions and reset clearWait.
            explosions.clear();
            clearWait=0;
        }
        
    }
}
