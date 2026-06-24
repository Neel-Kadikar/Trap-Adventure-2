/* 
Boy.java
Neel Kadikar, Arsal Gazi
This program handles the boy and his movement through his various modes like walking, swimming, and moving in the submarine. The boy has attributes, like x, y, and different gravity types.

The boy has three different states with differing movement: walking, swimming, and submarine moving. In all modes he is controlled through the WASD keys
In walking mode, he moves at a set speed, has the ability to jump, and falls from places due to his gravity.
In swimming mode, his gravity (sinking in the water) is far slower, and he is able to move up and down in his space (water) more freely. He cannot jump
In submarine mode, his gravity is none! The submarine prevents him from sinking at all. He is also able to move up and down similar to swimming, but a bit faster.
Once again, he cannot jump while submarine moving. While in the submarine, he also drops bombs at a set interval. This is controlled by other classes, but is exclusive to the submarine mode.

In this class, many collisions are also handled for the various solid objects in the game, like traps, and bricks. For bricks, including level bricks, blocks, moving 
bricks and more. For the majority of traps, the level resets when he collides with them.

This class also tracks the player's state, and handles the physics of the boy, like the gravity mentioned above. This is done by checking if the player is on the ground,
that is, touching bricks or other solid objects.
*/

//Imports necessary libraries
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.lang.reflect.Array;
import javax.sound.sampled.*;
import javax.swing.*;
import java.util.ArrayList;


//Main class - boy. Handles all the modes, dimensions, location, speed, and interactions of boy.
class Boy{
 public static final int IDLER  = 0, IDLEL = 1, SWIML = 2, SWIMR = 3, SUBL = 4, SUBR = 5;
 private int x, y, state; //state is for the boy's method of movement
 private int width, height;
 private String lastSwimHorizontalDirection = "LEFT"; //start in left as this is the default direction when spawning into the swimming levels. Used to keep consistency when moving vertically
 private String lastSubHorizontalDirection = "LEFT"; //start in left as this is the default direction when spawning into the submarine levels
 private double speed, verticalSwimSpeed, subSpeed, verticalSubSpeed; 
 private double vy, gravity, waterGravity, subGravity, jump; //gravities and velocities and jump
 private boolean onGround, swimming, submarineMoving;
 private Image walkR, walkL, idleR, idleL, jumpL, jumpR, crouchR, crouchL, swimR, swimL, subL, subR; //sprites for the different methods of moving
 private Image boy;
 private boolean onPlatform = false; //booleans for checking boy in relation to platform
 private Platform theplatform;
 private boolean platHop = false;
 private ArrayList<Ball> balls = new ArrayList<>();
 private boolean ballSpawn = false;
 private boolean inSubmarine = false;
 private Clip sound; //sound variables
 private String currentSoundFile = "";
 private boolean soundLoaded = false;
 private Clip jumpSound;
 private Rectangle dismountRect; //for location where he can dismount from the submarine
 private boolean dismounted;
 private boolean inRocket = false;
 
 //Constructor - boy. Handles the images and x and y coordinates of the boy.
 public Boy(int xx, int yy, int ss){
  x = xx;
  y = yy; //sets the x and y position of where the boy is on the screen
  state = ss;
  dismountRect = null;
  dismounted=false;

  walkR = new ImageIcon("Sprites/boyWalkingR.gif").getImage(); //various sprites for the boy
  walkL = new ImageIcon("Sprites/boyWalkingL.gif").getImage();
  idleR = new ImageIcon("Sprites/boyR.png").getImage();
  idleL = new ImageIcon("Sprites/boyL.png").getImage();
  jumpL = new ImageIcon("Sprites/boyFallL.png").getImage();
  jumpR = new ImageIcon("Sprites/boyFallR.png").getImage();
  crouchR = new ImageIcon("Sprites/boyCrouchR.png").getImage();
  crouchL = new ImageIcon("Sprites/boyCrouchL.png").getImage();
  swimR = new ImageIcon("Sprites/boySwimmingR.gif").getImage();
  swimL = new ImageIcon("Sprites/boySwimmingL.gif").getImage();
  subL = new ImageIcon("Sprites/subIdleL.gif").getImage();
  subR = new ImageIcon("Sprites/subIdleR.gif").getImage();

  
  loadSound();

  setState(ss);
 }

