package com.mygdx.game.enginePackage.systems;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.QueryCallback;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.mygdx.game.enginePackage.EntityType;
import com.mygdx.game.enginePackage.components.*;
import com.mygdx.game.enginePackage.components.combatComponents.ShooterComponent;
import com.mygdx.game.enginePackage.components.playerComponents.EnergyComponent;
import com.mygdx.game.enginePackage.components.playerComponents.PlayerComponent;
import com.mygdx.game.game.KeyboardController;
import com.mygdx.game.game.Tile;

public class PlayerPowerSystem extends IteratingSystem {
    //todo: should move this also lol
    private static final float IMPULSE_STRENGTH = 0.2f;
    private static final float MAX_MULTIPLIER = 10f;
    private static final float MIN_MULTIPLIER = 2f;
    private static final float SLOW_DOWN_ZONE = 1f;
    private static final float LIFT_STRENGTH = 1;
    private static final float IMPULSE_AOE = 1;
    private World world;
    private KeyboardController controller;
    private Viewport viewport;
    private ComponentMapper<BodyComponent> bodyM;
    private ComponentMapper<EntityTypeComponent> entityM;
    private ComponentMapper<EnergyComponent> energyM;
    private ComponentMapper<PositionComponent> posM;
    private ComponentMapper<ShooterComponent> shooterM;
    private float mouseX;
    private float mouseY;
    private boolean checkShoot = false;
    private QueryCallback mycallBack;
    private Array<Entity> entityList;

    // todo: Disable balls before impact with enemy to make it more balanced.
    //  balanced this another way so not a problem anymore, might be a good feature though.
    public PlayerPowerSystem(World world, KeyboardController controller, final Viewport viewport) {
        super(Family.all(PlayerComponent.class).get());
        this.world = world;
        this.controller = controller;
        this.viewport = viewport;
        bodyM = ComponentMapper.getFor(BodyComponent.class);
        energyM = ComponentMapper.getFor(EnergyComponent.class);
        posM = ComponentMapper.getFor(PositionComponent.class);
        shooterM = ComponentMapper.getFor(ShooterComponent.class);
        entityM = ComponentMapper.getFor(EntityTypeComponent.class);
        entityList = new Array<>();
        mycallBack = new QueryCallback() {
            @Override
            public boolean reportFixture(Fixture fixture) {
                if (fixture.getBody().getUserData() instanceof Entity) {
                    Entity entity = (Entity) fixture.getBody().getUserData();
                    BodyComponent bodyComponent = bodyM.get(entity);
                    EntityTypeComponent entityTypeComponent = entityM.get(entity);
                    if (bodyComponent != null && entityTypeComponent != null) {
                        if (entityTypeComponent.entityType == EntityType.ROCK) {
                            if (bodyComponent.body.getMass() < LIFT_STRENGTH) {
                                if (!entityList.contains(entity, true)) {
                                    bodyComponent.body.setGravityScale(0);
                                    entityList.add(entity);
                                }
                            }
                        }
                    }
                }
                return false;
            }
        };
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        Vector3 mousePos = viewport.unproject(new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0));
        mouseX = mousePos.x / Tile.tileSize;
        mouseY = mousePos.y / Tile.tileSize;
        EnergyComponent energy = energyM.get(entity);
        if (controller.elementPull && energy.mana > 1) {
            world.QueryAABB(mycallBack, mouseX - IMPULSE_AOE, mouseY - IMPULSE_AOE, mouseX + IMPULSE_AOE, mouseY + IMPULSE_AOE);
        } else {
            entityList.clear();
        }
        if (controller.explode && energy.mana > 1) {
            for (Entity ball : entityList) {
                if (bodyM.get(ball) != null) {
                    Vector3 dirTmp = posM.get(ball).position.sub(posM.get(entity).position);
                    Vector2 dir = new Vector2(dirTmp.x,dirTmp.y);
                    Body body = bodyM.get(ball).body;
                    body.applyLinearImpulse(dir.nor().scl(IMPULSE_STRENGTH*15), body.getWorldCenter(), true);
                }
            }
            entityList.clear();
            //todo: add bang bang iväg från spelaren.
        } if (controller.implode && energy.mana > 1) {
            for (Entity ball : entityList) {
                if (bodyM.get(ball) != null) {
                    Vector3 dirTmp = posM.get(entity).position.sub(posM.get(ball).position);
                    Vector2 dir = new Vector2(dirTmp.x,dirTmp.y);
                    Body body = bodyM.get(ball).body;
                    body.applyLinearImpulse(dir.nor().scl(IMPULSE_STRENGTH*15), body.getWorldCenter(), true);
                }
            }
            entityList.clear();
            //todo: Add dra skit till spelaren.
        }
        for (Entity ball : entityList) {
            if (bodyM.get(ball) != null) {
                //todo: change to linearvelocity, gradually change the velocity depending on angle to point. gradually depending on the mass of the body being moved.
                Body body = bodyM.get(ball).body;
                Vector2 dir = new Vector2(mouseX - body.getWorldCenter().x, mouseY - body.getWorldCenter().y);
                if(dir.len() < MIN_MULTIPLIER){
                    dir.nor().scl(MIN_MULTIPLIER);
                }else if(dir.len() > MAX_MULTIPLIER){
                    dir.nor().scl(MAX_MULTIPLIER);
                }
                body.setLinearVelocity(dir.scl(3));
                body.setLinearVelocity(MathUtils.lerp(body.getLinearVelocity().x, dir.x, 0.50f), MathUtils.lerp(body.getLinearVelocity().y, dir.y, 0.50f));

                //OLD IMPULSE SYSTEM
                /*if (dir.len() < SLOW_DOWN_ZONE) {
                    body.setLinearVelocity(MathUtils.lerp(body.getLinearVelocity().x, 0, 0.2f), MathUtils.lerp(body.getLinearVelocity().y, 0, 0.2f));

                } else {
                    if (dir.len() > MAX_MULTIPLIER) {
                        dir.nor().scl(MAX_MULTIPLIER * IMPULSE_STRENGTH);
                    } else {
                        dir.scl(IMPULSE_STRENGTH);
                    }
                    body.applyLinearImpulse(dir.scl(IMPULSE_STRENGTH), body.getWorldCenter(), true);
                }
                body.applyForce(new Vector2(0,-GRAVITY/2.5f), body.getWorldCenter(), true);*/

                //energy.mana -= bodyM.get(ball).body.getMass();
            }
        }
        if(checkShoot){
            if (controller.shoot) {
                ShooterComponent shooter = shooterM.get(entity);
                Vector3 playerPos = posM.get(entity).position;
                shooter.dir = new Vector2(mouseX - playerPos.x, mouseY - playerPos.y);
                shooter.shoot = true;
            }
        }
        if(!controller.shoot){
            checkShoot = true;
        }else {
            checkShoot = false;
        }





    }
}
