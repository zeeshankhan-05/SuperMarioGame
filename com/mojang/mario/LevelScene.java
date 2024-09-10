package com.mojang.mario;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.GraphicsConfiguration;
import java.awt.Image;
import java.io.*;
import java.text.DecimalFormat;
import java.util.*;

import com.mojang.mario.sprites.*;
import com.mojang.sonar.FixedSoundSource;
import com.mojang.mario.level.*;

@SuppressWarnings("unchecked")

public class LevelScene extends Scene implements SpriteContext
{
  // The following methods have been moved to 
  // the top of the file for your convenience
  //
  // Methods Moved:
  //  - powerUpMarioByMushroom()
  //  - powerUpMario()
  //  - getHurtMario()
  
  public void powerUpMarioByMushroom()
  {
    // Remove ALL of these comments once the BigMario class is completed
    
    paused = true;
    setPowerUpTime( 18 );
    sound.play( Art.samples[ Art.SAMPLE_MARIO_POWER_UP ], this, 1, 1, 1 );
      
    if ( mario instanceof BigMario )
      mario.getCoin();
    else
    {
      BigMario big = new BigMario( this );
      big.setIsLarge( true );
      setMario( big );
    }
    paused = false;
    
  }
  
  public void powerUpMario()
  {
    // Remove ALL of these comments once the FireMario class is completed
    
    
    
    paused = true;
    setPowerUpTime( 18 );
    sound.play( Art.samples[ Art.SAMPLE_MARIO_POWER_UP ], this, 1, 1, 1 );
    
    if( mario instanceof FireMario ) //From most specific to least specific
    {
      mario.getCoin();
    }
    else if( mario instanceof BigMario )
    {
      FireMario fire = new FireMario( this );
      fire.setIsLarge( true ); 
      setMario(fire);
    }
    else
    {
      BigMario big = new BigMario( this );
      big.setIsLarge( true );  
      setMario( big );
      
    }
    
    paused = false;
    
    
  }
  
  public void getHurtMario()
  {
    if ( mario.getDeathTime() > 0 || paused ) 
      return;
    
    if ( mario.getInvulnerableTime() > 0 ) 
      return;
    
    // Remove ALL of these comments once the FireMario class is completed
    
    
    
    if( mario instanceof FireMario )
    {
      paused = true;
      setPowerUpTime( -18 );
      sound.play( Art.samples[ Art.SAMPLE_MARIO_POWER_DOWN ], this, 1, 1, 1 );
      mario.setInvulnerableTime( 32 );
      
      BigMario big = new BigMario( this );
      setMario( big );
      mario.setInvulnerableTime( 32 );
      return;
    }
    
    

    
    // Remove ALL of these comments once the BigMario class is completed
    
    
    
    if( mario instanceof BigMario )
    {
      paused = true;
      setPowerUpTime( -18 );
      sound.play( Art.samples[ Art.SAMPLE_MARIO_POWER_DOWN ], this, 1, 1, 1 );
      mario.setInvulnerableTime( 32 );
      Mario original = new Mario( this );
      original.setIsLarge( false );
      setMario( original );
      mario.setInvulnerableTime( 32 );
      return;
    }
    
      
      
    //If not Big or FireMario then die
    mario.die();   
  }
  
  
  private List sprites = new ArrayList();
  private List spritesToAdd = new ArrayList();
  private List spritesToRemove = new ArrayList();
  
  public Level level;
  public Mario mario, oldMario;
  private double xCam, yCam, xCamO, yCamO;
  public static Image tmpImage;
  private int tick;
  private int powerUpTime;
  
  public int fireballsOnScreen = 0;
  
  List shellsToCheck = new ArrayList();
  
  private LevelRenderer layer;
  private BgRenderer[] bgLayer = new BgRenderer[2];
  
  private GraphicsConfiguration graphicsConfiguration;
  
  public boolean paused = false;
  public int startTime = 0;
  private int timeLeft;
  
