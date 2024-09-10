package com.mojang.mario.sprites;

import java.awt.Image;
import com.mojang.mario.Art;
import com.mojang.mario.LevelScene;

public class FireFlower extends Sprite
{
  private int life;
  
  public FireFlower( LevelScene world, int x, int y )
  {
    super( world, x, y, 0, Art.items, 1, 0, 16, 16, 8, 15 );
    setHeight( 12 );
    setFacing( 1 );
    
    life = 0;
  }
  
  public int getLife() {  return life;  }
  public void setLife( int life ) {  this.life = life;  }
  
  public void move()
  {
    if ( life < 9 )
    {
      setLayer( 0 );
      setY( getY() - 1 );
      life++;
      return;
    }
  }
  
  public void collideCheck()
  {
    if ( collideCheckFlower() )
    {
      getWorld().powerUpMario();
    }
  }
  
  private boolean collideCheckFlower()
  {
    if ( getWorld().getMario().getDeathTime() > 0 || getWorld().isPaused() )
      return false;
    
    double xMarioD = getWorld().getMario().getX() - getX();
    double yMarioD = getWorld().getMario().getY() - getY();
    double w = 16;
    
    if ( xMarioD > -16 && xMarioD < 16 )
    {
      if ( yMarioD > -getHeight() && yMarioD < getWorld().getMario().getHeight() )
      {
        Sprite.context.removeSprite( this );
        return true;
      }
    }
    
    return false;
  }
}