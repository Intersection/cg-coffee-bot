package com.controlgroup.coffeesystem.zelda.client;

/**
 * Created by timmattison on 2/19/15.
 */
public interface Steam {
    public boolean[][] next();

    public boolean[][] next(long limiter);

    public boolean[][] current();
}
