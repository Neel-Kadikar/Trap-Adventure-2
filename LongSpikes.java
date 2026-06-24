/* 
LongSpikes.java
Neel Kadikar, Arsal Gazi
This program handles spieks that pop up from random areas in the level but, it grows???

This class is the same as the appear spikes so more info on that is there BUT THIS ONE GROWS
The spike grows in the getActiveBricks method where we just add to the height
*/

//imports libraries
import java.awt.*;

import javax.sound.sampled.*;
import javax.swing.*;
import java.util.*;
import java.awt.geom.*;
import java.io.File;

class LongSpikes {

    private ArrayList<Rectangle> triggers;
    private ArrayList<Rectangle> spikes;
    private ArrayList<Boolean> active;//lists cehcks and rects
    private ArrayList<Boolean> upside;
    private Image spike;
    private Clip sound;//sounds
    private boolean soundLoaded = false;

    public LongSpikes(int lvl) {
        triggers = new ArrayList<>();
        spikes = new ArrayList<>();
        active = new ArrayList<>();
        upside = new ArrayList<>();//sets as empty
        spike = new ImageIcon("Sprites/longSpike.png").getImage();
        loadSound();

        if(lvl == 6){
            addTrap(new Rectangle(1100,275, 10, 1000),new Rectangle(1045,275-19, 55, 30), false);
            addTrap(new Rectangle(1100 - 3*55,275, 10, 1000),new Rectangle(1045 - 3*55,275 + 3*55 + 10, 55, 30), true);
            addTrap(new Rectangle(1100 - 6*55,275, 10, 1000),new Rectangle(1045 - 6*55,275-19, 55, 30), false);
            addTrap(new Rectangle(1100 - 11*55,275, 10, 1000),new Rectangle(1045 - 11*55,275-19, 55, 30), false);//level 6 mainly uses them
            addTrap(new Rectangle(1100 - 14*55,275, 10, 1000),new Rectangle(1045 - 14*55,275 + 3*55 + 10, 55, 30), true);
            addTrap(new Rectangle(1100 - 17*55,275, 10, 1000),new Rectangle(1045 - 17*55,275-19, 55, 30), false);
        }

    }

    //adds the parameters to the lists
    private void addTrap(Rectangle trigger, Rectangle spike, boolean up){
        triggers.add(trigger);
        spikes.add(spike);
        active.add(false);
        upside.add(up);
    }

    //updates to rect ehceck if on
    public void update(Rectangle boyRect) {
        for (int i = 0; i < triggers.size(); i++) {
            if (active.get(i) == false && triggers.get(i).intersects(boyRect)) {
                playSound();
                active.set(i, true);
            }
        }
    }

    //grows the spieks while getting the list of rects too
    public ArrayList<Rectangle> getActiveSpikes() {
        ArrayList<Rectangle> list = new ArrayList<>();
        for (int i = 0; i < spikes.size(); i++) {
            if (active.get(i) == true) {
                Rectangle rect = spikes.get(i);
                if(rect.height < 220){//max height is 220 so if less, we add
                    rect.height +=3;
                    if(upside.get(i) == true){//for upside down, we move the y, not the height
                        rect.y -= 3;
                    }
                }
                list.add(rect);
            }
        }
        return list;//add to empty list and retunr
    }

    //code from mr mckenzie and reddit
    private void loadSound(){
        try{
            AudioInputStream audioStream = AudioSystem.getAudioInputStream(new File("Sounds/Spike.wav").getAbsoluteFile());
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

    //draws upside down using Graphics 2d
    public void draw(Graphics g){
        Graphics2D g2d = (Graphics2D) g;//graphics 2d is better LOL
        for(int i = 0; i < spikes.size(); i++){
            if(active.get(i)){
                Rectangle r = spikes.get(i);
                
                if(upside.get(i) == true){
                    int centerX = r.x + r.width / 2;
                    int centerY = r.y + r.height / 2;//center where we roatrte based on (pivot)
                
                    g2d.rotate(Math.PI, centerX, centerY);
                    g2d.drawImage(spike, r.x, r.y, r.width, r.height, null);
                    g2d.rotate(-Math.PI, centerX, centerY);//rotates based on center
                }
                else{
                    g.drawImage(spike, r.x, r.y, r.width, r.height, null);//if not upside down, draw normally
                }
            }
        }
    }
}