package com.mojang.mario.sprites;

import com.mojang.mario.Art;
import com.mojang.mario.Scene;
import com.mojang.mario.LevelScene;

public class BigMario extends Mario {

    public BigMario(LevelScene world) {
        super( world );
    
        setSheet( Art.mario );
        setXPicOrigin( 16 );
        setYPicOrigin( 31 );
        setWPic( 32 );
        setHPic( 32 );
        setFacing( 1 );
        setHeight( 24 );
    }

    @Override
    public void move() {
        if (downPressed()) {
            downAction();
            super.afterMove();
        } else {
            super.move();
            selectPicture();
            setHeight(24);
        }
    }

    public void downAction() {
        if (super.isOnGround()) {
            setXPic(14);
            setHeight(12);
        }
    }
}

