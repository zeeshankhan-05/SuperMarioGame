package com.mojang.mario.sprites;

import com.mojang.mario.LevelScene;

public class FlowerEnemy extends Enemy
{
  private int tick;
  private int yStart;
  private int jumpTime = 0;
  private LevelScene world;
  
  public FlowerEnemy(LevelScene world, int x, int y)
  {
    super(world, x, y, 1, ENEMY_SPIKY, false);
    
    noFireballDeath = false;
    this.world = world;
    setXPic( 0 );
    setYPic( 6 );
    setYPicOrigin( 24 );
    setHeight( 12 );
    setWidth( 2 );
    
    yStart = y;
    setYA( -8 );
    
    setY( getY() - 1 );
    setLayer( 0 );
    
    for ( int i = 0; i < 4; i++ )
    {
      move();
    }
  }
  
  public void move()
  {
    if ( getDeadTime() > 0 )
    {
      setDeadTime( getDeadTime() - 1 );
      
      if ( getDeadTime() == 0 )
      {
        setDeadTime( 1 );
        
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
        
        Sprite.context.removeSprite(this);
      }
      
      setX( getX() + getXA() );
      setY( getY() + getYA() );
      setYA( getYA() * 0.95 + 1 );
      
      return;
    }
    
    tick++;
    
    if ( getY() >= yStart)
    {
      setY( yStart );
      
      int xd = (int) ( Math.abs( getWorld().getMario().getX() - getX() ) );
      jumpTime++;
      
      if ( jumpTime > 40 && xd > 24)
        setYA( -8 );
      else
        setYA( 0 );
    }
    else
    {
      jumpTime = 0;
    }
    
    setY( getY() + getYA() );
    setYA( getYA() * 0.9 + .1 );
    setXPic( ( (tick/2) & 1 ) * 2 + ( (tick/6) & 1 ) );
  }
}