  private long levelSeed;
  private MarioComponent renderer;
  private int levelType;
  private int levelDifficulty;
  
  public LevelScene(GraphicsConfiguration graphicsConfiguration, MarioComponent renderer, long seed, int levelDifficulty, int type)
  {
    this.graphicsConfiguration = graphicsConfiguration;
    this.levelSeed = seed;
    //System.out.println(seed);
    this.renderer = renderer;
    this.levelDifficulty = levelDifficulty;
    this.levelType = type;
  }
  public LevelScene(LevelScene ls)
  {
    sprites = ls.sprites;
    spritesToAdd = ls.spritesToAdd;
    spritesToRemove = ls.spritesToRemove;
    
    level = ls.level;
    mario = ls.mario;
    xCam = ls.xCam;
    yCam = ls.yCam;
    xCamO  = ls.xCamO;
    yCamO = ls.yCamO;
    tmpImage = ls.tmpImage;
    tick = ls.tick;
    
    layer = ls.layer;
    bgLayer = ls.bgLayer;
    
    graphicsConfiguration = ls.graphicsConfiguration;
    
    paused = ls.paused;
    startTime = ls.startTime;
    timeLeft = ls.timeLeft;
    
    levelSeed = ls.levelSeed;
    renderer = ls.renderer;
    levelType = ls.levelType;
    levelDifficulty = ls.levelDifficulty;
  }
  
  public Level getLevel() {  return level;  }
  
  public boolean isPaused() {  return paused;  }
  public void setPaused( boolean paused ) {  this.paused = paused;  }
  
  public void init()
  {
    try
    {
      Level.loadBehaviors(new DataInputStream(LevelScene.class.getResourceAsStream("/tiles.dat")));
    }
    catch (IOException e)
    {
      e.printStackTrace();
      System.exit(0);
    }
    //System.out.println("levelScene is LevelScene init() is "+this);
    /*        if (replayer!=null)
     {
     level = LevelGenerator.createLevel(2048, 15, replayer.nextLong());
     }
     else
     {*/
//        level = LevelGenerator.createLevel(320, 15, levelSeed);
    level = LevelGenerator.createLevel(320, 15, levelSeed, levelDifficulty, levelType);
    //        }
    
    /*        if (recorder != null)
     {
     recorder.addLong(LevelGenerator.lastSeed);
     }*/
    
    if (levelType==LevelGenerator.TYPE_OVERGROUND)
      Art.startMusic(1);
    else if (levelType==LevelGenerator.TYPE_UNDERGROUND)
      Art.startMusic(2);
    else if (levelType==LevelGenerator.TYPE_CASTLE)
      Art.startMusic(3);
    
    
    paused = false;
    Sprite.context = this;
    sprites.clear();
    layer = new LevelRenderer(level, graphicsConfiguration, 320, 240);
    for (int i = 0; i < 2; i++)
    {
      int scrollSpeed = 4 >> i;
      int w = ((level.width * 16) - 320) / scrollSpeed + 320;
      int h = ((level.height * 16) - 240) / scrollSpeed + 240;
      Level bgLevel = BgLevelGenerator.createLevel(w / 32 + 1, h / 32 + 1, i == 0, levelType);
      bgLayer[i] = new BgRenderer(bgLevel, graphicsConfiguration, 320, 240, scrollSpeed);
    }
    mario = new Mario(this);
    sprites.add(mario);
    oldMario = mario;
    startTime = 1;
    
    timeLeft = 200*15;
    
    tick = 0;
  }
  
  public void checkShellCollide(Shell shell)
  {
    shellsToCheck.add(shell);
  }
  
  List fireballsToCheck = new ArrayList();
  
  public void checkFireballCollide(Fireball fireball)
  {
    fireballsToCheck.add(fireball);
  }
  
