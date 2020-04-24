package com.mygdx.game.game;


import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.QueryCallback;
import com.badlogic.gdx.physics.box2d.World;
import com.mygdx.game.components.*;
import com.mygdx.game.gamestates.StateChangeListener;
import com.mygdx.game.systems.*;

public class Level {
    private static final float GRAVITY = -9.8f * 2.5f;
    private static final String ENGINE_FILE_NAME = "/Engine/";
    public static final String MAP_FILE_NAME = "/Map/";

    private OrthogonalTiledMapRenderer renderer;
    public Map map;
    private String levelName;
    private World world;
    private Engine engine;
    private Entity player;
    private float playerXPos;
    private float playerYPos;
    private EntityCreator creator;
    private ComponentMapper<HealthComponent> healthM = ComponentMapper.getFor(HealthComponent.class);


    //new Level constructor.
    public Level(String fileName, int mapWidth, int mapHeight, KeyboardController controller) {
        levelName = fileName;
        map = new Map();
        map.newMap(mapWidth, mapHeight);
        createLevel(controller);
    }

    //load level constructor
    public Level(String fileName, KeyboardController controller) {
        levelName = fileName;
        map = new Map();
        map.loadMap(fileName + MAP_FILE_NAME);

        createLevel(controller);
        engine.loadFromFile(fileName + ENGINE_FILE_NAME);

    }

    public void createLevel(KeyboardController controller) {
        renderer = new OrthogonalTiledMapRenderer(map);
        world = new World(new Vector2(0, GRAVITY), true);
        world.setContactListener(new MyContactListener());
        engine = new Engine(world);
        engine.createSystems(controller);
        engine.addMapToEngine(map);
        creator = new EntityCreator(engine, world);
        player = creator.createPlayer(engine.getPlayerStartX(), engine.getPlayerStartY());
        engine.addEntity(player);
        BasicEnemyMovement enemyMovement = new BasicEnemyMovement(player);
        engine.addSystem(enemyMovement);
        //if u create entity before u create player, playerCollisionSystem wont work with that entity??
    }

    public void saveLevel() {
        map.saveToFile(levelName + MAP_FILE_NAME);
        engine.saveToFile(levelName + ENGINE_FILE_NAME);
    }

    public void render(OrthographicCamera camera, SpriteBatch batch) {
        camera.update();
        renderer.setView(camera);
        renderer.render();
        batch.setProjectionMatrix(camera.combined);
        engine.render(batch);
    }


    public void dispose() {
        world.dispose();
    }

    public void update(float step, StateChangeListener stateChangeListener) {
        engine.update(step);
        playerXPos = player.getComponent(PositionComponent.class).position.x * Tile.tileSize;
        playerYPos = player.getComponent(PositionComponent.class).position.y * Tile.tileSize;
        if (player.getComponent(HealthComponent.class).health <= 0) {
            stateChangeListener.popState();
        }
    }

    public float getPlayerXpos() {
        return player.getComponent(PositionComponent.class).position.x * Tile.tileSize;
    }
    public float getPlayerYpos() {
        return player.getComponent(PositionComponent.class).position.y * Tile.tileSize;
    }

    public void createEntity(float mouseX, float mouseY, int currentEntityId) {
        creator.createEntity(currentEntityId,mouseX,mouseY);
    }

    public void removeEntity(float mouseX, float mouseY) {
        QueryCallback mycallBack = new QueryCallback() {
            @Override
            public boolean reportFixture(Fixture fixture) {
                if(fixture.getBody().getUserData() instanceof Entity){
                    Entity entity = (Entity) fixture.getBody().getUserData();
                    HealthComponent healthComponent = healthM.get(entity);
                    //todo: change this to some kind of removal listener.
                    if(healthComponent != null) {
                        healthComponent.health = 0;
                    }
                }
             return false;
            }
        };
        world.QueryAABB(mycallBack, mouseX-0.1f, mouseY-0.1f,mouseX+0.1f,mouseY+0.1f);
        //engine.getSystem(PhysicsSystem.class).update(0);
    }
}