 //Method = sets the boolean of if boy is in rocket or not (for last level)
 public void setInRocket(boolean val) {
    stopSound();
    inRocket = val;
  }

  // method - returns if boy is in rocket or not
  public boolean isInRocket(){
    return inRocket;
  }

  //Method - sets the boy's x
  public void setX(int newX){
    x = newX;
  }
  //Method - sets the boy's y
  public void setY(int newY) {
    y = newY;
  }

  //Method - move. Handles the boys movement in walking, not swimming or submarine movement. Requires many parameters for moving around the various traps and objects of the game, and handling movement via WASD.
 public void move(int lvl, boolean []keys, Level level, MovingBricks movingBricks, SpikeWall spikeWall, Platform platform, Blocks blocks, Submarine submarine, BreakableBricks breakableBricks, InvisibleBricks invisibleBricks){ 
  if(level == null){ //don't move if not in a level
    return;
  }
  if (isInSubmarine()) { //use submarine movement, not walking movement if they boy is in the sub
    submarineMovement(lvl, keys, level, movingBricks, spikeWall, platform, blocks, submarine, breakableBricks, invisibleBricks);
    return;
  }
  
  
  boolean moving = false; //booleans for movement and crouching
  boolean crouching = false;
  int nextX = x; //used to check if next location is valid
  int nextY = y;
  if(keys[KeyEvent.VK_D]){ //moving right
    for(int i=1; i<=speed; i++){ //if it doesn't collide with any game objects that it shouldn't be able to pass through, move forward 
      if(collides(x + i, y, level, movingBricks,spikeWall,platform,blocks, submarine, breakableBricks, invisibleBricks, dismountRect) == false){
        nextX = x + i; //checks if the next x would collide, if it doesn't: move 
      }
      else{
        stopSound();
        break;
      }
    }
    boy = walkR; //sprite set
   moving = true;
  }

  else if(keys[KeyEvent.VK_A]){ //moving left
   for(int i=1; i<=speed; i++){ //same as above, should not collide with anything when moving
    if(collides(x - i, y, level, movingBricks,spikeWall,platform,blocks, submarine, breakableBricks, invisibleBricks, dismountRect) == false){
      nextX = x-i;
    }
    else{
      break;
    }
   }
    boy = walkL;
    moving = true;
  }

  x = nextX;

  if(keys[KeyEvent.VK_W] && onGround == true){  //when jumping, must be on the ground.
    vy = jump;
    playSound(jumpSound);
  }

  if(onGround == false ){ //bring him down via gravity if he is not on the ground
    vy += gravity;
  }

  

  land(level, movingBricks, spikeWall,platform,blocks, breakableBricks, invisibleBricks); //call land method for checking vertical collisions
  onGround = checkGround(level, movingBricks, spikeWall, platform,blocks, breakableBricks, invisibleBricks);

  if(onPlatform == true && platform != null){ // if the boy is on the platform, he has different movement
    stopSound();
    boolean left = false;
    boolean right = false;

    if(keys[KeyEvent.VK_A]){
        theplatform.move("left"); //move the platform instead of moving the boy
        left = true;
    }
    if(keys[KeyEvent.VK_D]){ //same as above for right
        theplatform.move("right");
        right = true;
    }
    //move him in accordance to the platform's movement to help keep up
    x = theplatform.x + theplatform.width / 2 - width / 2;
    y = theplatform.y - height;

    //spawn new balls if he pressess "s" for the ball and blocks level
    if(keys[KeyEvent.VK_S] && ballSpawn == false){
      Ball b = new Ball(x, theplatform.y + theplatform.height + 1);
      balls.add(b); //call appropriate methods from ball
    }

    //Detach from platform with w
    if (keys[KeyEvent.VK_W]) {
        theplatform.detach(this);
    }

    ballSpawn = keys[KeyEvent.VK_S];
  }

  for(Ball b : balls){
    b.move(blocks, theplatform, level); //move the spawned in balls
  }

  //Crouching - must be on the ground, and hitbox will change
  if(keys[KeyEvent.VK_S] && onGround == true && moving == false){
    crouching = true;
    if(height == 36){
      height = 27;
      y += 9;
    }
  }
  else{
    crouching = false;
    if(crouching == false && height == 27){
      height = 36;
      y -= 9;
    }
  }
  //Don't go off screen vertically or horizontally
  if(x + width > 1280){
    x = 1280 - width;
  }
  else if(x < 0){
    x = 0;
  }

  if(y < 0){
    y = 0;
  }
  else if(y + height > 720){
    y = 720 - height;
  }
   
  if(onGround == false){
    stopSound();
    if(boy == jumpL || boy == walkL || boy == idleL || boy == crouchL){ //if the boy was in a left direction before he jumped, jump him facing left
      boy = jumpL;
    }
    else if(boy == jumpR || boy == walkR || boy == idleR || boy == crouchR){ //same as above for right
      boy = jumpR;
    }
  }
  else if(moving == true){
    playSound("BoyWalk.wav", true);
    if(boy == jumpL || boy == walkL || boy == idleL || boy == crouchL){ //if he was crouching, jumping or idling left, move him in the left direction when he moves
      boy = walkL;
    }
    else if(boy == jumpR || boy == walkR || boy == idleR || boy == crouchR){ //same as above but for right
      boy = walkR;
    }
  }
  else if(crouching == true){ // same logic as previous two if statements, maintains direction between movement options
    stopSound();
    if(boy == jumpL || boy == walkL || boy == idleL || boy == crouchL){
      boy = crouchL;
    }
    else if(boy == jumpR || boy == walkR || boy == idleR || boy == crouchR){
      boy = crouchR;
    }
  }
  else{
    stopSound();
    if(boy == jumpL || boy == walkL || boy == idleL || boy == crouchL){ //same as above
      boy = idleL;
    }
    else if(boy == jumpR || boy == walkR || boy == idleR || boy == crouchR){
      boy = idleR;
    }
  }
 }

