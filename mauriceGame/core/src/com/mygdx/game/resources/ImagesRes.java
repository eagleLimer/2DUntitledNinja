package com.mygdx.game.resources;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class ImagesRes {
    public TextureRegion playerImage;
    public TextureRegion entityImage;
    public TextureRegion backgroundImage;
    public TextureRegion bossImage;
    public TextureRegion rockImage;
    public TextureRegion skyImage;
    public TextureRegion hillsImage;

    public ImagesRes() {
        loadImages();
    }

    private void loadImages() {
        Texture tmpTex = new Texture(Gdx.files.internal("epicRun.png"));
        playerImage = new TextureRegion(tmpTex, 0, 0, 64, 64);

        tmpTex = new Texture(Gdx.files.internal("charLeft.png"));
        entityImage = new TextureRegion(tmpTex, 0, 0, 32, 32);

        tmpTex = new Texture(Gdx.files.internal("brightBackground.png"));
        backgroundImage = new TextureRegion(tmpTex, 0, 0, 653, 490);

        tmpTex = new Texture(Gdx.files.internal("boss.png"));
        bossImage = new TextureRegion(tmpTex, 0, 0, 256, 256);

        tmpTex = new Texture(Gdx.files.internal("smolRock.png"));
        rockImage = new TextureRegion(tmpTex, 0, 0, 16, 16);

        tmpTex = new Texture(Gdx.files.internal("skyParallax.jpg"));
        skyImage = new TextureRegion(tmpTex, 0, 0, 1663, 1060);

        tmpTex = new Texture(Gdx.files.internal("smoothHills.png"));
        hillsImage = new TextureRegion(tmpTex, 0, 0, 1280, 720);
    }
}
