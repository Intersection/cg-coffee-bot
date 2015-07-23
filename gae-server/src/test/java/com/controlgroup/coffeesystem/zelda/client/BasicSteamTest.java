package com.controlgroup.coffeesystem.zelda.client;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * Created by timmattison on 2/19/15.
 */
public class BasicSteamTest {
    private BasicSteam basicSteam;

    @Before
    public void setup() {
        basicSteam = new BasicSteam();
    }

    @Test
    public void test1() {
        boolean[][] current = basicSteam.current();

        for (int loop = 0; loop < current.length; loop++) {
            current[loop][current[loop].length - 1] = true;
        }

        basicSteam.moveCurrentStateUpOneRow();

        for (int loop = 0; loop < current.length; loop++) {
            Assert.assertEquals(current[loop][current[loop].length - 2], true);
        }
    }

    @Test
    public void test2() {
        for (int loop = 0; loop < 100; loop++) {
            print(basicSteam);
            basicSteam.next();
        }
    }

    private void print(Steam steam) {
        boolean[][] current = steam.current();

        for (int x = 0; x < current.length; x++) {
            StringBuilder stringBuilder = new StringBuilder();

            for (int y = 0; y < current[x].length; y++) {
                stringBuilder.append(current[x][y] ? 'x' : ' ');
            }

            System.out.println(stringBuilder.toString());
        }

        System.out.println("");
        System.out.println("-------------------------------------------------");
        System.out.println("");
    }
}
