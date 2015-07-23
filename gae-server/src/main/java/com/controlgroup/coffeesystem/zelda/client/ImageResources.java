package com.controlgroup.coffeesystem.zelda.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.ImageResource;

public interface ImageResources extends ClientBundle {
    public static final ImageResources INSTANCE = GWT.create(ImageResources.class);

    @Source("zelda/background.png")
    public ImageResource zeldaBackground();

    @Source("zelda/half-heart.png")
    public ImageResource halfHeart();

    @Source("zelda/empty-heart.png")
    public ImageResource emptyHeart();

    @Source("zelda/coffee-cup.png")
    public ImageResource coffeeCup();

    @Source("zelda/fire-0.png")
    public ImageResource fire0();

    @Source("zelda/fire-1.png")
    public ImageResource fire1();

    @Source("zelda/nes-font.png")
    public ImageResource nesFont();

    @Source("zelda/old-man.png")
    public ImageResource oldMan();

    @Source("zelda/cave-wall.png")
    public ImageResource caveWall();

    @Source("zelda/link-up-1.png")
    public ImageResource linkUp1();

    @Source("zelda/link-up-2.png")
    public ImageResource linkUp2();

    @Source("zelda/link-down-1.png")
    public ImageResource linkDown1();

    @Source("zelda/link-down-2.png")
    public ImageResource linkDown2();

    @Source("zelda/link-left-1.png")
    public ImageResource linkLeft1();

    @Source("zelda/link-left-2.png")
    public ImageResource linkLeft2();

    @Source("zelda/link-right-1.png")
    public ImageResource linkRight1();

    @Source("zelda/link-right-2.png")
    public ImageResource linkRight2();

}