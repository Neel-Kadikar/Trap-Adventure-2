/* 
Cannon.java
Neel Kadikar, Arsal Gazi
This program handles the cannon and the timing when it shoots a missile towards the boy, and the rect of which is the boy collides with, he will die

The cannon is an object in level 8 and can shoot missiles, so it needs a timer of when to shoot missiles, when to shoot in terms of where the boy is
, and the sounds that allow it to fire

This program also takes care of missiles being drawn and where they are being drawn
*/

//Imports
import java.awt.*;
import java.io.File;

import javax.sound.sampled.*;
import javax.swing.*;
import java.util.*;

class Cannon {
    private Image cannon;//image
    private Clip sound;
    private String currentSoundFile = ""; //sound variables
    private boolean soundLoaded = false;
    private int x, y;
    private int fireTimer = 30;//timer of when the misiile shoots
    private Rectangle triggerRect;
    private Rectangle cannonRect;//rects of trigger and the cannon collide death
    private ArrayList<Missile> missiles = new ArrayList<>();//list of mkssiles

    public Cannon(int xx, int yy){
        x = xx;
        y = yy;
        triggerRect = new Rectangle(0, 93, 1280, 440);
        cannonRect = new Rectangle(x, y, 110, 110);
        cannon = new ImageIcon("Sprites/cannon.png").getImage();
        loadSound();//load the fire sound
    }

    //checks if the boy is in the rect available to shoot
    public boolean update(Boy boy){
        for(int i = missiles.size() - 1; i >= 0; i--){
            Missile m = missiles.get(i);
            m.update();

            if(m.hitsBoy(boy) == true){
                missiles.clear();
                return true;//if it hits the boy, all the missiles reset for the levl reset
            }

            if(m.offScreen() == true){
                missiles.remove(i);
            }//if the missile has exploded, its gone from the list
        }

        if(boy.getRect().intersects(triggerRect) == false){
            return false;
        }
        fireTimer++;//add 1 per frame as the timer

        if(fireTimer > 60){//every 60 frmes (1 second)
            playSound();
            missiles.add(new Missile(x + 20, y, boy));//shoot a new missile
            fireTimer = 0;
        }

        

        return false;
    }

    //returns all the missiles
    public ArrayList<Missile> getMissiles(){
        return missiles;
    }

    //clears the list for reseting levels pyurposes
    public void clearMissiles() {
        missiles.clear();
    }

    public Rectangle getRect(){
        return cannonRect;
    }

    //code form Mr Mckenzie & Reddit
    private void loadSound(){
        try{
            AudioInputStream audioStream = AudioSystem.getAudioInputStream(new File("Sounds/Shoot.wav").getAbsoluteFile());
            sound = AudioSystem.getClip();
            sound.open(audioStream);
            soundLoaded = true;
        } catch(Exception e){
            soundLoaded = false;
        }
    }

    
    //from Mr Mckenzies sound file
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

    public void draw(Graphics g){
        for(Missile m : missiles){
            m.draw(g);//draws missiles
        }
        
        g.drawImage(cannon, x, y, 110, 110, null);//draws cannon
    }
}