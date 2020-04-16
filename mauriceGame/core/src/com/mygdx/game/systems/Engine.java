package com.mygdx.game.systems;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.core.PooledEngine;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;

import com.badlogic.gdx.utils.viewport.Viewport;
import com.mygdx.game.components.PositionComponent;
import com.mygdx.game.components.TextureComponent;
import com.mygdx.game.game.Tile;

import java.util.Comparator;


public class Engine extends PooledEngine {
    private Array<Entity> renderQueue;
    private ImmutableArray<Entity> entityArray;

    private Comparator<Entity> comparator;
    private ComponentMapper<TextureComponent> textureM = ComponentMapper.getFor(TextureComponent.class);
    private ComponentMapper<PositionComponent> positionM = ComponentMapper.getFor(PositionComponent.class);

    private Viewport viewport;
    private SpriteBatch batch;


    public Engine(Viewport viewport, SpriteBatch batch) {
        super();
        this.viewport = viewport;
        this.batch = batch;
        comparator = new ZComparator();
    }

    public void render() {
        //put all entities in renderqueue and sort it after the z variable in position
        //alterantive render map then enemies then player.

        Vector2 scaleVector = CalculateScale();

        renderQueue = new Array<>();
        entityArray = getEntitiesFor(Family.all(PositionComponent.class, TextureComponent.class).get());
        for (Entity entity : entityArray) {
            renderQueue.add(entity);
        }
        renderQueue.sort(comparator);
        batch.begin();
        for (Entity entity : renderQueue) {
            TextureRegion region = textureM.get(entity).region;
            Vector3 position = positionM.get(entity).position;

            float width = region.getRegionWidth();
            float height = region.getRegionHeight();

            Vector2 newCoords = viewport.project(new Vector2(position.x * Tile.tileSize, position.y * Tile.tileSize));

            batch.draw(region,
                    newCoords.x - (width / 2) * scaleVector.x, newCoords.y - (height / 2) * scaleVector.y,
                    scaleVector.x * width, scaleVector.y * height);
        }


        batch.end();
        renderQueue.clear();
    }

    private Vector2 CalculateScale() {
        Vector2 scaleCoords1 = viewport.project(new Vector2(1, 1));
        Vector2 scaleCoords2 = viewport.project(new Vector2(2, 2));

        float ogWidth = 1 - 2;
        float newWidth = scaleCoords1.x - scaleCoords2.x;
        float ogHeight = 1 - 2;
        float newHeight = scaleCoords1.y - scaleCoords2.y;

        float scaleX = Math.abs(newWidth / ogWidth);
        float scaleY = Math.abs(newHeight / ogHeight);
        return new Vector2(scaleX, scaleY);
    }
}

