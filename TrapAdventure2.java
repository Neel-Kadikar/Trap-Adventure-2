/* 
TrapAdventure2.java

Arsal Gazi, Neel Kadikar

Main program file for our game. Contains the gamepanel, and initializes features like the key input, timer, and more. Loads in the images for the start
screen, and also loads in the features of the game, including laser, balls, blocks, bricks, the player, spikes, shurikens, and other traps and features of the game.

This class also handles certain inputs, for example, checking the level once the player has given an input to determine the right way to move it.
It also handles scenarios to do with loading in new levels, or resetting the current level. It does this by essentially reloading all the required elements of the level.

Finally, it handles the drawing of the sprites by referring to their individual draw methods at the very end, and also handles displaying elements that are visible no matter the level,
Such as lives, and level display.
*/

// Import necessary libraries
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

//import javafx.scene.paint.Stop;

import java.io.*;
import java.util.ArrayList;
import javax.sound.sampled.*;

// Main Class
// Sets the screen name, handles closing the game, the game panel, and start the game.
public class TrapAdventure2 extends JFrame{
  GamePanel game;
  
  public TrapAdventure2(){
    super("Trap Adventure 2");
    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    game= new GamePanel();
    add(game);
    pack();  
    setVisible(true);
  } 
  
  public static void main(String[] args){
    new TrapAdventure2();      
  }
}

//Class - GamePanel.
// This class handles the features of the game. Starts by loading in all the features from other classes, including game features such as the boy, spikes, and bricks.
// It also loads in the images necessary for this class. It initializes the timer, which is used for various features through out the game.
// Some basic checks are handled here, such as the collisions between spikes and the player, or other traps 
class GamePanel extends JPanel implements KeyListener, ActionListener, MouseListener, MouseMotionListener{
  private boolean []keys;
  private Timer timer;
  private Image startScreen;
  private Image livesSelect;
  private Image laser;
  private Image ballScreen;
  private Image life;
  private Image gameover;
  private Image ending;

  private Clip music;
  private boolean playing = false;
  private String currentMusic = "";
  private Rectangle normalRect;
  private Rectangle hardRect;
  private int mx,my;
  private boolean loading = false;
  private int loaddelay = 0;
  private Clip sound;
  private Clip gameoverSound;
  private boolean soundLoaded = false;
  private String currentSoundFile = "";
  

  //private Balls balls; //main ball object
  
  private int lvl = -1; 
  private int lives = 0;
  private String mode = "";
  private Font fnt; //iniatilize font
  private Font endfnt;
  private Boy gameBoy; //Creates main character boy
  private Level level; //Creates current level
  
  private Blocks blocks; //Creates blocks of the levels
  private Shurikens shurikens; //Creates shurikens of the levels
  private Spikes spikes; //Create spikes of the levels
  private AppearSpikes appearSpikes; //create appear spikes
  private AppearWater appearWater; 
  //Create various other traps from the game
  private LongSpikes longSpikes;
  private MovingSpikes movingSpikes;
  private SpikeWall spikeWall;
  private Platform platform;
  private boolean boyFollow;
  private MovingBricks movingBricks;
  private Rectangle nextlvl;
  private Cannon cannon;
  private Submarine submarine;
  private Bomb bomb;
  private Explosion explosion;
  private BreakableBricks breakableBricks;
  private InvisibleBricks invisibleBricks;
  private Rocket rocket;

  //Method - gamepanel. Main controller of the entire game, sets dimensions on the screen, loads images, and determines tick speed. Load sin mouse key and mouse motion listeners
  public GamePanel(){
    setPreferredSize(new Dimension(1280, 720)); //screen size
    setFocusable(true);    
    requestFocus();

    startScreen = new ImageIcon("Sprites/start-screen.gif").getImage(); //images
    livesSelect = new ImageIcon("Sprites/mode-screen.png").getImage();
    laser = new ImageIcon("Sprites/laser.gif").getImage();
    ballScreen = new ImageIcon("Sprites/game-background.jpg").getImage();
    life = new ImageIcon("Sprites/lives.png").getImage();
    gameover = new ImageIcon("Sprites/gameover.gif").getImage();
    ending = new ImageIcon("Sprites/end-screen.gif").getImage();

    normalRect = new Rectangle(171, 210, 442, 274);
    hardRect = new Rectangle(686, 210, 442, 274);

    keys = new boolean[2000];            
    gameBoy = new Boy(120, 100, Boy.IDLER); // Set up boy and starting location
    fnt = loadFont("Sprites/monaco.ttf", 55); //load custom font
    endfnt = loadFont("Sprites/monaco.ttf", 100); //load custom font

    addKeyListener(this); //key and mouse listeners
    addMouseListener(this);
    addMouseMotionListener(this);
    timer = new Timer(15, this);
    timer.start();

    loadSound("Select.wav");
  }
  
