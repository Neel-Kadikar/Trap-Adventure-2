/*
Blocks.java
Neel Kadikar, Arsal Gazi
Blocks are used in level 5. These look similar to the arkanoid blocks. The player releases a ball that collides with the blocks, and then they are removed.
Blocks are drawn in four colours and use a system similar to level to get drawn in. they use a grid to represent locations on the screen.
The blocks only appear in level 5.
*/

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.util.*;

//Main class - blocks. holds the methods and images necessary for the blocks. Also creates the map and loads in the blocks in their correct location
// for level 5.
class Blocks{

    private Image yellowBlock, blueBlock, redBlock, greenBlock;
    private int[][] map;
    private ArrayList<Block> solidblocks;
    //private Rectangle nextlvl;

    public Blocks(int lvl){
        yellowBlock = new ImageIcon("Sprites/yellowBlock.png").getImage(); //images for blocks and colours
        blueBlock = new ImageIcon("Sprites/blueBlock.png").getImage();
        redBlock = new ImageIcon("Sprites/redBlock.png").getImage();
        greenBlock = new ImageIcon("Sprites/greenBlock.png").getImage();

        map = new int[14][24];
        //blocks underneath the platform in level 5
        if(lvl == 5){
            map = new int[][]{
            {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},
            {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},
            {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},
            {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},
            {0,0,0,2,2,2,2,2,2,2,2,2,3,3,3,3,3,3,3,3,3,3,0,0},
            {0,0,0,2,2,2,2,2,2,2,2,2,3,3,3,3,3,3,3,3,3,3,0,0},
            {0,0,0,4,4,4,4,4,4,4,4,4,5,5,5,5,5,5,5,5,5,5,0,0},
            {0,0,0,4,4,4,4,4,4,4,4,4,5,5,5,5,5,5,5,5,5,5,0,0},
            {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},
            {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},
            {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},
            {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},
            {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},};
        }

        solidMake(); //call solid make to create the blocks which the boy can jump/fall on to
    }

    //Method - getMap. Simply return the map seen above.
    public int[][] getMap() {
        return map;
    }

    //Method - solid make. Goes through the blocks in the map, and adds them to solid blocks if their values match. 
    private void solidMake() {
        if(solidblocks == null){
            solidblocks = new ArrayList<>();
        }
        //Calculations for numbers and dimensions are based on our game in an aim to match the original trap adventure 2
        for(int i = 0; i < map.length; i++){
            for(int j = 0; j < map[0].length; j++){
                if(map[i][j] >= 2){
                    int x = j * 55;
                    int y = i * 82 - 17 - 27; 
                    solidblocks.add(new Block(x, y, map[i][j]));
                }
            }
        }
    }

    //Method - getSolid. returns the solid blocks in the map. In the case of blocks, all of them are solid (player can collide with them) until removed
    public ArrayList<Rectangle> getSolid(){
        ArrayList<Rectangle> rects = new ArrayList<>();
        for (Block block : solidblocks) {
            rects.add(block.rect);
        }
        return rects;
    }

    //Method - removeBlock. Removes a blocks from the list. Used in the case it is broken
    public void removeBlock(int i) {
        if(i >= 0 && i < solidblocks.size()) {
            solidblocks.remove(i);
        }
    }

    //Method - draw. Simply draws in all the solid/real blocks. This will not draw in any broken ones.
    // Colours are assigned different values, as seen in the map above.
    // 2 is yellow, 3 is blue, 4 is red, 5 is green
    // the unique images for these sprites were loaded in above.
    public void draw(Graphics g){
        for(Block block : solidblocks){
            Image img = null;
            if(block.color == 2){
                img = yellowBlock;
            }
            else if(block.color == 3){
                img = blueBlock;
            }
            else if(block.color == 4){
                img = redBlock;
            }
            else if(block.color == 5){
                img = greenBlock;
            }
            g.drawImage(img, block.rect.x, block.rect.y, block.rect.width, block.rect.height, null);
        }
    }
}
