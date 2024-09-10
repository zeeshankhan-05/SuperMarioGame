package com.mojang.mario.sprites;

import com.mojang.mario.Art;
import com.mojang.mario.Scene;
import com.mojang.mario.level.*;
import com.mojang.mario.LevelScene;

public abstract class Avatar extends Sprite
{
  public static int lives = 3;
  public static String levelString = "none";
  
  private double sideWaysSpeed;
  private boolean wasOnGround;
  private int jumpTime;
  private double xJumpSpeed;
  private double yJumpSpeed;
  
  private int xDeathPos;
  private int yDeathPos;
  private int deathTime;
  private int winTime;
  private boolean large;
  private int invulnerableTime;
  private Sprite carriedSprite;
  
  public static final int KEY_LEFT = 0;
  public static final int KEY_RIGHT = 1;
  public static final int KEY_DOWN = 2;
  public static final int KEY_UP = 3;
  public static final int KEY_S = 4;
  public static final int KEY_A = 5;
  private boolean[] keys;
  
  public Avatar( LevelScene world )
  {
    super( world );
    
    Avatar.levelString = "AP CS";
    
    sideWaysSpeed = 0;
    wasOnGround = false;
    jumpTime = 0;
    xJumpSpeed = 0;
    yJumpSpeed = 0;
    
    xDeathPos = 0;
    yDeathPos = 0;
    deathTime = 0;    
    winTime = 0;
    large = false;
    invulnerableTime = 0;    
    carriedSprite = null;
    
    keys = Scene.keys;
  }
  
  public double getSideWaysSpeed() {  return sideWaysSpeed;  }
  public void setSideWaysSpeed( double sideWaysSpeed ) {  this.sideWaysSpeed = sideWaysSpeed;  }
  public boolean wasOnGround() {  return wasOnGround;  }
  public void setWasOnGround( boolean wasOnGround ) {  this.wasOnGround = wasOnGround;  }
  public int getJumpTime() {  return jumpTime;  }
  public void setJumpTime( int jumpTime ) {  this.jumpTime = jumpTime;  }
  public double getXJumpSpeed() {  return xJumpSpeed;  }
  public void setXJumpSpeed( double xJumpSpeed ) {  this.xJumpSpeed = xJumpSpeed;  }
  public double getYJumpSpeed() {  return yJumpSpeed;  }
  public void setYJumpSpeed( double yJumpSpeed ) {  this.yJumpSpeed = yJumpSpeed;  }
  
  public int getXDeathPos() {  return xDeathPos;  }
  public void setXDeathPos( int xDeathPos ) {  this.xDeathPos = xDeathPos;  }
  public int getYDeathPos() {  return yDeathPos;  }
  public void setYDeathPos( int yDeathPos ) {  this.yDeathPos = yDeathPos;  }
  public int getDeathTime() {  return deathTime;  }
  public void setDeathTime( int deathTime ) {  this.deathTime = deathTime;  }
  public int getWinTime() {  return winTime;  }
  public void setWinTime( int winTime ) {  this.winTime = winTime;  }
  public boolean isLarge() {  return large;  }
  public void setIsLarge( boolean large ) {  this.large = large;  }
  public int getInvulnerableTime() {  return invulnerableTime;  }
  public void setInvulnerableTime( int invulnerableTime ) {  this.invulnerableTime = invulnerableTime;  }
  public Sprite getCarriedSprite() {  return carriedSprite;  } 
  public void setCarriedSprite( Sprite carriedSprite ) {  this.carriedSprite = carriedSprite;  } 
  
  public boolean[] getKeys() {  return keys;  }
  
  public void move()
  {
    if ( preMove() )
    {
      return;
    }
    else
    {
      if ( invulnerableTime > 0 )
        invulnerableTime--;
      
      if ( ((invulnerableTime / 2) & 1) == 0 )
        makeVisible();
      else
        makeInvisible();
      
      wasOnGround = isOnGround();
      
      skidCheck();
      airTime();
      
      if ( leftPressed() )
        leftAction();
      if ( rightPressed() )
        rightAction();
      if ( sPressed() )
        sAction();
      
      if ( aPressed() )
        aAction();
      else
        sideWaysSpeed = 0.6;
      
      afterMove();
    }
  }
  
  public boolean preMove()
  {
    if ( winCheck() )
      return true;
    
    if ( deathCheck() )
      return true;
    
    if ( getWorld().powerUpCheck() )
      return true;
    
    return false;
  }
  
  public void afterMove()
  {
    setXFlipPic( getFacing() == -1 );
    setRunTime( getRunTime() + Math.abs( getXA() ) + 5 );
    
    if ( Math.abs( getXA() ) < 0.5 )
    {
      setRunTime( 0 );
      setXA( 0 );
    }
    
    completeMove();    
  }
  
