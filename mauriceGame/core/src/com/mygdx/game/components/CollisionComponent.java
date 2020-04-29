package com.mygdx.game.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.utils.Array;

public class CollisionComponent implements Component {
    public Array<Entity> collidingEntities = new Array<>();
}
