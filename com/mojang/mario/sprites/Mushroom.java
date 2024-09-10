package com.mojang.mario.sprites;

import java.awt.Image;
import com.mojang.mario.Art;
import com.mojang.mario.LevelScene;

public class Mushroom extends Movers
{
  private int life;
  
  public Mushroom( LevelScene world, int x, int y )
  {
    super( world, x, y, 0, Art.items, 0, 0, 16, 16, 8, 15, 12, 4, 1 );
    life = 0;
  }
  
  public void collideCheck()
  {
    if ( getWorld().getMario().getDeathTime() > 0 || getWorld().isPaused() ) 
      return;
    
    double xMarioD = getWorld().getMario().getX() - getX();
    double yMarioD = getWorld().getMario().getY() - getY();
    
    if ( xMarioD > -16 && xMarioD < 16 )
    {
      if ( yMarioD > -getHeight() && yMarioD < getWorld().getMario().getHeight() )
      {
        Sprite.context.removeSprite(this);
        getWorld().powerUpMarioByMushroom();
      }
    }
  }
  
  public void move()
  {
    if ( life < 9 )
    {
      setLayer( 0 );
      setY( getY() - 1 );
      life++;
      return;
    }
    
    double sideWaysSpeed = 1.75;
    setLayer( 1 );
    
    if ( getXA() > 2 )
    {
      setFacing( 1 );
    }
    
    if ( getXA() < -2 )
    {
      setFacing( -1 );
    }
    
    setXA( getFacing() * sideWaysSpeed );
    setXFlipPic( getFacing() == -1 );
    setRunTime( getRunTime() + Math.abs( getXA() ) + 5 );    
        
    if ( !move( getXA(), 0 ) ) 
      setFacing( -getFacing() );
    
    offGround();
    move( 0, getYA() );
    setYA( getYA() * 0.85 );
    
    if ( isOnGround() )
      setXA( getXA() * Sprite.GROUND_INERTIA );
    else
      setXA( getXA() * Sprite.AIR_INERTIA );
        
    if ( !isOnGround() )
    {
      setYA( getYA() + 2 );
    }
  }
  
  public void bumpCheck( int xTile, int yTile )
  {
    if ( getX() + getWidth() > xTile * 16 && getX() - getWidth() < xTile * 16 + 16 && yTile == (int)( (getY()-1) / 16 ) )
    {
      setFacing( -getWorld().getMario().getFacing() );
      setYA( -10 );
    }
  }
}