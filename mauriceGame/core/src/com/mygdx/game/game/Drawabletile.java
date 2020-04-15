package com.mygdx.game.game;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;

public class Drawabletile implements Drawable {
    private TextureRegion textureRegion;

    public Drawabletile(TextureRegion textureRegion) {
        super();
        this.textureRegion = textureRegion;
    }

    @Override
    public void draw(Batch batch, float x, float y, float width, float height) {
        batch.draw(textureRegion,x,y,width,height);
    }

    @Override
    public float getLeftWidth() {
        return 32;
    }

    @Override
    public void setLeftWidth(float leftWidth) {

    }

    @Override
    public float getRightWidth() {
        return 32;
    }

    @Override
    public void setRightWidth(float rightWidth) {

    }

    @Override
    public float getTopHeight() {
        return 32;
    }

    @Override
    public void setTopHeight(float topHeight) {

    }

    @Override
    public float getBottomHeight() {
        return 32;
    }

    @Override
    public void setBottomHeight(float bottomHeight) {

    }

    @Override
    public float getMinWidth() {
        return 32;
    }

    @Override
    public void setMinWidth(float minWidth) {

    }

    @Override
    public float getMinHeight() {
        return 32;
    }

    @Override
    public void setMinHeight(float minHeight) {

    }
}
