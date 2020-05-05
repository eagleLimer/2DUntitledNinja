package com.mygdx.game.enginePackage;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.physics.box2d.*;
import com.mygdx.game.enginePackage.components.*;

public class MyContactListener implements ContactListener {
    //THIS IS USEFUL IF WE WANT COLLISIONS TO IGNORE VELOCITY AND MASS

    //todo: THIS NEEDS TESTING, DOES ALL ENTITIES GET REMOVED FROM LIST THAT SHOULD BE REMOVED
    //todo: CHANGE MAPTILES TO ANOTHER TYPE THAN ENTITY, WE DONT REALLY NEED TO DO ANYTHING SPECIAL WITH EVERY MAPTILE

    public void beginContact(Contact contact) {
        Fixture fa = contact.getFixtureA();
        Fixture fb = contact.getFixtureB();
        if (fa.getBody().getUserData() instanceof Entity && fb.getBody().getUserData() instanceof Entity) {
            Entity ent = (Entity) fa.getBody().getUserData();
            Entity ent2 = (Entity) fb.getBody().getUserData();

            entityCollision2(ent, ent2);
            return;
        }
    }
    private void entityCollision2(Entity ent, Entity ent2) {

            CollisionComponent col = ent.getComponent(CollisionComponent.class);
            CollisionComponent colb = ent2.getComponent(CollisionComponent.class);

            if (col != null) {
                col.collidingEntities.add(ent2);
            }
            if (colb != null) {
                colb.collidingEntities.add(ent);
            }
        }

    @Override
    public void endContact(Contact contact) {
        Fixture fa = contact.getFixtureA();
        Fixture fb = contact.getFixtureB();
        if (fa.getBody().getUserData() instanceof Entity && fb.getBody().getUserData() instanceof Entity) {
            Entity ent = (Entity) fa.getBody().getUserData();
            Entity ent2 = (Entity) fb.getBody().getUserData();
            endedCollision(ent, ent2);
        }
    }

    private void endedCollision(Entity ent, Entity ent2) {

            CollisionComponent col = ent.getComponent(CollisionComponent.class);
            CollisionComponent colb = ent2.getComponent(CollisionComponent.class);

            if (col != null) {
                if(col.collidingEntities.contains(ent2, true)) {
                    col.collidingEntities.removeValue(ent2, true);
                }

            } if (colb != null) {
                if(colb.collidingEntities.contains(ent, true)) {
                    colb.collidingEntities.removeValue(ent, true);
                }
        }
    }

    /*
    If the fixture never needs to collide with anything you could make it a sensor.
    If you need it to collide with some things but not others you could do contact->SetEnabled(false)
    in the PreSolve of the collision listener, depending on what it collided with.
     */
    @Override
    public void preSolve(Contact contact, Manifold oldManifold) {
        //todo: move postsolve to presolve and deal damage based on velocity and mass of colliding object.
        // maybe check if entity has impactComponent, if true then set damage to velocity*Mass*multiplier.
        // after that collision will be handled in beginContact? or in separate new system.
    }

    //THIS IS USEFUL IF WE WANT COLLISION DAMAGE TO DEPEND ON VELOCITY AND MASS.
    @Override
    public void postSolve(Contact contact, ContactImpulse impulse) {
        Fixture fa = contact.getFixtureA();
        Fixture fb = contact.getFixtureB();
        //b2body.body.applyLinearImpulse(0, velocity.jumpForce, b2body.body.getWorldCenter().x,b2body.body.getWorldCenter().y, true);

        //fa.getBody().applyLinearImpulse(impulse.getCount(),impulse.getCount(),fa.getBody().getWorldCenter().x, fa.getBody().getWorldCenter().y,true);
        if (fa.getBody().getUserData() instanceof Entity) {
            Entity ent = (Entity) fa.getBody().getUserData();
            entityImpact(ent, fb, impulse);
            //return;
        }
        if (fb.getBody().getUserData() instanceof Entity) {
            Entity ent = (Entity) fb.getBody().getUserData();
            entityImpact(ent, fa, impulse);
            return;
        }
    }

    private void entityImpact(Entity ent, Fixture fb, ContactImpulse impulse) {
        /*if (ent.getComponent(PlayerComponent.class) != null) {
            HealthComponent health = ent.getComponent(HealthComponent.class);
            if (health != null) {
                float damage = 0;
                for (float imp : impulse.getNormalImpulses()
                ) {
                    damage += imp;
                }
                if (damage > 2) {
                    health.health -= damage/2;
                }
            }
        }*/
        if (ent.getComponent(CollisionTypeComponent.class)== null)return;
        if(ent.getComponent(CollisionTypeComponent.class).type == CollisionTypeComponent.ENEMY){
            if(fb.getBody().getUserData() instanceof Entity){
                Entity entb = (Entity) fb.getBody().getUserData();
                EntityTypeComponent type = entb.getComponent(EntityTypeComponent.class);
                if(type != null){
                    if(type.entityType == EntityType.ROCK){
                        HealthComponent health = ent.getComponent(HealthComponent.class);
                        if (health != null) {
                            float damage = 0;
                            for (float imp : impulse.getNormalImpulses()
                            ) {
                                damage += imp;
                            }
                            if (damage > 4) {
                                health.health -= damage*5;
                            }
                        }
                    }
                }
            }
        }
    }
}
