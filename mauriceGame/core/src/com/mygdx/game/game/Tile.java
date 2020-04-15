package com.mygdx.game.game;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.MapObjects;
import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.maps.tiled.TiledMapTile;

public class Tile implements TiledMapTile {
    public static final int tileSize = 32;
    private int id = 0;
    private TextureRegion textureRegion = new TextureRegion();
    private BlendMode blendMode = BlendMode.ALPHA;
    //todo: ta bort collideable i guesss, collisionlayer blir simplare kanske ta bort hela denna klass? :)
    private boolean collideable = false;
    @Override
    public int getId() {
        return id;
    }

    @Override
    public void setId(int id) {
        this.id = id;
    }

    @Override
    public BlendMode getBlendMode() {
        return blendMode;
    }

    @Override
    public void setBlendMode(BlendMode newBlendMode) {
        blendMode = newBlendMode;
    }

    @Override
    public TextureRegion getTextureRegion() {
        return textureRegion;
    }

    @Override
    public void setTextureRegion(TextureRegion textureRegion) {
        this.textureRegion = textureRegion;
    }

    @Override
    public float getOffsetX() {
        return 0;
    }

    @Override
    public void setOffsetX(float offsetX) {

    }

    @Override
    public float getOffsetY() {
        return 0;
    }

    @Override
    public void setOffsetY(float offsetY) {

    }

    @Override
    public MapProperties getProperties() {
        return null;
    }

    @Override
    public MapObjects getObjects() {
        return null;
    }

    public boolean isCollideable() {
        return collideable;
    }

    public void setCollideable(boolean collideable) {
        this.collideable = collideable;
    }
}
