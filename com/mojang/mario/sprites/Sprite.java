package com.mojang.mario.sprites;

import java.awt.Graphics;
import java.awt.Image;

import com.mojang.mario.LevelScene;
import com.mojang.mario.level.SpriteTemplate;
import com.mojang.sonar.SoundSource;

public abstract class Sprite implements SoundSource
{
  public static final double GROUND_INERTIA = 0.89;
  public static final double AIR_INERTIA = 0.89;
  
  // Store a sprites positioning/acceleration
  private double x;
  private double y;
  private double xOld;
  private double yOld;
  private double xA;
  private double yA;  
  
  // Store a sprites movement characteristics
  private int facing;
  private int width;
  private int height;
  private double runTime;
  private boolean onGround;
  private boolean avoidCliffs;
  
  // Store a sprites graphical appearance
  private int xPic;
  private int yPic;
  private int wPic;
  private int hPic;
  private int xPicO;
  private int yPicO;
  private boolean xFlipPic;
  private boolean yFlipPic;
  
  private Image[][] sheet;
  private boolean visible;
  private int layer = 1;
  private SpriteTemplate template;
  public static SpriteContext context;
    
  // Store the LevelScene a sprite is located in
  private LevelScene world;
  
  // Sprite Constructor
  public Sprite( LevelScene world, double x, double y, double yA, Image[][] sheet, int xPic, int yPic, int wPic, int hPic, int xPicO, int yPicO )
  {
    this.x = x;
    this.y = y;
    xOld = 0.0;
    yOld = 0.0;
    xA = 0.0;
    this.yA = yA;
    
    facing = 0;
    width = 4;
    height = 24;
    runTime = 0.0;
    onGround = false;
    avoidCliffs = false;
    
    this.xPic = xPic;
    this.yPic = yPic;
    this.wPic = wPic;
    this.hPic = hPic;
    this.xPicO = xPicO;
    this.yPicO = yPicO;
    xFlipPic = false;
    yFlipPic = false;
    
    this.sheet = sheet;
    visible = true;
    layer = 1;
    template = null;
    
    this.world = world;
  }
  
  public Sprite( Sprite sprite )
  {
    x = sprite.getX();
    y = sprite.getY();
    xOld = sprite.getXOld();
    yOld = sprite.getYOld();
    xA = sprite.getXA();
    yA = sprite.getYA();
    
    facing = sprite.getFacing();
    width = sprite.getWidth();
    height = sprite.getHeight();
    runTime = sprite.getRunTime();
    onGround = sprite.isOnGround();
    avoidCliffs = sprite.avoidingCliffs();
    
    xPic = sprite.getXPic();
    yPic = sprite.getYPic();
    wPic = sprite.getWPic();
    hPic = sprite.getHPic();
    xPicO = sprite.getXPicOrigin();
    yPicO = sprite.getYPicOrigin();
    xFlipPic = sprite.getXFlipPic();
    yFlipPic = sprite.getYFlipPic();
    
    sheet = sprite.getSheet();
    visible = sprite.isVisible();
    layer = sprite.getLayer();
    template = sprite.getSpriteTemplate();
  }
  
  // Sprite Constructor - built specifically for Mario
  public Sprite( LevelScene world )
  {
    Mario mario = world.getMario();
    
    if ( mario != null )
    {
      x = mario.getX();
      y = mario.getY();
      xOld = mario.getXOld();
      yOld = mario.getYOld();
      xA = mario.getXA();
      yA = mario.getYA();
      
      facing = 0;
      width = 4;
      height = 24;
      runTime = 0.0;
      onGround = false;
      avoidCliffs = false;
      
      xPic = mario.getXPic();
      yPic = mario.getYPic();
      wPic = mario.getWPic();
      hPic = mario.getHPic();
      xPicO = mario.getXPicOrigin();
      yPicO = mario.getYPicOrigin();
      xFlipPic = mario.getXFlipPic();
      yFlipPic = mario.getYFlipPic();
      
      sheet = mario.getSheet();
      visible = mario.isVisible();
      layer = mario.getLayer();
      template = mario.getSpriteTemplate();
      context = Sprite.context;
    }
    
    this.world = world;
  }
  
  // Accessor and Mutator Methods
  public double getX() {  return x;  }
  public void setX( double x ) {  this.x = x;  }
  public void changeX( double deltaX ) {  x += deltaX;  }
  
  public double getY() {  return y;  }
  public void setY( double y ) {  this.y = y;  }
  public void changeY( double deltaY ) {  y += deltaY;  }
  
  public double getXOld() {  return xOld;  }
  public void setXOld( double xOld ) {  this.xOld = xOld;  }
  public double getYOld() {  return yOld;  }
  public void setYOld( double yOld ) {  this.yOld = yOld;  }
  
  public double getXA() {  return xA;  }
  public void setXA( double xA ) {  this.xA = xA;  }
  public double getYA() {  return yA;  }
  public void setYA( double yA ) {  this.yA = yA;  }
  
