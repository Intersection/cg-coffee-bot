package com.controlgroup.coffeesystem.zelda.client;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.core.Is.is;

public class SimpleCollisionDetectionTest {
    private final int mapCellWidth = 32;
    private final int mapCellHeight = 32;

    private final int spriteWidth = 32;
    private final int spriteHeight = 32;

    private SimpleCollisionDetection simpleCollisionDetection;

    @Before
    public void setup() {
        simpleCollisionDetection = new SimpleCollisionDetection();
    }

    @Test
    public void shouldReturnOnlyZeroZero() {
        int spriteX = 0;
        int spriteY = 0;

        XY[] output = simpleCollisionDetection.overlapping(mapCellWidth, mapCellHeight, spriteX, spriteY, spriteWidth, spriteHeight);

        Assert.assertThat(output.length, is(1));
        Assert.assertThat(output[0], is(new XY(0, 0)));
    }

    @Test
    public void shouldReturnFourValues() {
        int spriteX = 1;
        int spriteY = 1;

        XY[] output = simpleCollisionDetection.overlapping(mapCellWidth, mapCellHeight, spriteX, spriteY, spriteWidth, spriteHeight);

        Assert.assertThat(output.length, is(4));
        Assert.assertThat(output[0], is(new XY(0, 0)));
        Assert.assertThat(output[1], is(new XY(1, 0)));
        Assert.assertThat(output[2], is(new XY(0, 1)));
        Assert.assertThat(output[3], is(new XY(1, 1)));
    }

    @Test
    public void realWorldTest1() {
        XY[] output = simpleCollisionDetection.overlapping(48, 48, 336, 624, 90, 90);
        Assert.assertThat(output.length, is(4));
        Assert.assertThat(output[0], is(new XY(7, 13)));
        Assert.assertThat(output[1], is(new XY(8, 13)));
        Assert.assertThat(output[2], is(new XY(7, 14)));
        Assert.assertThat(output[3], is(new XY(8, 14)));
    }
    /*
    @Override
    public XY[] overlapping(int[][] map, int mapCellWidth, int mapCellHeight, int spriteX, int spriteY, int spriteWidth, int spriteHeight) {
        // NOTE: Assumes that the map has the same numbers of columns in every row
        int mapWidth = map[0].length;
        int mapHeight = map.length;

        int spriteEndX = spriteX + spriteWidth;
        int spriteEndY = spriteY + spriteHeight;

        int spriteMapStartX = spriteX / mapCellWidth;
        int spriteMapStartY = spriteY / mapCellHeight;

        int spriteMapEndX = (spriteEndX / mapCellWidth) + (((spriteEndX % mapCellWidth) != 0) ? 1 : 0);
        int spriteMapEndY = (spriteEndY / mapCellHeight) + (((spriteEndY % mapCellHeight) != 0) ? 1 : 0);

        int xCount = spriteMapEndX - spriteMapStartX;
        int yCount = spriteMapEndY - spriteMapStartY;

        XY[] output = new XY[xCount * yCount];

        for(int x = spriteMapStartX; x < spriteMapEndX; x++) {
            for (int y = spriteMapStartY; y < spriteMapEndY; y++) {
                output[x * xCount + y] = new XY(x, y);
            }
        }

        return output;
    }
    */
}
