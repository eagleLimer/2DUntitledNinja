package com.mygdx.game.systems;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.core.PooledEngine;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;

import com.badlogic.gdx.utils.viewport.Viewport;
import com.mygdx.game.components.*;

import com.mygdx.game.game.Tile;

import java.util.Comparator;


public class Engine extends PooledEngine {
    private Array<Entity> renderQueue;
    private ImmutableArray<Entity> entityArray;

    private Comparator<Entity> comparator;
    private ComponentMapper<TextureComponent> textureM = ComponentMapper.getFor(TextureComponent.class);
    private ComponentMapper<PositionComponent> positionM = ComponentMapper.getFor(PositionComponent.class);
    private ComponentMapper<HealthComponent> healthM = ComponentMapper.getFor(HealthComponent.class);

    private Viewport viewport;
    private Vector2 scaleVector;
    //OrthographicCamera camera;

    public Engine(Viewport viewport) {
        super();
        this.viewport = viewport;
        comparator = new ZComparator();
        //this.camera = (OrthographicCamera) viewport.getCamera();
        //batch.setProjectionMatrix(camera.combined);
    }

    public void render(OrthographicCamera camera, SpriteBatch batch) {
        //put all entities in renderqueue and sort it after the z variable in position
        //alternative render map then enemies then player.
        //CalculateScale();
        renderHealthBars(batch); // this way healthBars doesn't cover anything.
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

            float originX = width/2;
            float originY = height/2;

            //float scaleX = viewport.get/viewport.getWorldWidth();
            //float scaleY = viewport.getScreenHeight()/viewport.getWorldHeight();
            //batch.draw(region, position.x*Tile.tileSize - originX, position.y*Tile.tileSize - originY); //this works fine however got no rotation.
            batch.draw(region,
                    position.x*Tile.tileSize-originX, position.y*Tile.tileSize - originY,
                    originX, originY,
                     width,  height,
                    1,1,
                    positionM.get(entity).rotation);
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

    private void renderHealthBars(SpriteBatch batch) {
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

            float originX = width/2;
            float originY = height/2;

            batch.draw(healthComponent.region,
                    position.x*Tile.tileSize - originX, position.y*Tile.tileSize + (region.getRegionHeight() / 2) + height,
                    originX * healthComponent.health/healthComponent.maxHealth, originY,
                    healthComponent.healthWidth*healthComponent.health/healthComponent.maxHealth, healthComponent.healthHeight,
                    1,1,
                    0);

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

    public void setViewport(Viewport viewport) {
        this.viewport = viewport;
    }
}