 //Method - attach
 //attaches the boy to the platform and updates necessary variables
 public void attach(Platform plat){
    stopSound();
    onPlatform = true;
    theplatform = plat;
    x = plat.x + plat.width / 2 - width / 2;
    y = plat.y - height;
    vy = 0;
}

//method - detatch
//updates necessary variables for detatching from level 5 platform
public void detach() {
    stopSound();
    onPlatform = false;
    theplatform = null;
    y += 100;
    vy = 0;
    platHop = true;
}
 

 //Method - Swim. Handles the movement of the boy in levels past 5, with swimming having very different physics from walking. Must take in many parameters to handle collisions
 // with blocks and traps underwater
 public void swim(int lvl, boolean []keys, Level level, MovingBricks movingBricks, SpikeWall spikeWall, Platform platform, Blocks blocks, Submarine submarine, BreakableBricks breakableBricks, InvisibleBricks invisibleBricks){ 
  if(level == null){
    return;
  }

  
  
  int nextX = x;
  int nextY = y;
  if(keys[KeyEvent.VK_D]){ //swimming right. Checks if next position will collide with anything.
   for(int i=1; i<=speed; i++){
    if(collides(x + i, y, level, movingBricks,spikeWall,platform,blocks, submarine, breakableBricks, invisibleBricks, dismountRect) == false){
      swimming = true;
      nextX = x+i;
    }
    else{
      break;
    }
   }
   swimming = true;
   boy = swimR;
   lastSwimHorizontalDirection="RIGHT"; //used to keep track of last swam direction to maintain proper sprites when swimming up and down
  }

  else if(keys[KeyEvent.VK_A]){ //swimming left. Checks if next position will collide with anything.
   for(int i=1; i<=speed; i++){
    if(collides(x - i, y, level, movingBricks,spikeWall,platform,blocks, submarine, breakableBricks, invisibleBricks, dismountRect) == false){
      swimming = true;
      nextX = x-i;
    }
    else{
      break;
    }
   }
   swimming = true;
   boy = swimL;
   lastSwimHorizontalDirection="LEFT"; //used to keep track of last swam direction to maintain proper sprites when swimming up and down
  }

  x = nextX; //move if valid and not colliding

  
  //vertical swimming upwards - this is unique from walking. 
  //Underwater, the boy can rise in the water almost freely. A new w input must be added to replace jumping, in which he moves up with 
  //the necessary collision checks
  if(keys[KeyEvent.VK_W]){ 
   for(int i=1; i<=verticalSwimSpeed; i++){
    if(collides(x, y-i, level, movingBricks,spikeWall,platform,blocks, submarine, breakableBricks, invisibleBricks, dismountRect) == false){
      swimming = true;
      nextY = y-i;
    }
    else{
      break;
    }
   }
  }
  
  //vertical swimming upwards - this is unique from walking. 
  //Same as above, but for moving down.
  // in game player must be mindful as they also underwater gravity acting on them
  else if(keys[KeyEvent.VK_S]){
   for(int i=1; i<=verticalSwimSpeed; i++){
    if(collides(x, y+i, level, movingBricks,spikeWall,platform,blocks, submarine, breakableBricks, invisibleBricks, dismountRect) == false){
      swimming = true;
      nextY = y+i;
    }
    else{
      break;
    }
   }
  }

  else{
    for(int i = 1; i <= verticalSwimSpeed; i++){
      if(collides(x, y + i, level, movingBricks,spikeWall,platform,blocks, submarine, breakableBricks, invisibleBricks, dismountRect) == false){
        swimming = true;
        nextY += waterGravity; //move the player down by water gravity amount. They will not stay idle and must keep swimming to avoid falling down
      } 
      else{
        vy = 0;
        break;
      }
    }
  }

  y = nextY;
  //Once again, don't leave the screen
  if(x + width > 1280){
    x = 1280 - width;
  }
  else if(x < 0){
    x = 0;
  }

  if(y < 0){
    y = 0;
  }
  else if(y + height > 720){
    y = 720 - height;
  }

 }


