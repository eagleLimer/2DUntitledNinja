package com.mygdx.game.resources;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class ImagesRes {
    public TextureRegion playerImage;
    public TextureRegion entityImage;
    public TextureRegion backgroundImage;
    public TextureRegion bossImage;


    public ImagesRes(){
        loadImages();
    }

    private void loadImages() {
        Texture tmpTex = new Texture(Gdx.files.internal("epicRun.png"));
        playerImage = new TextureRegion(tmpTex,0,0,64,64);

        tmpTex = new Texture(Gdx.files.internal("charLeft.png"));
        entityImage = new TextureRegion(tmpTex,0,0,32,32);

        tmpTex = new Texture(Gdx.files.internal("meerkat.jpg"));
        backgroundImage = new TextureRegion(tmpTex,0,0,1920,1080);

        tmpTex = new Texture(Gdx.files.internal("boss.png"));
        bossImage = new TextureRegion(tmpTex, 0,0,256,256);
    }
}
