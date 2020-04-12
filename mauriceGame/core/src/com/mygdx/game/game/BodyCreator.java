package com.mygdx.game.game;

import com.badlogic.gdx.physics.box2d.*;

public class BodyCreator {
    World world;
    public BodyCreator(World world){
        this.world = world;
    }

    public Body makeRectBody(float posx, float posy, float width,float height, BodyMaterial material, BodyDef.BodyType bodyType, boolean fixedRotation){
        // create a definition
        float tilesToMeters= (float)1/Tile.tileSize;
        System.out.println(tilesToMeters);
        BodyDef boxBodyDef = new BodyDef();
        boxBodyDef.type = bodyType;
        boxBodyDef.position.x = posx*tilesToMeters;
        boxBodyDef.position.y = posy*tilesToMeters;
        boxBodyDef.fixedRotation = fixedRotation;

        //create the body to attach said definition
        Body boxBody = world.createBody(boxBodyDef);
        PolygonShape polygonShape = new PolygonShape();
        polygonShape.setAsBox((width/2)*tilesToMeters,(height/2)*tilesToMeters);
        boxBody.createFixture(makeFixture(material,polygonShape));
        polygonShape.dispose();
        return boxBody;
    }

    private FixtureDef makeFixture(BodyMaterial material, Shape shape) {
        FixtureDef fixture = new FixtureDef();
        fixture.shape = shape;
        fixture.density = material.getDensity();
        fixture.restitution = material.getRestitution();
        fixture.friction = material.getFriction();

        return fixture;
    }

}
