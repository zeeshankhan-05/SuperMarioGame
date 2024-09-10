package com.mojang.mario.sprites;

import com.mojang.mario.Art;
import com.mojang.mario.LevelScene;

public class BulletBill extends Sprite
{
  private int anim;
  private boolean dead;
  private int deadTime;
  
  public BulletBill( LevelScene world, double x, double y, int dir )
  {
    super( world, x, y, -5, Art.enemies, 0, 5, 16, 0, 8, 31 );
    
    setHeight( 12 );
    setFacing( dir );
    
    anim = 0;
    dead = false;
    deadTime = 0;
  }
  
  public int getAnim() {  return anim;  }
  public boolean getDead() {  return dead;  }
  public int getDeadTime() {  return deadTime;  }
  
  public void move()
  {
    if ( deadTime > 0 )
    {
      deadTime--;
      
      if ( deadTime == 0 )
      {
        deadTime = 1;
        
        for ( int i = 0; i < 8; i++ )
        {
          int x = (int) ( getX() + Math.random() * 16 - 8 ) + 4;
          int y = (int) ( getY() - Math.random() * 8 ) + 4;
          double xA = Math.random() * 2 - 1;
          double yA = Math.random() * -1;
          int xPic = 0;
          int yPic = 1;
          int timeSpan = 5;
          Sparkle s = new Sparkle( x, y, xA, yA, xPic, yPic, timeSpan );
          getWorld().addSprite( s );
        }
        
        Sprite.context.removeSprite( this );
      }
      
      setX( getX() + getXA() );
      setY( getY() + getYA() );
      setYA( getYA() * 0.95 + 1 );
      
      return;
    }
    
    double sideWaysSpeed = 4;
    
    setXA( getFacing() * sideWaysSpeed );
    setXFlipPic( getFacing() == -1 );
    move( getXA(), 0 );
  }
  
  private boolean move( double xA, double yA )
  {
    setX( getX() + xA );
    return true;
  }
  
  public void collideCheck()
  {
    if ( dead )
      return;
    
    double xMarioD = getWorld().getMario().getX() - getX();
    double yMarioD = getWorld().getMario().getY() - getY();
    double w = 16;
    
    if ( xMarioD > -16 && xMarioD < 16 )
    {
      if ( yMarioD > -getHeight() && yMarioD < getWorld().getMario().getHeight() )
      {
        if ( getWorld().getMario().getYA() > 0 && yMarioD <= 0 && ( !getWorld().getMario().isOnGround() || !getWorld().getMario().wasOnGround() ) )
        {
          dead = true;
          
          setXA( 0 );
          setYA( 1 );
          deadTime = 100;
        }
        else
        {
          getWorld().getHurtMario();
        }
      }
    }
  }
  
  public boolean shellCollideCheck( Shell s )
  {
    if ( deadTime != 0 )
      return false;
    
    double xD = s.getX() - getX();
    double yD = s.getY() - getY();
    
    if ( xD > -16 && xD < 16 )
    {
      if ( yD > -getHeight() && yD < s.getHeight() )
      {
        getWorld().getSound().play( Art.samples[ Art.SAMPLE_MARIO_KICK ], this, 1, 1, 1 );
        
        dead = true;
        
        setXA( 0 );
        setYA( 1 );
        deadTime = 100;
        
        return true;
      }
    }
    
    return false;
  }
  
  public boolean fireballCollideCheck( Fireball f )
  {
    if ( deadTime != 0 )
      return false;
    
    double xD = f.getX() - getX();
    double yD = f.getY() - getY();
    
    if ( xD > -16 && xD < 16 )
    {
      if ( yD > -getHeight() && yD < f.getHeight() )
      {
        return true;
      }
    }
    
    return false;
  }
}
