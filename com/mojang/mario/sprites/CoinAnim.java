package com.mojang.mario.sprites;

import com.mojang.mario.Art;

public class CoinAnim extends Sprite
{
  private int life;
  
  public CoinAnim( int xTile, int yTile )
  {
    super( null, xTile * 16, yTile * 16 - 16, -6, Art.level, 0, 2, 16, 16, 0, 0 );
    
    life = 10;
  }
  
  public int getLife() {  return life;  }
  public void setLife( int life ) {  this.life = life;  }
  
  public void move()
  {
    if ( life-- < 0 )
    {
      Sprite.context.removeSprite( this );
      for ( int xx = 0; xx < 2; xx++ )
      {
        for ( int yy = 0; yy < 2; yy++ )
        {
          int x = (int) getX() + xx * 8 + (int) ( Math.random() * 8 );
          int y = (int) getY() + yy * 8 + (int) ( Math.random() * 8 );
          double xA = 0;
          double yA = 0;
          int xPic = 0;
          int yPic = 2;
          int timeSpan = 5;
          Sparkle s = new Sparkle( x, y, xA, yA, xPic, yPic, timeSpan );
          Sprite.context.addSprite( s );
        }
      }
    }
    
    setXPic( life & 3 );
    setX( getX() + getXA() );
    setY( getY() + getYA() );
    setYA( getYA() + 1 );
  }
}