  public void completeMove()
  {
    offGround();
    move( getXA(), 0 );
    move( 0, getYA() );
    
    if ( getY() > getWorld().getLevel().getHeight() * 16 + 16 )
    {
      die();
    }
    
    if ( getX() < 0 )
    {
      setX( 0 );
      setXA( 0 );
    }
    
    if ( getX() > getWorld().getLevel().getXExit() * 16 )
    {
      win();
    }
    
    if ( getX() > getWorld().getLevel().getWidth() * 16 )
    {
      setX( getWorld().getLevel().getWidth() * 16 );
      setXA( 0 );
    }
    
    setYA( getYA() * 0.85 );
    
    if ( isOnGround() )
      setXA( getXA() * Sprite.GROUND_INERTIA );
    else
      setXA( getXA() * Sprite.AIR_INERTIA );
    
    if ( !isOnGround() )
      setYA( getYA() + 3 );
    
    if ( carriedSprite != null )
    {
      carriedSprite.setX( getX() + getFacing() * 8 );
      carriedSprite.setY( getY() - 2 );
      
      if ( !aPressed() )
      {
        ((Shell) carriedSprite).release( this );
        carriedSprite = null;
      }
    }
  }
  
  public void airTime()
  {
    if ( sPressed() || ( jumpTime < 0 && !isOnGround() ) )
    {
      if ( jumpTime < 0 )
      {
        setXA( xJumpSpeed );
        setYA( -jumpTime * yJumpSpeed );
        jumpTime++;
      }
      else if ( jumpTime > 0 )
      {
        setXA( getXA() + xJumpSpeed );
        setYA( jumpTime * yJumpSpeed );
        jumpTime--;
      }
    }
    else
    {
      jumpTime = 0;
    }
  }
  
  public void skidCheck()
  {
    if ( getXA() > 2 )
      skid( "right" );
        
    if ( getXA() < -2 )
      skid( "left" );      
  }
  
  public void skid( String direction )
  {
    if ( direction.equals( "left" ) )
      setFacing( -1 );
    else
      setFacing( 1 );
  }
  
  public boolean deathCheck()
  {
    if ( deathTime > 0 )
    {
      deathTime++;
      
      if ( deathTime < 11 )
      {
        setXA( 0 );
        setYA( 0 );
      }
      else if ( deathTime == 11 )
      {
        setYA( -15 );
      }
      else
      {
        setYA( getYA() + 2 );
      }
      
      setX( getX() + getXA() );
      setY( getY() + getYA() );
    
      return true;
    }
    else
    {
      return false;
    }
  }
  
  public boolean winCheck()
  {
    if ( winTime > 0 )
    {
      winTime++;
      setXA( 0 );
      setYA( 0 );
      
      return true;
    }
    else
    {
      return false;
    }
  }
  
  public boolean move( double xA, double yA )
  {
    while ( xA > 8 )
    {
      if ( !move( 8, 0 ) )
        return false;
      
      xA -= 8;
    }
    
    while ( xA < -8 )
    {
      if ( !move( -8, 0 ) )
        return false;
      
      xA += 8;
    }
    
    while ( yA > 8 )
    {
      if ( !move( 0, 8 ) )
        return false;
      
      yA -= 8;
    }
    
    while ( yA < -8 )
    {
      if ( !move( 0, -8 ) )
        return false;
      
      yA += 8;
    }
    
    boolean collide = false;
    
    if ( yA > 0 )
    {
      if ( isBlocking( getX() + xA - getWidth(), getY() + yA, xA, 0 ) )
        collide = true;
      else if ( isBlocking( getX() + xA + getWidth(), getY() + yA, xA, 0 ) )
        collide = true;
      else if ( isBlocking( getX() + xA - getWidth(), getY() + yA + 1, xA, yA ) )
        collide = true;
      else if ( isBlocking( getX() + xA + getWidth(), getY() + yA + 1, xA, yA ) )
        collide = true;
    }
    
    if ( yA < 0 )
    {
      if ( isBlocking( getX() + xA, getY() + yA - getHeight(), xA, yA ) )
        collide = true;
      else if ( collide || isBlocking( getX() + xA - getWidth(), getY() + yA - getHeight(), xA, yA ) )
        collide = true;
      else if ( collide || isBlocking( getX() + xA + getWidth(), getY() + yA - getHeight(), xA, yA ) )
        collide = true;
    }
    
    if ( xA > 0 )
    {
      if ( isBlocking( getX() + xA + getWidth(), getY() + yA - getHeight(), xA, yA ) )
        collide = true;
      if ( isBlocking( getX() + xA + getWidth(), getY() + yA - getHeight() / 2, xA, yA ) )
        collide = true;
      if ( isBlocking( getX() + xA + getWidth(), getY() + yA, xA, yA ) )
        collide = true;
      
      int x = (int) ((getX() + xA + getWidth()) / 16);
      int y = (int) (getY() / 16 + 1);
      if ( avoidingCliffs() && isOnGround() && !getWorld().getLevel().isBlocking( x, y, xA, 1 ) )
        collide = true;
    }
    
    if ( xA < 0 )
    {
      if ( isBlocking( getX() + xA - getWidth(), getY() + yA - getHeight(), xA, yA ) )
        collide = true;
      if ( isBlocking( getX() + xA - getWidth(), getY() + yA - getHeight() / 2, xA, yA ) )
        collide = true;
      if ( isBlocking( getX() + xA - getWidth(), getY() + yA, xA, yA ) )
        collide = true;
      
      int x = (int) ((getX() + xA - getWidth()) / 16);
      int y = (int) (getY() / 16 + 1);
      if ( avoidingCliffs() && isOnGround() && !getWorld().getLevel().isBlocking( x, y, xA, 1 ) )
        collide = true;
    }
    
    if ( collide )
    {
      if ( xA < 0 )
      {
        setX( (int) ( (getX() - getWidth() ) / 16 ) * 16 + getWidth() );
        setXA( 0 );
      }
      if ( xA > 0 )
      {
        setX( (int) ( (getX() + getWidth() ) / 16 + 1 ) * 16 - getWidth() - 1 );
        setXA( 0 );
      }
      if ( yA < 0 )
      {
        setY( (int) ( (getY() - getHeight() ) / 16 ) * 16 + getHeight() );
        setYA( 0 );
      }
      if ( yA > 0 )
      {
        setY( (int) ( getY() / 16 + 1 ) * 16 - 1 );
        onGround();
      }
      
      return false;
    }
    else
    {
      setX( getX() + xA );
      setY( getY() + yA );
      return true;
    }
  }
  
