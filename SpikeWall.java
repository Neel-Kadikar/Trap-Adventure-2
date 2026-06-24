/* 
SpikeWall.java
Neel Kadikar, Arsal Gazi
This program handles  a spikewall coming from the end of level 3 and making it fall

This class uses a polygon as the intersection point that kills and has a boolean that lets us knwo if it fell
if it did, its a normal bricks we can walk on as a spikewall has one side spikes, other side bricks

The polygon object uss arrays of X's and another of Y's and the final index incates the nume=ber of verticies (our case is 4)
It also uses Graphics 2d to rotate the image based on an ange and pivot we have
It also uses a timer to indicate when it needs to fall
 */

//imports
import java.awt.*;
import java.io.File;

import javax.sound.sampled.*;
import javax.swing.*;

class SpikeWall {
    private Image img;
    private Polygon poly;//polygon
    private int[] baseX, baseY;//arrays of x's and y's
    private boolean active = false;
    private boolean falling = false;//varibale for checking
    private boolean landed = false;
    private int frameTimer;//timing
    private double angle = 0;
    private Rectangle groundRect;//ground rect that te oyx can wal on
    private Rectangle trigger;//the trigger rect as isual

    private double posX;
    private double targetX;//for moving the spikewall in so it just dosent spawn all of a sudden

    private Clip sound;
    private String currentSoundFile = "";//sounds
    private boolean soundLoaded = false;

    public SpikeWall(int lvl) {
        img = new ImageIcon("Sprites/spikeWall.png").getImage();
        if (lvl == 3) {
            trigger = new Rectangle(1045, 0, 55, 720);

            baseX = new int[]{0, 91, 91, 0};
            baseY = new int[]{0, 0, 275, 275};
            targetX = 1280;
            posX = 1280 + 200;//the original x wanting to be at targetX

            rebuild();
        }
        
        loadSound();
    }

    //updates the spikewall based on the booleans
    public void update(Rectangle boy) {
        if(landed == true && baseY[1] < 258){
            return;//if onground cool
        }

        if(active == false && trigger != null && trigger.intersects(boy)){
            active = true;
            frameTimer = 0;//the spikewall spawns off screen
            posX = 1280 + 200;
        }

        if(active == true && falling == false){
            if(posX > targetX){
                posX -= 15;//the spikewall moves in
                if(posX < targetX){
                    posX = targetX;//yay, its at targetX
                }
            }
            frameTimer++;
            if(frameTimer >= 60){
                falling = true;//after 1 second (60 frames) the spikewall falls
            }
        }

        if(falling == true){
            angle -= 0.08;//the ange changes
            if(angle <= -Math.PI / 2){
                
                playSound();
                angle = -Math.PI / 2;
                falling = false;//boolean when they are on ground
                landed = true;
            }
            if(angle >= -Math.PI/2){
              rebuild();//rebuidls the polygon xs and ys
            }
            else{
                angle = -Math.PI/2;
            }
        }
    }

    //checks if the polygon intersects the boy when it wasnt on the ground
    public boolean killsBoy(Rectangle boy) {
        if(poly == null || landed == true){
            return false;
        }
        return poly.intersects(boy);
    }


    //rebuidls the polygon
    private void rebuild(){
        int[] x = new int[4];
        int[] y = new int[4];

        int offset = (int)(36 * -angle);//just on offset so it stays above the bricks

        for(int i = 0; i < 4; i++){    
            x[i] = (int)((baseX[i]-91) * Math.cos(angle) - (baseY[i] - 275)* Math.sin(angle)) + (int)posX;
            //for each the x and y it rotates based on unit ircle with new radius to pivot for all the points
            y[i] = (int)((baseX[i]-91) * Math.sin(angle) + (baseY[i] - 275) * Math.cos(angle)) + 313 - offset;
        }

        poly = new Polygon(x, y, 4);//polygon changes
    }

    //gets the rect thats safe for the boy to step on when it landed
    public Rectangle getGroundRect() {
        if(landed == true){
            groundRect = new Rectangle(1280 - 275, 313 - 55, 275, 55);
            return groundRect;
        }
        return null;//if not landed, its not here yet
    }

    //code from mr mckenzie and reddit
    private void loadSound(){
        try{
            AudioInputStream audioStream = AudioSystem.getAudioInputStream(new File("Sounds/Stomp.wav").getAbsoluteFile());
            sound = AudioSystem.getClip();
            sound.open(audioStream);
            soundLoaded = true;
        } catch(Exception e){
            soundLoaded = false;
        }
    }

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


    public void draw(Graphics g) {
        if (active == false){
            return;
        }

        Graphics2D g2 = (Graphics2D) g;//graphics 2d for rotation
        int offset = (int)(36 * -angle);
        g2.rotate(angle, posX, 313);
        g2.drawImage(img, (int)posX - 91 + offset, 313 - 275, null);//based on angle and offset for the img
        g2.rotate(-angle, posX, 313);
    }
}