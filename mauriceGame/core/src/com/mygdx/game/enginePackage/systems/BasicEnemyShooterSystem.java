package com.mygdx.game.enginePackage.systems;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.mygdx.game.enginePackage.BulletInfo;
import com.mygdx.game.enginePackage.EntityType;
import com.mygdx.game.enginePackage.components.PlantShooterComponent;
import com.mygdx.game.enginePackage.components.PositionComponent;
import com.mygdx.game.enginePackage.components.ShooterComponent;

public class BasicEnemyShooterSystem extends IteratingSystem {
    private ComponentMapper<ShooterComponent> shooterM;
    private ComponentMapper<PositionComponent> posM;
    private Array<BulletInfo> newBullets;
    private Entity player;

    public BasicEnemyShooterSystem(Array<BulletInfo> newBullets, Entity player) {
        super(Family.all(ShooterComponent.class, PlantShooterComponent.class).get());
        this.newBullets = newBullets;
        this.player = player;
        shooterM = ComponentMapper.getFor(ShooterComponent.class);
        posM = ComponentMapper.getFor(PositionComponent.class);
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        ShooterComponent shooter = shooterM.get(entity);

        if(shooter.bulletTick <= 0 && (shooter.shoot || shooter.alwaysShoot)){
            Vector3 playerPos = posM.get(player).position;
            Vector3 shooterPos = posM.get(entity).position;
            shooter.dir = new Vector2(playerPos.x - shooterPos.x, playerPos.y - shooterPos.y).nor();
            Vector2 pos = new Vector2(shooterPos.x+shooter.dir.x *shooter.bulletSpawn,shooterPos.y+shooter.dir.y*shooter.bulletSpawn);
            newBullets.add(new BulletInfo(pos,shooter.dir.scl(shooter.bulletType.bulletSpeed), shooter.bulletType));
            shooter.bulletTick = shooter.bulletCd;
            shooter.shoot = false;
        }else{
            shooter.shoot = false;
        }
        shooter.bulletTick -= deltaTime;
    }
}
