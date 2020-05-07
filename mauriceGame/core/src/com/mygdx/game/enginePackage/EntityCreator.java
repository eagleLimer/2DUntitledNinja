package com.mygdx.game.enginePackage;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.World;
import com.mygdx.game.enginePackage.components.*;
import com.mygdx.game.enginePackage.components.combatComponents.*;
import com.mygdx.game.enginePackage.components.enemyComponents.*;
import com.mygdx.game.enginePackage.components.playerComponents.EnergyBarComponent;
import com.mygdx.game.enginePackage.components.playerComponents.EnergyComponent;
import com.mygdx.game.enginePackage.components.playerComponents.PlayerComponent;
import com.mygdx.game.resources.AnimationsRes;
import com.mygdx.game.resources.ImagesRes;

import static com.mygdx.game.enginePackage.Constants.*;
import static com.mygdx.game.enginePackage.EntityType.GHOST;
import static com.mygdx.game.enginePackage.EntityType.PLAYER;
import static com.mygdx.game.enginePackage.Constants.RENDERUNITS_PER_METER;

//todo: make different health bar types and sizes, maybe depending on rarity and health amount.
public class EntityCreator {
    private MyEngine myEngine;
    private World world;
    private BodyCreator entityBodyCreator;
    private ComponentMapper<PositionComponent> posM;

    public EntityCreator(MyEngine myEngine, World world) {
        this.myEngine = myEngine;
        this.world = world;
        entityBodyCreator = new BodyCreator(world);
        posM = ComponentMapper.getFor(PositionComponent.class);
    }

    public void createEntity(EntityType entityType, float x, float y) {
        switch (entityType) {
            case PLAYER:
                createPlayer(x, y);
                break;
            case BASIC_ENEMY:
                createBasicEnemy(x, y);
                break;
            case BOSS:
                createBoss(x, y);
                break;
            case ROCK:
                createBall(x, y);
                break;
            case PLANT_ENEMY:
                createPlant(x,y);
                break;
            case GHOST:
                createGhost(x,y);
        }
    }

    private void createPlant(float posx, float posy){
        Entity plant = new Entity();
        AnimationComponent animationComponent = new AnimationComponent();

        Animation animation = AnimationsRes.plantAni;
        animationComponent.animationMap.put(StateComponent.STATE_NORMAL, animation);
        animation = AnimationsRes.playerDeath;
        animationComponent.animationMap.put(StateComponent.STATE_DEAD,animation);
        Body body = entityBodyCreator.makeRectBody(posx, posy, 64/RENDERUNITS_PER_METER, 32/RENDERUNITS_PER_METER, BodyMaterial.METAL,
                BodyDef.BodyType.DynamicBody, false, CATEGORY_ENEMY);
        body.setUserData(plant);

        plant.add(new EnemyRarityComponent(5));
        plant.add(new AggressiveComponent());
        plant.add(new HealthBarComponent(150,20,false,true));
        plant.add(new ActivatedComponent());
        plant.add(animationComponent);
        plant.add(new EntityTypeComponent(EntityType.PLANT_ENEMY));
        plant.add(new CollisionTypeComponent(CollisionTypeComponent.ENEMY));
        plant.add(new PositionComponent(new Vector3(posx,posy,0)));
        plant.add(new TextureComponent(ImagesRes.plantImage));
        plant.add(new BodyComponent(body));
        plant.add(new StateComponent());
        plant.add(new CollisionComponent());
        plant.add(new PlantShooterComponent());
        plant.add(new ShooterComponent(1.5f,BulletType.ENEMY_BULLET,true));
        plant.add(new DamageComponent(30));
        plant.add(new HealthComponent(100,100,10));
        myEngine.addEntity(plant);
    }
    private void createGhost(float posx, float posy){
        Entity ghost = new Entity();

        Body body = entityBodyCreator.makeRectSensor(posx, posy, 64/RENDERUNITS_PER_METER, 64/RENDERUNITS_PER_METER, BodyMaterial.METAL,
                BodyDef.BodyType.DynamicBody, false, CATEGORY_ENEMY);
        body.setUserData(ghost);
        body.setGravityScale(0);

        ghost.add(new EnemyRarityComponent(5));
        ghost.add(new VelocityComponent(2,10,10));
        ghost.add(new GhostMovementComponent());
        ghost.add(new AggressiveComponent());
        ghost.add(new HealthBarComponent(150,20,false,true));
        ghost.add(new ActivatedComponent());
        ghost.add(new EntityTypeComponent(EntityType.GHOST));
        ghost.add(new CollisionTypeComponent(CollisionTypeComponent.ENEMY));
        ghost.add(new PositionComponent(new Vector3(posx,posy,0)));
        ghost.add(new TextureComponent(GHOST.getRegion()));
        ghost.add(new BodyComponent(body));
        ghost.add(new StateComponent());
        ghost.add(new CollisionComponent());
        ghost.add(new PlantShooterComponent());
        ghost.add(new ShooterComponent(1.5f,BulletType.GHOST_BULLET,true));
        ghost.add(new DamageComponent(20));
        ghost.add(new HealthComponent(100,100,10));
        myEngine.addEntity(ghost);
    }

