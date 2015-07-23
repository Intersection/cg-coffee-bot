package com.controlgroup.coffeesystem.zelda.client;

import java.util.Random;

/**
 * Created by timmattison on 2/19/15.
 */
public class BasicSteam implements Steam {
    private static final int WIDTH = 16;
    private static final int HEIGHT = 32;
    private static final long MAX_LIMITER = 6;
    private boolean[][] currentCells = new boolean[WIDTH][HEIGHT];
    private boolean[][] nextCells = new boolean[WIDTH][HEIGHT];
    private Random random = new Random();

    @Override
    public boolean[][] next() {
        return next(0);
    }

    @Override
    public boolean[][] next(long limiter) {
        moveCurrentStateUpOneRow();

        moveToNextState();

        copyNextStateToCurrentState();

        randomizeBottomRow(limiter);

        return currentCells;
    }

    public void randomizeBottomRow(long limiter) {
        if (limiter > MAX_LIMITER) {
            limiter = MAX_LIMITER;
        }

        for (int x = 0; x < WIDTH; x++) {
            if ((x < limiter) || (x > (WIDTH - limiter))) {
                currentCells[x][HEIGHT - 1] = false;
            } else {
                currentCells[x][HEIGHT - 1] = random.nextBoolean();
            }
        }
    }

    @Override
    public boolean[][] current() {
        return currentCells;
    }

    public void moveCurrentStateUpOneRow() {
        for (int x = 0; x < WIDTH; x++) {
            for (int y = 0; y < HEIGHT - 1; y++) {
                currentCells[x][y] = currentCells[x][y + 1];
            }
        }
    }

    public void copyNextStateToCurrentState() {
        for (int x = 0; x < WIDTH; x++) {
            for (int y = 0; y < HEIGHT; y++) {
                currentCells[x][y] = nextCells[x][y];
            }
        }
    }

    public void moveToNextState() {
        for (int x = 1; x < WIDTH - 1; x++) {
            for (int y = 1; y < HEIGHT - 1; y++) {
                nextCells[x][y] = next(x, y);
            }
        }
    }

    public boolean next(int x, int y) {
        int count = 0;

        count += currentCells[x - 1][y + 1] ? 1 : 0;
        count += currentCells[x][y + 1] ? 1 : 0;
        count += currentCells[x + 1][y + 1] ? 1 : 0;
        count += currentCells[x - 1][y] ? 1 : 0;
        count += currentCells[x + 1][y] ? 1 : 0;

        if (count < 3) {
            return false;
        }

        return true;
    }
}
