package com.controlgroup.coffeesystem.zelda.client;

import com.google.gwt.canvas.dom.client.Context2d;
import com.google.gwt.canvas.dom.client.CssColor;
import com.google.gwt.dom.client.CanvasElement;
import com.google.gwt.dom.client.ImageElement;
import com.google.gwt.user.client.Window;

/**
 * Created by timmattison on 1/20/15.
 */
public class Drawing {
    public static final int gridElementWidth = Graphics.caveWallImageElement.getWidth();
    public static final int gridElementHeight = Graphics.caveWallImageElement.getHeight();
    public static final int linkPadding = 40;
    public static final int linkWidth = 48;
    public static final int linkHeight = 48;
    private static final int letterWidth = 21;
    private static final int letterHeight = 21;
    private static final int spriteSheetHorizontalLetterSpacing = 27;
    private static final int spriteSheetVerticalLetterSpacing = 27;
    private static final int CHARACTER_SPRITE_SHEET_HORIZONTAL_COUNT = 16;
    private static final CssColor steamColor = CssColor.make(255, 255, 255);

    /**
     * 1 == Wall
     * 2 == Where Link starts out
     * 3 == Blank, but a place where Link can't walk
     */
    public static final int[][] caveWalls = {
            {1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1},
            {1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1},
            {1, 1, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 1, 1},
            {1, 1, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 1, 1},
            {1, 1, 0, 0, 3, 3, 3, 3, 3, 3, 3, 3, 0, 0, 1, 1},
            {1, 1, 0, 0, 3, 3, 3, 3, 3, 3, 3, 3, 0, 0, 1, 1},
            {1, 1, 0, 0, 3, 3, 3, 3, 3, 3, 3, 3, 0, 0, 1, 1},
            {1, 1, 0, 0, 3, 3, 3, 3, 3, 3, 3, 3, 0, 0, 1, 1},
            {1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1},
            {1, 1, 1, 1, 1, 1, 1, 2, 0, 1, 1, 1, 1, 1, 1, 1},
            {1, 1, 1, 1, 1, 1, 1, 0, 0, 1, 1, 1, 1, 1, 1, 1},
    };

    public static final int HUD_VERTICAL_SIZE = 48 * 3;
    private static Steam[] steam = null;
    private static final long MINUTES_PER_REDUCTION = 15;

    public static void clear(CssColor redrawColor, Context2d context) {
        CanvasElement canvasElement = context.getCanvas();
        int width = canvasElement.getWidth();
        int height = canvasElement.getHeight();

        context.setFillStyle(redrawColor);
        context.fillRect(0, 0, width, height);
    }

    public static void drawFire(int frameCounter, Context2d context) {
        // Alternate between the fire images on each frame
        ImageElement fireImageElement = Graphics.fire0ImageElement;

        if (frameCounter % 2 == 0) {
            fireImageElement = Graphics.fire1ImageElement;
        }

        // Draw the fire on the left
        context.drawImage(fireImageElement, Constants.LEFT_FIRE_X, Constants.LEFT_FIRE_Y, fireImageElement.getWidth(), fireImageElement.getHeight());

        // Draw the fire on the right
        context.drawImage(fireImageElement, Constants.RIGHT_FIRE_X, Constants.RIGHT_FIRE_Y, fireImageElement.getWidth(), fireImageElement.getHeight());
    }

    public static void drawCaveWalls(Context2d context, int xOffset, int yOffset) {
        int y = 0;

        for (int[] rowData : caveWalls) {
            int x = 0;

            for (int columnData : rowData) {
                if (columnData == 1) {
                    drawCaveWall(context, xOffset + (x * Graphics.caveWallImageElement.getWidth()), yOffset + (y * Graphics.caveWallImageElement.getHeight()));
                }

                x++;
            }

            y++;
        }
    }

    private static void drawCaveWall(Context2d context, int destinationX, int destinationY) {
        context.drawImage(Graphics.caveWallImageElement, destinationX, destinationY, Graphics.caveWallImageElement.getWidth(), Graphics.caveWallImageElement.getHeight());
    }

    public static void drawOldGuy(CssColor redrawColor, Context2d context) {
        context.setFillStyle(redrawColor);
        context.drawImage(Graphics.oldManImageElement, Constants.OLD_GUY_STATIC_X, Constants.OLD_GUY_STATIC_Y, Constants.OLD_GUY_STATIC_WIDTH, Constants.OLD_GUY_STATIC_HEIGHT);
    }

    public static void drawCoffeeCups(Context2d context, int count, long coffeeAgeInMinutes) {
        // TODO - Make the coffee cups dance
        // Draw multiple coffee cups

        if (steam == null) {
            steam = new Steam[Constants.MAX_CUPS_TO_DRAW];

            for (int loop = 0; loop < Constants.MAX_CUPS_TO_DRAW; loop++) {
                steam[loop] = new BasicSteam();
            }
        }

        if (count > Constants.MAX_CUPS_TO_DRAW) {
            count = Constants.MAX_CUPS_TO_DRAW;
        }

        int coffeeCupWidth = Graphics.coffeeCupImageElement.getWidth();
        int coffeeCupHeight = Graphics.coffeeCupImageElement.getHeight();

        int offset = (count * (coffeeCupWidth + Constants.COFFEE_CUPS_BUFFER)) / 2;

        for (int loop = 0; loop < count; loop++) {
            int center = Constants.COFFEE_CUPS_X_CENTER - (coffeeCupWidth * loop) - (Constants.COFFEE_CUPS_BUFFER * loop) + offset;
            context.drawImage(Graphics.coffeeCupImageElement, center, Constants.COFFEE_CUPS_Y, coffeeCupWidth, coffeeCupHeight);
            drawSteam(context, steam[loop], center + 8, Constants.COFFEE_CUPS_Y - 14, coffeeAgeInMinutes);
        }
    }