    private void createBoss(float posx, float posy) {
        Entity boss = createBasicEntity(new Vector3(posx,posy,0.6f),true, 3, 9, 0.5f ,256 / RENDERUNITS_PER_METER, ImagesRes.bossImage,
                BodyMaterial.GLASS, CollisionTypeComponent.ENEMY, EntityType.BOSS, CATEGORY_ENEMY);

        //boss.add(new BasicEnemyComponent());
        boss.add(new EnemyRarityComponent(10));
        boss.add(new ActivatedComponent());
        boss.add(new AggressiveComponent());
        boss.add(new HealthBarComponent(300,50,false,true));
        boss.add(new ShooterComponent(0.3f,BulletType.ENEMY_BULLET,true));
        boss.add(new DamageComponent(30));
        boss.add(new HealthComponent(400,400,10));
        myEngine.addEntity(boss);
    }

    private void createBasicEnemy(float posx, float posy) {
        Entity enemy = createBasicEntity(new Vector3(posx,posy,0.4f),true, 5, 14, 2f ,32/RENDERUNITS_PER_METER - 0.05f, ImagesRes.entityImage,
                BodyMaterial.GLASS, CollisionTypeComponent.ENEMY, EntityType.BASIC_ENEMY, CATEGORY_ENEMY);
        AnimationComponent animationComponent = new AnimationComponent();

        Animation animation = AnimationsRes.dabAni;
        animationComponent.animationMap.put(StateComponent.STATE_NORMAL, animation);
        enemy.add(new EnemyRarityComponent(2));
        enemy.add(new AggressiveComponent());
        enemy.add(new ActivatedComponent());
        enemy.add(new HealthBarComponent(30,7,false,true));
        enemy.add(animationComponent);
        enemy.add(new StateComponent());
        enemy.add(new DamageComponent(15));
        enemy.add(new BasicEnemyComponent());
        enemy.add(new HealthComponent(30,30,2));
        myEngine.addEntity(enemy);
    }

    private void createBall(float posx, float posy) {
        Entity ball = createBasicEntity(new Vector3(posx,posy,0.3f),false, 6, 9, 0.5f ,16/RENDERUNITS_PER_METER, ImagesRes.rockImage,
                BodyMaterial.BOUNCY, CollisionTypeComponent.OTHER, EntityType.ROCK, CATEGORY_FRIENDLY);

        //ball.add(new HealthComponent(10,10,1));
        //ball.add(new HealthBarComponent());
        ball.add(new DeactivatedComponent());
        myEngine.addEntity(ball);
    }

