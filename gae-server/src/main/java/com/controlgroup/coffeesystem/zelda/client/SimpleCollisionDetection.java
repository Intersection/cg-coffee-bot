package com.controlgroup.coffeesystem.zelda.client;

/**
 * Created by timmattison on 1/26/15.
 */
public class SimpleCollisionDetection implements CollisionDetection {
    @Override
    public XY[] overlapping(int mapCellWidth, int mapCellHeight, int spriteX, int spriteY, int spriteWidth, int spriteHeight) {
        int spriteEndX = spriteX + spriteWidth;
        int spriteEndY = spriteY + spriteHeight;

        int spriteMapStartX = spriteX / mapCellWidth;
        int spriteMapStartY = spriteY / mapCellHeight;

        int spriteMapEndX = (spriteEndX / mapCellWidth);
        int spriteMapEndY = (spriteEndY / mapCellHeight);

        int xCount = spriteMapEndX - spriteMapStartX;
        int yCount = spriteMapEndY - spriteMapStartY;

        XY[] output = new XY[xCount * yCount];

        for (int x = spriteMapStartX; x < spriteMapEndX; x++) {
            for (int y = spriteMapStartY; y < spriteMapEndY; y++) {
                int index = (y - spriteMapStartY) * xCount + (x - spriteMapStartX);
                output[index] = new XY(x, y);
            }
        }

        return output;
    }
}
