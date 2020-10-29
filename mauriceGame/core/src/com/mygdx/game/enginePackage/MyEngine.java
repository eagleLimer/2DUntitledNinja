package com.mygdx.game.enginePackage;

import com.badlogic.ashley.core.*;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.tiled.TiledMapTile;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Base64Coder;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.mygdx.game.enginePackage.components.BasicComponents.*;
import com.mygdx.game.enginePackage.components.combatComponents.HealthBarComponent;
import com.mygdx.game.enginePackage.components.combatComponents.HealthComponent;
import com.mygdx.game.enginePackage.components.enemyComponents.EnemyRarityComponent;
import com.mygdx.game.enginePackage.components.playerComponents.PlayerComponent;
import com.mygdx.game.enginePackage.systems.*;
import com.mygdx.game.enginePackage.systems.enemySystems.AggressiveSystem;
import com.mygdx.game.enginePackage.systems.enemySystems.BasicEnemyMovement;
import com.mygdx.game.enginePackage.systems.enemySystems.BasicEnemyShooterSystem;
import com.mygdx.game.enginePackage.systems.enemySystems.GhostMovementSystem;
import com.mygdx.game.game.*;
import com.mygdx.game.gameData.EngineData;
import com.mygdx.game.gameData.EntityData;
import com.mygdx.game.gameData.LevelSensorData;

import java.util.Comparator;

import static com.badlogic.gdx.net.HttpRequestBuilder.json;
import static com.mygdx.game.enginePackage.Constants.CATEGORY_SCENERY;
import static com.mygdx.game.enginePackage.Constants.RENDERUNITS_PER_METER;

public class MyEngine extends Engine {
    private static final float CHAR_SIZE = 10;
    private Array<Entity> renderQueue;
    private ImmutableArray<Entity> entityArray;
    private Array<BulletInfo> newBullets;
    private Array<Entity> toBeRemoved;

    private Comparator<Entity> comparator;
    private ComponentMapper<TextureComponent> textureM = ComponentMapper.getFor(TextureComponent.class);
    private ComponentMapper<PositionComponent> positionM = ComponentMapper.getFor(PositionComponent.class);
    private ComponentMapper<HealthComponent> healthM = ComponentMapper.getFor(HealthComponent.class);
    private ComponentMapper<CollisionTypeComponent> typeM = ComponentMapper.getFor(CollisionTypeComponent.class);
    private ComponentMapper<EntityTypeComponent> entityM = ComponentMapper.getFor(EntityTypeComponent.class);
    private ComponentMapper<PlayerComponent> playerM = ComponentMapper.getFor(PlayerComponent.class);
    private ComponentMapper<LevelSensorComponent> levelM = ComponentMapper.getFor(LevelSensorComponent.class);
    private ComponentMapper<HealthBarComponent> healthBarM = ComponentMapper.getFor(HealthBarComponent.class);
    private ComponentMapper<AnimationComponent> animationM = ComponentMapper.getFor(AnimationComponent.class);
    private ComponentMapper<NameComponent> nameM = ComponentMapper.getFor(NameComponent.class);
    private ComponentMapper<EnemyRarityComponent> rarityM = ComponentMapper.getFor(EnemyRarityComponent.class);

    private LevelManager levelManager;
    private World world;
    private EntityCreator creator;
    private int playerStartX = 3;
    private int playerStartY = 3;

    public MyEngine(World world, LevelManager levelManager) {
        this.levelManager = levelManager;
        this.world = world;
        comparator = new ZComparator();
        newBullets = new Array<>();
        toBeRemoved = new Array<>();
        creator = new EntityCreator(this, world);
    }

    @Override
    public void update(float deltaTime) {
        super.update(deltaTime);
        for (BulletInfo bulletInfo : newBullets) {
            creator.createBullet(bulletInfo);
        }
        newBullets.clear();
        removeOldEntities();
    }

    public void removeOldEntities() {
        for (Entity entity : toBeRemoved) {
            if (entity != null && playerM.get(entity)==null) {
                if(animationM.get(entity)!= null && animationM.get(entity).animationMap.get(StateComponent.STATE_DEAD) != null) {
                    PositionComponent positionComponent = positionM.get(entity);
                    if(positionComponent != null) {
                        creator.createDeathAni(positionComponent.position, animationM.get(entity).animationMap.get(StateComponent.STATE_DEAD));
                    }
                }
                if(typeM.get(entity)!=null){
                    if(typeM.get(entity).type == CollisionTypeComponent.ENEMY){
                        PositionComponent positionComponent = positionM.get(entity);
                        EnemyRarityComponent rarityComponent = rarityM.get(entity);
                        if(rarityComponent != null) {
                            for (int i = 0; i < rarityComponent.rarityLevel; i++) {
                                for (int j = 0; j < rarityComponent.rarityLevel; j++) {
                                    creator.createCoin(positionComponent.position.x - 1 + i / 3f, positionComponent.position.y + j / 3f);
                                }
                            }
                        }
                    }
                }
                this.removeEntity(entity);
            }
        }
        toBeRemoved.clear();
    }

