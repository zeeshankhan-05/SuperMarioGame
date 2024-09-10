package com.mojang.mario.sprites;

import java.awt.Graphics;

import com.mojang.mario.Art;
import com.mojang.mario.LevelScene;


public class Enemy extends Movers
{
    public static final int ENEMY_RED_KOOPA = 0;          //--
    public static final int ENEMY_GREEN_KOOPA = 1;        //   \
    public static final int ENEMY_GOOMBA = 2;             //    > These numbers correspond to the enemysheet.png
    public static final int ENEMY_SPIKY = 3;              //   /
    public static final int ENEMY_FLOWER = 4;             //--

    public boolean flyDeath = false;

    private int wingTime = 0;
    
    public boolean noFireballDeath;
    private int type;
    public boolean winged = false;
    
    public Enemy(LevelScene world, int x, int y, int dir, int type, boolean winged)
    {//world, type, sheet, x, y, ya, xPicO, yPicO, xPic, yPic, wPic, hPic, height, width, facing 
     super(world, x, y, 0, Art.enemies, 0, 0, 16, 32, 8, 31, 24, 4, 0);
     this.type = type;
     this.winged=winged;
     /*
        sheet = Art.enemies;
        this.winged = winged;

        this.x = x;
        this.y = y;

        xPicO = 8;
        yPicO = 31;
  */
        setAvoidingCliffs( type == Enemy.ENEMY_RED_KOOPA );
        
        noFireballDeath = type == Enemy.ENEMY_SPIKY;
  
        setYPic( type );
        if (getYPic() > 1) setHeight( 12 );
        setFacing( dir );
        if (getFacing() == 0) setFacing( 1 );
        //this.wPic = 16;
        
    }
    /**
     * Constructor for enemy, takes a LevelScene to place them in, their position (x/y)
     * the direction they're facing (dir) and whether or not they have wings.  Also, Red Koopas 
     * turn aroud at cliffs, and spinys don't die due to fireball attacks
     * @param world
     * @param x
     * @param y
     * @param dir
     * @param type
     * @param winged
     */

    /**
     * Checking the collision between Mario and an enemy, Checks to see if he jumped (stomped) them or was hurt by them
     */
    public void collideCheck()
    {
        if (getDeadTime() != 0)
        {
            return;
        }
        //Checking to see if Mario jumped on the enemy
        double xMarioD = getWorld().getMario().getX() - getX();
        double yMarioD = getWorld().getMario().getY() - getY();
        double w = 16;
        if (xMarioD > -getWidth()*2-4 && xMarioD < getWidth()*2+4)
        {
            if (yMarioD > -getHeight() && yMarioD < getWorld().getMario().getHeight() )
            {
                if (type != Enemy.ENEMY_SPIKY && getWorld().getMario().getYA() > 0 && yMarioD <= 0 && (!getWorld().getMario().isOnGround() || !getWorld().getMario().wasOnGround()))
                {
                    getWorld().getMario().stomp(this);
                    if (winged)
                    {
                        winged = false;
                        setYA( 0 );
                    }
                    else
                    {
                        setYPicOrigin( 31 - (32 - 8) );
                        setHPic( 8 );
                        if (getSpriteTemplate() != null) getSpriteTemplate().setIsDead( true );
                        setDeadTime( 10 );
                        winged = false;

                        if (type == 0)
                        {
                            Sprite.context.addSprite(new Shell(getWorld(), getX(), getY(), 0));
                        }
                        else if (type == 1)
                        {
                            Sprite.context.addSprite(new Shell(getWorld(), getX(), getY(), 1));
                        }
                    }
                }
                else
                {
                    getWorld().getHurtMario();
                }
            }
        }
    }

