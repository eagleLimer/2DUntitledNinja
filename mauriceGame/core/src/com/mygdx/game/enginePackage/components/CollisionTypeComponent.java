package com.mygdx.game.enginePackage.components;

import com.badlogic.ashley.core.Component;

public class CollisionTypeComponent implements Component {
    public static final int FRIENDLY = 0;
    public static final int ENEMY = 1;
    public static final int SCENERY = 2;
    public static final int ITEM = 3;
    public static final int LEVEL_PORTAL = 4;
    public static final int OTHER = 5;


    public int type;

    public CollisionTypeComponent(int type) {
        this.type = type;
    }
}
