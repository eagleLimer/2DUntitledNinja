package com.mygdx.game.systems;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.core.PooledEngine;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.tiled.TiledMapTile;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Base64Coder;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.mygdx.game.components.*;
import com.mygdx.game.game.*;
import com.mygdx.game.gameData.EngineData;
import com.mygdx.game.gameData.EntityData;

import java.util.Comparator;

import static com.badlogic.gdx.net.HttpRequestBuilder.json;

public class Engine extends PooledEngine {
    private Array<Entity> renderQueue;
    private ImmutableArray<Entity> entityArray;
    private Array<BulletInfo> newBullets;
    private Array<Entity> toBeRemoved;

    private Comparator<Entity> comparator;
    private ComponentMapper<TextureComponent> textureM = ComponentMapper.getFor(TextureComponent.class);
    private ComponentMapper<PositionComponent> positionM = ComponentMapper.getFor(PositionComponent.class);
    private ComponentMapper<HealthComponent> healthM = ComponentMapper.getFor(HealthComponent.class);
    private ComponentMapper<TypeComponent> typeM = ComponentMapper.getFor(TypeComponent.class);
    private ComponentMapper<PlayerComponent> playerM = ComponentMapper.getFor(PlayerComponent.class);

    private World world;
    private EntityCreator creator;
    private int playerStartX = 3;
    private int playerStartY = 3;

    public Engine(World world) {
        super();
        this.world = world;
        comparator = new ZComparator();
        newBullets = new Array<>();
        toBeRemoved = new Array<>();
        creator = new EntityCreator(this,world);
    }

    @Override
    public void update(float deltaTime){
        super.update(deltaTime);
        for (BulletInfo bulletInfo:newBullets) {
            creator.createBullet(bulletInfo);
        }
        newBullets.clear();
        removeOldEntities();
    }
    public void removeOldEntities(){
        for (Entity entity: toBeRemoved) {
            this.removeEntity(entity);
            if (entity != null) {
            }
        }
        toBeRemoved.clear();
    }

