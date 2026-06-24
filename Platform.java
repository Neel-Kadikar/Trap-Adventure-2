/*
Platform.java
Neel Kadikar, Arsal Gazi
This handles the platform for level 5. the player can stand on the platform, moves along with it, and can launch balls to break blocks from it.
The platform has different switches and sprites for the directions. This program also handles for if the platform has the boy, detaching and attaching him.

 */

//import libraries
import java.awt.*;
import java.io.File;
import javax.sound.sampled.*;
import javax.swing.*;
import java.util.*;

//Main class - platform. Handles the platform's dimensions, sprites, and if it has the boy or not.
class Platform {
    public int x, y, width, height;
    private Image idle, boy, left, right;
    private boolean hasBoy;
    private Clip sound;
    private String currentSoundFile = "";

    //Constructor - platform. Describes platform dimensions and sprites
    public Platform(int xx, int yy) { 
        x = xx;
        y = yy - 30;
        width = 162;
        height = 63;

        idle = new ImageIcon("Sprites/platform.png").getImage();
        boy = new ImageIcon("Sprites/platformIdle.png").getImage();
        left = new ImageIcon("Sprites/platformLeft.png").getImage();
        right = new ImageIcon("Sprites/platformRight.png").getImage();

        hasBoy = false;
    }

    //Method - getRect. Returns the rect
    public Rectangle getRect(){
        return new Rectangle(x, y + 37, width, 26);
    }

    // method - hasBoy(). Returns if boy is on the platform
    public boolean hasBoy(){
        return hasBoy;
    }

    //Method - attach. Attaches the boy to the platform and updates boolean has boy
    public void attach(Boy boy){
        stopSound();
        hasBoy = true;
        boy.attach(this);
    }

    // method - detatch. Detatches the boy and updates boolean hasboy
    public void detach(Boy boy){
        stopSound();
        hasBoy = false;
        boy.detach();
    }

    //Method - move. Moves the platform in the provided direction by the player at a given rate (6)
    public void move(String direction){
        if(x > 169 && direction.equals("left")) {
            x -= 6;
        }
        if(x < 1204 - width && direction.equals("right")) {
            x += 6;
        }
    }

    //Method - resetPos. Resets the position of the platform in the case that the level is reset as the player has died
    public void resetPos(int lvl){
        stopSound();
        hasBoy = false;
        if(lvl == 5){
          x = 167;
          y = 0;
        }
    }

    //Sound method - given by Mr. McKenzie
    public void playSound(String soundFile, boolean loop) {
    try{
        if(sound != null && sound.isRunning() && currentSoundFile != null && currentSoundFile.equals(soundFile)){
            return;
        }
        
        if(sound != null && sound.isRunning()){
            sound.stop();
            sound.close();
        }
        
        AudioInputStream audioStream = AudioSystem.getAudioInputStream(new File("Sounds/" + soundFile).getAbsoluteFile());
        sound = AudioSystem.getClip();
        sound.open(audioStream);
        
        if(loop == true){
            sound.loop(Clip.LOOP_CONTINUOUSLY);
        }
        else {
            sound.start();
        }
        
        currentSoundFile = soundFile;
        
    } catch (Exception e) {
        
    }
  }

  //Sound method - given by Mr. McKenzie
  public void playSound(String soundFile){
    playSound(soundFile, false);
  }

  //Sound method - given by Mr. McKenzie
  public void stopSound(){
    if(sound != null && sound.isRunning()){
        sound.stop();
        sound.close();
        currentSoundFile = "";
    }
  }

  //Method - draw. Draws in the platform in its different states of not/having the boy, and not/moving
    public void draw(Graphics g, boolean movingLeft, boolean movingRight){
        if(hasBoy == false){
            g.drawImage(idle, x, y, width, height, null);
        }
        else if(movingLeft){
            playSound("Platform.wav", true);
            g.drawImage(left, x, y, width, height, null);

        }
        else if(movingRight) {
            playSound("Platform.wav", true);
            g.drawImage(right, x, y, width, height, null);
        }
        else{
            stopSound();
            g.drawImage(boy, x, y, width, height, null);
        }
    }
}


