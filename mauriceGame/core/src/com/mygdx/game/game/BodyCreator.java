package com.mygdx.game.game;

import com.badlogic.gdx.physics.box2d.*;

public class BodyCreator {
    private World world;

    public BodyCreator(World world) {
        this.world = world;
    }

    public Body makeRectBody(float posx, float posy, float width, float height, BodyMaterial material, BodyDef.BodyType bodyType, boolean fixedRotation) {
        BodyDef boxBodyDef = new BodyDef();
        boxBodyDef.type = bodyType;
        boxBodyDef.position.x = posx;
        boxBodyDef.position.y = posy;
        boxBodyDef.fixedRotation = fixedRotation;

        Body boxBody = world.createBody(boxBodyDef);
        PolygonShape polygonShape = new PolygonShape();
        polygonShape.setAsBox(width / 2, height / 2);
        boxBody.createFixture(makeFixture(material, polygonShape));
        polygonShape.dispose();
        return boxBody;
    }

    public Body makeCirclePolyBody(float posx, float posy, float radius, BodyMaterial material, BodyDef.BodyType bodyType, boolean fixedRotation) {
        // create a definition
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = bodyType;
        bodyDef.position.x = posx;
        bodyDef.position.y = posy;
        bodyDef.fixedRotation = fixedRotation;

        //create the body to attach bodyDef
        Body circleBody = world.createBody(bodyDef);
        CircleShape circleShape = new CircleShape();
        circleShape.setRadius(radius * 2 / 2);
        circleBody.createFixture(makeFixture(material, circleShape));
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
