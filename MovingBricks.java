/* 
Moving Bricks.java
Neel Kadikar, Arsal Gazi
This program handles bricks that move around a pivot in a circular motion (only used in lvl 3) based on a trigger
that moves it futher left if the trigger is on (button held down logic)

This class has lists of radius, points of pivots, and angles based on where the bick is based on the angle
It also uses a normal Rect object instead of a Polygon, cause I didnt figure out polygons until later LOL

The class also rotates the image suing graphics 2d
 */

//imports
import java.awt.*;
import javax.swing.*;
import java.util.*;
import java.awt.geom.*;

class MovingBricks {

    private ArrayList<Rectangle> triggers;
    private ArrayList<Rectangle> movingBricks;
    private ArrayList<Double> radiuses;
    private ArrayList<Point> points;//all lists
    private ArrayList<Double> angles;
    private Image brick;

    public MovingBricks(int lvl) {
        triggers = new ArrayList<>();
        movingBricks = new ArrayList<>();
        radiuses = new ArrayList<>();
        points = new ArrayList<>();//sets as empty
        angles = new ArrayList<>();
        brick = new ImageIcon("Sprites/brick.png").getImage();


        if (lvl == 3) {
            addTrap(new Rectangle(605, 275, 55, 55),new Rectangle(440,313, 55, 55),new Point(645, 330));//a parametr as the point of pivot (center circle)
        }
    }

    //adds the parameters to the lists
    private void addTrap(Rectangle trigger, Rectangle movingBrick, Point pt) {
        triggers.add(trigger);
        movingBricks.add(movingBrick);
        radiuses.add(pt.x - (movingBrick.getX()+27.5));//the radius logic as brick starts on left
        angles.add(Math.PI - 0.06);//the brick is flat right now
        points.add(pt);
    }

    //updaets the bricks potiosn
    public void update(Rectangle boyRect) {
        for (int i = 0; i < triggers.size(); i++) {
            Rectangle rect = movingBricks.get(i);
            double angle = angles.get(i);

            if (triggers.get(i).intersects(boyRect)) {
              angle -= 0.05;
              if(angle < 0.06){
                angle = 0.06;
              }//the angle changes and the angke cant go above 0.06, else it ouldnt be on te -y of the cartigan plane
            }
            else{
              angle += 0.05;
              if(angle > Math.PI - 0.06){
                angle = Math.PI - 0.06;//else it move back to the start postion still being on -y on cartigan plane
              }
            }
            angles.set(i, angle);//changea ngles based on it

            rect.y = (int)(points.get(i).y + radiuses.get(i) * Math.sin(angles.get(i)) - 55/2);
            //uses the unit circle as a copy based on sin and cos and the unit circle radius
            rect.x = (int)(points.get(i).x + radiuses.get(i) * Math.cos(angles.get(i)) - 55/2);
        }
        
    }

    //gets the active bricks by adding to new list and returning it
    public ArrayList<Rectangle> getActiveBricks() {
        ArrayList<Rectangle> list = new ArrayList<>();
        for (int i = 0; i < movingBricks.size(); i++) {
            list.add(movingBricks.get(i));
        }
        return list;
    }

    public void draw(Graphics g) {
      Graphics2D g2d = (Graphics2D) g;//since the image rotates, we using graphics 2D

      for (int i = 0; i < movingBricks.size(); i++) {
           Rectangle rec = movingBricks.get(i);
           double angle = (angles.get(i) - 0.06) / (Math.PI - 0.12) * Math.PI + Math.PI; // Normalize angle between 0 and PI

           AffineTransform old = g2d.getTransform();
           AffineTransform rot = new AffineTransform();
           rot.rotate(angle, rec.x + 55/2, rec.y + 55/2);//based on pivot and angle

           g2d.setTransform(rot);
           g2d.drawImage(brick, rec.x, rec.y, 55, 55, null);
           g2d.setTransform(old);
       }
    }
}