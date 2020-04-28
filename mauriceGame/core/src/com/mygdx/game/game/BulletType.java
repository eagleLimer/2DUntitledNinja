package com.mygdx.game.game;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.mygdx.game.components.TypeComponent;
import com.mygdx.game.resources.ImagesRes;

public enum BulletType {
    PLAYER_BULLET(0.25f,20,100, TypeComponent.BULLET, ImagesRes.bulletImage), ENEMY_BULLET(0.25f, 10, 100, TypeComponent.BASIC_ENEMY, ImagesRes.bulletImage);
    public float radius;
    public float damage;
    public int type;
    public float bulletTimer;
    public TextureRegion region;

    private BulletType(float radius, float damage,float bulletTimer, int type, TextureRegion region){
        this.radius = radius;
        this.damage = damage;
        this.bulletTimer = bulletTimer;
        this.type = type;
        this.region = region;
    }
}
