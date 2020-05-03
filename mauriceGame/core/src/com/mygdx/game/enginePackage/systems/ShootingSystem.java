package com.mygdx.game.enginePackage.systems;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.mygdx.game.enginePackage.components.ActivatedComponent;
import com.mygdx.game.enginePackage.components.BasicShooterComponent;
import com.mygdx.game.enginePackage.components.PositionComponent;
import com.mygdx.game.enginePackage.components.ShooterComponent;
import com.mygdx.game.enginePackage.BulletInfo;

public class ShootingSystem extends IteratingSystem {
    private ComponentMapper<ShooterComponent> shooterM;
    private ComponentMapper<PositionComponent> posM;
    private Array<BulletInfo> newBullets;
    public ShootingSystem(Array<BulletInfo> newBullets) {
        super(Family.all(ShooterComponent.class, BasicShooterComponent.class, ActivatedComponent.class).get());
        this.newBullets = newBullets;
        shooterM = ComponentMapper.getFor(ShooterComponent.class);
        posM = ComponentMapper.getFor(PositionComponent.class);
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        ShooterComponent shooter = shooterM.get(entity);

        if(shooter.bulletTick <= 0 && (shooter.shoot || shooter.alwaysShoot)){
            Vector3 playerPos = posM.get(entity).position;
            Vector2 dir = shooter.dir.nor();
            Vector2 pos = new Vector2(playerPos.x+dir.x *shooter.bulletSpawn,playerPos.y+dir.y*shooter.bulletSpawn);
            newBullets.add(new BulletInfo(pos,dir.scl(shooter.bulletType.bulletSpeed), shooter.bulletType));
            shooter.bulletTick = shooter.bulletCd;
            shooter.shoot = false;
        }else{
            shooter.shoot = false;
        }
        shooter.bulletTick -= deltaTime;
    }
}
