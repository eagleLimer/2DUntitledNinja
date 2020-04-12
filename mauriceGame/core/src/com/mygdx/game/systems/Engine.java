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

    public void render(){
        //put all entities in renderqueue and sort it after the z variable in position
        //alterantive render map then enemies then player.

        renderQueue = new Array<>();
        entityArray = getEntitiesFor(Family.all(PositionComponent.class, TextureComponent.class).get());
        for (Entity entity : entityArray) {
            renderQueue.add(entity);
        }
        renderQueue.sort(comparator);
        batch.begin();
        for (Entity entity : renderQueue){
            TextureRegion region = textureM.get(entity).region;
            Vector3 position = positionM.get(entity).position;
            Vector2 vector2 = viewport.project(new Vector2(position.x* Tile.tileSize, position.y*Tile.tileSize));
            batch.draw(region, vector2.x ,vector2.y);
        }
        batch.end();
        renderQueue.clear();
    }
}

