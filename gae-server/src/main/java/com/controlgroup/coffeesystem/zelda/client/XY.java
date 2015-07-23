package com.controlgroup.coffeesystem.zelda.client;

/**
 * Created by timmattison on 1/26/15.
 */
public class XY {
    private final int x;
    private final int y;

    public XY(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        XY xy = (XY) o;

        if (x != xy.x) return false;
        if (y != xy.y) return false;

        return true;
    }
}
