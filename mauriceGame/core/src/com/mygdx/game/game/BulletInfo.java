package com.mygdx.game.game;

import com.badlogic.gdx.math.Vector2;

public class BulletInfo {
    public Vector2 pos;
    public Vector2 dir;
    public BulletType bulletType;
    public BulletInfo(Vector2 pos, Vector2 dir, BulletType bulletType){
        this.pos = pos;
        this.dir = dir;
        this.bulletType = bulletType;
    }
}
