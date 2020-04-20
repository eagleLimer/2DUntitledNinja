package com.mygdx.game.game;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.physics.box2d.*;
import com.mygdx.game.components.CollisionComponent;
import com.mygdx.game.components.HealthComponent;
import com.mygdx.game.components.PlayerComponent;

public class MyContactListener implements ContactListener {
    @Override
    public void beginContact(Contact contact) {
        Fixture fa = contact.getFixtureA();
        Fixture fb = contact.getFixtureB();
        if (fa.getBody().getUserData() instanceof Entity) {
            Entity ent = (Entity) fa.getBody().getUserData();
            entityCollision(ent, fb);
            return;
        } else if (fb.getBody().getUserData() instanceof Entity) {
            Entity ent = (Entity) fb.getBody().getUserData();
            entityCollision(ent, fa);
            return;
        }
    }

    private void entityCollision(Entity ent, Fixture fb) {

        if (fb.getBody().getUserData() instanceof Entity) {
            Entity colEnt = (Entity) fb.getBody().getUserData();

            CollisionComponent col = ent.getComponent(CollisionComponent.class);
            CollisionComponent colb = colEnt.getComponent(CollisionComponent.class);

            if (col != null) {
                col.collisionEntity = colEnt;
            } else if (colb != null) {
                colb.collisionEntity = ent;
            }
        }
    }

    @Override
    public void endContact(Contact contact) {

    }

    @Override
    public void preSolve(Contact contact, Manifold oldManifold) {

    }

    @Override
    public void postSolve(Contact contact, ContactImpulse impulse) {
        Fixture fa = contact.getFixtureA();
        Fixture fb = contact.getFixtureB();
        //b2body.body.applyLinearImpulse(0, velocity.jumpForce, b2body.body.getWorldCenter().x,b2body.body.getWorldCenter().y, true);

        //fa.getBody().applyLinearImpulse(impulse.getCount(),impulse.getCount(),fa.getBody().getWorldCenter().x, fa.getBody().getWorldCenter().y,true);
        if (fa.getBody().getUserData() instanceof Entity) {
            Entity ent = (Entity) fa.getBody().getUserData();
            entityImpact(ent, fb, impulse);
            return;
        }  if (fb.getBody().getUserData() instanceof Entity) {
            Entity ent = (Entity) fb.getBody().getUserData();
            entityImpact(ent, fa, impulse);
            return;
        }
    }

    private void entityImpact(Entity ent, Fixture fb, ContactImpulse impulse) {
        if(ent.getComponent(PlayerComponent.class) != null) {
            HealthComponent health = ent.getComponent(HealthComponent.class);
            if (health != null) {
                float damage = 0;
                for (float imp : impulse.getNormalImpulses()
                ) {
                    damage += imp;
                }
                if (damage > 2) {
                    health.health -= damage;
                }
                System.out.println(damage);
            }
        }
    }
}
