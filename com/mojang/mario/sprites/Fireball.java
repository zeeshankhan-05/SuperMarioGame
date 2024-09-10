package com.mojang.mario.sprites;

import java.awt.Image;

import com.mojang.mario.Art;
import com.mojang.mario.LevelScene;


public class Fireball extends Movers
{
  
  public Fireball(LevelScene world, float x, float y, int facing)
  {  
    super( world, x, y, 4, Art.particles, 4, 3, 8, 8, 4, 4, 8, 4, facing);
  }
  
  public void move()
  {
    if (getDeadTime() > 0)
    {
      for (int i = 0; i < 8; i++)
      {
        int x = (int) ( getX() + Math.random() * 8 - 4 ) + 4;
        int y = (int) ( getY() - Math.random() * 8 - 4 ) + 2;
        double xA = Math.random() * 2 - 1 - getFacing();
        double yA = Math.random() * 2 -1;
        int xPic = 0;
        int yPic = 1;
        int timeSpan = 5;
        Sparkle s = new Sparkle( x, y, xA, yA, xPic, yPic, timeSpan );
        getWorld().addSprite( s );
      }
      Sprite.context.removeSprite(this);
      
      return;
    }
    
    if ( getFacing() != 0 ) 
      setAnim( getAnim() + 1 );
    
    double sideWaysSpeed = 8;
    
    if ( getXA() > 2 )
    {
      setFacing( 1 );
    }
    if ( getXA() < -2 )
    {
      setFacing( -1 );
    }
    
    setXA( getFacing() * sideWaysSpeed );
    getWorld().checkFireballCollide(this);
    setXFlipPic( getFacing() == -1 );
    setRunTime( getRunTime() + Math.abs( getXA() ) + 5 );
    setXPic( getAnim() % 4 );    
        
    if ( !move( getXA(), 0 ) )
    {
      die();
    }
    
    offGround();
    move( 0, getYA() );
    
    if ( isOnGround() ) 
      setYA( -10 );
    
    setYA( getYA() * 0.95 );
    
    if ( isOnGround() )
    {
      setXA( getXA() * Sprite.GROUND_INERTIA );
    }
    else
    {
      setXA( getXA() * Sprite.AIR_INERTIA );
    }
    
    if ( !isOnGround() )
    {
      setYA( getYA() + 1.5 );
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
      if ( !move(-8, 0) ) 
        return false;
      
      xA += 8;
    }
    
    while ( yA > 8 )
    {
      if ( !move(0, 8) ) 
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
    
    return blocking;
  }
  
  public void die()
  {
    setIsDead( true );
    setXA( -getFacing() * 2 );
    setYA( -5 );
    setDeadTime( 100 );
  }
}