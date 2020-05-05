package com.mygdx.game.enginePackage.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;

public class PositionComponent implements Component {
    public final Vector3 position;
    public final Vector2 scale = new Vector2(1.0f, 1.0f);
    public float rotation = 0.0f;

    public boolean isHidden = false;

    public PositionComponent(Vector3 position) {
        this.position = position;
    }
}
