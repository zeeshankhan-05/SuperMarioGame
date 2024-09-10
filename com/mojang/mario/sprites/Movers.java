package com.mojang.mario.sprites;

import java.awt.Image;
import com.mojang.mario.LevelScene;

public class Movers extends Sprite
{
  private int dir;
  private int anim;
  
  private boolean dead;
  private int deadTime;
  private boolean carried;
  
  public Movers( LevelScene world, double x, double y, double yA, Image[][] sheet, int xPic, int yPic, int wPic, int hPic, int xPicO, int yPicO, int height, int width, int facing )
  {
    super( world, x, y, yA, sheet, xPic, yPic, wPic, hPic, xPicO, yPicO );
    setWidth( width );
    setHeight( height );
    setFacing( facing );
    
    dir = 0;
    anim = 0;
    
    dead = false;
    deadTime = 0;
    carried = false;
  }
  
  public Movers( LevelScene world )
  {
    super( world );
    
    dir = 0;
    anim = 0;
    
    dead = false;
    deadTime = 0;
    carried = false;
  }
  
  public int getDir() {  return dir;  }
  public void setDir( int dir ) {  this.dir = dir;  }
  public int getAnim() {  return anim;  }
  public void setAnim( int anim ) {  this.anim = anim;  }
  
  public boolean isDead() {  return dead;  }
  public void setIsDead( boolean dead ) {  this.dead = dead;  }  
  public int getDeadTime() {  return deadTime;  }
  public void setDeadTime( int deadTime ) {  this.deadTime = deadTime;  }
  public boolean getCarried() {  return carried;  }
  public void setCarried( boolean carried ) {  this.carried = carried;  }
  
  public void move()
  {
    setX( getX() + getXA() );
    setY( getY() + getYA() );
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
    
    if ( blocking && yA == 0 && xA != 0 )
    {
      getWorld().bump( x, y, true );
    }
    
    return blocking;
  }
}