package com.mojang.mario.sprites;

import com.mojang.mario.Art;
import com.mojang.mario.Scene;
import com.mojang.mario.LevelScene;

public class FireMario extends BigMario {

    private boolean oneShotPerKey;

    public FireMario(LevelScene world) {
        super(world);
        
        setSheet(Art.fireMario);
        oneShotPerKey = true;
    }
    
    @Override
    public void aAction() {
        super.aAction();

        LevelScene world = getWorld();
        float x = (float) getX();
        float y = (float) getY();

        if (world.fireballsOnScreen < 2 && oneShotPerKey)
        {
        world.getSound().play(Art.samples[Art.SAMPLE_MARIO_FIREBALL], this, 1,1,1);
        world.addSprite(new Fireball(world, x+getFacing()*6, y-20, getFacing()));
        }

        if(oneShotPerKey)
        oneShotPerKey = false; //one shot per aPressed()
        else
        oneShotPerKey = true;

    }

}