  //Method - Action performed. Runs every frame, checks all ways to die by means of player colliding with deadly object, updating behaviour of traps and other game objects, level progression
  public void actionPerformed(ActionEvent e){
    if(lvl == -2){ //death screen
      if(playing == true){
        stopMusic();
      }
      
      repaint();
      return;
    }
    if(loading == true){ //loading boolean
      loaddelay++; //adds a delay to moving between levels
      if(loaddelay >= 60){ // 60 ticks
        loading = false;
        if(lvl == 1){
          levelLoad();
        }
      }
      else{
        repaint();
        return;
      }
    }
    //checks if player is in a level, not a start/end screen
    if(lvl > 0 && lvl < 11){
        if (gameBoy.isInSubmarine()) { //use submarine movement if he's in sub
            gameBoy.submarineMovement(lvl, keys, level, movingBricks, spikeWall, platform, blocks, submarine, breakableBricks, invisibleBricks);

            
        } 
        else if (lvl > 5 && lvl < 9) { //swim for water levels (between 5 and 9)
            gameBoy.swim(lvl, keys, level, movingBricks, spikeWall, platform, blocks, submarine, breakableBricks, invisibleBricks);
        } 
        else if(gameBoy.isInRocket() == false){ //move normally if not in ending rocket
            gameBoy.move(lvl, keys, level, movingBricks, spikeWall, platform, blocks, submarine, breakableBricks, invisibleBricks);
        }
        
        //VARIOUS collisions with traps
        //level gets reset if boy collides with them

        for(Rectangle spike : spikes.getSpikes()){ //spikes collisions
          if(spike.intersects(gameBoy.getRect())){
            levelReset();
          }
        }

         if (invisibleBricks != null) { //can't move through invisible bricks
            invisibleBricks.update(gameBoy.getRect());
        }
        

        if (appearSpikes != null){ //update appear spikes, and check collisions
          appearSpikes.update(gameBoy.getRect());
          appearSpikes.wideUpdate(gameBoy.getBalls());
          for (Rectangle rec : appearSpikes.getActiveSpikes()) {
            if (rec.intersects(gameBoy.getRect())) {
              levelReset();
            }
          } 
        }

        
        if(shurikens != null){ //shuriken checks
          shurikens.update(gameBoy.getRect(), lvl);
          for (Rectangle rec : shurikens.getActiveShurikens()){
            if (rec.intersects(gameBoy.getRect())) {
              levelReset();
            }
          } 
        }

        if(bomb != null){ //updating bomb - some special cases here
          ArrayList<Rectangle> removeBombs = new ArrayList<Rectangle>();
          boolean hitBrick = false;
          bomb.update(gameBoy);

          if (breakableBricks != null){
          // for bomb, must also check if it collides with breakable bricks (get rid of them) or with regular bricks (explode on contact)
          for (Rectangle bombRect : new ArrayList<>(bomb.getBombs())) {

            boolean collided = false;

            if (level.getSolid() != null) { //for regular level bricks
                for (Rectangle solid : level.getSolid()) {
                    if (bombRect.intersects(solid)) {
                        collided = true;
                        break;
                    }
                }
            }

            if (collided == false && breakableBricks != null) { //for breakable bricks
                for (Rectangle brick : breakableBricks.getActiveBreakableBricks()) {
                    if (bombRect.intersects(brick)) {
                        collided = true;
                        break;
                    }
                }
            }

            if(collided  == false&& shurikens != null){ //colliding bomb with shurikens - explodes here too
                for (Rectangle s : shurikens.getShurikens()) {
                    if (bombRect.intersects(s)) {
                        collided = true;
                        break;
                    }
                }
            }

            if(collided == true){
                boolean subExploded = false; //for checking if the boy is caught by his own explosion
                explosion.explode(bombRect); //cause the explosion after collidign with something

                Rectangle explosionArea = new Rectangle(bombRect.x - 90,bombRect.y - 90,180,180);
                if (gameBoy.getRect().intersects(explosionArea)) { //if the boy is caught in his own explosion level reset
                    levelReset();
                    subExploded=true;
                    
                }

            if(breakableBricks != null && subExploded==false){ //if boy hasn't exploded himself, and has hit a breakable brick instead, then remove the bricks via update()
                for (Rectangle brick : new ArrayList<>(breakableBricks.getActiveBreakableBricks())) {
                    if (brick.intersects(explosionArea)) {
                        breakableBricks.update(brick);
                    }
                }
            }

            bomb.getBombs().remove(bombRect); //remove bombs after checking
        }
      }
    }

          

          
        
      }
      

        if (longSpikes != null) { //reset from longSpikes
          longSpikes.update(gameBoy.getRect());
          for (Rectangle rec : longSpikes.getActiveSpikes()){
            if (rec.intersects(gameBoy.getRect())) {
              levelReset();
            }
          } 
        }

        
        if (appearWater != null){  //update hidden water path 
          appearWater.update(gameBoy.getRect());
        }

        if(movingSpikes != null){ //update the moving of the moving spikes
          movingSpikes.update(gameBoy.getRect());
          for(Rectangle rec : movingSpikes.getActiveSpikes()){
            if(gameBoy.getX() > rec.x + 27){
              boyFollow = true; //follow the boy if past a certain point
            }
            if(boyFollow == true){ //follow the boy
              rec.x = gameBoy.getX() - 27;
              if(rec.x < 1018 && lvl == 2){
                rec.x = 1018;
                boyFollow = false;
              }
            }
            if(rec.intersects(gameBoy.getRect())){ //die if he touches moving spikes
              levelReset();
            }
          }
        }
        //handles mounting submarine in level 8
        //sets booleans like inSubmarine and setMounted to true
        if (submarine != null && gameBoy.isInSubmarine() == false && gameBoy.hasDismounted() == false && gameBoy.getRect().intersects(submarine.getSubRect()) && lvl == 8) {
            gameBoy.setInSubmarine(true);
            submarine.setMounted(true);
        }

        //update movingBricks in level 3
        if(lvl == 3 && movingBricks != null){
            movingBricks.update(gameBoy.getRect());
        }
                
        if (spikeWall != null) { //update spikeWall motion and the level reset if in contact with boy
          spikeWall.update(gameBoy.getRect());

          if (spikeWall.killsBoy(gameBoy.getRect())) {
            levelReset();
          }
        }

        // if ball touches boy in level 5, level reset
        for(Ball b : new ArrayList<>(gameBoy.getBalls())){
          if(b.getRect().intersects(gameBoy.getRect()) == true){
            levelReset();
          }
        }

        //check with cannon if it has hit the boy, reset if so, also if the boy touches the cannon
        if(cannon != null){
          boolean check = cannon.update(gameBoy);
          if(check == true){
            levelReset();
          }
          if(cannon.getRect().intersects(gameBoy.getRect())){
            levelReset();
          }
        }

        //check if boy enters rocket for end of game
        if (rocket != null && rocket.isMounted() == false && gameBoy.getRect().intersects(rocket.getRect())) {
          rocket.setMounted(true);
          gameBoy.setInRocket(true);
        }

        //update rocket if boy mounted it
        if(rocket != null && rocket.isMounted() == true){
          rocket.update();
    
          if(rocket.hasStartedFlying()){
            gameBoy.setX(rocket.getX()); //update boy coords with the rocket
            gameBoy.setY(rocket.getY());
          }
    
          if(rocket.getY() < -100){
            rocket.stopSounds();
            lvl++;
            levelLoad();
          }
        }

      //stop music on the platform
      if(platform != null && platform.hasBoy() == true){
        stopMusic();
      }
    }

    //if boy is in the next level rect
    if(nextCheck(gameBoy, lvl) == true){
      lvl+=1;
      levelLoad();
    }
  
    repaint();
  }

