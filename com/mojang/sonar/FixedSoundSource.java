package com.mojang.sonar;

/**
 * @author Administrator
 */
public class FixedSoundSource implements SoundSource
{
 private double x;
 private double y;

 public FixedSoundSource(double x, double y)
 {
  this.x = x;
  this.y = y;
 }

 public FixedSoundSource(SoundSource soundSource)
 {
  this.x = soundSource.getXPixel(1);
  this.y = soundSource.getYPixel(1);
 }

    public double getXPixel(double alpha)
    {
        return x;
    }

    public double getYPixel(double alpha)
    {
        return y;
    }
}