  public void tick()
  {
    timeLeft--;
    if (timeLeft==0)
    {
      mario.die();
    }
    
    //Calculate the cams based on original cams? How initialized?
    xCamO = xCam;
    yCamO = yCam;
    // System.out.println("mario in tick is "+mario);
    if (startTime > 0)
    {
      startTime++;
    }
    
    double targetXCam = mario.getX() - 160;
    
    xCam = targetXCam;
    
    if (xCam < 0) xCam = 0;
    if (xCam > level.width * 16 - 320) xCam = level.width * 16 - 320;
    
    fireballsOnScreen = 0;
    
    for (int i=0;i<sprites.size();i++)//Sprite sprite : sprites)
    {
      Sprite sprite = (Sprite) sprites.get(i);
      //System.out.println("Sprites in levelScene tick "+sprite);
      if (sprite != mario)
      {
        double xd = sprite.getX() - xCam;
        double yd = sprite.getY() - yCam;
        if (xd < -64 || xd > 320 + 64 || yd < -64 || yd > 240 + 64)
        {
          removeSprite(sprite);
        }
        else
        {
          if (sprite instanceof Fireball)
          {
            fireballsOnScreen++;
          }
        }
      }
    }
    
    if (paused)
    {
      //System.out.println("In paused");
      for (int i=0;i<sprites.size();i++)//Sprite sprite : sprites)
      {
        Sprite sprite = (Sprite) sprites.get(i);
        if (sprite == mario || sprite == oldMario)
        {
          sprite.tick();//Mario's tick
        }
        else
        {
          sprite.tickNoMove();
        }
      }
    }
    else
    {
      //System.out.println("In else paused");
      tick++;
      level.tick();
      
      boolean hasShotCannon = false;
      int xCannon = 0;
      
      for (int x = (int) xCam / 16 - 1; x <= (int) (xCam + layer.width) / 16 + 1; x++)
        for (int y = (int) yCam / 16 - 1; y <= (int) (yCam + layer.height) / 16 + 1; y++)
      {
        int dir = 0;
        
        if (x * 16 + 8 > mario.getX() + 16) dir = -1;
        if (x * 16 + 8 < mario.getX() - 16) dir = 1;
        
        SpriteTemplate st = level.getSpriteTemplate(x, y);
        
        if (st != null)
        {
          if (st.lastVisibleTick != tick - 1)
          {
            if (st.sprite == null || !sprites.contains(st.sprite))
            {
              st.spawn(this, x, y, dir);
            }
          }
          st.lastVisibleTick = tick;
        }
        
        if (dir != 0)
        {
          byte b = level.getBlock(x, y);
          if (((Level.TILE_BEHAVIORS[b & 0xff]) & Level.BIT_ANIMATED) > 0)
          {
            if ((b % 16) / 4 == 3 && b / 16 == 0)
            {
              if ((tick - x * 2) % 100 == 0)
              {
                xCannon = x;
                for (int i = 0; i < 8; i++)
                {
                  addSprite(new Sparkle(x * 16 + 8, y * 16 + (int) (Math.random() * 16), (float) Math.random() * dir, 0, 0, 1, 5));
                }
                //addSprite(new BulletBill(this, x * 16 + 8 + dir * 8, y * 16 + 15, dir));
                hasShotCannon = true;
              }
            }
          }
        }
      }
      
      if (hasShotCannon)
      {
        sound.play(Art.samples[Art.SAMPLE_CANNON_FIRE], new FixedSoundSource(xCannon * 16, yCam + 120), 1, 1, 1);
      }
      
      for (int i=0;i<sprites.size();i++)//Sprite sprite : sprites)
      {
        Sprite sprite = (Sprite) sprites.get(i);
        sprite.tick();
      }
      
      for (int i=0;i<sprites.size();i++)//Sprite sprite : sprites)
      {
        Sprite sprite = (Sprite) sprites.get(i);
        sprite.collideCheck();
      }
      
      
      for (int j=0;j<shellsToCheck.size();j++)//Sprite sprite : sprites)
      {
        Shell shell = (Shell) shellsToCheck.get(j);
        for (int i=0;i<sprites.size();i++)//Sprite sprite : sprites)
        {
          Sprite sprite = (Sprite) sprites.get(i);
          if (sprite != shell && !shell.isDead())
          {
            if (sprite.shellCollideCheck(shell))
            {
              if (mario.getCarriedSprite() == shell && !shell.isDead())
              {
                mario.setCarriedSprite( null );
                shell.die();
              }
            }
          }
        }
      }
      shellsToCheck.clear();
      
      for (int j=0;j<fireballsToCheck.size();j++)//Sprite sprite : sprites)
      {
        Fireball fireball = (Fireball) fireballsToCheck.get(j);
        for (int i=0;i<sprites.size();i++)//Sprite sprite : sprites)
        {
          Sprite sprite = (Sprite) sprites.get(i);
          if (sprite != fireball && !fireball.isDead())
          {
            if (sprite.fireballCollideCheck(fireball))
            {
              fireball.die();
            }
          }
        }
      }
      fireballsToCheck.clear();
    }
    
    sprites.addAll(0, spritesToAdd);
    sprites.removeAll(spritesToRemove);
    spritesToAdd.clear();
    spritesToRemove.clear();
  }
  
