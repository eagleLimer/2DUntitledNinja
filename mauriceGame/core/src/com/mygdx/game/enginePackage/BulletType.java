package com.mygdx.game.enginePackage;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.mygdx.game.enginePackage.components.BasicComponents.CollisionTypeComponent;
import com.mygdx.game.resources.ImagesRes;

public enum BulletType {
    PLAYER_BULLET(0.25f,10,1f, 0.5f,0.02f, CollisionTypeComponent.FRIENDLY, ImagesRes.bulletImage,true, BodyDef.BodyType.KinematicBody),
    ENEMY_BULLET(0.25f, 20, 10, 0.05f, 0.2f, CollisionTypeComponent.ENEMY, ImagesRes.enemyBulletImage,false, BodyDef.BodyType.DynamicBody),
    GHOST_BULLET(0.25f,10,10,0.1f,0.2f,CollisionTypeComponent.ENEMY,ImagesRes.enemyBulletImage,true, BodyDef.BodyType.KinematicBody);
    public float radius;
    public float damage;
    public int type;
    public float bulletSpeed;
    public float bulletTimer;
    public TextureRegion region;
    public float bulletDamageTick;
    public boolean piercing;
    public BodyDef.BodyType bodyType;

    private BulletType(float radius, float damage,float bulletTimer,float bulletSpeed, float bulletDamageTick,int type,
                       TextureRegion region, boolean piercing, BodyDef.BodyType bodyType){
        this.radius = radius;
        this.damage = damage;
        this.bulletTimer = bulletTimer;
        this.bulletSpeed = bulletSpeed;
        this.bulletDamageTick = bulletDamageTick;
        this.type = type;
        this.region = region;
        this.piercing = piercing;
        this.bodyType = bodyType;
    }
}
