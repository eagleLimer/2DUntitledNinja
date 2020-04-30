package com.mygdx.game.enginePackage;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.World;
import com.mygdx.game.enginePackage.components.*;
import com.mygdx.game.resources.AnimationsRes;
import com.mygdx.game.resources.ImagesRes;

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

    public void createEntity(int entityId, float x, float y) {
        switch (entityId) {
            case TypeComponent.PLAYER:
                createPlayer(x, y);
                break;
            case TypeComponent.BASIC_ENEMY:
                createBasicEnemy(x, y);
                break;
            case TypeComponent.BOSS:
                createBoss(x, y);
                break;
            case TypeComponent.BALL:
                createBall(x, y);
                break;
        }
    }

    private void createBoss(float posx, float posy) {
        Entity boss = createBasicEntity(posx, posy, 3, 9, 256 / 32, ImagesRes.bossImage, BodyMaterial.GLASS, TypeComponent.BOSS);
        HealthComponent healthComponent = myEngine.createComponent(HealthComponent.class);
        //BasicEnemyComponent enemyComponent = engine.createComponent(BasicEnemyComponent.class);
        healthComponent.hidden = false;
        healthComponent.maxHealth = 400;
        healthComponent.health = 400;
        healthComponent.healthHeight = 50;
        healthComponent.healthWidth = 300;
        //boss.add(enemyComponent);
        boss.add(healthComponent);
        myEngine.addEntity(boss);
    }

    private void createBasicEnemy(float posx, float posy) {
        Entity enemy = createBasicEntity(posx, posy, 6, 9, 1 - 0.05f, ImagesRes.entityImage, BodyMaterial.GLASS, TypeComponent.BASIC_ENEMY);
        HealthComponent healthComponent = myEngine.createComponent(HealthComponent.class);
        BasicEnemyComponent enemyComponent = myEngine.createComponent(BasicEnemyComponent.class);
        DamageComponent damageComponent = myEngine.createComponent(DamageComponent.class);

        damageComponent.damage = 10;
        healthComponent.hidden = false;
        healthComponent.healthReg = 2f;
        healthComponent.maxHealth = 40;
        healthComponent.health = 40;

        enemy.add(damageComponent);
        enemy.add(enemyComponent);
        enemy.add(healthComponent);
        myEngine.addEntity(enemy);
    }

    private void createBall(float posx, float posy) {
        Entity ball = createBasicEntity(posx, posy, 6, 9, 1 / 2f, ImagesRes.rockImage, BodyMaterial.BOUNCY, TypeComponent.BALL);
        HealthComponent healthComponent = myEngine.createComponent(HealthComponent.class);

        healthComponent.hidden = true;
        healthComponent.healthReg = 1;
        healthComponent.maxHealth = 10;
        healthComponent.health = 10;
        //ball.add(healthComponent);
        myEngine.addEntity(ball);
    }

    public Entity createPlayer(float posx, float posy) {
        Entity player = createBasicEntity(posx, posy, 8, 19, 2 - 0.05f, ImagesRes.playerImage, BodyMaterial.GLASS, TypeComponent.PLAYER);
        VelocityComponent velocity = player.getComponent(VelocityComponent.class);
        velocity.jumpCooldown = 0.5f;
        AnimationComponent animationComponent = myEngine.createComponent(AnimationComponent.class);
        HealthComponent healthComponent = myEngine.createComponent(HealthComponent.class);
        BodyComponent bodyComponent = player.getComponent(BodyComponent.class);
        bodyComponent.body.setFixedRotation(true);
        EnergyComponent energyComponent = myEngine.createComponent(EnergyComponent.class);
        ShooterComponent shooter = myEngine.createComponent(ShooterComponent.class);

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

        player.add(shooter);
        player.add(healthComponent);
        player.add(animationComponent);
        player.add(velocity);
        player.add(energyComponent);
        player.add(myEngine.createComponent(EnergyBarComponent.class));
        player.add(myEngine.createComponent(PlayerComponent.class));
        return player;
    }

    private Entity createBasicEntity(float posx, float posy, float sprintVelocity, float jumpVelocity, float size, TextureRegion region, BodyMaterial material, int type) {
        Entity basicEntity = myEngine.createEntity();
        BodyComponent entityBody = myEngine.createComponent(BodyComponent.class);
        PositionComponent entityPosition = myEngine.createComponent(PositionComponent.class);
        TextureComponent entityTexture = myEngine.createComponent(TextureComponent.class);
        VelocityComponent entityVelocity = myEngine.createComponent(VelocityComponent.class);
        TypeComponent typeComponent = myEngine.createComponent(TypeComponent.class);

        typeComponent.type = type;
        entityVelocity.sprintSpeed = sprintVelocity;
        entityVelocity.jumpSpeed = jumpVelocity;
        entityTexture.region = region;
        entityPosition.position.set(posx, posy, 1);
        entityBody.body = entityBodyCreator.makeCirclePolyBody(entityPosition.position.x, entityPosition.position.y, size / 2, material,
                BodyDef.BodyType.DynamicBody, false);
        entityBody.body.setUserData(basicEntity);

        basicEntity.add(typeComponent);
        basicEntity.add(entityVelocity);
        basicEntity.add(entityPosition);
        basicEntity.add(entityTexture);
        basicEntity.add(entityBody);
        basicEntity.add(myEngine.createComponent(StateComponent.class));
        basicEntity.add(myEngine.createComponent(CollisionComponent.class));
        return basicEntity;
    }

    public void createBullet(BulletInfo bulletInfo) {
        Entity bullet = myEngine.createEntity();
        BodyComponent bulletBody = myEngine.createComponent(BodyComponent.class);
        PositionComponent bulletPosition = myEngine.createComponent(PositionComponent.class);
        TextureComponent bulletTexture = myEngine.createComponent(TextureComponent.class);
        TypeComponent bulletType = myEngine.createComponent(TypeComponent.class);
        DamageComponent bulletDamage = myEngine.createComponent(DamageComponent.class);
        BulletComponent bulletComponent = myEngine.createComponent(BulletComponent.class);
        HealthComponent bulletHealth = myEngine.createComponent(HealthComponent.class);

        bulletType.type = bulletInfo.bulletType.type;
        bulletBody.body = entityBodyCreator.makeCirclePolyBody(bulletInfo.pos.x, bulletInfo.pos.y,
                bulletInfo.bulletType.radius, BodyMaterial.BULLET, BodyDef.BodyType.DynamicBody, false);
        bulletBody.body.applyLinearImpulse(bulletInfo.dir, bulletBody.body.getWorldCenter(), true);
        bulletBody.body.setUserData(bullet);
        bulletBody.body.setAngularVelocity(300);

        bulletPosition.position.set(bulletInfo.pos, 0);
        bulletTexture.region = bulletInfo.bulletType.region;
        bulletDamage.damage = bulletInfo.bulletType.damage;
        bulletHealth.maxHealth = bulletInfo.bulletType.bulletTimer;
        bulletHealth.health = bulletInfo.bulletType.bulletTimer-0.1f;
        bulletHealth.hidden = true;
        bulletHealth.healthReg = -1f;

        bullet.add(bulletHealth);
        bullet.add(bulletComponent);
        bullet.add(bulletType);
        bullet.add(bulletBody);
        bullet.add(bulletPosition);
        bullet.add(myEngine.createComponent(CollisionComponent.class));
        bullet.add(bulletTexture);
        bullet.add(bulletDamage);

        try {
            myEngine.addEntity(bullet);
        } catch (Exception e) {
            System.out.println("Well that didnt work");
            e.printStackTrace();
        }
    }

    public void createLevelSensor(float posx, float posy, String currentPortalName) {
        Entity levelSensor = myEngine.createEntity();
        BodyComponent levelBody = myEngine.createComponent(BodyComponent.class);
        PositionComponent levelPosition = myEngine.createComponent(PositionComponent.class);
        TextureComponent levelTexture = myEngine.createComponent(TextureComponent.class);
        TypeComponent typeComponent = myEngine.createComponent(TypeComponent.class);
        LevelSensorComponent levelComponent = myEngine.createComponent(LevelSensorComponent.class);

        typeComponent.type = TypeComponent.LEVEL_SENSOR;
        levelTexture.region = ImagesRes.levelImage;
        levelPosition.position.set(posx, posy, 0.1f);
        levelBody.body = entityBodyCreator.makeRectSensor(levelPosition.position.x, levelPosition.position.y, 3, 4, BodyMaterial.BULLET,
                BodyDef.BodyType.StaticBody, false);
        levelBody.body.setUserData(levelSensor);
        levelComponent.nextLevelName = currentPortalName;

        levelSensor.add(levelComponent);
        levelSensor.add(typeComponent);
        levelSensor.add(levelPosition);
        levelSensor.add(levelTexture);
        levelSensor.add(levelBody);
        myEngine.addEntity(levelSensor);
    }
}
