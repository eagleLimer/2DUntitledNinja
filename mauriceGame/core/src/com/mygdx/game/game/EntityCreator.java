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
public class EntityCreator {
    Engine engine;
    World world;
    ImagesRes imagesRes;
    AnimationsRes animationsRes;
    public EntityCreator(Engine engine, World world){
        this.engine = engine;
        this.world = world;
        imagesRes = new ImagesRes();
        animationsRes = new AnimationsRes();
    }
    public void createBoss(int posx, int posy) {
        Entity boss = createBasicEntity(posx,posy,7,9,256/32,imagesRes.bossImage, BodyMaterial.GLASS, TypeComponent.ENEMY);
        HealthComponent healthComponent = engine.createComponent(HealthComponent.class);
        healthComponent.hidden = false;
        healthComponent.maxHealth = 400;
        healthComponent.health = 400;
        healthComponent.healthHeight = 50;
        healthComponent.healthWidth = 300;
        boss.add(healthComponent);
        engine.addEntity(boss);
    }
    public void createBasicEnemy(int posx, int posy){
        Entity enemy = createBasicEntity(posx,posy,7,9,1/2f,imagesRes.rockImage, BodyMaterial.BOUNCY, TypeComponent.ENEMY);
        HealthComponent healthComponent = engine.createComponent(HealthComponent.class);
        healthComponent.hidden = false;
        healthComponent.maxHealth = 10;
        healthComponent.health = 10;
        enemy.add(healthComponent);
        engine.addEntity(enemy);
    }
    public Entity createPlayer(int posx, int posy) {
        Entity player = createBasicEntity(posx,posy,8,17,2,imagesRes.playerImage,BodyMaterial.GLASS, TypeComponent.PLAYER);
        VelocityComponent velocity = player.getComponent(VelocityComponent.class);
        velocity.jumpCooldown = 0.5f;
        AnimationComponent animationComponent = engine.createComponent(AnimationComponent.class);
        HealthComponent healthComponent = engine.createComponent(HealthComponent.class);

        healthComponent.maxHealth = 100;
        healthComponent.health = 100;
        Animation animation = animationsRes.playerRight;
        animationComponent.animationMap.put(StateComponent.STATE_RIGHT, animation);
        animation = animationsRes.playerLeft;
        animationComponent.animationMap.put(StateComponent.STATE_LEFT, animation);
        animation = animationsRes.playerFalling;
        animationComponent.animationMap.put(StateComponent.STATE_FALLING, animation);
        animation = animationsRes.playerNormal;
        animationComponent.animationMap.put(StateComponent.STATE_NORMAL, animation);
        animation = animationsRes.playerJumping;
        animationComponent.animationMap.put(StateComponent.STATE_JUMPING, animation);

        velocity.jumpCooldown = 0.5f;

        player.add(healthComponent);
        player.add(animationComponent);
        player.add(velocity);
        player.add(engine.createComponent(PlayerComponent.class));

        return player;
    }
    public Entity createBasicEntity(int posx, int posy, float sprintVelocity, float jumpVelocity, float size, TextureRegion region, BodyMaterial material, int type) {
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
        BodyCreator entityBodyCreator = new BodyCreator(world);
        entityPosition.position.set(posx, posy, 0);
        entityBody.body = entityBodyCreator.makeCirclePolyBody(entityPosition.position.x, entityPosition.position.y, size/2, material,
                BodyDef.BodyType.DynamicBody, false);
        entityBody.body.setUserData(basicEntity);

        basicEntity.add(typeComponent);
        basicEntity.add(entityVelocity);
        basicEntity.add(entityPosition);
        basicEntity.add(entityTexture);
        basicEntity.add(entityBody);
        basicEntity.add(engine.createComponent(StateComponent.class));
        basicEntity.add(engine.createComponent(CollisionComponent.class));
        return basicEntity;
    }
}
