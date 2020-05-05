package com.mygdx.game.enginePackage;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.mygdx.game.enginePackage.components.CollisionTypeComponent;
import com.mygdx.game.resources.ImagesRes;

public enum BulletType {
    PLAYER_BULLET(0.25f,20,10, 0.1f,0.02f, CollisionTypeComponent.FRIENDLY, ImagesRes.bulletImage), ENEMY_BULLET(0.25f, 20, 10, 0.05f, 0.2f, CollisionTypeComponent.ENEMY, ImagesRes.enemyBulletImage);
    public float radius;
    public float damage;
    public int type;
    public float bulletSpeed;
    public float bulletTimer;
    public TextureRegion region;
    public float bulletDamageTick;

    private BulletType(float radius, float damage,float bulletTimer,float bulletSpeed, float bulletDamageTick,int type, TextureRegion region){
        this.radius = radius;
        this.damage = damage;
        this.bulletTimer = bulletTimer;
        this.bulletSpeed = bulletSpeed;
        this.bulletDamageTick = bulletDamageTick;
        this.type = type;
        this.region = region;
    }
}