    public void editRemoveEntities(){
        for (Entity entity:toBeRemoved) {
            if (entity != null && playerM.get(entity)==null) {
                this.removeEntity(entity);
            }
        }
    }

    public void render(SpriteBatch batch) {
        renderHealthBars(batch); // this way healthBars doesn't cover anything.
        renderNames(batch);
        renderQueue = new Array<>();
        entityArray = getEntitiesFor(Family.all(PositionComponent.class, TextureComponent.class, ActivatedComponent.class).get());
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
                    position.x * RENDERUNITS_PER_METER - originX, position.y * RENDERUNITS_PER_METER - originY,
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
        entityArray = getEntitiesFor(Family.all(PositionComponent.class, TextureComponent.class, HealthBarComponent.class).get());
        /*for (Entity entity:entityArray) {
            renderQueue.add(entity);
        }
        renderQueue.sort(comparator);*/
        batch.begin();
        for (Entity entity : entityArray) {
            HealthBarComponent barComponent = healthBarM.get(entity);
            if(!barComponent.hidden) {
                HealthComponent healthComponent = healthM.get(entity);
                Vector3 position = positionM.get(entity).position;
                TextureRegion region = textureM.get(entity).region;

                float width = barComponent.healthWidth;
                float height = barComponent.healthHeight;

                float originX = width / 2;
                float originY = height / 2;

                batch.draw(barComponent.region,
                        position.x * RENDERUNITS_PER_METER - originX, position.y * RENDERUNITS_PER_METER + (region.getRegionHeight() / 2) + height,
                        originX * healthComponent.health / healthComponent.maxHealth, originY,
                        barComponent.healthWidth * healthComponent.health / healthComponent.maxHealth, barComponent.healthHeight,
                        1, 1,
                        0);
            }
        }
        batch.end();
        renderQueue.clear();
    }
    private void renderNames(SpriteBatch batch){
        BitmapFont font = new BitmapFont();
        entityArray = getEntitiesFor(Family.all(PositionComponent.class, TextureComponent.class, NameComponent.class).get());
        batch.begin();
        for (Entity entity:entityArray) {
            NameComponent nameComponent = nameM.get(entity);
            if(!nameComponent.hidden) {
                Vector3 position = positionM.get(entity).position;
                TextureRegion region = textureM.get(entity).region;
                font.setColor(nameComponent.color);
                float nameWidth = nameComponent.name.length()*CHAR_SIZE;
                font.draw(batch, nameComponent.name, position.x * RENDERUNITS_PER_METER - nameWidth/2, position.y * RENDERUNITS_PER_METER + (region.getRegionHeight()/2)*1.8f);
            }
        }
        batch.end();
    }

    public void createSystems(KeyboardController controller, Viewport viewport, Entity player) {
        PlayerCollisionSystem collisionSystem = new PlayerCollisionSystem(levelManager, controller, toBeRemoved);
        PlayerControlSystem playerControlSystem = new PlayerControlSystem(controller);
        PhysicsSystem physicsSystem = new PhysicsSystem(world);
        AnimationSystem animationSystem = new AnimationSystem();
        MyEntityListener entityListener = new MyEntityListener(world);
        PlayerPowerSystem powerSystem = new PlayerPowerSystem(world, controller, viewport);

        this.addSystem(new LevelSystem());
        this.addSystem(new CollisionSystem());
        this.addSystem(new CollectorSystem());
        this.addSystem(new GhostMovementSystem(player));
        this.addSystem(new AggressiveSystem(player));
        this.addSystem(new ActivationSystem(player));
        this.addSystem(new DeactivationSystem(player));
        this.addSystem(new BasicEnemyMovement(player));
        this.addSystem(new BasicEnemyShooterSystem(newBullets, player));
        this.addSystem(new DamageSystem());
        this.addEntityListener(entityListener);
        this.addSystem(new ShootingSystem(newBullets));
        this.addSystem(new BulletCollisionSystem(toBeRemoved));
        this.addSystem(new HealthSystem(toBeRemoved));
        this.addSystem(new DeathTimerSystem(toBeRemoved));
        this.addSystem(new EnergySystem());
        this.addSystem(powerSystem);
        this.addSystem(collisionSystem);
        this.addSystem(playerControlSystem);
        this.addSystem(physicsSystem);
        this.addSystem(animationSystem);
    }

