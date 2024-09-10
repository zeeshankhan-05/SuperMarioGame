package com.mojang.mario.sprites;

import com.mojang.mario.Art;
import com.mojang.mario.LevelScene;

public class Shell extends Movers
{
  public Shell( LevelScene world, double x, double y, int type )
  {
    super( world, x, y, -5, Art.enemies, 4, 0, 16, 32, 8, 31, 12, 4, 0 );
    setYPic( type );
  }
  
  public boolean fireballCollideCheck( Fireball fireball )
  {
    if ( getDeadTime() != 0 )
      return false;
    
    double xD = fireball.getX() - getX();
    double yD = fireball.getY() - getY();
    
    if ( xD > -16 && xD < 16 )
    {
      if ( yD > -getHeight() && yD < fireball.getHeight() )
      {
        if ( getFacing() != 0 )
          return true;
        
        getWorld().getSound().play( Art.samples[ Art.SAMPLE_MARIO_KICK ], this, 1, 1, 1 );
        
        setXA( fireball.getFacing() * 2 );
        setYA( -5 );
        
        if ( getSpriteTemplate() != null )
          getSpriteTemplate().setIsDead( true );
        
        setDeadTime( 100 );
        setHPic( -getHPic() );
        setYPicOrigin( -getYPicOrigin() + 16 );
        return true;
      }
    }
    
    return false;
  }
  
  public void collideCheck()
  {
    if ( getCarried() || isDead() || getDeadTime() > 0 )
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
          getWorld().getMario().stomp( this );
          
          if ( getFacing() != 0 )
          {
            setXA( 0 );
            setFacing( 0 );
          }
          else
          {
            setFacing( getWorld().getMario().getFacing() );
          }
        }
        else
        {
          if ( getFacing() != 0 )
          {
            getWorld().getHurtMario();
          }
          else
          {
            getWorld().getMario().kick( this );
            setFacing( getWorld().getMario().getFacing() );
          }
        }
      }
    }
  }
  
  public void move()
  {
    if ( getCarried() )
    {
      getWorld().checkShellCollide( this );
      return;
    }
    
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
        
        Sprite.context.removeSprite( this );
      }
      
      setX( getX() + getXA() );
      setY( getY() + getYA() );
      setYA( getYA() * 0.95 + 1 );
      
      return;
    }
    
    if ( getFacing() != 0 )
      setAnim( getAnim() + 1 );
    
    double sideWaysSpeed = 11;
    
    if ( getXA() > 2 )
      setFacing( 1 );
    if ( getXA() < -2 )
      setFacing( -1 );
    
    setXA( getFacing() * sideWaysSpeed );
    
    if ( getFacing() != 0 )
      getWorld().checkShellCollide( this );
    
    setXFlipPic( getFacing() == -1 );
    setRunTime( getRunTime() + Math.abs( getXA() ) + 5 );
    setXPic( ( getAnim() / 2 ) % 4 + 3 );
    
    if ( !move( getXA(), 0 ) )
    {
      getWorld().getSound().play( Art.samples[ Art.SAMPLE_SHELL_BUMP ], this, 1, 1, 1 );
      setFacing( -getFacing() );
    }
    
    offGround();
    move( 0, getYA() );
    
    setYA( getYA() * .85 );
    
    if ( isOnGround() )
      setXA( getXA() * GROUND_INERTIA );
    else
      setXA( getXA() * AIR_INERTIA );
    
    if ( !isOnGround() )
      setYA( getYA() + 2 );
  }
  
  public void bumpCheck( int xTile, int yTile )
  {
    if ( getX() + getWidth() > xTile * 16 && getX() - getWidth() < xTile * 16 + 16 && yTile == (int) ( (getY() - 1) / 16 ) )
    {
      setFacing( -getWorld().getMario().getFacing() );
      setYA( -10 );
    }
  }
  
  public void die()
  {
    setIsDead( true );
    setCarried( false );
    
    setXA( -getFacing() * 2 );
    setYA( -5 );
    setDeadTime( 100 );
  }
  
  public boolean shellCollideCheck( Shell shell )
  {
    if ( getDeadTime() != 0 )
      return false;
    
    double xD = shell.getX() - getX();
    double yD = shell.getY() - getY();
    
    if ( xD > -16 && xD < 16 )
    {
      if ( yD > -getHeight() && yD < shell.getHeight() )
      {
        getWorld().getSound().play( Art.samples[ Art.SAMPLE_MARIO_KICK ], this, 1, 1, 1 );
        
        if ( getWorld().getMario().getCarriedSprite() == shell || getWorld().getMario().getCarriedSprite() == this )
          getWorld().getMario().setCarriedSprite( null );
        
        die();
        shell.die();
        return true;
      }
    }
    
    return false;
  }
  
  public void release( Avatar avatar )
  {
    setCarried( false );
    setFacing( avatar.getFacing() );
    setX( getX() + getFacing() * 8 );
  }
}