 //Submarine movement
 // this is very different from both swimming and walking movement
 // the player has no gravity as a buff from the submarine
 // they can also move much faster
 // additionally, they drop bombs while in the submarine.
 public void submarineMovement(int lvl, boolean []keys, Level level, MovingBricks movingBricks, SpikeWall spikeWall, Platform platform, Blocks blocks, Submarine submarine, BreakableBricks breakableBricks, InvisibleBricks invisibleBricks){ 
  if(level == null){
    return;
  }

  width = 77;
  height = 63;
  

  int nextX = x;
  int nextY = y;
  if(keys[KeyEvent.VK_D]){ //right movement in submarine
   for(int i=1; i<=subSpeed; i++){
    if(collides(x + i, y, level, movingBricks,spikeWall,platform,blocks, submarine, breakableBricks, invisibleBricks, dismountRect) == false){
      submarineMoving = true;
      nextX = x+i;
    }
    else{
      break;
    }
   }
   submarineMoving = true;
   boy = subR;
   lastSubHorizontalDirection="RIGHT";
  }

  else if(keys[KeyEvent.VK_A]){ //left movement in submarine
   for(int i=1; i<=subSpeed; i++){
    if(collides(x-i, y, level, movingBricks,spikeWall,platform,blocks, submarine, breakableBricks, invisibleBricks, dismountRect ) == false){
      submarineMoving = true;
      nextX = x-i;
    }
    else{
      break;
    }
   }
   submarineMoving = true;
   boy = subL;
   lastSubHorizontalDirection="LEFT";
  }

  else{ //maintain correct facing direction when moving up or down
    if(lastSubHorizontalDirection.equals("LEFT")){ 
      boy = subL;
    }
    else{
      boy = subR;
    }
  }

  x = nextX;

  // upward movement in the submarine
  if(keys[KeyEvent.VK_W]){
   for(int i=1; i<=verticalSubSpeed; i++){
    if(collides(x, y-i, level, movingBricks,spikeWall,platform, blocks, submarine, breakableBricks, invisibleBricks, dismountRect) == false && dismounted!=true){
      submarineMoving = true;
      nextY = y-i;
    }
    else{
      break;
    }
   }
   //Dismounting submarine - getting out when at the correct point in level 9 near the end to switch back to land based movement
   if (dismountRect != null && getRect().intersects(dismountRect) && !dismounted) { //player must be in the dismountRect and not dismounted already
     
     
     // Set dismounted flag first
     dismounted = true;
     
     // Store current position - for safety
     int oldX = x;
     int oldY = y;
     
     // Exit submarine mode first
     setInSubmarine(false);
     
     // Set walking state
     setState(IDLER);
     
     //position boy on land
      x=1000;
      y = dismountRect.y + dismountRect.height/2 - height/2; //around the middle of dismount rect
     
     onGround = false;
     return; // exit the method so we don't process more submarine movement
   }
  }
  
  //downward movement in the sub
  else if(keys[KeyEvent.VK_S]){
   for(int i=1; i<=verticalSubSpeed; i++){
    if(collides(x, y+i, level, movingBricks,spikeWall,platform, blocks, submarine, breakableBricks, invisibleBricks, dismountRect) == false){
      submarineMoving = true;
      nextY = y+i;
    }
    else{
      break;
    }
   }
  }

  else{
    for(int i = 0; i < 2; i++){
      if (!collides(x, y + 1, level, movingBricks, spikeWall, platform, blocks, submarine, breakableBricks, invisibleBricks, dismountRect)) {
        submarineMoving = true;
        nextY += subGravity; //used in case we did want to add sub gravity, however the sub provides a buff so gravity is 0.
      } 
      else{
        vy = 0;
        break;
      }
    }
  }

  y = nextY; //move if valid

  //once again dont go off screen
  if(x + width > 1280){ 
    x = 1280 - width;
  }
  else if(x < 0){
    x = 0;
  }

  if(y < 0){
    y = 0;
  }
  else if(y + height > 720){
    y = 720 - height;
  }

 }

