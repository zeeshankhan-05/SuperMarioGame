package com.mojang.mario.sprites;

import com.mojang.mario.Art;
import com.mojang.mario.Scene;
import com.mojang.mario.LevelScene;

public class Mario extends Avatar
{
  protected static int coins = 0;
  
  public Mario ( LevelScene world )
  {
    super( world );
    
    setSheet( Art.smallMario );
    setXPicOrigin( 8 );
    setYPicOrigin( 15 );
    setWPic( 16 );
    setHPic( 16 );
    setFacing( 1 );
    
    if ( getWorld().getMario() == null )
    {
      coins = 0;
      setX( 32 );
    }
    
    setHeight( 12 );
  }
  
  public int getCoins() {  return coins;  }
  
  public void move()
  {
    super.move();
    selectPicture();
  }
  
  public void stomp( Enemy enemy )
  {
    if ( getDeathTime() > 0 || getWorld().isPaused() )
      return;
    
    double targetY = enemy.getY() - enemy.getHeight() / 2;
    move( 0, targetY - getY() );
    
    getWorld().getSound().play( Art.samples[ Art.SAMPLE_MARIO_KICK ], this, 1, 1, 1 );
    setXJumpSpeed( 0 );
    setYJumpSpeed( -1.9 );
    setJumpTime( 8 );
    setYA( getJumpTime() * getYJumpSpeed() );
    offGround();    
    setInvulnerableTime( 1 );
  }
  
  public void stomp( Shell shell )
  {
    if ( getDeathTime() > 0 || getWorld().isPaused() )
      return;
    
    if ( aPressed() && shell.getFacing() == 0 )
    {
      setCarriedSprite( shell );
      shell.setCarried( true );
    }
    else
    {
      double targetY = shell.getY() - shell.getHeight() / 2;
      move( 0, targetY - getY() );
      
      getWorld().getSound().play( Art.samples[ Art.SAMPLE_MARIO_KICK ], this, 1, 1, 1 );
      setXJumpSpeed( 0 );
      setYJumpSpeed( -1.9 );
      setJumpTime( 8 );
      setYA( getJumpTime() * getYJumpSpeed() );
      onGround();
      setInvulnerableTime( 1 );
    }
  }
  
  public void kick( Shell shell )
  {
    if ( getDeathTime() > 0 || getWorld().isPaused() )
      return;
    
    if ( aPressed() )
    {
      setCarriedSprite( shell );
      shell.setCarried( true );
    }
    else
    {
      getWorld().getSound().play( Art.samples[ Art.SAMPLE_MARIO_KICK ], this, 1, 1, 1 );
      setInvulnerableTime( 1 );
    }
  }
  
  public void leftAction()
  {
    setXA( getXA() - getSideWaysSpeed() );
    
    if ( getJumpTime() >= 0 )
      setFacing( -1 );
  }
  
  public void rightAction()
  {
    setXA( getXA() + getSideWaysSpeed() );
    
    if ( getJumpTime() >= 0 )
      setFacing( 1 );
  }
  
  public void sAction()
  {
    if ( isOnGround() )
    {
      getWorld().getSound().play( Art.samples[ Art.SAMPLE_MARIO_JUMP ], this, 1, 1, 1 );
      setXJumpSpeed( 0 );
      setYJumpSpeed( -1.9 );
      setJumpTime( 7 );
      setYA( getJumpTime() * getYJumpSpeed() );
      offGround();
    }
  }
  
  public void aAction()
  {
    setSideWaysSpeed( 1.2 );
  }
  
  public void selectPicture()
  {
    int runFrame = 0;
    
    runFrame = ( (int) ( getRunTime() / 20 ) ) % 2;
    
    if ( getCarriedSprite() == null && Math.abs( getXA() ) > 10 )
      runFrame += 2;
    if ( getCarriedSprite() != null )
      runFrame += 8;
    
    if ( !isOnGround() )
    {
      if ( getCarriedSprite() != null )
        runFrame = 9;
      else if ( Math.abs( getXA() ) > 10 )
        runFrame = 5;
      else
        runFrame = 4;
    }
    
    if ( isOnGround() && ( ( getFacing() == -1 && getXA() > 0 ) || ( getFacing() == 1 && getXA() < 0 ) ) )
    {
      if ( getXA() > 1 || getXA() < -1 )
        runFrame = 7;
      if ( getXA() > 3 || getXA() < -3 )
      {
        for ( int i = 0; i < 3; i++ )
        {
          int x = (int) ( getX() + Math.random() * 8 - 4 );
          int y = (int) ( getY() - Math.random() * 4 );
          double xA = Math.random() * 2 - 1;
          double yA = Math.random() * -1;
          int xPic = 0;
          int yPic = 1;
          int timeSpan = 5;
          Sparkle s = new Sparkle( x, y, xA, yA, xPic, yPic, timeSpan );
          getWorld().addSprite( s );
        }
      }
    }
    
    setXPic( runFrame );
  }
  
  public void getCoin()
  {
    coins++;
    
    if ( coins == 100 )
    {
      coins = 0;
      get1Up();
    }
    
    getWorld().getSound().play( Art.samples[ Art.SAMPLE_GET_COIN ], this, 1, 1, 1 );
  }
  
  public void addCoins( int number )
  {
    coins += number;
    
    if ( coins == 100 )
    {
      coins = 0;
      get1Up();
    }
    
    getWorld().getSound().play( Art.samples[ Art.SAMPLE_GET_COIN ], this, 1, 1, 1 );
  } 
}