package com.mygdx.game.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.math.Vector2;
import com.mygdx.game.game.BulletType;

public class ShooterComponent implements Component {
    public float bulletCd;
    public float bulletTick = 0;
    public BulletType bulletType;
    public boolean shoot = false;
    public Vector2 dir = new Vector2(1,0);
    public float bulletSpawn = 0.5f;
}
