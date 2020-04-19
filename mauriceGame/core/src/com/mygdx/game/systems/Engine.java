package com.mygdx.game.systems;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.core.PooledEngine;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;

import com.badlogic.gdx.utils.viewport.Viewport;
import com.mygdx.game.components.*;
import com.mygdx.game.game.Tile;
import com.mygdx.game.gamestates.StateChangeListener;

import java.util.Comparator;


public class Engine extends PooledEngine {
    private Array<Entity> renderQueue;
    private ImmutableArray<Entity> entityArray;

    private Comparator<Entity> comparator;
    private ComponentMapper<TextureComponent> textureM = ComponentMapper.getFor(TextureComponent.class);
    private ComponentMapper<PositionComponent> positionM = ComponentMapper.getFor(PositionComponent.class);
    private ComponentMapper<HealthComponent> healthM = ComponentMapper.getFor(HealthComponent.class);

    private Viewport viewport;
    private SpriteBatch batch;
    private Vector2 scaleVector;

    private StateChangeListener stateChangeListener;


    public Engine(Viewport viewport, SpriteBatch batch, StateChangeListener stateChangeListener) {
        super();
        this.viewport = viewport;
        this.batch = batch;
        comparator = new ZComparator();
        this.stateChangeListener = stateChangeListener;
    }

    public void render() {
        //put all entities in renderqueue and sort it after the z variable in position
        //alternative render map then enemies then player.

        CalculateScale();
        renderHealthBars(); // this was healthBars doesn't cover anything.
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
            /*if(entity.getComponent(HealthComponent.class) != null){
                HealthComponent healthComponent = healthM.get(entity);
                if(healthM.get(entity).health <= 0) {
                    if (entity.getComponent(PlayerComponent.class) == null) {
                        this.removeEntity(entity);
                    }
                }
                float healthWidth = healthComponent.healthWidth;
                float healthHeight = healthComponent.healthHeight;
                batch.draw(healthComponent.region,
                        newCoords.x - (healthWidth/2)*scaleVector.x , newCoords.y + (height / 2) * scaleVector.y + healthHeight*scaleVector.y,
                        scaleVector.x * healthComponent.healthWidth * healthComponent.health/healthComponent.maxHealth, scaleVector.y * healthComponent.healthHeight);
            }*/
        }
        batch.end();
        renderQueue.clear();
    }

    private void renderHealthBars() {
        renderQueue = new Array<>();
        entityArray = getEntitiesFor(Family.all(PositionComponent.class, TextureComponent.class,HealthComponent.class).get());
        for (Entity entity : entityArray) {
            if(healthM.get(entity).health <= 0){
                if(entity.getComponent(PlayerComponent.class) == null) {
                    this.removeEntity(entity);
                }
            }else {
                if (!healthM.get(entity).hidden) renderQueue.add(entity);
            }
        }
        renderQueue.sort(comparator);
        batch.begin();
        for (Entity entity : renderQueue) {

            Vector3 position = positionM.get(entity).position;
            TextureRegion region = textureM.get(entity).region;
            HealthComponent healthComponent = healthM.get(entity);


            float width = healthComponent.healthWidth;
            float height = healthComponent.healthHeight;

            Vector2 newCoords = viewport.project(new Vector2(position.x * Tile.tileSize, position.y * Tile.tileSize));
            batch.draw(healthComponent.region,
                    newCoords.x - (width/2)*scaleVector.x , newCoords.y + (region.getRegionHeight() / 2) * scaleVector.y + height*scaleVector.y,
                    scaleVector.x * healthComponent.healthWidth * healthComponent.health/healthComponent.maxHealth, scaleVector.y * healthComponent.healthHeight);

        }
        batch.end();
        renderQueue.clear();

    }

    private void CalculateScale() {
        Vector2 scaleCoords1 = viewport.project(new Vector2(1, 1));
        Vector2 scaleCoords2 = viewport.project(new Vector2(2, 2));

        float ogWidth = 1 - 2;
        float newWidth = scaleCoords1.x - scaleCoords2.x;
        float ogHeight = 1 - 2;
        float newHeight = scaleCoords1.y - scaleCoords2.y;

        float scaleX = Math.abs(newWidth / ogWidth);
        float scaleY = Math.abs(newHeight / ogHeight);
        scaleVector = new Vector2(scaleX, scaleY);
    }
}

