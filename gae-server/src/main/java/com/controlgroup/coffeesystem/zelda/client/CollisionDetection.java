package com.controlgroup.coffeesystem.zelda.client;

/**
 * Created by timmattison on 1/26/15.
 */
public interface CollisionDetection {
    public XY[] overlapping(int mapCellWidth, int mapCellHeight, int spriteX, int spriteY, int spriteWidth, int spriteHeight);
}