  public int getFacing() {  return facing;  }
  public void setFacing( int facing ) {  this.facing = facing;  }
  
  public int getWidth() {  return width;  }
  public void setWidth( int width ) {  this.width = width;  }
  public int getHeight() {  return height;  }
  public void setHeight( int height ) {  this.height = height;  }
  
  public double getRunTime() {  return runTime;  }
  public void setRunTime( double runTime ) {  this.runTime = runTime;  }
  
  public boolean isOnGround() {  return onGround;  }
  public void onGround() {  onGround = true;  }
  public void offGround() {  onGround = false;  }
  
  public boolean avoidingCliffs() {  return avoidCliffs;  }
  public void setAvoidingCliffs( boolean avoidCliffs ) {  this.avoidCliffs = avoidCliffs;  }
  
  public int getXPic() {  return xPic;  }
  public void setXPic( int xPic ) {  this.xPic = xPic;  }
  public int getYPic() {  return yPic;  }
  public void setYPic( int yPic ) {  this.yPic = yPic;  }
  public int getWPic() {  return wPic;  }
  public void setWPic( int wPic ) {  this.wPic = wPic;  }
  public int getHPic() {  return hPic;  }
  public void setHPic( int hPic ) {  this.hPic = hPic;  }
  public int getXPicOrigin() {  return xPicO;  }
  public void setXPicOrigin( int xPicO ) {  this.xPicO = xPicO;  }
  public int getYPicOrigin() {  return yPicO;  }
  public void setYPicOrigin( int yPicO ) {  this.yPicO = yPicO;  }
  
  public boolean getXFlipPic() {  return xFlipPic;  }
  public void setXFlipPic( boolean xFlipPic ) {  this.xFlipPic = xFlipPic;  }  
  public boolean getYFlipPic() {  return yFlipPic;  }
  public void setYFlipPic( boolean yFlipPic ) {  this.yFlipPic = yFlipPic;  }  
  
  public Image[][] getSheet() {  return sheet;  }
  public void setSheet( Image[][] sheet ) {  this.sheet = sheet;  }
  
  public boolean isVisible() {  return visible;  }
  public void makeVisible() {  visible = true;  }
  public void makeInvisible() {  visible = false;  }
  
  public int getLayer() {  return layer;  }
  public void setLayer( int layer ) {  this.layer = layer;  }
  
  public SpriteTemplate getSpriteTemplate() {  return template;  }
  public void setSpriteTemplate( SpriteTemplate template ) {  this.template = template;  }
  
  public LevelScene getWorld() {  return world;  }
  public void setWorld( LevelScene world ) {  this.world = world;  }
  
  // Method: move()
  // Allows a Sprite to move.  Must be overwritten in subclasses.
  public abstract void move();
  
  // Method: tick()
  // Sets the current x/y positions to xOld/yOld and advances the Sprite forward
  public void tick()
  {
    xOld = x;
    yOld = y;
    move();
  }
  
  // Method: tickNoMove()
  // Sets the current x/y positions to xOld/yOld but does not move the Sprite
  public void tickNoMove()
  {
    xOld = x;
    yOld = y;
  }
  
  // Method: getXPixel( double alpha )
  // Returns the xPixel in the render method
  public double getXPixel( double alpha )
  {
    return ( xOld + ( x - xOld ) * alpha ) - xPicO;
  }
  
  // Method: getYPixel( double alpha )
  // Returns the yPixel in the render method
  public double getYPixel( double alpha )
  {
    return ( yOld + ( y - yOld ) * alpha ) - yPicO;
  }
  
  // Method: render( Graphics og, double alpha )
  // Draws the Sprite to the screen
  public void render( Graphics og, double alpha )
  {
    if ( !visible )
      return;
    
    int xPixel = (int) getXPixel( alpha );
    int yPixel = (int) getYPixel( alpha );
    
    Image spriteSheet = sheet[ xPic ][ yPic ];
    int xCoordinate = xPixel;
    int yCoordinate = yPixel;
    int w = wPic;
    int h = hPic;
    
    if ( xFlipPic ) 
    {
      xCoordinate += wPic;
      w = -w;
    }
    if ( yFlipPic ) 
    {
      yCoordinate += hPic;
      h = -h;
    }
    
    og.drawImage( spriteSheet, xCoordinate, yCoordinate, w, h, null );
  }
  
  // Method: bumpCheck( int xTile, int yTile )
  // Empty here.  Must be overwritten in subclasses.
  public void bumpCheck( int xTile, int yTile ) {  }
  
  // Method: collideCheck( )
  // Empty here.  Must be overwritten in subclasses.
  public void collideCheck() {  }
  
  // Method: shellCollideCheck( Shell s )
  // Checks to see if a Shell collided with something
  public boolean shellCollideCheck( Shell s ) 
  {  
    return false;
  }
  
  // Method: fireballCollideCheck( Fireball f )
  // Checks to see if a Fireball collided with something
  public boolean fireballCollideCheck( Fireball f ) 
  {  
    return false;
  }
}