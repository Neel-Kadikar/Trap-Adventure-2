/*
Ball.java
Neel Kadikar, Arsal Gazi
This handles the ball for level 5. In the original game, this level appears to be heavily inspired by arkanoid. We some ideas from our arkanoid classes
and changed them greatly to suit the new game. The ball is released when the player is on a platform in level 5, and can break the bricks in said level
If the player touches the ball, they die and must restart the level.
*/

//importing necessary libraries
import java.awt.*; 
import java.awt.event.*;
import java.io.File;
import javax.sound.sampled.*;
import javax.swing.*;
import java.util.*;


//Main class - ball. Holds the ball x,y, velocity, dimensions, and images. Houses all the methods necessary for ball.
public class Ball {
    private int x,y,vx,vy;
    private int speed;
    private int width, height;
    private Image ball;
    private Clip sound;
    private String currentSoundFile = "";
    private boolean soundLoaded = false;

    //Constructor - ball. Assigns values to the variables previously defined. Establishes ball width height and velocity.
    public Ball(int xx, int yy){
        x = xx; //ball dimensions
        y = yy;
        width = 41;
        height = 41;
        vx = 10;
        vy = 10;
        ball = new ImageIcon("Sprites/ball.png").getImage();
        loadSound();
    }


    //same as arkanoid ball with a velocityx and verlocity y
    public void move(Blocks blocks, Platform platform, Level lvl){
        //we add to the x and check for collisions with the level/bourdaries/blocks
        x += vx;
        for(int i = blocks.getSolid().size() - 1; i >= 0; i--){
          Rectangle rec = blocks.getSolid().get(i);
          if(getRect().intersects(rec)){
            if(vx > 0){
              x = rec.x - width;
            } 
            else{
              x = rec.x + rec.width;
            } 
            blocks.removeBlock(i);
            vx *= -1;//bounce off
            playBounceSound();
          }
        }
        for(Rectangle rec : lvl.getSolid()){
          if(getRect().intersects(rec)){
            if(vx > 0){
              x = rec.x - width;
            }
            else{
              x = rec.x + rec.width;
            }
            vx *= -1;//nounce off
            playBounceSound();
          }
        }

        //we add to the y and check for collisions with the level/bourdaries/blocks
        y += vy;
        for(int i = blocks.getSolid().size() - 1; i >= 0; i--){
          Rectangle rec = blocks.getSolid().get(i);
          if(getRect().intersects(rec)){
            if(vy > 0){
              y = rec.y - height;
            }
            else{
              y = rec.y + rec.height;
            }
            blocks.removeBlock(i);
            vy *= -1;//bounce off
            playBounceSound();
          }
        }
        for(Rectangle rec : lvl.getSolid()){
          if(getRect().intersects(rec)){
            if(vy > 0){
              y = rec.y - height;
            }
            else{
              y = rec.y + rec.height;
            }
            vy *= -1;//boucne off
            playBounceSound();
          }
        }

        //this platform just bounce normally, not like the arkanoid vaus
        if(platform != null && getRect().intersects(platform.getRect())){
          if(vy > 0){
            y = platform.getRect().y - height;
          }
          else{
            y = platform.getRect().y + platform.getRect().height;//platform has a rect and can bounce off that
          }
          vy *= -1;
          playBounceSound();
        }

        //BOUNDARIES
        if(x < 150){
          x = 150;
          vx *= -1;
          playBounceSound();
        }
        if(y < 28){
          y = 28;
          vy *= -1;
          playBounceSound();
        }
    }

//get the rect
    public Rectangle getRect(){
        return new Rectangle(x, y, width, height);
    }

    //code from mr mckenzie and reddit
    private void loadSound(){
        try{
            AudioInputStream audioStream = AudioSystem.getAudioInputStream(new File("Sounds/Bounce.wav").getAbsoluteFile());
            sound = AudioSystem.getClip();
            sound.open(audioStream);
            soundLoaded = true;
        } catch(Exception e){
            System.out.println("Could not load bounce sound: Bounce.wav");
            soundLoaded = false;
        }
    }

    private void playBounceSound(){
        if(soundLoaded == false){
          return;
        }
        if(sound.isRunning() == true){
            sound.stop();
        }
        sound.setFramePosition(0);
        sound.start();
    }

    //ball draw
    public void draw(Graphics g){
        g.drawImage(ball, x, y, null);
    }
}