  private DecimalFormat df = new DecimalFormat("00");
  private DecimalFormat df2 = new DecimalFormat("000");
  
  public void render(Graphics g, float alpha)
  {
    int xCam = (int) (mario.getXOld() + (mario.getX() - mario.getXOld()) * alpha) - 160;
    int yCam = (int) (mario.getYOld() + (mario.getY() - mario.getYOld()) * alpha) - 120;
    //int xCam = (int) (xCamO + (this.xCam - xCamO) * alpha);
    //        int yCam = (int) (yCamO + (this.yCam - yCamO) * alpha);
    if (xCam < 0) xCam = 0;
    if (yCam < 0) yCam = 0;
    if (xCam > level.width * 16 - 320) xCam = level.width * 16 - 320;
    if (yCam > level.height * 16 - 240) yCam = level.height * 16 - 240;
    
    //      g.drawImage(Art.background, 0, 0, null);
    
    for (int i = 0; i < 2; i++)
    {
      bgLayer[i].setCam(xCam, yCam);
      bgLayer[i].render(g, tick, alpha);
    }
    
    g.translate(-xCam, -yCam);
    for (int i=0;i<sprites.size();i++)//Sprite sprite : sprites)
    {
      Sprite sprite = (Sprite) sprites.get(i);
      if (sprite.getLayer() == 0) sprite.render(g, alpha);
    }
    g.translate(xCam, yCam);
    
    layer.setCam(xCam, yCam);
    layer.render(g, tick, paused?0:alpha);
    layer.renderExit0(g, tick, paused?0:alpha, mario.getWinTime()==0);
    
    g.translate(-xCam, -yCam);
    for (int i=0;i<sprites.size();i++)//Sprite sprite : sprites)
    {
      Sprite sprite = (Sprite) sprites.get(i);
      if (sprite.getLayer() == 1) sprite.render(g, alpha);
    }
    g.translate(xCam, yCam);
    g.setColor(Color.BLACK);
    layer.renderExit1(g, tick, paused?0:alpha);
    
    drawStringDropShadow(g, "MARIO " + df.format(Mario.lives), 0, 0, 7);
    //drawStringDropShadow(g, "00000000", 0, 1, 7);
    
    drawStringDropShadow(g, "COIN", 14, 0, 7);
    drawStringDropShadow(g, " "+df.format(mario.getCoins()), 14, 1, 7);
    
    drawStringDropShadow(g, "WORLD", 24, 0, 7);
    drawStringDropShadow(g, " "+Mario.levelString, 24, 1, 7);
    
    drawStringDropShadow(g, "TIME", 35, 0, 7);
    int time = (timeLeft+15-1)/15;
    if (time<0) time = 0;
    drawStringDropShadow(g, " "+df2.format(time), 35, 1, 7);
    
    
    if (startTime > 0)
    {
      float t = startTime + alpha - 2;
      t = t * t * 0.6f;
      renderBlackout(g, 160, 120, (int) (t));
    }
//        mario.x>level.xExit*16
    if (mario.getWinTime() > 0)
    {
      double t = mario.getWinTime() + alpha;
      t = t * t * 0.2f;
      
      if (t > 900)
      {
        renderer.levelWon();
        //              replayer = new Replayer(recorder.getBytes());
//                init();
      }
      
      renderBlackout(g, (int) (mario.getXDeathPos() - xCam), (int) (mario.getYDeathPos() - yCam), (int) (320 - t));
    }
    
    if (mario.getDeathTime() > 0)
    {
      double t = mario.getDeathTime() + alpha;
      t = t * t * 0.4f;
      
      if (t > 1800)
      {
        renderer.levelFailed();
        //              replayer = new Replayer(recorder.getBytes());
//                init();
      }
      
      renderBlackout(g, (int) (mario.getXDeathPos() - xCam), (int) (mario.getYDeathPos() - yCam), (int) (320 - t));
    }
  }
  