    public void addMapToEngine(Map map) {
        BodyCreator bodyCreator = new BodyCreator(world);

        for (int col = 0; col < map.getMapHeight(); col++) {
            for (int row = 0; row < map.getMapWidth(); row++) {
                TiledMapTile tile = map.getCell(row * Tile.tileSize, col * Tile.tileSize, Map.COLLISION_LAYER_NAME).getTile();
                if (tile != null) {
                    /*TextureComponent texture = createComponent(TextureComponent.class);
                    texture.region = tile.getTextureRegion();
                    mapTile.add(texture);*/
                    Entity mapTile = new Entity();
                    Body body = bodyCreator.makeRectBody(row + 0.5f, col + 0.5f, 1, 1, BodyMaterial.METAL,
                            BodyDef.BodyType.StaticBody, true, CATEGORY_SCENERY);
                    body.setUserData(mapTile);

                    mapTile.add(new ActivatedComponent());
                    mapTile.add(new CollisionTypeComponent(CollisionTypeComponent.SCENERY));
                    mapTile.add(new PositionComponent(new Vector3(row+ 0.5f,col+0.5f,0)));
                    mapTile.add(new BodyComponent(body));
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
            if (entityData == null) {
                System.out.println("failed to create entity! :(");
            } else {
                creator.createEntity(EntityType.getByID(entityData.getId()), entityData.getxPos(), entityData.getyPos());
            }
        }
        LevelSensorData[] levelSensorList = engineData.getLevelSensorDataList();
        for (LevelSensorData levelData : levelSensorList) {
            creator.createLevelSensor(levelData.getxPos(), levelData.getyPos(), levelData.getLevelName());
        }
    }

    public void saveToFile(String fileName) {
        FileHandle fileHandle = Gdx.files.local(fileName);
        EngineData engineData = new EngineData();
        engineData.setPlayerStartX(playerStartX);
        engineData.setPlayerStartY(playerStartY);
        ImmutableArray<Entity> entityDataArray = getEntitiesFor(Family.all(CollisionTypeComponent.class, PositionComponent.class, EntityTypeComponent.class).get());
        //check important length of entityDataArray
        int index = 0;
        int levelSensorIndex = 0;
        for (Entity entity : entityDataArray) {
            if (levelM.get(entity) != null) {
                levelSensorIndex++;
            } else if (typeM.get(entity).type != CollisionTypeComponent.SCENERY && playerM.get(entity) == null) {
                index++;
            }
        }
        engineData.setEntitiesLength(entityDataArray.size());
        EntityData[] entityDataList = new EntityData[index];
        LevelSensorData[] levelDataList = new LevelSensorData[levelSensorIndex];
        index = 0;
        levelSensorIndex = 0;
        for (Entity entity : entityDataArray) {
            if (levelM.get(entity) != null) {
                Vector3 position = positionM.get(entity).position;
                String nextLevelName = levelM.get(entity).nextLevelName;
                levelDataList[levelSensorIndex] = new LevelSensorData();
                levelDataList[levelSensorIndex].setxPos(position.x);
                levelDataList[levelSensorIndex].setyPos(position.y);
                levelDataList[levelSensorIndex].setLevelName(nextLevelName);
                levelSensorIndex++;
            } else if (typeM.get(entity).type != CollisionTypeComponent.SCENERY && playerM.get(entity) == null) {
                Vector3 position = positionM.get(entity).position;
                entityDataList[index] = new EntityData();
                entityDataList[index].setId(entityM.get(entity).entityType.getID());
                entityDataList[index].setxPos(position.x);
                entityDataList[index].setyPos(position.y);
                index++;
            }
        }
        engineData.setEntitiesDataList(entityDataList);
        engineData.setLevelSensorDataList(levelDataList);
        fileHandle.writeString(Base64Coder.encodeString(json.toJson(engineData)), false);
    }

    public float getPlayerStartX() {
        return playerStartX;
    }

    public float getPlayerStartY() {
        return playerStartY;
    }

    public void setPlayerStartPos(int startX, int startY) {
        playerStartX = startX;
        playerStartY = startY;

    }
    public void scheduleForRemoval(Entity entity) {
        toBeRemoved.add(entity);
    }
}