  //Method - nextCheck. Checks if the boy is in a specified rectangle, if so, then he is moved to the next level.
  public boolean nextCheck(Boy boy, int lvl){
        if(lvl > -2 && lvl < 5){ //same for up to level 4
            nextlvl = new Rectangle(1230, 0, 50, 720);
        }
        if (lvl == 5){ //5 and onwards differ
          nextlvl = new Rectangle(1100, 660, 110, 110);
        }
        if (lvl == 6){
          nextlvl = new Rectangle(0, 55, 55, 110);
        }
        if (lvl == 7){
          nextlvl = new Rectangle(0, 55, 55, 600);
        }
        if (lvl == 8){
          nextlvl = new Rectangle(330, 665, 330, 55); 
        }
        if(lvl == 9){
            nextlvl = new Rectangle(1230, 0, 50, 720);
        }
        if(lvl == 10){
            nextlvl = new Rectangle(0,0,0,0);
        }

        if(boy.getRect().intersects(nextlvl)){ //true if he is in the rect
            return true;
        }
        return false;
    }


  //Method - levelReset. //for when the player loses a life, put them back to the start and restart the level. Requires reloading states, and all the objects in the level.
  public void levelReset(){ 
    lives--; //reduce lives
    loadSound("Death.wav");
    playSound();
    if(lives == 0){ //end screen if 0 lives left
      lvl = -2;
      
      loadSound("GameOver.wav");
      playSound();
      stopMusic();
      repaint();
      return;
    }
    
    if (lvl == 9){ //reset to submarine if level 9
      gameBoy.setInSubmarine(true);
      gameBoy.setState(Boy.SUBR);  
    }

    // also reset the boy's state to swimming if in swimming level
    else if (lvl > 5 && lvl < 9) {
      gameBoy.setInSubmarine(false);
        gameBoy.setState(Boy.SWIML); // Reset to swimming state
    }
    else {
      gameBoy.setInSubmarine(false);
        gameBoy.setState(Boy.IDLER); // Reset to walking state
    }
    
   
    //recreate the traps and other objects like spikeWall and breakable bricks
    shurikens = new Shurikens(lvl);
    bomb = new Bomb(lvl);
    explosion = new Explosion(lvl);
    blocks = new Blocks(lvl);
    spikes = new Spikes(lvl);
    appearSpikes = new AppearSpikes(lvl);
    submarine = new Submarine(lvl);
    appearWater = new AppearWater(lvl);
    longSpikes = new LongSpikes(lvl);
    movingSpikes =  new MovingSpikes(lvl);
    movingBricks = new MovingBricks(lvl);
    spikeWall = new SpikeWall(lvl);
    breakableBricks = new BreakableBricks(lvl);
    invisibleBricks = new InvisibleBricks(lvl);
    if (lvl == 8){
      gameBoy.setInSubmarine(false); //don't reset to submarine if in level 8
    }
    if (lvl == 5){ //new platform only in level 5
        platform = new Platform(167, 110);
    }
    else {
        platform  = null;
    }
    if(cannon != null){ //clear cannon
      cannon.clearMissiles();
    }

    gameBoy.resetPos(lvl); //finally, reset boy's coordinates
    
  }