    public static void drawLetter(Context2d context, char letter, int destinationX, int destinationY) {
        int sourceX = -1;
        int sourceY = -1;

        if ((letter >= '0') && (letter <= '9')) {
            sourceY = 0;
            sourceX = (letterWidth + spriteSheetHorizontalLetterSpacing) * (letter - '0');
        } else if ((letter >= 'A') && (letter <= 'Z')) {
            int letterIndex = letter - 'A';

            // Move past the numbers
            letterIndex += 10;

            sourceX = letterIndex % CHARACTER_SPRITE_SHEET_HORIZONTAL_COUNT;
            sourceY = letterIndex / CHARACTER_SPRITE_SHEET_HORIZONTAL_COUNT;
        } else {
            sourceY = 2;

            if (letter == ',') {
                sourceX = 4;
            } else if (letter == '!') {
                sourceX = 5;
            } else if (letter == '\'') {
                sourceX = 6;
            } else if (letter == '&') {
                sourceX = 7;
            } else if (letter == '.') {
                sourceX = 8;
            } else if (letter == '"') {
                sourceX = 9;
            } else if (letter == '?') {
                sourceX = 10;
            } else if (letter == '-') {
                sourceX = 11;
            } else if (letter == ' ') {
                sourceX = 15;
            } else {
                // Can't do anything with this character
                Window.alert("Cannot process letter: [" + letter + "]");
                return;
            }
        }

        sourceX = (letterWidth + spriteSheetHorizontalLetterSpacing) * sourceX;
        sourceY = (letterHeight + spriteSheetVerticalLetterSpacing) * sourceY;

        context.drawImage(Graphics.nesFontImageElement, sourceX, sourceY, letterWidth, letterHeight, destinationX, destinationY, letterWidth, letterHeight);
    }

    public static void drawString(Context2d context, String string, int destinationX, int destinationY, int characterSpacing) {
        string = string.toUpperCase();
        int currentX = destinationX;

        for (char letter : string.toCharArray()) {
            drawLetter(context, letter, currentX, destinationY);

            currentX += (letterWidth + characterSpacing);
        }
    }

    public static void copyBufferOverOtherBuffer(CssColor redrawColor, Context2d destination, Context2d source) {
        CanvasElement canvasElement = destination.getCanvas();
        int width = canvasElement.getWidth();
        int height = canvasElement.getHeight();

        destination.setFillStyle(redrawColor);
        destination.fillRect(0, 0, width, height);
        destination.drawImage(source.getCanvas(), 0, 0, width, height);
    }

    public static void drawBackground(Context2d context) {
        // Draw the background
        context.drawImage(Graphics.backgroundImageElement, 0, 0, context.getCanvas().getWidth(), context.getCanvas().getHeight());
    }

    public static void drawLifeMeter(int frameCounter, Context2d context, boolean alive) {
        // Alternate between half heart and empty heart on each frame:
        ImageElement heartImageElement = Graphics.halfHeartImageElement;

        if (!alive || (frameCounter % 2 == 0)) {
            heartImageElement = Graphics.emptyHeartImageElement;
        }

        // Draw the first heart
        context.drawImage(heartImageElement, Constants.FIRST_HEART_X, Constants.FIRST_HEART_Y, heartImageElement.getWidth(), heartImageElement.getHeight());

        // Draw the rest of the hearts
        for (int loop = 1; loop < Constants.HEARTS_TO_DRAW; loop++) {
            context.drawImage(Graphics.emptyHeartImageElement,
                    Constants.FIRST_HEART_X + (Constants.HEART_BUFFER * loop) + (Graphics.emptyHeartImageElement.getWidth() * loop),
                    Constants.FIRST_HEART_Y, Graphics.emptyHeartImageElement.getWidth(), Graphics.emptyHeartImageElement.getHeight());
        }
    }

    public static void drawLink(Context2d context, ImageElement linkImageElement, int destinationX, int destinationY) {
        context.drawImage(linkImageElement, 0, 0, linkHeight, linkWidth, destinationX, destinationY, linkHeight, linkWidth);
    }

    public static void drawSteam(Context2d context, Steam steam, int destinationX, int destinationY, long coffeeAgeInMinutes) {
        context.setFillStyle(steamColor);

        // Calculate the "limiter" which is how much we'll reduce the steam based on the coffee's age in minutes
        long limiter = coffeeAgeInMinutes / MINUTES_PER_REDUCTION;

        boolean[][] currentSteam = steam.next(limiter);

        for (int x = 0; x < currentSteam.length; x++) {
            for (int y = 0; y < currentSteam[x].length; y++) {
                if (currentSteam[x][y] == true) {
                    context.fillRect(destinationX + x, destinationY + y, 1, 1);
                }
            }
        }
    }
}
