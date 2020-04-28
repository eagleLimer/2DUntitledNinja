package com.mygdx.game.systems;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.QueryCallback;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.mygdx.game.components.*;
import com.mygdx.game.game.BulletInfo;
import com.mygdx.game.game.BulletType;
import com.mygdx.game.game.KeyboardController;
import com.mygdx.game.game.Tile;

public class PlayerPowerSystem extends IteratingSystem {
    //todo: should move this also lol
    private static final float IMPULSE_STRENGTH = 0.2f;
    private static final float LIFT_STRENGTH = 1;
    private static final float IMPULSE_AOE = 1;
    private World world;
    private KeyboardController controller;
    private Viewport viewport;
    private ComponentMapper<BodyComponent> bodyM;
    private ComponentMapper<TypeComponent> typeM;
    private ComponentMapper<EnergyComponent> energyM;
    private ComponentMapper<PositionComponent> posM;
    private ComponentMapper<ShooterComponent> shooterM;
    private float mouseX;
    private float mouseY;
    private QueryCallback mycallBack;
    private Array<Entity> entityList;

    // todo: Disable balls before impact with enemy to make it more balanced. (alternative add cooldown to when enemy can be hit by same ball again).
    public PlayerPowerSystem(World world, KeyboardController controller, final Viewport viewport) {
        super(Family.all(PlayerComponent.class).get());
        this.world = world;
        this.controller = controller;
        this.viewport = viewport;
        bodyM = ComponentMapper.getFor(BodyComponent.class);
        typeM = ComponentMapper.getFor(TypeComponent.class);
        energyM = ComponentMapper.getFor(EnergyComponent.class);
        posM = ComponentMapper.getFor(PositionComponent.class);
        shooterM = ComponentMapper.getFor(ShooterComponent.class);

        entityList = new Array<>();
        mycallBack = new QueryCallback() {
            @Override
            public boolean reportFixture(Fixture fixture) {
                if (fixture.getBody().getUserData() instanceof Entity) {
                    Entity entity = (Entity) fixture.getBody().getUserData();
                    BodyComponent bodyComponent = bodyM.get(entity);
                    if (bodyComponent != null && typeM.get(entity).type == TypeComponent.BALL) {
                        if (bodyComponent.body.getMass() < LIFT_STRENGTH) {
                            if (!entityList.contains(entity, true)) {
                                entityList.add(entity);
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
        if (controller.rightButton && energy.mana > 1) {
            world.QueryAABB(mycallBack, mouseX - IMPULSE_AOE, mouseY - IMPULSE_AOE, mouseX + IMPULSE_AOE, mouseY + IMPULSE_AOE);
        } else {
            entityList.clear();
        }
        for (Entity ball : entityList) {
            if (bodyM.get(ball) != null) {
                //todo: change to linearvelocity, gradually change the velocity depending on angle to point. gradually depending on the mass of the body being moved.
                Body body = bodyM.get(ball).body;
                float xDiff = mouseX - body.getWorldCenter().x;
                float yDiff = mouseY - body.getWorldCenter().y;
                Vector2 dir = new Vector2(mouseX - body.getWorldCenter().x, mouseY - body.getWorldCenter().y).nor();

                body.applyLinearImpulse(dir.scl(IMPULSE_STRENGTH), body.getWorldCenter(), true);
                energy.mana -= bodyM.get(ball).body.getMass();
                /*if (Math.abs(body.getLinearVelocity().x) > MAX_SPEED) {
                    body.setLinearVelocity(body.getLinearVelocity().x * 0.9f, body.getLinearVelocity().y);
                }
                if (Math.abs(body.getLinearVelocity().y) > MAX_SPEED) {
                    body.setLinearVelocity(body.getLinearVelocity().x, body.getLinearVelocity().y * 0.9f);
                }*/
            }
        }
        if(controller.leftButton){
            ShooterComponent shooter = shooterM.get(entity);
            Vector3 playerPos = posM.get(entity).position;
            shooter.dir = new Vector2(mouseX-playerPos.x, mouseY - playerPos.y);
            shooter.shoot = true;
        }


    }
}