    public void render(SpriteBatch batch) {
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

            float originX = width / 2;
            float originY = height / 2;

            batch.draw(region,
                    position.x * Tile.tileSize - originX, position.y * Tile.tileSize - originY,
                    originX, originY,
                    width, height,
                    1, 1,
                    positionM.get(entity).rotation);
        }
        batch.end();
        renderQueue.clear();
    }

    private void renderHealthBars(SpriteBatch batch) {
        renderQueue = new Array<>();
        entityArray = getEntitiesFor(Family.all(PositionComponent.class, TextureComponent.class, HealthComponent.class).get());
        for (Entity entity : entityArray) {
            if (healthM.get(entity).health <= 0) {
                if (playerM.get(entity) == null) {
                    toBeRemoved.add(entity);
                }
            } else {
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

            float originX = width / 2;
            float originY = height / 2;

            batch.draw(healthComponent.region,
                    position.x * Tile.tileSize - originX, position.y * Tile.tileSize + (region.getRegionHeight() / 2) + height,
                    originX * healthComponent.health / healthComponent.maxHealth, originY,
                    healthComponent.healthWidth * healthComponent.health / healthComponent.maxHealth, healthComponent.healthHeight,
                    1, 1,
                    0);
        }
        batch.end();
        renderQueue.clear();
    }

    public void createSystems(KeyboardController controller, Viewport viewport) {
        PlayerCollisionSystem collisionSystem = new PlayerCollisionSystem();
        PlayerControlSystem playerControlSystem = new PlayerControlSystem(controller);
        PhysicsSystem physicsSystem = new PhysicsSystem(world);
        AnimationSystem animationSystem = new AnimationSystem();
        MyEntityListener entityListener = new MyEntityListener(world);
        PlayerPowerSystem powerSystem = new PlayerPowerSystem(world,controller,viewport);

        this.addSystem(new DamageSystem());
        this.addEntityListener(entityListener);
        this.addSystem(new ShootingSystem(newBullets));
        this.addSystem(new BulletCollisionSystem(toBeRemoved));
        this.addSystem(new HealthSystem());
        this.addSystem(new EnergySystem());
        this.addSystem(powerSystem);
        this.addSystem(collisionSystem);
        this.addSystem(playerControlSystem);
        this.addSystem(physicsSystem);
        this.addSystem(animationSystem);
    }

    public void addMapToEngine(Map map) {
        BodyCreator bodyCreator = new BodyCreator(world);
        BodyComponent bodyComponent = this.createComponent(BodyComponent.class);
        PositionComponent position = this.createComponent(PositionComponent.class);
        TypeComponent type = this.createComponent(TypeComponent.class);

        for (int col = 0; col < map.getMapHeight(); col++) {
            for (int row = 0; row < map.getMapWidth(); row++) {
                TiledMapTile tile = map.getCell(row * Tile.tileSize, col * Tile.tileSize, Map.COLLISION_LAYER_NAME).getTile();

                if (tile != null) {
                    /*TextureComponent texture = createComponent(TextureComponent.class);
                    texture.region = tile.getTextureRegion();
                    mapTile.add(texture);*/
                    Entity mapTile = createEntity();
                    type.type = TypeComponent.SCENERY;
                    position.position.set(row, col, 0);
                    bodyComponent.body = bodyCreator.makeRectBody(position.position.x + 0.5f, position.position.y + 0.5f, 1, 1, BodyMaterial.METAL,
                            BodyDef.BodyType.StaticBody, true);
                    bodyComponent.body.setUserData(mapTile);

                    mapTile.add(type);
                    mapTile.add(position);
                    mapTile.add(bodyComponent);
                    this.addEntity(mapTile);
                }
            }
        }
    }

    public void loadFromFile(String fileName) {
        FileHandle fileHandle = Gdx.files.local(fileName);
        EngineData engineData = json.fromJson(EngineData.class, Base64Coder.decodeString(fileHandle.readString()));
        playerStartX = engineData.getPlayerStartX();
        playerStartY = engineData.getPlayerStartY();
        EntityData[] entityDataList = engineData.getEntitiesDataList();
        for (EntityData entityData : entityDataList) {
            creator.createEntity(entityData.getId(), entityData.getxPos(), entityData.getyPos());
        }
    }

    public void saveToFile(String fileName) {
        FileHandle fileHandle = Gdx.files.local(fileName);
        EngineData engineData = new EngineData();
        engineData.setPlayerStartX(playerStartX);
        engineData.setPlayerStartY(playerStartY);
        ImmutableArray<Entity> entityDataArray = getEntitiesFor(Family.all(TypeComponent.class, PositionComponent.class).get());
        //check important length of entityDataArray
        int index = 0;
        for (Entity entity : entityDataArray) {
            TypeComponent type = typeM.get(entity);
            if(type.type != TypeComponent.SCENERY && type.type != TypeComponent.PLAYER){
                index++;
            }
        }
        engineData.setEntitiesLength(entityDataArray.size());
        EntityData[] entityDataList = new EntityData[index];
        index = 0;
        for (Entity entity : entityDataArray) {
            TypeComponent typeComponent = typeM.get(entity);
            if(typeComponent.type != TypeComponent.SCENERY && typeComponent.type != TypeComponent.PLAYER) {
                Vector3 position = positionM.get(entity).position;
                entityDataList[index] = new EntityData();
                entityDataList[index].setId(typeComponent.type);
                entityDataList[index].setxPos(position.x);
                entityDataList[index].setyPos(position.y);
                index++;
            }
        }
        engineData.setEntitiesDataList(entityDataList);
        fileHandle.writeString(Base64Coder.encodeString(json.toJson(engineData)), false);
    }

    public float getPlayerStartX() {
        return playerStartX;
    }
    public float getPlayerStartY() {
        return playerStartY;
    }

    public void scheduleForRemoval(Entity entity) {
        toBeRemoved.add(entity);
    }
}

