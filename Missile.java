/* 
Missile.java
Neel Kadikar, Arsal Gazi
This program handles the missile object, the alignment with the boy, the time to shoot, the wall hit, and the explosion

The class is used in Level 8 where there are different timers for each state of the missile
The missile has 4 main states, the alignment, the pause, the shoot, and the hit each done for each missile

The alignment state occurs right when the missile spawns where the rect goes up until the boy rects y (x is fixed)
The pause state occurs to give boy time to move in time before the missile shoots (x and y are fixed)
The shoot state occurs when the missile shoots FAST horizontally on the same x axis (y is fixed)
The hit state occurs when the missile hits the wall with a constant y of 1155 and the missile explodes
*/

//Improts
import java.awt.*;
import java.io.File;

import javax.sound.sampled.*;
import javax.swing.*;
import java.util.*;

class Missile{

    private Rectangle rect;//rect of collision
    private Image img;
    private Image explosion;//image for missile and explosion
    private Clip sound;
    private String currentSoundFile = "";//sound for explosion
    private boolean soundLoaded = false;

    private boolean align = true;
    private boolean paused = false;
    private boolean exploded = false;
    private boolean hitwall = false;
    private int pauseTimer = 0;
    private int alignTimer = 0;
    private int explodeTimer = 0;
    //timers and boolean for checking and timing the 4 states

    private int targetY;//the y we want the missile to be in

    //This class loads the missile and saves a Y position for the Missile to go to, as boy y is always constant
    public Missile(int startX, int startY, Boy boy) {
        rect = new Rectangle(startX, startY, 70, 55);
        img = new ImageIcon("Sprites/missile.png").getImage();
        explosion = new ImageIcon("Sprites/explosion.gif").getImage();

        targetY = boy.getRect().y;//saves old boy position
        loadSound();//load sound
    }

    //updates missile states
    public void update(){
        if(exploded == true){
          explodeTimer++;
          return;
        }

        if(hitwall == false && rect.x + rect.width >= 1155){
          playSound();
          rect.x = 1155 - rect.width;
          hitwall = true;
          exploded = true;
          explodeTimer = 0;
          return;
        }//hitwall is now here and eplode sound

        if(hitwall == true){
            return;
        }

        if(align == true){
          alignTimer++; //aligns the missile to the boy

          if(rect.y < targetY){
            rect.y += 20;
            if(rect.y > targetY){
                rect.y = targetY;
            }
            if(rect.y < 93){
                rect.y = 93;
            }//bounds
          }
          else if(rect.y > targetY){
            rect.y  = rect.y - 55;
          }//bounds
        
          if(rect.y == targetY || alignTimer >= 20){
            align = false;
            paused = true;
            pauseTimer = 0;
          }//if the timer to aling ended or the rect found the boy, it moves to the pause state

        return;
        }

        if(paused == true){
            pauseTimer++;
            if (pauseTimer >= 10) {
                paused = false;
            }
            return;
        }//pause state is just a timer until it ends

        rect.x += 60;//then it shoots into the shoot state
    }

    //checks if the missile hit the boy
    public boolean hitsBoy(Boy boy){
        if(exploded == true){
            return false;
        }//if missile not here, it didnt touch
        return rect.intersects(boy.getRect());//check intersection return
    }

    //check if the missile exploded
    public boolean offScreen(){
        if(exploded == true){
            return explodeTimer >= 30;//if yes, check if the timer is timer s bigger than 30 indicating its gone
        }
        return rect.x + rect.width < 0;
    }

    //check if it exploded
    public boolean hasExploded() {
        return exploded;
    }

    //code form Mr Mckenzie & Reddit
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

    private void playSound(){
        explosion.flush();
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
        //if it exploded, no missile = explosion time
        if(exploded == true){
            explodeTimer++;
            if(explodeTimer == 1){ 
                explosion = new ImageIcon("Sprites/explosion.gif").getImage();
                //since we want the explosion gif to start at frame 1, we load it in again
            }
            g.drawImage(explosion, rect.x, rect.y, 70, 55, null);
        }
        else{//load the missile
          g.drawImage(img, rect.x, rect.y, 70, 55, null);
        }
    }
}