  //Method delayLoad. Used to delay the loading of screens between intro screens, to improve look of changing and make it more cohesive with music, avoid jumble of sounds
  public void delayLoad(){ 
    if(lvl == 1 || lvl == 0){
      loading = true;
      loaddelay = 0;
      return;
    }
    levelLoad();
    
    
  }

  //Method levelLoad. Loads in a new level. Involves creating a new version of all the game objects, and depending on the levels loads in specific elements
  public void levelLoad(){
    if(lvl == 11){ //end screen - level 11
      if(playing == true && currentMusic.equals("Sounds/Space.wav") == false){
        stopMusic();
        playMusic("Sounds/Space.wav");
      }
      else if(playing == true && currentMusic.equals("Sounds/Space.wav") == true){
        //do nothing
      }
      else{
        playMusic("Sounds/Space.wav");
      }
      return;
    }
    //create new versions of the level, traps, and other solid objects
    level = new Level(lvl);
    blocks = new Blocks(lvl);
    shurikens = new Shurikens(lvl);
    bomb = new Bomb(lvl);
    explosion = new Explosion(lvl);
    spikes = new Spikes(lvl);
    submarine = new Submarine(lvl);
    appearSpikes = new AppearSpikes(lvl);
    appearWater = new AppearWater(lvl);
    longSpikes = new LongSpikes(lvl);
    movingSpikes = new MovingSpikes(lvl);
    movingBricks = new MovingBricks(lvl);
    spikeWall = new SpikeWall(lvl);
    breakableBricks = new BreakableBricks(lvl);
    invisibleBricks = new InvisibleBricks(lvl);
    if (lvl == 5){
        platform = new Platform(167, 110); //platform for level 5
    } else {
        platform  = null;
    }
    if(lvl==9){
      gameBoy.dismountRectSetup(lvl); //setup the rectangle for dismounting area if in level 9
    }
    if(lvl > 8 && lvl < 10){ //set boy to in submarine if in submarine levels
      gameBoy.setInSubmarine(true);
      gameBoy.setState(Boy.SUBR);
    }
    if (lvl > 5 && lvl < 9) { //set swimming for swimming levels 
      gameBoy.setInSubmarine(false);
      gameBoy.setState(Boy.SWIML);
    }
    else if (lvl < 6) {
      // If going back to earlier levels, exit submarine mode
      gameBoy.setInSubmarine(false);
      gameBoy.setState(Boy.IDLER);
    }

    // walking in level 10
    else if (lvl == 10){ 
      gameBoy.setInSubmarine(false);
      gameBoy.setState(Boy.IDLER);
    }
    if(lvl == 7){
      cannon = new Cannon(165, 533); //cannon for level 7
    }
    else {
      cannon = null;
    }
    if(lvl == 10){
      rocket = new Rocket(lvl); //rocket in level 10
    }
    else {
      rocket = null;
    }
    gameBoy.resetPos(lvl); //reset position when loading a new level

    if (lvl <= 5){ //play the platformer music when in the appropriate levels
      if(playing == true && currentMusic.equals("Sounds/Platformer.wav") == false){
        stopMusic();
        playMusic("Sounds/Platformer.wav");
      }
      else if(playing == true && currentMusic.equals("Sounds/Platformer.wav") == true){
        //do nothing
      }
      else{
        playMusic("Sounds/Platformer.wav");
      }
    }
    if (lvl > 5){ //play underwater soundtrack for underwater levels
      if(playing == true && currentMusic.equals("Sounds/Underwater.wav") == false){
        stopMusic();
        playMusic("Sounds/Underwater.wav");
      }
      else if(playing == true && currentMusic.equals("Sounds/Underwater.wav") == true){
        //do nothing
      }
      else{
        playMusic("Sounds/Underwater.wav");
      }
    }
  
  }
  
