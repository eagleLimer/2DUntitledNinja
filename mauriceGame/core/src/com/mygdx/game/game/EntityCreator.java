package com.mygdx.game.game;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.World;
import com.mygdx.game.components.*;
import com.mygdx.game.resources.AnimationsRes;
import com.mygdx.game.resources.ImagesRes;
import com.mygdx.game.systems.Engine;

//todo: make enum class containing different types of entities. hårdkodat är inte ok!
//todo: make different health bar types and sizes, maybe depending on rarity and health amount.
public class EntityCreator {
    private Engine engine;
    private World world;
    private BodyCreator entityBodyCreator;

    public EntityCreator(Engine engine, World world) {
        this.engine = engine;
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
        }
    }

    private void createBoss(float posx, float posy) {
        Entity boss = createBasicEntity(posx, posy, 3, 9, 256 / 32, ImagesRes.bossImage, BodyMaterial.GLASS, TypeComponent.BOSS);
        HealthComponent healthComponent = engine.createComponent(HealthComponent.class);
        //BasicEnemyComponent enemyComponent = engine.createComponent(BasicEnemyComponent.class);
        healthComponent.hidden = false;
        healthComponent.maxHealth = 400;
        healthComponent.health = 400;
        healthComponent.healthHeight = 50;
        healthComponent.healthWidth = 300;
        //boss.add(enemyComponent);
        boss.add(healthComponent);
        engine.addEntity(boss);
    }

    private void createBasicEnemy(float posx, float posy) {
        Entity enemy = createBasicEntity(posx, posy, 6, 9, 1-0.05f, ImagesRes.entityImage, BodyMaterial.GLASS, TypeComponent.BASIC_ENEMY);
        HealthComponent healthComponent = engine.createComponent(HealthComponent.class);
        BasicEnemyComponent enemyComponent = engine.createComponent(BasicEnemyComponent.class);
        DamageComponent damageComponent = engine.createComponent(DamageComponent.class);

        damageComponent.damage = 10;
        healthComponent.hidden = false;
        healthComponent.healthReg = 0.1f;
        healthComponent.maxHealth = 40;
        healthComponent.health = 40;

        enemy.add(damageComponent);
        enemy.add(enemyComponent);
        enemy.add(healthComponent);
        engine.addEntity(enemy);
    }
    private void createBall(float posx, float posy) {
        Entity ball = createBasicEntity(posx, posy, 6, 9, 1 / 2f, ImagesRes.rockImage, BodyMaterial.BOUNCY, TypeComponent.BALL);
        HealthComponent healthComponent = engine.createComponent(HealthComponent.class);

        healthComponent.hidden = true;
        healthComponent.healthReg = 1;
        healthComponent.maxHealth = 10;
        healthComponent.health = 10;
        //ball.add(healthComponent);
        engine.addEntity(ball);
    }

    public Entity createPlayer(float posx, float posy) {
        Entity player = createBasicEntity(posx, posy, 8, 19, 2-0.05f, ImagesRes.playerImage, BodyMaterial.GLASS, TypeComponent.PLAYER);
        VelocityComponent velocity = player.getComponent(VelocityComponent.class);
        velocity.jumpCooldown = 0.5f;
        AnimationComponent animationComponent = engine.createComponent(AnimationComponent.class);
        HealthComponent healthComponent = engine.createComponent(HealthComponent.class);
        BodyComponent bodyComponent = player.getComponent(BodyComponent.class);
        bodyComponent.body.setFixedRotation(true);
        EnergyComponent energyComponent = engine.createComponent(EnergyComponent.class);
        ShooterComponent shooter = engine.createComponent(ShooterComponent.class);

        shooter.bulletCd = 0.5f;
        //playerRadius+bulletRadius
        shooter.bulletSpawn = 1.5f+BulletType.PLAYER_BULLET.radius;
        shooter.bulletType = BulletType.PLAYER_BULLET;
        energyComponent.maxMana = 200;
        energyComponent.mana = 200;
        energyComponent.manaReg = 10f;

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
        player.add(engine.createComponent(EnergyBarComponent.class));
        player.add(engine.createComponent(PlayerComponent.class));
        return player;
    }

    private Entity createBasicEntity(float posx, float posy, float sprintVelocity, float jumpVelocity, float size, TextureRegion region, BodyMaterial material, int type) {
        Entity basicEntity = engine.createEntity();
        BodyComponent entityBody = engine.createComponent(BodyComponent.class);
        PositionComponent entityPosition = engine.createComponent(PositionComponent.class);
        TextureComponent entityTexture = engine.createComponent(TextureComponent.class);
        VelocityComponent entityVelocity = engine.createComponent(VelocityComponent.class);
        TypeComponent typeComponent = engine.createComponent(TypeComponent.class);


        typeComponent.type = type;
        entityVelocity.sprintSpeed = sprintVelocity;
        entityVelocity.jumpSpeed = jumpVelocity;
        entityTexture.region = region;
        entityPosition.position.set(posx, posy, 0);
        entityBody.body = entityBodyCreator.makeCirclePolyBody(entityPosition.position.x, entityPosition.position.y, size / 2, material,
                BodyDef.BodyType.DynamicBody, false);
        entityBody.body.setUserData(basicEntity);

        System.out.println(type);

        basicEntity.add(typeComponent);
        basicEntity.add(entityVelocity);
        basicEntity.add(entityPosition);
        basicEntity.add(entityTexture);
        basicEntity.add(entityBody);
        basicEntity.add(engine.createComponent(StateComponent.class));
        basicEntity.add(engine.createComponent(CollisionComponent.class));
        return basicEntity;
    }
    public void createBullet(BulletInfo bulletInfo){
        Entity bullet = engine.createEntity();
        BodyComponent bulletBody = engine.createComponent(BodyComponent.class);
        PositionComponent bulletPosition = engine.createComponent(PositionComponent.class);
        TextureComponent bulletTexture = engine.createComponent(TextureComponent.class);
        TypeComponent bulletType = engine.createComponent(TypeComponent.class);
        DamageComponent bulletDamage = engine.createComponent(DamageComponent.class);
        BulletComponent bulletComponent = engine.createComponent(BulletComponent.class);

        bulletType.type = bulletInfo.bulletType.type;
        bulletBody.body = entityBodyCreator.makeCirclePolyBody(bulletInfo.pos.x,bulletInfo.pos.y,
                bulletInfo.bulletType.radius*2, BodyMaterial.BULLET, BodyDef.BodyType.DynamicBody, false);
        bulletBody.body.applyLinearImpulse(bulletInfo.dir,bulletBody.body.getWorldCenter(),true);
        bulletBody.body.setUserData(bullet);

        bulletPosition.position.set(bulletInfo.pos,0);
        bulletTexture.region = bulletInfo.bulletType.region;
        bulletDamage.damage = bulletInfo.bulletType.damage;
        bulletComponent.bulletTimer = bulletInfo.bulletType.bulletTimer;

        bullet.add(bulletComponent);
        bullet.add(bulletType);
        bullet.add(bulletBody);
        bullet.add(bulletPosition);
        bullet.add(engine.createComponent(CollisionComponent.class));
        bullet.add(bulletTexture);
        bullet.add(bulletDamage);

        engine.addEntity(bullet);
    }
}
