package com.mygdx.game.components;

import com.badlogic.ashley.core.Component;

public class BulletComponent implements Component {
    public float bulletTimer = 100;
    public float bulletMinSpeed = 5;
    public boolean piercing = false;
}