 //Method - set in submarine. Simply sets the state for the sub if given true and sets a location
  public void setInSubmarine(boolean val) { 
    if(inSubmarine == val){
      return;
    }
    inSubmarine = val; 
    if(val == true){
      setState(SUBL);
      x = 622;
      y = 50;
    }
    dismounted = false;
  }
  //method used to get last horizontal direction to know which way to face sub when moving vertically
  public String getLastSubHorizontalDirection() { 
    return lastSubHorizontalDirection;
  }
  // method - simply return if boy is in submarine
  public boolean isInSubmarine() { 
    return inSubmarine; 
  }
  //method - returns the boy's state
  public int getState(){
    return state;
 }

 //method setState - used to set the boy's state based on given values. Used by various other methods. sets new dimensions and gravity when changing movement modes
 public void setState (int newState) {
  state=newState;
  if(state == IDLER || state == IDLEL){ //for walking movement. Regular size, highest gravity, ability to jump
    width = 27;
    height = 36;//idle width and heights
    speed = 6.9;
    subSpeed = 7.5;
    verticalSwimSpeed = 4;
    verticalSubSpeed = 5;
    vy = 0;
    gravity = 1.05;
    waterGravity = 1;
    subGravity = 0.5;
    jump = -22;
    if(state == IDLER){//relative to the direction
      boy = idleR;//for each mode, set the appropriate sprite
    }
    else{
      boy = idleL;
    }
  }
  else if(state == SWIML || state == SWIMR){ //for swimming movement. Regular size, but horizontal, low gravity, ability to move vertically freely
    width = 32;
    height = 32;//swim mode width and heights
    if(state == SWIMR){
      boy = new ImageIcon("Sprites/boySwimR.png").getImage();
    }
    else{
      boy = new ImageIcon("Sprites/boySwimL.png").getImage();
    }
  }
  else if(state == SUBL || state == SUBR){ //for submarine movements. Large size, free movement across all directions, dropping bombs, no gravity
    y -= 14;
    x-= 25;
    width = 77;
    height = 63;//submarine mode width and heights
     

    if(state == SUBR){
        boy = new ImageIcon("Sprites/subIdleR.gif").getImage();
    }
    else{
        boy = new ImageIcon("Sprites/subIdleL.gif").getImage();
    }
    }
 }

 //method - sets up the rect for dismounting for level 9 only!
 public void dismountRectSetup(int lvl){
  if (lvl==9) {
      dismountRect = new Rectangle(880, 280, 110, 110);
  }
 }

 //method - return if boy dismounted
 public boolean hasDismounted() {
    return dismounted;
 }