    public Entity createPlayer(float posx, float posy) {
        Entity player = createBasicEntity(new Vector3(posx,posy,1),true, 8, 19, 0.5f, 64/RENDERUNITS_PER_METER - 0.05f, ImagesRes.playerImage,
                BodyMaterial.GLASS, CollisionTypeComponent.FRIENDLY, PLAYER, CATEGORY_PLAYER);
        AnimationComponent animationComponent = new AnimationComponent();

        Animation animation = AnimationsRes.playerRight;
        animationComponent.animationMap.put(StateComponent.STATE_RIGHT, animation);
        animation = AnimationsRes.playerLeft;
        animationComponent.animationMap.put(StateComponent.STATE_LEFT, animation);
        animation = AnimationsRes.playerFalling;
        animationComponent.animationMap.put(StateComponent.STATE_FALLING, animation);
        animation = AnimationsRes.playerNormal;
        animationComponent.animationMap.put(StateComponent.STATE_NORMAL, animation);
        animation = AnimationsRes.playerJumping;
        animationComponent.animationMap.put(StateComponent.STATE_JUMPING, animation);

        //player.add(new NameComponent("PLAYER"));
        player.add(new InventoryComponent());
        player.add(animationComponent);
        player.add(new ActivatedComponent());
        player.add(new HealthBarComponent(60,10,false,true));
        player.add(new ShooterComponent(0.1f,BulletType.PLAYER_BULLET,false));
        player.add(new HealthComponent(100,100,10));
        player.add(new EnergyComponent(200, 15));
        player.add(new EnergyBarComponent(60,10));
        player.add(new PlayerComponent());
        player.add(new BasicShooterComponent());
        createCollector(player, 12);
        return player;
    }
    private void createCollector(Entity parent, float pullRange){
        Entity collector = new Entity();
        Vector3 parentPos = posM.get(parent).position;
        PositionComponent positionComponent = new PositionComponent(new Vector3(parentPos.x,parentPos.y,parentPos.z));
        Body body = entityBodyCreator.makeCircleSensor(positionComponent.position.x, positionComponent.position.y, pullRange, BodyMaterial.BULLET,
                BodyDef.BodyType.KinematicBody, true, CATEGORY_COLLECTOR);
        body.setUserData(collector);

        collector.add(new ActivatedComponent());
        collector.add(new CollectorComponent(parent,pullRange));
        collector.add(new CollisionTypeComponent(CollisionTypeComponent.OTHER));
        collector.add(new CollisionComponent());
        //collector.add(new TextureComponent(ImagesRes.ghostImage));
        collector.add(positionComponent);
        collector.add(new BodyComponent(body));
        myEngine.addEntity(collector);
    }

    private Entity createBasicEntity(Vector3 pos ,boolean fixedRotation, float sprintVelocity, float jumpVelocity, float jumpCd,float size,
                                     TextureRegion region, BodyMaterial material, int collisionType, EntityType entityType, short typeBits) {
        Entity basicEntity = new Entity();

        Body body = entityBodyCreator.makeCirclePolyBody(pos.x, pos.y, size / 2, material,
                BodyDef.BodyType.DynamicBody, fixedRotation, typeBits);
        body.setUserData(basicEntity);

        basicEntity.add(new EntityTypeComponent(entityType));
        basicEntity.add(new CollisionTypeComponent(collisionType));
        basicEntity.add(new VelocityComponent(sprintVelocity,jumpVelocity,jumpCd));
        basicEntity.add(new PositionComponent(pos));
        basicEntity.add(new TextureComponent(region));
        basicEntity.add(new BodyComponent(body));
        basicEntity.add(new StateComponent());
        basicEntity.add(new CollisionComponent());
        return basicEntity;
    }

    public void createBullet(BulletInfo bulletInfo) {
        Entity bullet = new Entity();

        short bitType = CATEGORY_ENEMY;

        switch (bulletInfo.bulletType){
            case PLAYER_BULLET:
                bitType = CATEGORY_FRIENDLY;
                break;
            case ENEMY_BULLET: case GHOST_BULLET:
                bitType = CATEGORY_ENEMY;
                break;
        }
        Body body;
        if(bulletInfo.bulletType.piercing){
            body = entityBodyCreator.makeRectSensor(bulletInfo.pos.x, bulletInfo.pos.y, bulletInfo.bulletType.radius*2, bulletInfo.bulletType.radius*2, BodyMaterial.BULLET,
                    bulletInfo.bulletType.bodyType, false, bitType);
            body.setLinearVelocity(bulletInfo.dir.scl(100));
        }else {
            body = entityBodyCreator.makeCirclePolyBody(bulletInfo.pos.x, bulletInfo.pos.y,
                    bulletInfo.bulletType.radius, BodyMaterial.BULLET, bulletInfo.bulletType.bodyType, false, bitType);
            body.applyLinearImpulse(bulletInfo.dir, body.getWorldCenter(), true);
        }

        body.setUserData(bullet);
        body.setAngularVelocity(300);
        bullet.add(new HealthBarComponent(60,10,true,true));
        bullet.add(new ActivatedComponent());
        bullet.add(new HealthComponent(bulletInfo.bulletType.bulletTimer,bulletInfo.bulletType.bulletTimer, 0));
        bullet.add(new DeathTimerComponent(bulletInfo.bulletType.bulletTimer));
        bullet.add(new BulletComponent());
        bullet.add(new CollisionTypeComponent(bulletInfo.bulletType.type));
        bullet.add(new BodyComponent(body));
        bullet.add(new PositionComponent(new Vector3(bulletInfo.pos, 0)));
        bullet.add(new CollisionComponent());
        bullet.add(new TextureComponent(bulletInfo.bulletType.region));
        bullet.add(new DamageComponent(bulletInfo.bulletType.damage, bulletInfo.bulletType.bulletDamageTick));

        myEngine.addEntity(bullet);
    }

