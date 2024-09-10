package com.mojang.mario.sprites;

//***********************************************************
// Bumpable Interface to require some sprites to have a     *
// bump reaction to Tile movement                           *
//***********************************************************

public interface Bumpable
{
	public void bumpCheck(int x, int y);
}