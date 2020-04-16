package com.mygdx.game.resources;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class AnimationsRes {
    public Animation playerRight;
    public Animation playerLeft;
    public Animation playerNormal;
    public Animation playerFalling;
    public Animation playerJumping;

    public AnimationsRes() {
        loadAnimations();
    }

    private void loadAnimations() {
        loadPlayerAnimations();
    }

    private void loadPlayerAnimations() {
        Texture walkSheet = new Texture(Gdx.files.internal("epicRun.png"));
        int frame_cols = 8;
        int frame_rows = 1;
        float frameTime = 0.025f;
        playerRight = createAnimation(walkSheet, frame_rows, frame_cols, frameTime);
        playerLeft = createFlippedAnimation(walkSheet, frame_rows, frame_cols, frameTime);

        walkSheet = new Texture(Gdx.files.internal("faling.png"));
        frame_cols = 1;
        frame_rows = 1;
        frameTime = 1f;
        playerFalling = createAnimation(walkSheet, frame_rows, frame_cols, frameTime);

        walkSheet = new Texture(Gdx.files.internal("normal.png"));
        frame_cols = 1;
        frame_rows = 1;
        frameTime = 1f;
        playerNormal = createAnimation(walkSheet, frame_rows, frame_cols, frameTime);

        walkSheet = new Texture(Gdx.files.internal("jumping.png"));
        frame_cols = 1;
        frame_rows = 1;
        frameTime = 1f;
        playerJumping = createAnimation(walkSheet, frame_rows, frame_cols, frameTime);


    }

    private Animation createAnimation(Texture sheet, int frame_rows, int frame_cols, float frameTime) {
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

    private Animation createFlippedAnimation(Texture sheet, int frame_rows, int frame_cols, float frameTime) {
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
