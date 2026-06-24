/* 
Rocket.java
Neel Kadikar, Arsal Gazi
This program handles the rocket object with 3 stages, resting, firing up, shooting up

the rocket is like the submarinw as an object the player can go "into" but we set it as become. However this time, the rocket
is not a playable object, once you get in you win the game and you go into the end screen

The three stages are based off timing
stage 1 is the idle and it goes until the boy hops on, then the boy cant be seen
stage 2 starts after a second 60 frames and the gif is on
stage 3 it moves up into the air which takes 280 frames seconds to get to
*/

//imports
import java.awt.*;
import java.io.File;

import javax.sound.sampled.*;
import javax.swing.*;
import java.util.*;

class Rocket {
    private Image rocketIdle, rocketFire;//images
    private int x, y, width, height;
    private boolean mounted;
    private int state;
    private int timer;//states, and ints
    private boolean startedFlying;

    private Clip sound;
    private String currentSoundFile = "";
    private boolean soundLoaded = false;
    private Clip fireUpSound;//sounds
    private Clip rocketSound;
    
    public Rocket(int lvl) {
        rocketIdle = new ImageIcon("Sprites/rocket.png").getImage();
        rocketFire = new ImageIcon("Sprites/rocketFire.gif").getImage();
        mounted = false;
        state = 0;//state idle
        timer = 0;
        startedFlying = false;
        
        if(lvl == 10){
            x = 605;
            y = 313;
            width = 109;//lvl is always lvl 10
            height = 108;
        }
        else{
            x = 0;
            y = 0;
            width = 0;
            height = 0;
        }

        loadSound();
    }
    
    //updates the stae of the rocket and position
    public void update(){
        if (mounted == false){
            return;
        }

        timer++;//timer goes up
        
        if(timer >= 60 && state == 0){
            state = 1;//stage is firing up
            playSound("FireUp.wav", true);
        }
        
        if(timer >= 280 && state == 1){
            state = 2;//the stage is now in shooting up mode
            startedFlying = true;
            stopSound();
            playSound("Rocket.wav", true);
        }
        
        if(state == 2){
            y -= 3;//stage 2 is firing up and the rocket moves up
        }
    }

  //stops all the sounds oming rom rocket so it dosent play in the ending
  public void stopSounds() {
    stopSound();
    
    if(fireUpSound != null && fireUpSound.isRunning()){
        fireUpSound.stop();
    }
    if(rocketSound != null && rocketSound.isRunning()){
        rocketSound.stop();
    }
  }
    //rect
    public Rectangle getRect(){
        return new Rectangle(x, y, width, height);
    }
    
    //we set if the rocket has been mounted onto and starts the stages
    public void setMounted(boolean mm){
        mounted = mm;
        if(mounted == true){
            timer = 0;
            state = 0;
            startedFlying = false;
            stopSound();
        }
        else{
            stopSound();
        }
    }
    
    //checks if boy is n it
    public boolean isMounted() {
        return mounted;
    }
    
    //checks if the rocket is flyig
    public boolean hasStartedFlying() {
        return startedFlying;
    }
    
    //cehcks state
    public int getState() {
        return state;
    }
    
    public int getX() {
        return x;
    }
    
    public int getY() {
        return y;
    }
    
    public int getWidth() {
        return width;
    }
    
    public int getHeight() {
        return height;
    }

    //code from mr mckenzie and reddit
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

  public void stopSound(){
    if(sound != null && sound.isRunning()){
        sound.stop();
        sound.close();
        currentSoundFile = "";
    }
  }

  private void loadSound(){
        try{
            // Load FireUp.wav
            AudioInputStream fireUpStream = AudioSystem.getAudioInputStream(new File("Sounds/FireUp.wav").getAbsoluteFile());
            fireUpSound = AudioSystem.getClip();
            fireUpSound.open(fireUpStream);
            
            // Load Rocket.wav  
            AudioInputStream rocketStream = AudioSystem.getAudioInputStream(new File("Sounds/Rocket.wav").getAbsoluteFile());
            rocketSound = AudioSystem.getClip();
            rocketSound.open(rocketStream);
            
            soundLoaded = true;
            
        } catch(Exception e){
            soundLoaded = false;
            
        }
    }
    
    public void draw(Graphics g) {
        if(state == 0){//only state 0 is where the rocket is a png, otehrwise its fireing
            g.drawImage(rocketIdle, x, y, width, height, null);
        }
        else if(state == 1 || state == 2){
            g.drawImage(rocketFire, x, y, width, height, null);
        }
    }
}