  private void drawStringDropShadow(Graphics g, String text, int x, int y, int c)
  {
    drawString(g, text, x*8+5, y*8+5, 0);
    drawString(g, text, x*8+4, y*8+4, c);
  }
  
  private void drawString(Graphics g, String text, int x, int y, int c)
  {
    char[] ch = text.toCharArray();
    for (int i = 0; i < ch.length; i++)
    {
      g.drawImage(Art.font[ch[i] - 32][c], x + i * 8, y, null);
    }
  }
  
  private void renderBlackout(Graphics g, int x, int y, int radius)
  {
    if (radius > 320) return;
    
    int[] xp = new int[20];
    int[] yp = new int[20];
    for (int i = 0; i < 16; i++)
    {
      xp[i] = x + (int) (Math.cos(i * Math.PI / 15) * radius);
      yp[i] = y + (int) (Math.sin(i * Math.PI / 15) * radius);
    }
    xp[16] = 320;
    yp[16] = y;
    xp[17] = 320;
    yp[17] = 240;
    xp[18] = 0;
    yp[18] = 240;
    xp[19] = 0;
    yp[19] = y;
    g.fillPolygon(xp, yp, xp.length);
    
    for (int i = 0; i < 16; i++)
    {
      xp[i] = x - (int) (Math.cos(i * Math.PI / 15) * radius);
      yp[i] = y - (int) (Math.sin(i * Math.PI / 15) * radius);
    }
    xp[16] = 320;
    yp[16] = y;
    xp[17] = 320;
    yp[17] = 0;
    xp[18] = 0;
    yp[18] = 0;
    xp[19] = 0;
    yp[19] = y;
    
    g.fillPolygon(xp, yp, xp.length);
  }
  
  
  public void addSprite(Sprite sprite)
  {
    spritesToAdd.add(sprite);
    sprite.tick();
  }
  
  public void removeSprite(Sprite sprite)
  {
    spritesToRemove.add(sprite);
  }
  
  public double getXPixel(double alpha)
  {
    int xCam = (int) (mario.getXOld() + (mario.getX() - mario.getXOld()) * alpha) - 160;
    //        int yCam = (int) (mario.yOld + (mario.y - mario.yOld) * alpha) - 120;
    //int xCam = (int) (xCamO + (this.xCam - xCamO) * alpha);
    //        int yCam = (int) (yCamO + (this.yCam - yCamO) * alpha);
    if (xCam < 0) xCam = 0;
    //        if (yCam < 0) yCam = 0;
    //        if (yCam > 0) yCam = 0;
    return xCam + 160;
  }
  
  public double getYPixel(double alpha)
  {
    return 0;
  }
  