  //Method - keyPressed. Gets key input. Used for hidden developer tricks in our game, like p for previous level, n for next level, and r for restart at level 1
  public void keyPressed(KeyEvent e){ 
    int key = e.getKeyCode();
    keys[key] = true; 

    if (lvl >= 1) {
      if(keys[KeyEvent.VK_N]){ //n for skipping to next level - dev tool ONLY
        if(lvl < 11 && lvl > 0){
          lvl+=1;
        }
        levelLoad();
        
      }
      else if(keys[KeyEvent.VK_P]){ //p for previous - dev tool
        if (lvl > 1){
          lvl-=1;
          levelLoad();
        }
      }
      else if(keys[KeyEvent.VK_R]){ // - r for restart - another dev tool
        lvl=1;
        levelLoad();
      }
    }
  }
  
  //Method - keyReleased. Given from in class examples. used for detecting key inputs
  public void keyReleased(KeyEvent e) {
    int key = e.getKeyCode();
    keys[key] = false;
  }
  
  //Method - mouse clicked. used for getting mouse clicked coordinates
  public void mouseClicked(MouseEvent e) {
    mx = e.getX();
    my = e.getY();

    if (lvl == -1){ // when clicking on start screen
      lvl +=1;
      loadSound("Select.wav");
      playSound();
      delayLoad();
    }

    else if(lvl == 0) { //if clicking the normal mode rect in mode selection screen
      if(normalRect.contains(mx, my)){
        lives = 10;
        lvl = 1;
        mode = "Normal";
        playSound();
        delayLoad();
        
      }
      else if(hardRect.contains(mx, my)){ //if clicking the hard mode rect (1 life only) on mode selection screen
        lives = 1;
        lvl = 1;
        mode = "Hard";
        playSound();
        delayLoad();
        
      }
    }

    else if(lvl == -2){ //if clicking on death screen
      lvl = -1; // Go back to start screen
      lives = 0;
      mode = "";
      gameBoy = new Boy(120, 100, Boy.IDLER); // Reset boy
      stopMusic(); // Ensure music is stopped
      playing = false;
      currentMusic = "";
      repaint();
      return;
    }

    else if(lvl == 11){
      lvl = -1;
      lives = 0;
      mode = "";
      gameBoy = new Boy(120, 100, Boy.IDLER); // Reset boy
      stopMusic(); // Ensure music is stopped
      playing = false;
      currentMusic = "";
      repaint();
      return;
    }
  }
 //Methods - given from in class examples
  public void mouseEntered(MouseEvent e) {}
  public void mouseExited(MouseEvent e) {}
  public void mousePressed(MouseEvent e) {}
  public void mouseReleased(MouseEvent e){}
  public void mouseMoved(MouseEvent e) {
    mx = e.getX();
    my = e.getY();
    if (lvl == 0) {
      repaint();
    }
  }
  
