package com.mygdx.game.resources;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class ImagesRes {
    public static final String ENTITY_PATH = "entityImages/";
    public static TextureRegion playerImage;
    public static TextureRegion entityImage;
    public static TextureRegion backgroundImage;
    public static TextureRegion bossImage;
    public static TextureRegion rockImage;
    public static TextureRegion skyImage;
    public static TextureRegion hillsImage;
    public static TextureRegion bulletImage;
    public static TextureRegion eraserImage;
    public static TextureRegion levelImage;
    public static TextureRegion enemyBulletImage;
    public static TextureRegion plantImage;
    public static TextureRegion ghostImage;
    public static TextureRegion coinImage;

    public static void loadImages() {
        Texture tmpTex = new Texture(Gdx.files.internal(ENTITY_PATH+"normal.png"));
        playerImage = new TextureRegion(tmpTex, 0, 0, 64, 64);

        tmpTex = new Texture(Gdx.files.internal(ENTITY_PATH+"charLeft.png"));
        entityImage = new TextureRegion(tmpTex, 0, 0, 32, 32);

        tmpTex = new Texture(Gdx.files.internal("brightBackground.png"));
        backgroundImage = new TextureRegion(tmpTex, 0, 0, 653, 490);

        tmpTex = new Texture(Gdx.files.internal(ENTITY_PATH+"boss.png"));
        bossImage = new TextureRegion(tmpTex, 0, 0, 256, 256);

        tmpTex = new Texture(Gdx.files.internal(ENTITY_PATH+"smolRock.png"));
        rockImage = new TextureRegion(tmpTex, 0, 0, 16, 16);

        tmpTex = new Texture(Gdx.files.internal("skyParallax.jpg"));
        skyImage = new TextureRegion(tmpTex, 0, 0, 1663, 1060);

        tmpTex = new Texture(Gdx.files.internal("smoothHills.png"));
        hillsImage = new TextureRegion(tmpTex, 0, 0, 1280, 720);

        tmpTex = new Texture(Gdx.files.internal(ENTITY_PATH+"bullet.png"));
        bulletImage = new TextureRegion(tmpTex, 0, 0, 16, 16);

        tmpTex = new Texture(Gdx.files.internal("eraser.png"));
        eraserImage = new TextureRegion(tmpTex, 0,0,32,32);

        tmpTex = new Texture(Gdx.files.internal(ENTITY_PATH+"levelSensor.png"));
        levelImage = new TextureRegion(tmpTex,0,0,96,128);

        tmpTex = new Texture(Gdx.files.internal(ENTITY_PATH+"enemyBullet.png"));
        enemyBulletImage = new TextureRegion(tmpTex,0,0,16,16);

        tmpTex = new Texture(Gdx.files.internal(ENTITY_PATH+"plant.png"));
        plantImage = new TextureRegion(tmpTex,0,0,64,64);

        tmpTex = new Texture(Gdx.files.internal(ENTITY_PATH+"Red Bullet.png"));
        ghostImage = new TextureRegion(tmpTex,0,0,64,64);

        tmpTex = new Texture(Gdx.files.internal(ENTITY_PATH+"coin.png"));
        coinImage = new TextureRegion(tmpTex,0,0,16,16);
    }
}
