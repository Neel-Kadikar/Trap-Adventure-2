/* 
AppearSpikes.java
Neel Kadikar, Arsal Gazi
This program handles spieks that pop up from random areas in the level

This program is based off the switch hit, trap open system. USing rects as triggers and more rects as the spikes
It can also be upside down using Graphics 2D rotation help

This program works by using ArrayList indexs as our connection between multiple spikes and triggers
*/

//imports
import java.awt.*;
import java.io.File;

import javax.sound.sampled.*;
import javax.swing.*;
import java.util.*;

class AppearSpikes {

    private ArrayList<Rectangle> triggers;//list of trigger rects, the spike rects, the checker if active, and the checker if upside down
    private ArrayList<Rectangle> spikes;
    private ArrayList<Boolean> active;
    private ArrayList<Boolean> upside;
    private Image spike, widespike;//images

    private Clip sound;
    private boolean soundLoaded = false;//sounds

    public AppearSpikes(int lvl) {
        triggers = new ArrayList<>();
        spikes = new ArrayList<>();
        active = new ArrayList<>();
        upside = new ArrayList<>();
        spike = new ImageIcon("Sprites/spikes.png").getImage();
        widespike = new ImageIcon("Sprites/wideSpike.png").getImage();
        loadSound();


        if (lvl == 2) {
            addTrap(new Rectangle(855, 55, 55, 165),new Rectangle(880,148, 55, 36), false);
        }

        else if (lvl == 4) {
            // top left - left spike
            addTrap(new Rectangle(550, 258, 55, 110), new Rectangle(550, 313, 55, 36), false);

            // top left - right spike
            addTrap(new Rectangle(605, 258, 55, 110), new Rectangle(605, 313, 55, 36), false);

            // top right - left spike
            addTrap(new Rectangle(660, 258, 165, 146), new Rectangle(715, 313, 55, 36), false); // top right's left spike - large trigger


            // bottom right - left spike
            addTrap(new Rectangle(715, 423, 55, 110), new Rectangle(715, 478, 55, 36), false);

            // floor - left spike
            addTrap(new Rectangle(495, 588, 55, 110), new Rectangle(495, 643, 55, 36), false);

            // floor - right spike
            addTrap(new Rectangle(550, 588, 55, 110), new Rectangle(550, 643, 55, 36), false);

            addTrap(new Rectangle(385, 149, 165, 146), new Rectangle(440, 204, 55, 36), false); // anti-jump spike - large trigger
        }

        if (lvl == 5) {
            addTrap(new Rectangle(200,0, 1080, 720),new Rectangle(110,39, 55, 36), false);
            addTrap(new Rectangle(165,0, 1115, 29), new Rectangle(165,23, 1115, 64), false);
        }
        if (lvl == 6){
            addTrap(new Rectangle(495,110, 55, 55),new Rectangle(330 + 55,93 - 55, 55, 36), true); 
            addTrap(new Rectangle(495,110, 55, 55),new Rectangle(275 + 55,93 - 55, 55, 36), true); 
            addTrap(new Rectangle(495,110, 55, 55),new Rectangle(165,93, 55, 36), false); 
            addTrap(new Rectangle(495,110, 55, 55),new Rectangle(110,93, 55, 36), false); 
        }

        if(lvl == 7){
            addTrap(new Rectangle(80,93, 55, 55),new Rectangle(55,93, 55, 36), true); 
        }

        //levels with appear spikes

    }

    private void addTrap(Rectangle trigger, Rectangle spike, boolean up) {
        triggers.add(trigger);
        spikes.add(spike);
        active.add(false);
        upside.add(up);//adds all the variable to the lists
    }

    //sets the appearspike as active or not
    public void update(Rectangle boyRect) {
        for (int i = 0; i < triggers.size(); i++) {
            if (active.get(i) == false && triggers.get(i).intersects(boyRect)) {
                playSound();
                active.set(i, true);
            }
        }
    }

    //does the same but for the wide spike in level 5
    public void wideUpdate(ArrayList<Ball> balls){
        for (int i = 0; i < triggers.size(); i++) {
            for(Ball ball : balls){
                if(active.get(i) == false && triggers.get(i).intersects(ball.getRect())){
                    active.set(i, true);
                }
            }
        }
    }

    //all the spikes that are active, we sne dthe rects of which teh spikes are up
    public ArrayList<Rectangle> getActiveSpikes() {
        ArrayList<Rectangle> list = new ArrayList<>();
        for (int i = 0; i < spikes.size(); i++) {
            if (active.get(i) == true) {
                list.add(spikes.get(i));//adds to empty list so all appearspikes are by themself
            }
        }
        return list;
    }

    //code form Mr Mckenzie & Reddit
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

    public void draw(Graphics g, int lvl) {
        Graphics2D g2d = (Graphics2D) g;
        for (int i = 0; i < spikes.size(); i++) {
             if ((active.get(i) == true && lvl != 5) || (active.get(i) == true && i == 0)) {
                Rectangle r = spikes.get(i);
                
                if(upside.get(i) == true){
                    int centerX = r.x + r.width / 2;
                    int centerY = r.y + r.height / 2;
                    //center to rotate from
                    
                    g2d.rotate(Math.PI, centerX, centerY);
                    g2d.drawImage(spike, r.x, r.y, 55, 36, null);
                    g2d.rotate(-Math.PI, centerX, centerY);
                    //if the spiek is upside down, we use Graphics @d with rotate
                }
                else{
                    g.drawImage(spike, r.x, r.y + 19, 55, 36, null);
                    //else do it normally
                }
            }
            else{
                if(active.get(i) == true){
                    Rectangle r = spikes.get(i);
                    g.drawImage(widespike,r.x,r.y, 1045,64,null);
                    //if its lvl 5 we draw the appear spikes as this
                }
            }
        }
    }
}