    public void move()
    {
        wingTime++;
        if (getDeadTime() > 0)
        {
            setDeadTime( getDeadTime() - 1 );

            if (getDeadTime() == 0)
            {
                setDeadTime( 1 );
                for (int i = 0; i < 8; i++)
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

            if (flyDeath)
            {
              setX( getX() + getXA() );
              setY( getY() + getYA() );
              setYA( getYA() * .95 + 1 );
            }
            return;
          }


        double sideWaysSpeed = 1.75;
        //        float sideWaysSpeed = onGround ? 2.5f : 1.2f;

        if (getXA() > 2)
        {
            setFacing( 1 );
        }
        if (getXA() < -2)
        {
            setFacing( -1 );
        }

        setXA( getFacing() * sideWaysSpeed );

        setXFlipPic( getFacing() == -1 );

        setRunTime( getRunTime() + Math.abs( getXA() ) + 5 );

        int runFrame = ((int) (getRunTime() / 20)) % 2;

        if (!isOnGround() )
        {
            runFrame = 1;
        }


        if (!move(getXA(), 0)) setFacing( -getFacing() );
        offGround();
        move(0, getYA());

        double temp = winged ? 0.95 : 0.85;
        setYA( getYA() * temp );
        if (isOnGround())
        {
            setXA( getXA() * Sprite.GROUND_INERTIA );
        }
        else
        {
            setXA( getXA() * Sprite.AIR_INERTIA );
        }
        //parameters for jumping?
        if (!isOnGround())
        {
            if (winged)
            {
                setYA( getYA() + 0.6 );
            }
            else
            {
                setYA( getYA() + 2 );
            }
        }
        else if (winged)
        {
            setYA( -10 );
        }

        if (winged) runFrame = wingTime / 4 % 2;

        setXPic( runFrame );
    }
    /**
     * Possibly when it turns enemies around
     * @param xa
     * @param ya
     * @return
     */

    private boolean isBlocking(float _x, float _y, float xa, float ya)
    {
        int x = (int) (_x / 16);
        int y = (int) (_y / 16);
        if (x == (int) (this.getX() / 16) && getY() == (int) (this.getY() / 16)) return false;

        boolean blocking = getWorld().getLevel().isBlocking(x, y, xa, ya);

        //byte block = world.level.getBlock(x, y);

        return blocking;
    }

    public boolean shellCollideCheck(Shell shell)
    {
        if (getDeadTime() != 0) return false;

        double xD = shell.getX() - getX();
        double yD = shell.getY() - getY();

        if (xD > -16 && xD < 16)
        {
            if (yD > -getHeight() && yD < shell.getHeight())
            {
                getWorld().getSound().play(Art.samples[Art.SAMPLE_MARIO_KICK], this, 1, 1, 1);

                setXA( shell.getFacing() * 2 );
                setYA( - 5 );
                flyDeath = true;
                if (getSpriteTemplate() != null) getSpriteTemplate().setIsDead( true );
                setDeadTime( 100 );
                winged = false;
                setHPic( -getHPic() );
                setYPicOrigin( -getYPicOrigin() + 16 );
                return true;
            }
        }
        return false;
    }

    public boolean fireballCollideCheck(Fireball fireball)
    {
        if (getDeadTime() != 0) return false;

        double xD = fireball.getX() - getX();
        double yD = fireball.getY() - getY();

        if (xD > -16 && xD < 16)
        {
            if (yD > -getHeight() && yD < fireball.getHeight())
            {
                if (noFireballDeath) return true;
                
                getWorld().getSound().play(Art.samples[Art.SAMPLE_MARIO_KICK], this, 1, 1, 1);

                setXA( fireball.getFacing() * 2 );
                setYA( -5 );
                flyDeath = true;
                if (getSpriteTemplate() != null) getSpriteTemplate().setIsDead( true );
                setDeadTime( 100 );
                winged = false;
                setHPic( -getHPic() );
                setYPicOrigin( -getYPicOrigin() + 16 );
                return true;
            }
        }
        return false;
    }

    public void bumpCheck(int xTile, int yTile)
    {
        if (getDeadTime() != 0) return;

        if (getX() + getWidth() > xTile * 16 && getX() - getWidth() < xTile * 16 + 16 && yTile == (int) ((getY() - 1) / 16))
        {
            getWorld().getSound().play(Art.samples[Art.SAMPLE_MARIO_KICK], this, 1, 1, 1);

            setXA( getWorld().getMario().getFacing() * 2 );
            setYA( -5 );
            flyDeath = true;
            if (getSpriteTemplate() != null) getSpriteTemplate().setIsDead( true );
            setDeadTime( 100 );
            winged = false;
            setHPic( -getHPic() );
            setYPicOrigin( -getYPicOrigin() + 16 );
        }
    }

    public void render(Graphics og, double alpha)
    {
        if (winged)
        {
            int xPixel = (int) (getXOld() + (getX() - getXOld()) * alpha) - getXPicOrigin();
            int yPixel = (int) (getYOld() + (getY() - getYOld()) * alpha) - getYPicOrigin();

            if (type == Enemy.ENEMY_GREEN_KOOPA || type == Enemy.ENEMY_RED_KOOPA)
            {
            }
            else
            {
                setXFlipPic( !getXFlipPic() );
                og.drawImage(getSheet()[wingTime / 4 % 2][4], xPixel + (getXFlipPic() ? getWPic() : 0) + (getXFlipPic() ? 10 : -10), yPixel + (getYFlipPic() ? getHPic() : 0) - 8, getXFlipPic() ? -getWPic() : getWPic(), getYFlipPic() ? -getHPic() : getHPic(), null);
                setXFlipPic( !getXFlipPic() );
            }
        }

        super.render(og, alpha);

        if (winged)
        {
            int xPixel = (int) (getXOld() + (getX() - getXOld()) * alpha) - getXPicOrigin();
            int yPixel = (int) (getYOld() + (getY() - getYOld()) * alpha) - getYPicOrigin();

            if (type == Enemy.ENEMY_GREEN_KOOPA || type == Enemy.ENEMY_RED_KOOPA)
            {
                og.drawImage(getSheet()[wingTime / 4 % 2][4], xPixel + (getXFlipPic() ? getWPic() : 0) + (getXFlipPic() ? 10 : -10), yPixel + (getYFlipPic() ? getHPic() : 0) - 10, getXFlipPic() ? -getWPic() : getWPic(), getYFlipPic() ? -getHPic() : getHPic(), null);
            }
            else
            {
                og.drawImage(getSheet()[wingTime / 4 % 2][4], xPixel + (getXFlipPic() ? getWPic() : 0) + (getXFlipPic() ? 10 : -10), yPixel + (getYFlipPic() ? getHPic() : 0) - 8, getXFlipPic() ? -getWPic() : getWPic(), getYFlipPic() ? -getHPic() : getHPic(), null);
            }
        }
    }
}