    public void createLevelSensor(float posx, float posy, String currentPortalName) {
        Entity levelSensor = new Entity();

        Body body = entityBodyCreator.makeRectSensor(posx, posy, 96/RENDERUNITS_PER_METER, 128/RENDERUNITS_PER_METER, BodyMaterial.BULLET,
                BodyDef.BodyType.StaticBody, true, CATEGORY_SCENERY);
        body.setUserData(levelSensor);

        levelSensor.add(new ActivatedComponent());
        levelSensor.add(new NameComponent(currentPortalName));
        levelSensor.add(new LevelSensorComponent(currentPortalName));
        levelSensor.add(new EntityTypeComponent(EntityType.LEVEL_SENSOR));
        levelSensor.add(new CollisionTypeComponent(CollisionTypeComponent.LEVEL_PORTAL));
        levelSensor.add(new PositionComponent(new Vector3(posx,posy, 0)));
        levelSensor.add(new TextureComponent(ImagesRes.levelImage));
        levelSensor.add(new BodyComponent(body));
        myEngine.addEntity(levelSensor);
    }

    public void createCoin(float posx, float posy){
        Entity coin = new Entity();
        AnimationComponent animationComponent = new AnimationComponent();
        Animation animation = AnimationsRes.coinPickupAni;
        animationComponent.animationMap.put(StateComponent.STATE_DEAD,animation);
        Body body = entityBodyCreator.makeCirclePolyBody(posx, posy, (16/2f)/RENDERUNITS_PER_METER, BodyMaterial.BULLET,
                BodyDef.BodyType.DynamicBody, false, CATEGORY_ITEM);
        body.setUserData(coin);
        coin.add(animationComponent);
        coin.add(new CollisionComponent());
        coin.add(new StateComponent());
        coin.add(new ItemComponent(new Item(ItemType.COIN)));
        coin.add(new BodyComponent(body));
        coin.add(new CollisionTypeComponent(CollisionTypeComponent.ITEM));
        coin.add(new ActivatedComponent());
        coin.add(new PositionComponent(new Vector3(posx,posy,0)));
        //coin.add(new DeathTimerComponent(30));
        coin.add(new TextureComponent(ImagesRes.coinImage));
        myEngine.addEntity(coin);
    }

    public void createDeathAni(Vector3 position, Animation animation) {
        Entity deathAniEntity = new Entity();
        AnimationComponent deathAnimation = new AnimationComponent();
        deathAnimation.animationMap.put(StateComponent.STATE_NORMAL, animation);
        StateComponent stateComponent = new StateComponent();
        stateComponent.set(StateComponent.STATE_NORMAL);
        deathAniEntity.add(new ActivatedComponent());
        deathAniEntity.add(new DeathTimerComponent(animation.getAnimationDuration()));
        deathAniEntity.add(new PositionComponent(position));
        deathAniEntity.add(stateComponent);
        deathAniEntity.add(deathAnimation);
        deathAniEntity.add(new TextureComponent((TextureRegion) animation.getKeyFrame(0)));
        deathAniEntity.add(new CollisionTypeComponent(CollisionTypeComponent.OTHER));
        myEngine.addEntity(deathAniEntity);
    }
}
