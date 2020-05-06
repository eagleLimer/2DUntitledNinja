package com.mygdx.game.resources;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;

import com.badlogic.gdx.graphics.g2d.TextureRegion;

import static com.mygdx.game.resources.ImagesRes.ENTITY_PATH;

public class AnimationsRes {
    public static Animation playerRight;
    public static Animation playerLeft;
    public static Animation playerNormal;
    public static Animation playerFalling;
    public static Animation playerJumping;
    public static Animation playerDeath;
    public static Animation plantAni;
    public static Animation dabAni;


    public static void loadAnimations() {
        loadPlayerAnimations();
    }

    private static void loadPlayerAnimations() {
        Texture walkSheet = new Texture(Gdx.files.internal(ENTITY_PATH+"epicRun.png"));
        int frame_cols = 8;
        int frame_rows = 1;
        float frameTime = 0.025f;
        playerRight = createAnimation(walkSheet, frame_rows, frame_cols, frameTime);
        playerLeft = createFlippedAnimation(walkSheet, frame_rows, frame_cols, frameTime);

        walkSheet = new Texture(Gdx.files.internal(ENTITY_PATH+"fallingAni.png"));
        frame_cols = 5;
        frame_rows = 1;
        frameTime = 0.2f;
        playerFalling = createAnimation(walkSheet, frame_rows, frame_cols, frameTime);

        walkSheet = new Texture(Gdx.files.internal(ENTITY_PATH+"normalAni3.png"));
        frame_cols = 5;
        frame_rows = 1;
        frameTime = 0.05f;
        playerNormal = createAnimation(walkSheet, frame_rows, frame_cols, frameTime);

        walkSheet = new Texture(Gdx.files.internal(ENTITY_PATH+"jumping.png"));
        frame_cols = 5;
        frame_rows = 1;
        frameTime = 0.2f;
        playerJumping = createAnimation(walkSheet, frame_rows, frame_cols, frameTime);

        walkSheet = new Texture(Gdx.files.internal(ENTITY_PATH+"deathAni.png"));
        frame_cols = 16;
        frame_rows = 1;
        frameTime = 0.1f;
        playerDeath = createAnimation(walkSheet, frame_rows, frame_cols, frameTime);

        walkSheet = new Texture(Gdx.files.internal(ENTITY_PATH+"plantAni.png"));
        frame_cols = 2;
        frame_rows = 1;
        frameTime = 0.2f;
        plantAni = createAnimation(walkSheet, frame_rows, frame_cols, frameTime);

        walkSheet = new Texture(Gdx.files.internal(ENTITY_PATH+"dabAni.png"));
        frame_cols = 2;
        frame_rows = 1;
        frameTime = 0.5f;
        dabAni = createAnimation(walkSheet, frame_rows, frame_cols, frameTime);

    }

    private static Animation createAnimation(Texture sheet, int frame_rows, int frame_cols, float frameTime) {
        TextureRegion[][] tmp = TextureRegion.split(sheet, sheet.getWidth() / frame_cols, sheet.getHeight() / frame_rows);
        TextureRegion[] walkFrames = new TextureRegion[frame_cols * frame_rows];
        int index = 0;
        for (int i = 0; i < frame_rows; i++) {
            for (int j = 0; j < frame_cols; j++) {
                walkFrames[index++] = tmp[i][j];
            }
        }
        return new Animation<TextureRegion>(frameTime, walkFrames);
    }

    private static Animation createFlippedAnimation(Texture sheet, int frame_rows, int frame_cols, float frameTime) {
        TextureRegion[][] tmp = TextureRegion.split(sheet, sheet.getWidth() / frame_cols, sheet.getHeight() / frame_rows);
        TextureRegion[] walkFrames = new TextureRegion[frame_cols * frame_rows];
        int index = 0;
        for (int i = 0; i < frame_rows; i++) {
            for (int j = 0; j < frame_cols; j++) {
                walkFrames[index] = tmp[i][j];
                walkFrames[index++].flip(true, false);
            }
        }
        return new Animation<TextureRegion>(frameTime, walkFrames);
    }
}