  //Method - given.
  public void mouseDragged(MouseEvent e) {
    mouseMoved(e);
  }
  
  //Method - given
  public void keyTyped(KeyEvent e){}
  
  //Method - loadFont. Taught in class. We use this for our game's font
  public Font loadFont(String path, int size) {
    try {
        File file = new File(path);
        Font font = Font.createFont(Font.TRUETYPE_FONT, file);
        return font.deriveFont((float) size);
    } catch (Exception e) {
        e.printStackTrace();
        return new Font("Monospaced", Font.PLAIN, size);
    }
  }

  //Method - From Mr. McKenzie 
  private void playMusic(String filename) {
    try {
      if (music != null && music.isRunning()) {
          music.stop();
          music.close();
          music = null;
      }
            
      File audioFile = new File(filename);
      if (!audioFile.exists()) {
        return;
      }
      
      AudioInputStream audioStream = AudioSystem.getAudioInputStream(audioFile);
      music = AudioSystem.getClip();
      music.open(audioStream);
      music.loop(Clip.LOOP_CONTINUOUSLY);
      music.start();
            
      playing = true;
      currentMusic = filename;
            
    } catch (UnsupportedAudioFileException e) {
            e.printStackTrace();
        } catch (LineUnavailableException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //Method - stopMusic() - method given by Mr. McKenzie
  private void stopMusic() {
    if (music != null && music.isRunning()) {
      music.stop();
      music.close();
      playing = false;
      currentMusic = "";
    }
  }

  //Method - loadSOund() - method given by Mr. McKenzie
  private void loadSound(String filename){
        try{
            AudioInputStream audioStream = AudioSystem.getAudioInputStream(new File("Sounds/" + filename).getAbsoluteFile());
            sound = AudioSystem.getClip();
            sound.open(audioStream);
            soundLoaded = true;
        } catch(Exception e){
            soundLoaded = false;
        }
    }

    //Method - playSound() - method given by Mr. McKenzie
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

  //Method - stopSound() - method given by Mr. McKenzie
  private void stopSound(){
        if(soundLoaded == false){
          return;
        }
        if(sound.isRunning() == true){
            sound.stop();
        }
  }

  //Method - paint. We use this to draw all the sprites and images of our game, as well as the text of our end screen.
  @Override
  public void paint(Graphics g){
    if(lvl == 11){ //drawing end screen
      g.drawImage(ending, 0, 0, null);
      Graphics2D g2 = (Graphics2D) g;
      g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
      g2.setColor(Color.WHITE);
      g2.setFont(endfnt);
      g2.drawString("CONGRATULATIONS!", 375, 100);
      g2.drawString("THANK YOU FOR PLAYING <3", 275, 200); //end message
      g2.setFont(fnt);
      g2.drawString("> Click to Main Menu", 954, 680);
      return;
    }
    if(lvl == -1){ //drawing startScreen
     g.drawImage(startScreen, 0, 0, 1280, 720, null);
     stopMusic();
     stopSound();
    }
    else if(loading == true && lvl == 0){ //startScreen drawing
      g.drawImage(startScreen, 0, 0, 1280, 720, null);
    }
    else if(lvl == 0){ //Lives select drawing
      g.drawImage(livesSelect, 0, 0, 1280, 720, null);
      
      Graphics2D g2 = (Graphics2D) g;
      g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
      
      if (normalRect.contains(mx, my)) { //drawing rect for normal mode
        g2.setColor(new Color(255, 255, 255, 100));
        g2.fillRect(normalRect.x, normalRect.y, normalRect.width, normalRect.height);
      }
      if (hardRect.contains(mx, my)){ //drawing hard mode rect
        g2.setColor(new Color(255, 255, 255, 100));
        g2.fillRect(hardRect.x, hardRect.y, hardRect.width, hardRect.height);
      }
    }
    else if(loading == true && lvl == 1){ //draw lives select 
      g.drawImage(livesSelect, 0, 0, 1280, 720, null);
    }
    
    else if(lvl == -2){ //or game over
     g.drawImage(gameover, 0, 0, null);
    }
    

    else if (lvl > 0 && lvl < 11){ //if in game levels (1-10), draw in all the elements if they exist for that level
      
      g.setColor(Color.BLACK); //Reset background
      g.fillRect(0,0,getWidth(), getHeight());
      if(lvl == 5){ //level 5 specific elements
        g.drawImage(ballScreen, 165, 6, 1115, 780, null);
        g.drawImage(laser, 165, 6, 1115, 18, null);
        
      }
      
      if (spikeWall != null){ //spike wall
        spikeWall.draw(g);
      }
      level.draw(g); //Draw level and bricks
      
      spikes.draw(g); //spikes get drawn
      
      
      if (appearWater != null) { //drawing water path
        appearWater.draw(g);
      }
      if (longSpikes != null) { //long spikes drawn
        longSpikes.draw(g);
      }
      if(movingSpikes != null){ //moving spikes drawn
        movingSpikes.draw(g);
      }
      if (movingBricks != null){ //moving bricks drawn
        movingBricks.draw(g);
      }
      if(breakableBricks != null){
        breakableBricks.draw(g);
      }

      if(lvl == 5){ //drawing balls in level 5 
        blocks.draw(g);
        platform.draw(g,keys[KeyEvent.VK_A] == true && platform.hasBoy() == true,keys[KeyEvent.VK_D] == true && platform.hasBoy() == true);
        for(Ball b : gameBoy.getBalls()){
          b.draw(g);
        }
      }
      if(lvl == 7){ //level 7 specific element (cannon)
        cannon.draw(g);
      }
      if(lvl == 8 || lvl == 9){ //sub drawing for applicable levels
        submarine.draw(g, gameBoy);
      }
      
      if (appearSpikes != null) { //appear spikes drawing
        appearSpikes.draw(g, lvl);
      }
      if (lvl == 7 || lvl == 8) { //shurikens drawing if applicable
        if (shurikens != null) {
                shurikens.draw(g);
              }
      }

      if (gameBoy.isInSubmarine() || lvl > 8) { //draw bombs from sub if the boy is in the submarine
        if (bomb != null) {
                bomb.draw(g);
              }
      }
      if (explosion != null) { //explosions if applicable
          explosion.draw(g);
      }

      if (rocket != null) { //rocket if applicable
        rocket.draw(g);
      }
      
      
      
    
      for(int i = 0; i< lives; i++){ //draw lives
        g.drawImage(life, 42 * i, 0, null);
      }
      if(lvl < 10){ 
        g.setColor(Color.BLACK);
        g.fillRect(1253,0,27,36);
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g2.setColor(Color.WHITE);
        g2.setFont(fnt);
        g2.drawString(""+String.valueOf(lvl), 1257, 30);
      }
      else{
        g.setColor(Color.BLACK);
        g.fillRect(1238,0,100,36);
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g2.setColor(Color.WHITE);
        g2.setFont(fnt);
        g2.drawString(""+String.valueOf(lvl), 1246, 30);
      }

      if((platform == null || platform.hasBoy() == false) && gameBoy.isInRocket() == false){ //draw gameboy if not on platform or rocket
        gameBoy.draw(g);
        
      }
      
    }
  }
}