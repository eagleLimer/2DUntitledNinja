package com.mygdx.game.game;

import com.badlogic.gdx.physics.box2d.*;

public class BodyCreator {
    private World world;
    private float tilesToMetres;
    public BodyCreator(World world){
        this.world = world;
        tilesToMetres = (float)1/Tile.tileSize;
    }

    public Body makeRectBody(float posx, float posy, float width,float height, BodyMaterial material, BodyDef.BodyType bodyType, boolean fixedRotation){
        float tilesToMeters= (float)1/Tile.tileSize;
        BodyDef boxBodyDef = new BodyDef();
        boxBodyDef.type = bodyType;
        boxBodyDef.position.x = posx*tilesToMeters;
        boxBodyDef.position.y = posy*tilesToMeters;
        boxBodyDef.fixedRotation = fixedRotation;

        Body boxBody = world.createBody(boxBodyDef);
        PolygonShape polygonShape = new PolygonShape();
        polygonShape.setAsBox((width/2)*tilesToMeters,(height/2)*tilesToMeters);
        boxBody.createFixture(makeFixture(material,polygonShape));
        polygonShape.dispose();
        return boxBody;
    }
    public Body makeCirclePolyBody(float posx, float posy, float radius, BodyMaterial material, BodyDef.BodyType bodyType, boolean fixedRotation){
        // create a definition
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = bodyType;
        bodyDef.position.x = posx*tilesToMetres;
        bodyDef.position.y = posy*tilesToMetres;
        bodyDef.fixedRotation = fixedRotation;

        //create the body to attach bodyDef
        Body circleBody = world.createBody(bodyDef);
        CircleShape circleShape = new CircleShape();
        circleShape.setRadius((radius /2)*tilesToMetres);
        circleBody.createFixture(makeFixture(material,circleShape));
        circleShape.dispose();
        return circleBody;
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
