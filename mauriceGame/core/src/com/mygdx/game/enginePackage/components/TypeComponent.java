package com.mygdx.game.enginePackage.components;

import com.badlogic.ashley.core.Component;

public class TypeComponent implements Component {
    public static final int PLAYER = 0;
    public static final int BASIC_ENEMY = 1;
    public static final int BOSS = 2;
    public static final int BALL = 3;
    public static final int SCENERY = 4;
    public static final int BULLET = 5;
    public static final int LEVEL_SENSOR = 6;
    public static final int OTHER = 7;

    public int type = OTHER;

}
