package com.rbelcyr.kia.sol.Enitities;


import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;

public class Sensor{
    final float PIXELS_TO_METERS = 100f;

    Color detectedColor = Color.WHITE;
    public Body body;
    public int height = 7;
    public int width = 15;

    public Sensor(World world,Vector2 position) {
        this.setDetectedColor(Color.GRAY);
        BodyDef sensorDef = new BodyDef();
        sensorDef.type = BodyDef.BodyType.StaticBody;
        sensorDef.position.set(position);
        Body sensorBody = world.createBody(sensorDef);
        PolygonShape shape = new PolygonShape();
        shape.setAsBox(width/PIXELS_TO_METERS,height/PIXELS_TO_METERS);
        FixtureDef sensorFix = new FixtureDef();
        sensorFix.shape = shape;
        sensorFix.isSensor = true;
        sensorBody.createFixture(sensorFix);
        shape.dispose();
        this.body = sensorBody;
        sensorBody.setUserData(this);
    }

    public Sensor(World world,Vector2 position, int height,int width) {
        this.setDetectedColor(Color.GRAY);
        BodyDef sensorDef = new BodyDef();
        sensorDef.type = BodyDef.BodyType.StaticBody;
        sensorDef.position.set(position);
        Body sensorBody = world.createBody(sensorDef);
        PolygonShape shape = new PolygonShape();
        shape.setAsBox(width/PIXELS_TO_METERS,height/PIXELS_TO_METERS);
        FixtureDef sensorFix = new FixtureDef();
        sensorFix.shape = shape;
        sensorFix.isSensor = true;
        sensorBody.createFixture(sensorFix);
        shape.dispose();
        this.body = sensorBody;
        sensorBody.setUserData(this);
    }


    public Color getDetectedColor() {
        return detectedColor;
    }

    public void setDetectedColor(Color detectedColor) {
        this.detectedColor = detectedColor;
    }

}
