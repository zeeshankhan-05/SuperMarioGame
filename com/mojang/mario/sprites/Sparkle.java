package com.mojang.mario.sprites;

import com.mojang.mario.Art;

public class Sparkle extends Sprite
{
  private int life;
  private int xPicStart; 
  
  public Sparkle( int x, int y, double xA, double yA, int xPic, int yPic, int timeSpan )
  {
    super( null, x, y, yA, Art.particles, xPic, yPic, 8, 8, 4, 4 );
    setXA( xA );
    
    life = 10 + (int) ( Math.random() * timeSpan );
    xPicStart = xPic;
  }
  
  public Sparkle( int x, int y, double xA, double yA )
  {
    this( x, y, xA, yA, (int) ( Math.random() * 2 ), 0, 5 );
  }
  
  public int getLife() {  return life;  }
  public void setLife( int life ) {  this.life = life;  }
  
  public int getXPicStart() {  return xPicStart;  }
  public void setXPicStart( int xPicStart ) {  this.xPicStart = xPicStart;  }
  
  public void move()
  {
    if ( life > 10 )
      setXPic( 7 );
    else
      setXPic( xPicStart + (10 - life) * 4 / 10 );
    
    if ( life-- < 0 )
    {
      Sprite.context.removeSprite( this );
    }
    
    setX( getX() + getXA() );
    setY( getY() + getYA() );
  }
}