 //Method - resetPos. This method is used to reset the boy's position when entering a level or when he dies in a level and must restart it. 
 public void resetPos(int lvl){
  stopSound();
  platHop = false;
  onPlatform = false;
  theplatform = null;
  balls.clear();
  if(lvl == 1){
    x = 120;
    y = 100;
  }
  else if(lvl == 2){
    x = 30;
    y = 497;
  }
  else if(lvl == 3){
    x = 30;
    y = 277;
  }
  else if(lvl == 4){
    x = 30;
    y = 220;
  }
  
  else if(lvl == 5){
    x = 30;
    y = 220;
  }
  else if(lvl == 6){
    x = 1136;
    y = 0;
  }
  else if(lvl == 7){
    x = 1218;
    y = 61;
  }
  else if(lvl == 8){
    x = 1218;
    y = 110;
  }

  else if(lvl == 9 && dismounted==false){
    x = 72;
    y = 20;
 
  }

  else if(lvl == 10){
    x = 30;
    y = 275;
  }

 }

 //Method to return balls
 public ArrayList<Ball> getBalls(){
   return balls;
 }

 //Method - collides. This is used to check the boy's collisions with the elements of the game.
 public boolean collides(int nextX, int nextY, Level level, MovingBricks movingBricks, SpikeWall spikeWall, Platform platform, Blocks blocks, Submarine submarine, BreakableBricks breakableBricks, InvisibleBricks invisibleBricks, Rectangle dismountRect ){
   Rectangle nextplace = new Rectangle (nextX, nextY, width, height);

   for(Rectangle block : level.getSolid()){ //check solid level blocks for collisions
     if(nextplace.intersects(block)){
       return true;
     }
   }
   for(Rectangle blockk : blocks.getSolid()){ //check level 5 blocks 
     if(nextplace.intersects(blockk)){
       return true;
     }
   }
   if (invisibleBricks!=null){
    for(Rectangle brickk : invisibleBricks.getActiveInvisibleBricks()){ //check invisible bricks
        if(nextplace.intersects(brickk)){
          return true;
        }
      }
   }
   

   if(movingBricks != null){
    for(Rectangle block : movingBricks.getActiveBricks()){ //check for moving bricks
      if(nextplace.intersects(block)){
        return true;
      }
    }
   }
   if(spikeWall != null){
    Rectangle spikeRect = spikeWall.getGroundRect();
    if(spikeRect != null &&nextplace.intersects(spikeRect)){ //check spikewall
        return true;
      }
    }
    
    if(platform != null){
      Rectangle feet = new Rectangle(x, y + height + 1, width, 2);
      if(onPlatform == false && feet.intersects(platform.getRect()) && platHop == false) { //check for platform
          platform.attach(this);
      }
    }

    if(breakableBricks != null){
      for(Rectangle brick : breakableBricks.getActiveBreakableBricks()){ //check for breakable bricks
        if(nextplace.intersects(brick)){
          return true;
        }
      }
    }

    

   return false; //if not colliding with any of the above
 }

 //Method - checkground. Checks if the boy is standing on any solid surface. Works similar to collides.
 private boolean checkGround(Level level, MovingBricks movingBricks, SpikeWall spikeWall, Platform platform, Blocks blocks, BreakableBricks breakableBricks, InvisibleBricks invisibleBricks){
    Rectangle feet = new Rectangle(x,y + height + 1,width,2); //use smaller hitbox so he doesn't sink in the surfaces

    for(Rectangle block : level.getSolid()){ 
        if(feet.intersects(block)){
            return true;
        }
    }
    
    for(Rectangle blockk : blocks.getSolid()){ 
        if(feet.intersects(blockk)){
            return true;
        }
    }
    if (invisibleBricks!=null){
    for(Rectangle brickk : invisibleBricks.getActiveInvisibleBricks()){
        if(feet.intersects(brickk)){
          return true;
        }
      }
   }
    
   if(movingBricks != null){
    for(Rectangle block : movingBricks.getActiveBricks()){
      if(feet.intersects(block)){
        return true;
      }
    }
  }
  if (platform != null){
    if (onPlatform == false && feet.intersects(platform.getRect()) && platHop == false) {
      platform.attach(this);
    }
  }
  if (spikeWall != null && spikeWall.getGroundRect() != null){
    if (feet.intersects(spikeWall.getGroundRect())){
        return true;
    }
  }

  if(breakableBricks != null){
      for(Rectangle brick : breakableBricks.getActiveBreakableBricks()){
        if(feet.intersects(brick)){
          return true;
        }
      }
    }

  return false; 
 }

