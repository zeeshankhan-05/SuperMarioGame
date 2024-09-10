package com.mojang.mario.sprites;

import com.mojang.mario.Art;

public class Particle extends Sprite
{
    private int life;
    
    public Particle( int x, int y, double xA, double yA, int xPic, int yPic )
    {
      super( null, x, y, yA, Art.particles, xPic, yPic, 8, 8, 4, 4 );
      
      setXA( xA );
      
      life = 10;
    }
    
    public Particle( int x, int y, double xA, double yA )
    {
      this( x, y, xA, yA, (int) ( Math.random() * 2 ), 0 );
    }
    
    public int getLife() {  return life;  }
    public void setLife( int life ) {  this.life = life;  }
    
    public void move()
    {
      if ( life-- < 0 )
      {
        Sprite.context.removeSprite( this );
      }
      
      setX( getX() + getXA() );
      setY( getY() + getYA() );
      setYA( getYA() * 0.95 + 3 );      
    }
}