package com.mygdx.game.enginePackage;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.World;
import com.mygdx.game.enginePackage.components.*;
import com.mygdx.game.game.Tile;
import com.mygdx.game.resources.AnimationsRes;
import com.mygdx.game.resources.ImagesRes;

import static com.mygdx.game.enginePackage.BodyCreator.*;
import static com.mygdx.game.enginePackage.EntityType.PLAYER;

//todo: make enum class containing different types of entities. hårdkodat är inte det bästa :/
//todo: make different health bar types and sizes, maybe depending on rarity and health amount.
public class EntityCreator {
    private MyEngine myEngine;
    private World world;
    private BodyCreator entityBodyCreator;

    public EntityCreator(MyEngine myEngine, World world) {
        this.myEngine = myEngine;
        this.world = world;
        entityBodyCreator = new BodyCreator(world);
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
        }
    }

    private void createPlant(float posx, float posy){
        Entity plant = new Entity();
        HealthComponent healthComponent = new HealthComponent();
        DamageComponent damageComponent = new DamageComponent();
        ShooterComponent shooter = new ShooterComponent();
        BodyComponent entityBody = new BodyComponent();
        PositionComponent entityPosition = new PositionComponent();
        TextureComponent entityTexture = new TextureComponent();
        CollisionTypeComponent collisionTypeComponent = new CollisionTypeComponent();
        EntityTypeComponent entityTypeComponent = new EntityTypeComponent();
        AnimationComponent animationComponent = new AnimationComponent();

        Animation animation = AnimationsRes.plantAni;
        animationComponent.animationMap.put(StateComponent.STATE_NORMAL, animation);
        collisionTypeComponent.type = CollisionTypeComponent.ENEMY;
        entityTexture.region = ImagesRes.plantImage;
        entityPosition.position.set(posx, posy, 1);
        entityBody.body = entityBodyCreator.makeRectBody(entityPosition.position.x, entityPosition.position.y, 64/Tile.tileSize, 32/Tile.tileSize, BodyMaterial.METAL,
                BodyDef.BodyType.KinematicBody, false, BodyCreator.CATEGORY_ENEMY);
        entityBody.body.setUserData(plant);
        entityTypeComponent.entityType = EntityType.PLANT_ENEMY;
        healthComponent.hidden = false;
        healthComponent.maxHealth = 100;
        healthComponent.health = 100;
        healthComponent.healthHeight = 20;
        healthComponent.healthWidth = 150;
        healthComponent.healthReg = 10;
        damageComponent.damage = 30;
        shooter.bulletType = BulletType.ENEMY_BULLET;
        shooter.bulletCd = 1.5f;
        shooter.alwaysShoot = true;

        plant.add(new ActivatedComponent());
        plant.add(animationComponent);
        plant.add(entityTypeComponent);
        plant.add(collisionTypeComponent);
        plant.add(entityPosition);
        plant.add(entityTexture);
        plant.add(entityBody);
        plant.add(new StateComponent());
        plant.add(new CollisionComponent());
        plant.add(new PlantShooterComponent());
        plant.add(shooter);
        plant.add(damageComponent);
        plant.add(healthComponent);
        myEngine.addEntity(plant);
    }

    private void createBoss(float posx, float posy) {
        Entity boss = createBasicEntity(posx, posy, 3, 9, 256 / 32, ImagesRes.bossImage,
                BodyMaterial.GLASS, CollisionTypeComponent.ENEMY, EntityType.BOSS, CATEGORY_ENEMY);
        HealthComponent healthComponent = new HealthComponent();
        BasicEnemyComponent movementComponent = new BasicEnemyComponent();
        DamageComponent damageComponent = new DamageComponent();
        ShooterComponent shooter = new ShooterComponent();


        healthComponent.hidden = false;
        healthComponent.maxHealth = 400;
        healthComponent.health = 400;
        healthComponent.healthHeight = 50;
        healthComponent.healthWidth = 300;
        healthComponent.healthReg = 10;
        damageComponent.damage = 30;
        shooter.bulletType = BulletType.ENEMY_BULLET;
        shooter.bulletCd = 0.3f;
        shooter.alwaysShoot = true;
        //boss.add(movementComponent);
        boss.add(new ActivatedComponent());

        boss.add(shooter);
        boss.add(damageComponent);
        boss.add(healthComponent);
        myEngine.addEntity(boss);
    }

    private void createBasicEnemy(float posx, float posy) {
        Entity enemy = createBasicEntity(posx, posy, 5, 9, 1 - 0.05f, ImagesRes.entityImage,
                BodyMaterial.GLASS, CollisionTypeComponent.ENEMY, EntityType.BASIC_ENEMY, CATEGORY_ENEMY);
        HealthComponent healthComponent = new HealthComponent();
        BasicEnemyComponent movementComponent = new BasicEnemyComponent();
        DamageComponent damageComponent = new DamageComponent();

        damageComponent.damage = 10;
        healthComponent.hidden = false;
        healthComponent.healthReg = 2f;
        healthComponent.maxHealth = 30;
        healthComponent.health = 30;
        healthComponent.healthWidth = 30;
        healthComponent.healthHeight = 7;

        enemy.add(new ActivatedComponent());
        enemy.add(damageComponent);
        enemy.add(movementComponent);
        enemy.add(healthComponent);
        myEngine.addEntity(enemy);
    }

    private void createBall(float posx, float posy) {
        Entity ball = createBasicEntity(posx, posy, 6, 9, 1 / 2f, ImagesRes.rockImage,
                BodyMaterial.BOUNCY, CollisionTypeComponent.OTHER, EntityType.ROCK, CATEGORY_FRIENDLY);
        HealthComponent healthComponent = new HealthComponent();

        healthComponent.hidden = true;
        healthComponent.healthReg = 1;
        healthComponent.maxHealth = 10;
        healthComponent.health = 10;
        //ball.add(healthComponent);
        ball.add(new ActivatedComponent());
        myEngine.addEntity(ball);
    }

    public Entity createPlayer(float posx, float posy) {
        Entity player = createBasicEntity(posx, posy, 8, 19, 2 - 0.05f, ImagesRes.playerImage,
                BodyMaterial.GLASS, CollisionTypeComponent.FRIENDLY, PLAYER, CATEGORY_PLAYER);
        VelocityComponent velocity = player.getComponent(VelocityComponent.class);
        velocity.jumpCooldown = 0.5f;
        AnimationComponent animationComponent = new AnimationComponent();
        HealthComponent healthComponent = new HealthComponent();
        BodyComponent bodyComponent = player.getComponent(BodyComponent.class);
        bodyComponent.body.setFixedRotation(true);
        EnergyComponent energyComponent = new EnergyComponent();
        ShooterComponent shooter = new ShooterComponent();


        shooter.bulletCd = 0.5f;
        //playerRadius+bulletRadius not needed anymore :)
        shooter.bulletSpawn = 0.8f;
        shooter.bulletType = BulletType.PLAYER_BULLET;
        energyComponent.maxMana = 200;
        energyComponent.mana = 200;
        energyComponent.manaReg = 15f;

        healthComponent.healthReg = 10f;
        healthComponent.maxHealth = 100;
        healthComponent.health = 100;
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

        velocity.jumpCooldown = 0.5f;

        player.add(new ActivatedComponent());
        player.add(shooter);
        player.add(healthComponent);
        player.add(animationComponent);
        player.add(velocity);
        player.add(energyComponent);
        player.add(new EnergyBarComponent());
        player.add(new PlayerComponent());
        player.add(new BasicShooterComponent());
        return player;
    }

    private Entity createBasicEntity(float posx, float posy, float sprintVelocity, float jumpVelocity, float size,
                                     TextureRegion region, BodyMaterial material, int type, EntityType entityType, short typeBits) {
        Entity basicEntity = new Entity();
        BodyComponent entityBody = new BodyComponent();
        PositionComponent entityPosition = new PositionComponent();
        TextureComponent entityTexture = new TextureComponent();
        VelocityComponent entityVelocity = new VelocityComponent();
        CollisionTypeComponent collisionTypeComponent = new CollisionTypeComponent();
        EntityTypeComponent entityTypeComponent = new EntityTypeComponent();

        collisionTypeComponent.type = type;
        entityVelocity.sprintSpeed = sprintVelocity;
        entityVelocity.jumpSpeed = jumpVelocity;
        entityTexture.region = region;
        entityPosition.position.set(posx, posy, 1);
        entityBody.body = entityBodyCreator.makeCirclePolyBody(entityPosition.position.x, entityPosition.position.y, size / 2, material,
                BodyDef.BodyType.DynamicBody, false, typeBits);
        entityBody.body.setUserData(basicEntity);
        entityTypeComponent.entityType = entityType;

        basicEntity.add(entityTypeComponent);
        basicEntity.add(collisionTypeComponent);
        basicEntity.add(entityVelocity);
        basicEntity.add(entityPosition);
        basicEntity.add(entityTexture);
        basicEntity.add(entityBody);
        basicEntity.add(new StateComponent());
        basicEntity.add(new CollisionComponent());
        return basicEntity;
    }

    public void createBullet(BulletInfo bulletInfo) {
        Entity bullet = new Entity();
        BodyComponent bulletBody = new BodyComponent();
        PositionComponent bulletPosition = new PositionComponent();
        TextureComponent bulletTexture = new TextureComponent();
        CollisionTypeComponent bulletType = new CollisionTypeComponent();
        DamageComponent bulletDamage = new DamageComponent();
        BulletComponent bulletComponent = new BulletComponent();
        HealthComponent bulletHealth = new HealthComponent();

        short bitType = CATEGORY_ENEMY;

        bulletType.type = bulletInfo.bulletType.type;
        switch (bulletInfo.bulletType){
            case PLAYER_BULLET:
                bitType = CATEGORY_PLAYER;
                break;
            case ENEMY_BULLET:
                bitType = CATEGORY_ENEMY;
                break;
        }
        bulletBody.body = entityBodyCreator.makeCirclePolyBody(bulletInfo.pos.x, bulletInfo.pos.y,
                bulletInfo.bulletType.radius, BodyMaterial.BULLET, BodyDef.BodyType.DynamicBody, false, bitType);
        bulletBody.body.applyLinearImpulse(bulletInfo.dir, bulletBody.body.getWorldCenter(), true);
        bulletBody.body.setUserData(bullet);
        bulletBody.body.setAngularVelocity(300);

        bulletPosition.position.set(bulletInfo.pos, 0);
        bulletTexture.region = bulletInfo.bulletType.region;
        bulletDamage.damage = bulletInfo.bulletType.damage;
        bulletDamage.damageCD = 0.2f;
        bulletHealth.maxHealth = bulletInfo.bulletType.bulletTimer;
        bulletHealth.health = bulletInfo.bulletType.bulletTimer-0.1f;
        bulletHealth.hidden = true;
        bulletHealth.alwaysHidden = true;
        bulletHealth.healthReg = -1f;

        bullet.add(new ActivatedComponent());
        bullet.add(bulletHealth);
        bullet.add(bulletComponent);
        bullet.add(bulletType);
        bullet.add(bulletBody);
        bullet.add(bulletPosition);
        bullet.add(new CollisionComponent());
        bullet.add(bulletTexture);
        bullet.add(bulletDamage);

        myEngine.addEntity(bullet);
    }

    public void createLevelSensor(float posx, float posy, String currentPortalName) {
        Entity levelSensor = new Entity();
        BodyComponent levelBody = new BodyComponent();
        PositionComponent levelPosition = new PositionComponent();
        TextureComponent levelTexture = new TextureComponent();
        CollisionTypeComponent collisionTypeComponent = new CollisionTypeComponent();
        LevelSensorComponent levelComponent = new LevelSensorComponent();

        collisionTypeComponent.type = CollisionTypeComponent.LEVEL_PORTAL;
        levelTexture.region = ImagesRes.levelImage;
        levelPosition.position.set(posx, posy, 0.1f);
        levelBody.body = entityBodyCreator.makeRectSensor(levelPosition.position.x, levelPosition.position.y, 3, 4, BodyMaterial.BULLET,
                BodyDef.BodyType.StaticBody, false);
        levelBody.body.setUserData(levelSensor);
        levelComponent.nextLevelName = currentPortalName;

        levelSensor.add(new ActivatedComponent());
        levelSensor.add(levelComponent);
        levelSensor.add(collisionTypeComponent);
        levelSensor.add(levelPosition);
        levelSensor.add(levelTexture);
        levelSensor.add(levelBody);
        myEngine.addEntity(levelSensor);
    }
}
