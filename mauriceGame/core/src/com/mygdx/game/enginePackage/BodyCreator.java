package com.mygdx.game.enginePackage;

import com.badlogic.gdx.physics.box2d.*;
import com.mygdx.game.enginePackage.BodyMaterial;

import static com.mygdx.game.enginePackage.Constants.*;

public class BodyCreator {
    private World world;



    public BodyCreator(World world) {
        this.world = world;
    }

    public Body makeRectBody(float posx, float posy, float width, float height, BodyMaterial material, BodyDef.BodyType bodyType, boolean fixedRotation, short bitType) {
        BodyDef boxBodyDef = new BodyDef();
        boxBodyDef.type = bodyType;
        boxBodyDef.position.x = posx;
        boxBodyDef.position.y = posy;
        boxBodyDef.fixedRotation = fixedRotation;

        Body boxBody = world.createBody(boxBodyDef);
        PolygonShape polygonShape = new PolygonShape();
        polygonShape.setAsBox(width / 2, height / 2);
        boxBody.createFixture(makeFixture(material, polygonShape, bitType));
        polygonShape.dispose();
        return boxBody;
    }
    public Body makeRectSensor(float posx, float posy, float width, float height, BodyMaterial material, BodyDef.BodyType bodyType, boolean fixedRotation, short bitType) {
        BodyDef boxBodyDef = new BodyDef();
        boxBodyDef.type = bodyType;
        boxBodyDef.position.x = posx;
        boxBodyDef.position.y = posy;
        boxBodyDef.fixedRotation = fixedRotation;

        Body boxBody = world.createBody(boxBodyDef);
        PolygonShape polygonShape = new PolygonShape();
        polygonShape.setAsBox(width / 2, height / 2);
        boxBody.createFixture(makeSensorFixture(material, polygonShape, bitType));
        polygonShape.dispose();
        return boxBody;
    }
    public Body makeCircleSensor(float posx, float posy, float radius, BodyMaterial material, BodyDef.BodyType bodyType, boolean fixedRotation, short bitType) {
        BodyDef boxBodyDef = new BodyDef();
        boxBodyDef.type = bodyType;
        boxBodyDef.position.x = posx;
        boxBodyDef.position.y = posy;
        boxBodyDef.fixedRotation = fixedRotation;

        Body circleBody = world.createBody(boxBodyDef);
        CircleShape circleShape = new CircleShape();
        circleShape.setRadius(radius);
        circleBody.createFixture(makeSensorFixture(material, circleShape, bitType));
        circleShape.dispose();
        return circleBody;
    }

    private FixtureDef makeSensorFixture(BodyMaterial material, Shape shape, short bitType) {
        FixtureDef fixture = new FixtureDef();
        fixture.shape = shape;
        fixture.density = material.getDensity();
        fixture.restitution = material.getRestitution();
        fixture.friction = material.getFriction();
        fixture.isSensor = true;
        fixture.filter.categoryBits = bitType;
        return fixture;
    }

    public Body makeCirclePolyBody(float posx, float posy, float radius, BodyMaterial material, BodyDef.BodyType bodyType, boolean fixedRotation, short bitType) {
        // create a definition
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = bodyType;
        bodyDef.position.x = posx;
        bodyDef.position.y = posy;
        bodyDef.fixedRotation = fixedRotation;

        //create the body to attach bodyDef
        Body circleBody = world.createBody(bodyDef);
        CircleShape circleShape = new CircleShape();
        circleShape.setRadius(radius);
        circleBody.createFixture(makeFixture(material, circleShape, bitType));
        circleShape.dispose();
        return circleBody;
    }

    private FixtureDef makeFixture(BodyMaterial material, Shape shape, short bitType) {
        FixtureDef fixture = new FixtureDef();
        fixture.shape = shape;
        fixture.density = material.getDensity();
        fixture.restitution = material.getRestitution();
        fixture.friction = material.getFriction();
        switch (bitType){
            case CATEGORY_PLAYER:
                fixture.filter.categoryBits = CATEGORY_PLAYER;
                fixture.filter.maskBits = CATEGORY_ENEMY | CATEGORY_SCENERY | CATEGORY_ITEM;
                fixture.filter.groupIndex = -3;
                break;
            case CATEGORY_ENEMY:
                fixture.filter.categoryBits = CATEGORY_ENEMY;
                fixture.filter.maskBits = CATEGORY_FRIENDLY | CATEGORY_ENEMY | CATEGORY_SCENERY | CATEGORY_PLAYER;
                fixture.filter.groupIndex = 2;
                break;
            case CATEGORY_FRIENDLY:
                fixture.filter.categoryBits = CATEGORY_FRIENDLY;
                fixture.filter.maskBits = CATEGORY_FRIENDLY | CATEGORY_ENEMY | CATEGORY_SCENERY;
                fixture.filter.groupIndex = 1;
                break;
            case CATEGORY_SCENERY:
                fixture.filter.categoryBits = CATEGORY_SCENERY;
                fixture.filter.maskBits = CATEGORY_ITEM | CATEGORY_PLAYER | CATEGORY_ENEMY | CATEGORY_FRIENDLY;
                fixture.filter.groupIndex = -1;
                break;
            case CATEGORY_ITEM:
                fixture.filter.categoryBits = CATEGORY_ITEM;
                fixture.filter.maskBits = CATEGORY_SCENERY | CATEGORY_ITEM | CATEGORY_PLAYER | CATEGORY_COLLECTOR;
                fixture.filter.groupIndex = 4;
                break;
            case CATEGORY_COLLECTOR:
                fixture.filter.categoryBits = CATEGORY_COLLECTOR;
                fixture.filter.maskBits = CATEGORY_ITEM;
                fixture.filter.groupIndex = -5;
        }

        return fixture;
    }

}
