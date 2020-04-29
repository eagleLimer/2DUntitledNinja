package com.mygdx.game.game;


import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.QueryCallback;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.mygdx.game.components.*;
import com.mygdx.game.gamestates.StateChangeListener;
import com.mygdx.game.systems.*;

public class Level {
    private static final float GRAVITY = -9.8f * 2.5f;
    private static final String ENGINE_FILE_NAME = "/Engine/";
    public static final String MAP_FILE_NAME = "/Map/";
    private String currentPortalName;
    private OrthogonalTiledMapRenderer renderer;
    public Map map;
    private String levelName;
    private LevelManager levelManager;
    private World world;
    private Engine engine;
    private Entity player;
    private float playerXPos;
    private float playerYPos;
    private EntityCreator creator;
    private ComponentMapper<HealthComponent> healthM = ComponentMapper.getFor(HealthComponent.class);


    //new Level constructor.
    public Level(String fileName, int mapWidth, int mapHeight, KeyboardController controller, Viewport viewport, LevelManager levelManager) {
        levelName = fileName;
        map = new Map();
        map.newMap(mapWidth, mapHeight);
        createLevel(controller, viewport, levelManager);
    }

    //load level constructor
    public Level(String fileName, KeyboardController controller, Viewport viewport, LevelManager levelManager) {
        levelName = fileName;
        map = new Map();
        map.loadMap(fileName + MAP_FILE_NAME);

        createLevel(controller, viewport, levelManager);
        engine.loadFromFile(fileName + ENGINE_FILE_NAME);

    }

    public void createLevel(KeyboardController controller, Viewport viewport, LevelManager levelManager) {
        renderer = new OrthogonalTiledMapRenderer(map);
        this.levelManager = levelManager;
        world = new World(new Vector2(0, GRAVITY), true);
        world.setContactListener(new MyContactListener());
        engine = new Engine(world, levelManager);
        engine.createSystems(controller, viewport);
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
    public void editUpdate(float step) {
        engine.removeOldEntities();
    }

        public float getPlayerXpos() {
        return player.getComponent(PositionComponent.class).position.x * Tile.tileSize;
    }
    public float getPlayerYpos() {
        return player.getComponent(PositionComponent.class).position.y * Tile.tileSize;
    }

    public void createEntity(float mouseX, float mouseY, int currentEntityId) {
        if(currentEntityId == TypeComponent.LEVEL_SENSOR){
            creator.createLevelSensor(mouseX, mouseY, currentPortalName);
        }else {
            creator.createEntity(currentEntityId, mouseX, mouseY);
        }
    }

    public void removeEntity(float mouseX, float mouseY) {
        QueryCallback mycallBack = new QueryCallback() {
            @Override
            public boolean reportFixture(Fixture fixture) {
                if(fixture.getBody().getUserData() instanceof Entity){
                    Entity entity = (Entity) fixture.getBody().getUserData();
                    engine.scheduleForRemoval(entity);
                }
             return false;
            }
        };
        world.QueryAABB(mycallBack, mouseX-0.1f, mouseY-0.1f,mouseX+0.1f,mouseY+0.1f);
        //engine.getSystem(PhysicsSystem.class).update(0);
    }

    public void renderUi(OrthographicCamera camera, SpriteBatch batch) {
        HealthComponent healthComponent = player.getComponent(HealthComponent.class);
        EnergyComponent energyComponent = player.getComponent(EnergyComponent.class);
        EnergyBarComponent energyBarComponent = player.getComponent(EnergyBarComponent.class);

        TextureRegion region = healthComponent.region;
        float width = healthComponent.healthWidth*2;
        float height = healthComponent.healthHeight*2;

        float originX = width / 2;
        float originY = height / 2;
        renderBar(batch, width, height, originX, originY, region, healthComponent.health, healthComponent.maxHealth, MyGdxGame.worldHeight-100);
        region = energyBarComponent.region;
        width = energyBarComponent.energyWidth*2;
        height = energyBarComponent.energyHeight*2;

        originX = width / 2;
        originY = height / 2;
        renderBar(batch, width, height, originX, originY, region, energyComponent.mana, energyComponent.maxMana, MyGdxGame.worldHeight-150);
        /*batch.begin();
        batch.draw(healthComponent.region,
                100, MyGdxGame.worldHeight-100,
                originX * healthComponent.health / healthComponent.maxHealth, originY,
                healthWidth * healthComponent.health / healthComponent.maxHealth, healthHeight,
                1, 1,
                0);
        batch.end();*/
    }

    private void renderBar(SpriteBatch batch, float width, float height, float originX, float originY, TextureRegion region, float currentValue, float maxValue, float yLoc) {
        batch.begin();
        batch.draw(region,
                100, yLoc,
                originX * currentValue / maxValue, originY,
                width * currentValue / maxValue, height,
                1, 1,
                0);
        batch.end();
    }

    public String getCurrentPortalName() {
        return currentPortalName;
    }

    public void setCurrentPortalName(String currentPortalName) {
        this.currentPortalName = currentPortalName;
    }
}
