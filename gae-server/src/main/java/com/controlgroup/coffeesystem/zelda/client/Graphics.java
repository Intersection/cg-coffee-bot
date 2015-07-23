package com.controlgroup.coffeesystem.zelda.client;

import com.google.gwt.dom.client.ImageElement;
import com.google.gwt.user.client.ui.Image;

/**
 * Created by timmattison on 1/20/15.
 */
public class Graphics {
    // getSafeUri info from https://groups.google.com/forum/#!topic/google-web-toolkit/95O7tfO_bR0
    public static final Image zeldaBackground = new Image(ImageResources.INSTANCE.zeldaBackground());
    public static final ImageElement backgroundImageElement = ImageElement.as(zeldaBackground.getElement());

    public static final Image halfHeart = new Image(ImageResources.INSTANCE.halfHeart());
    public static final ImageElement halfHeartImageElement = ImageElement.as(halfHeart.getElement());

    public static final Image emptyHeart = new Image(ImageResources.INSTANCE.emptyHeart());
    public static final ImageElement emptyHeartImageElement = ImageElement.as(emptyHeart.getElement());

    public static final Image fire0 = new Image(ImageResources.INSTANCE.fire0());
    public static final ImageElement fire0ImageElement = ImageElement.as(fire0.getElement());

    public static final Image fire1 = new Image(ImageResources.INSTANCE.fire1());
    public static final ImageElement fire1ImageElement = ImageElement.as(fire1.getElement());

    public static final Image coffeeCup = new Image(ImageResources.INSTANCE.coffeeCup());
    public static final ImageElement coffeeCupImageElement = ImageElement.as(coffeeCup.getElement());

    public static final Image nesFont = new Image(ImageResources.INSTANCE.nesFont());
    public static final ImageElement nesFontImageElement = ImageElement.as(nesFont.getElement());

    public static final Image oldMan = new Image(ImageResources.INSTANCE.oldMan());
    public static final ImageElement oldManImageElement = ImageElement.as(oldMan.getElement());

    public static final Image caveWall = new Image(ImageResources.INSTANCE.caveWall());
    public static final ImageElement caveWallImageElement = ImageElement.as(caveWall.getElement());

    public static final Image linkUp1 = new Image(ImageResources.INSTANCE.linkUp1());
    public static final ImageElement linkUp1ImageElement = ImageElement.as(linkUp1.getElement());

    public static final Image linkUp2 = new Image(ImageResources.INSTANCE.linkUp2());
    public static final ImageElement linkUp2ImageElement = ImageElement.as(linkUp2.getElement());

    public static final Image linkDown1 = new Image(ImageResources.INSTANCE.linkDown1());
    public static final ImageElement linkDown1ImageElement = ImageElement.as(linkDown1.getElement());

    public static final Image linkDown2 = new Image(ImageResources.INSTANCE.linkDown2());
    public static final ImageElement linkDown2ImageElement = ImageElement.as(linkDown2.getElement());

    public static final Image linkLeft1 = new Image(ImageResources.INSTANCE.linkLeft1());
    public static final ImageElement linkLeft1ImageElement = ImageElement.as(linkLeft1.getElement());

    public static final Image linkLeft2 = new Image(ImageResources.INSTANCE.linkLeft2());
    public static final ImageElement linkLeft2ImageElement = ImageElement.as(linkLeft2.getElement());

    public static final Image linkRight1 = new Image(ImageResources.INSTANCE.linkRight1());
    public static final ImageElement linkRight1ImageElement = ImageElement.as(linkRight1.getElement());

    public static final Image linkRight2 = new Image(ImageResources.INSTANCE.linkRight2());
    public static final ImageElement linkRight2ImageElement = ImageElement.as(linkRight2.getElement());
}