  public boolean isBlocking( double myX, double myY, double xA, double yA )
  {
    int x = (int) ( myX / 16 );
    int y = (int) ( myY / 16 );
    
    if ( x == (int) ( getX() / 16 ) && y == (int) ( getY() / 16 ) )
      return false;
    
    boolean blocking = getWorld().getLevel().isBlocking( x, y, xA, yA );
    
    byte block = getWorld().getLevel().getBlock( x, y );
    
    if ( ( Level.TILE_BEHAVIORS[ block & 0xff ] & Level.BIT_PICKUPABLE ) > 0 )
    {
      getWorld().getMario().getCoin();
      getWorld().getLevel().setBlock( x, y, (byte) 0 );
      
      for ( int xx = 0; xx < 2; xx++ )
      {
        for ( int yy = 0; yy < 2; yy++ )
        {
          int sX = x * 16 + xx * 8 + (int) ( Math.random() * 8 );
          int sY = y * 16 + yy * 8 + (int) ( Math.random() * 8 );
          Sparkle s = new Sparkle( sX, sY, 0, 0, 0, 2, 5 );
          getWorld().addSprite( s );
        }
      }
    }
    
    if ( blocking && yA < 0 )
    {
      if ( large )
        getWorld().bump( x, y, true );
      else
        getWorld().bump( x, y, false );
    }
    
    return blocking;
  }
  
  public void win()
  {
    xDeathPos = (int) getX();
    yDeathPos = (int) getY();
    getWorld().setPaused( true );
    winTime = 1;
    Art.stopMusic();
    getWorld().getSound().play( Art.samples[ Art.SAMPLE_LEVEL_EXIT ], this, 1, 1, 1 );
  }
  
  public void die()
  {
    xDeathPos = (int) getX();
    yDeathPos = (int) getY();
    getWorld().setPaused( true );
    deathTime = 1;
    Art.stopMusic();
    getWorld().getSound().play( Art.samples[ Art.SAMPLE_MARIO_DEATH ], this, 1, 1, 1 );
  }
  
  public void get1Up()
  {
    lives++;
    
    if ( lives == 99 )
      lives = 99;
  }
  
  public boolean leftPressed() {  return keys[ KEY_LEFT ];  }
  public boolean rightPressed() {  return keys[ KEY_RIGHT ];  }
  public boolean upPressed() {  return keys[ KEY_UP ];  }
  public boolean downPressed() {  return keys[ KEY_DOWN ];  }
  public boolean sPressed() {  return keys[ KEY_S ];  }
  public boolean aPressed() {  return keys[ KEY_A ];  }
  
  public abstract void leftAction();
  public abstract void rightAction();
  public abstract void sAction();
  public abstract void aAction();
  
  public byte getKeyMask()
  {
    int mask = 0;
    
    for ( int i = 0; i < 7; i++ )
    {
      if ( keys[i] )
        mask |= (1 << i);
    }
    
    return (byte) mask;
  }
  
  public void setKeys( byte mask )
  {
    for ( int i = 0; i < 7; i++ )
    {
      keys[i] = ( mask & (1 << i) ) > 0;
    }
  }  
}