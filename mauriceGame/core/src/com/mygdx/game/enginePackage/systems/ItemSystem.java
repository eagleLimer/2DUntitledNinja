package com.mygdx.game.enginePackage.systems;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.QueryCallback;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.mygdx.game.enginePackage.components.*;

//todo: change this! world query is really inefficient, would be much better to just make another sensorBody over player that sucks the coins.
public class ItemSystem extends IteratingSystem {
    private static final float PULL_STRENGTH = 5;
    private ComponentMapper<CollectorComponent> collectorM;
    private ComponentMapper<ItemComponent> itemM;
    private ComponentMapper<BodyComponent> bodyM;
    private ComponentMapper<PositionComponent> posM;
    private ComponentMapper<InventoryComponent> inventoryM;
    private QueryCallback myCallBack;
    private Array<Entity> entityList;
    private World world;
    private Array<Entity> toBeRemoved;
    private float countDown = 1;

    public ItemSystem(World world, Viewport viewport, Array<Entity> toBeRemoved) {
        super(Family.all(CollectorComponent.class, ActivatedComponent.class).get());
        this.world = world;
        this.toBeRemoved = toBeRemoved;
        collectorM = ComponentMapper.getFor(CollectorComponent.class);
        itemM = ComponentMapper.getFor(ItemComponent.class);
        bodyM = ComponentMapper.getFor(BodyComponent.class);
        posM = ComponentMapper.getFor(PositionComponent.class);
        inventoryM = ComponentMapper.getFor(InventoryComponent.class);
        entityList = new Array<>();
        myCallBack = new QueryCallback() {
            @Override
            public boolean reportFixture(Fixture fixture) {
                if (fixture.getBody().getUserData() instanceof Entity) {
                    Entity entity = (Entity) fixture.getBody().getUserData();
                    if(itemM.get(entity)!=null) {
                        if (!entityList.contains(entity, true)) {
                            entityList.add(entity);
                        }
                    }
                }
                return false;
            }
        };
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        PositionComponent positionComponent = posM.get(entity);
        float collectingSize = collectorM.get(entity).pullDistance;
        countDown -= deltaTime;
        if(countDown< 0) {
            world.QueryAABB(myCallBack, positionComponent.position.x - collectingSize, positionComponent.position.y - collectingSize, positionComponent.position.x + collectingSize, positionComponent.position.y + collectingSize);
            countDown = 1;
        }
        for (Entity item: entityList) {
            System.out.println("we have something close!"+ entityList.size);
            Vector3 tmpDir = posM.get(entity).position.sub(posM.get(item).position);
            Vector2 dir = new Vector2(tmpDir.x,tmpDir.y);
            if(dir.len() < collectorM.get(entity).collectingDistance){
                ItemComponent itemComponent = itemM.get(item);
                InventoryComponent inventory = inventoryM.get(entity);
                switch (itemComponent.item.getItemType()){
                    case COIN:
                        inventory.coins++;
                        break;
                    default:
                        inventory.items.add(itemComponent.item);
                }
                toBeRemoved.add(item);
                entityList.removeValue(item,true);

            }else if(dir.len() > collectorM.get(entity).pullDistance){
                entityList.removeValue(item,true);
            }else{
                BodyComponent bodyComponent = bodyM.get(item);
                System.out.println("pull coin");
                float pullForce = PULL_STRENGTH/dir.len(); // pull stronger when item closer
                bodyComponent.body.applyForce(dir.nor().scl(pullForce),bodyComponent.body.getWorldCenter(),true);
            }

        }
    }
}