  public void bump(int x, int y, boolean canBreakBricks)
  {
    byte block = level.getBlock(x, y);
    
    if ((Level.TILE_BEHAVIORS[block & 0xff] & Level.BIT_BUMPABLE) > 0)
    {
      bumpInto(x, y - 1);
      level.setBlock(x, y, (byte) 4);
      level.setBlockData(x, y, (byte) 4);
      
      if (((Level.TILE_BEHAVIORS[block & 0xff]) & Level.BIT_SPECIAL) > 0)
      {
        sound.play(Art.samples[Art.SAMPLE_ITEM_SPROUT], new FixedSoundSource(x * 16 + 8, y * 16 + 8), 1, 1, 1);
        Random r = new Random();
        int chance = r.nextInt();
        
        
        if (chance%2==1 || !(mario.isLarge() ) )//!Mario.large)
        {
          addSprite(new Mushroom(this, x * 16 + 8, y * 16 + 8));
        }
        else
        {
          addSprite(new FireFlower(this, x * 16 + 8, y * 16 + 8));
        }
      }
      else
      {
        //System.out.println("LSBump getCoin");
        ((Mario)mario).getCoin();
        sound.play(Art.samples[Art.SAMPLE_GET_COIN], new FixedSoundSource(x * 16 + 8, y * 16 + 8), 1, 1, 1);
        addSprite(new CoinAnim(x, y));
      }
    }
    
    if ((Level.TILE_BEHAVIORS[block & 0xff] & Level.BIT_BREAKABLE) > 0)
    {
      bumpInto(x, y - 1);
      if (canBreakBricks)
      {
        sound.play(Art.samples[Art.SAMPLE_BREAK_BLOCK], new FixedSoundSource(x * 16 + 8, y * 16 + 8), 1, 1, 1);
        level.setBlock(x, y, (byte) 0);
        for (int xx = 0; xx < 2; xx++)
          for (int yy = 0; yy < 2; yy++)
          addSprite(new Particle(x * 16 + xx * 8 + 4, y * 16 + yy * 8 + 4, (xx * 2 - 1) * 4, (yy * 2 - 1) * 4 - 8));
      }
      else
      {
        level.setBlockData(x, y, (byte) 4);
      }
    }
  }
  public void blink(boolean on)
  {
    /*
     if(mario instanceof BigMario)
     {
     mario = (BigMario) mario;
     mario.selectPicture();//calls calcPic to display pic
     }
     */
  }
  
  public void bumpInto(int x, int y)
  {
    byte block = level.getBlock(x, y);
    if (((Level.TILE_BEHAVIORS[block & 0xff]) & Level.BIT_PICKUPABLE) > 0)
    {
      //System.out.println("LSBumpInto.getCoin");
      ((Mario)mario).getCoin();
      sound.play(Art.samples[Art.SAMPLE_GET_COIN], new FixedSoundSource(x * 16 + 8, y * 16 + 8), 1, 1, 1);
      level.setBlock(x, y, (byte) 0);
      addSprite(new CoinAnim(x, y + 1));
    }
    
    for (int i=0;i<sprites.size();i++)//Sprite sprite : sprites)
    {
      Sprite sprite = (Sprite) sprites.get(i);
      sprite.bumpCheck(x, y);
    }
  }
  
  public boolean powerUpCheck()
  {
    if (powerUpTime != 0)
    {//move routine for powering up
      if (powerUpTime > 0)
      {
        powerUpTime--;
        blink(((powerUpTime / 3) & 1) == 0);//calling a boolean with math?
      }
      else
      {
        powerUpTime++;
        blink(((-powerUpTime / 3) & 1) == 0);
      }
      
      if (powerUpTime == 0) paused = false;
      return true;
    }
    else
    {
      return false;
    }
  }
  
  public void setMario(Mario m)
  {
    
    addSprite(m);
    removeSprite(mario);
    mario = m;
  }
  public Mario getMario()
  {
    return this.mario;
  }
  
  
  /**
   * @param powerUpTime the powerUpTime to set
   */
  public void setPowerUpTime(int powerUpTime) {
    this.powerUpTime = powerUpTime;
  }  
}