 //Method - land. Used to check for the player's vertical collisions
 private void land(Level level, MovingBricks movingBricks, SpikeWall spikeWall, Platform platform, Blocks blocks, BreakableBricks breakableBricks, InvisibleBricks invisibleBricks){
  int nextY = y + (int)vy;
   Rectangle nextplace = new Rectangle (x, nextY, width, height);
   boolean landed = false;
   for(Rectangle block : level.getSolid()){
     if(nextplace.intersects(block)){
       if(vy > 0){
         vy = 0;
         y = block.y - height;
         landed = true;
       }
       else if(vy < 0){
         vy = 0;
         y = block.y + block.height;
       }
        return;
     }
   }

   if(invisibleBricks!=null){
    for(Rectangle brickk : invisibleBricks.getActiveInvisibleBricks()){
     if(nextplace.intersects(brickk)){
       if(vy > 0){
         vy = 0;
         y = brickk.y - height;
         landed = true;
       }
       else if(vy < 0){
         vy = 0;
         y = brickk.y + brickk.height;
       }
        return;
     }
   }
   }

   
    for(Rectangle blockk : blocks.getSolid()){
      if(nextplace.intersects(blockk)){
        if(vy > 0){
          vy = 0;
          y = blockk.y - height;
          landed = true;
        }
        else if(vy < 0){
          vy = 0;
          y = blockk.y + blockk.height;
        }
          return;
      }
    }
    
   
   
   if(movingBricks != null){
    for(Rectangle block : movingBricks.getActiveBricks()){
      if(nextplace.intersects(block)){
       if(vy > 0){
         vy = 0;
         y = block.y - height;
         landed = true;
       }
       else if(vy < 0){
         vy = 0;
         y = block.y + block.height;
       }
      }
    }
   }

   if (spikeWall != null && spikeWall.getGroundRect() != null) {
    Rectangle rect = spikeWall.getGroundRect();
    if (nextplace.intersects(rect) && vy > 0) {
        vy = 0;
        y = rect.y - height;
        return;
    }
  }

   if(platform != null && platHop == false){
    Rectangle platformRect = platform.getRect();
    if(nextplace.intersects(platformRect)){
       if(vy > 0){
         vy = 0;
         y = platformRect.y - height;
         landed = true;
       }
       else if(vy < 0){
         vy = 0;
         y = platformRect.y + platformRect.height;
       }
      }
  }

  if(breakableBricks != null){
      for(Rectangle brick : breakableBricks.getActiveBreakableBricks()){
        if(nextplace.intersects(brick)){
       if(vy > 0){
         vy = 0;
         y = brick.y - height;
         landed = true;
       }
       else if(vy < 0){
         vy = 0;
         y = brick.y + brick.height;
        }
      }
    }
  }

   

  if(landed == false){
    y = nextY;
  }
 }

 //Method - get x value
 public int getX(){
   return x;
 }
 //Method - get y value
 public int getY(){
   return y;
 }
 //Method - get width of boy
 public int getWidth(){
   return width;
 }
 //method - get height
 public int getHeight(){
   return height;
 }
 //method - getRect
 public Rectangle getRect(){
  return new Rectangle(x, y, width, height);
 }
 //Method - playsound. Given by Mr. McKenzie
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
  //Method - stopSound. Given by Mr. McKenzie
  public void stopSound(){
    if(sound != null && sound.isRunning()){
        sound.stop();
        sound.close();
        currentSoundFile = "";
    }
  }

  //Method - loadSound. Given by Mr. McKenzie
  private void loadSound(){
        try{
            AudioInputStream audioStream = AudioSystem.getAudioInputStream(new File("Sounds/BoyJump.wav").getAbsoluteFile());
            jumpSound = AudioSystem.getClip();
            jumpSound.open(audioStream);
            soundLoaded = true;
        } catch(Exception e){
            soundLoaded = false;
        }
    }
 //Method - playSound. Given by Mr. McKenzie
  private void playSound(Clip sound){
        if(soundLoaded == false){
          return;
        }
        if(sound.isRunning() == true){
            sound.stop();
        }
        sound.setFramePosition(0);
        sound.start();
    }
 //Method - draw. Draws the boy as long as he is not in the rocket
 public void draw(Graphics g){
  if(inRocket == false){
    g.drawImage(boy, x, y, null);
  }
  else{
    stopSound();
  }
  
  }
}