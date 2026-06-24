/*
Block.java
Neel Kadikar, Arsal Gazi

The object used in the Blocks.java class, this class shows the individual blocks for the lvl 5 arkanoid lvl
*/

//not this much libraries LOL
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.util.*;

class Block{
    public Rectangle rect;
    public int color;//each block has a color and rect

    Block(int x, int y, int cc) {
        rect = new Rectangle(x, y, 55, 27);
        color = cc;//set the color and rect
    }
}
