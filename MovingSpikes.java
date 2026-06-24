/* 
MovingSpikes.java
Neel Kadikar, Arsal Gazi
This program handles spieks that pop up from random areas in the level and move >:) based on the boys posiiton

This program is based off the switch hit, trap open system. USing rects as triggers and more rects as the spikes
It also takes the boys position and changes it x posiiton to move with the boy

This program works by using ArrayList indexs as our connection between multiple spikes and triggers
*/

//imports
import java.awt.*;
import java.io.File;

import javax.sound.sampled.*;
import javax.swing.*;
import java.util.*;
import java.util.*;

class MovingSpikes {

    private ArrayList<Rectangle> triggers;
    private ArrayList<Rectangle> spikes;//lists
    private ArrayList<Boolean> active;
    private Image spike;//image

    private Clip sound;
    private boolean soundLoaded = false;//sounds

    public MovingSpikes(int lvl) {
        triggers = new ArrayList<>();
        spikes = new ArrayList<>();
        active = new ArrayList<>();
        spike = new ImageIcon("Sprites/spikes.png").getImage();


         if (lvl == 2) {
             addTrap(new Rectangle(990, 220, 275, 55),new Rectangle(1025,259, 55, 36));//the trap in lvvl 2
        }
        loadSound();
    }

    //adds the varibales ito the lists
    private void addTrap(Rectangle trigger, Rectangle spike) {
        triggers.add(trigger);
        spikes.add(spike);
        active.add(false);
    }

    //set the spike as active
    public void update(Rectangle boyRect) {
        for (int i = 0; i < triggers.size(); i++) {
            if (active.get(i) == false && triggers.get(i).intersects(boyRect)) {
                playSound();
                active.set(i, true);//all true
            }
        }
    }

    //gets the spieks thats moving, or active and idle
    public ArrayList<Rectangle> getActiveSpikes() {
        ArrayList<Rectangle> list = new ArrayList<>();
        for (int i = 0; i < spikes.size(); i++) {
            if (active.get(i) == true) {
                list.add(spikes.get(i));//makes new list
            }
        }
        return list;
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

    public void draw(Graphics g) {
        for (int i = 0; i < spikes.size(); i++) {
            if (active.get(i)) {
                Rectangle r = spikes.get(i);
                g.drawImage(spike,r.x,r.y + 19,55,36,null);//+19 so spike is ON brick
            }
        }
    }
}

