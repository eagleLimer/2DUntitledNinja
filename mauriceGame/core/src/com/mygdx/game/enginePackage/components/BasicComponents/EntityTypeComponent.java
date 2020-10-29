package com.mygdx.game.enginePackage.components.BasicComponents;

import com.badlogic.ashley.core.Component;
import com.mygdx.game.enginePackage.EntityType;

public class EntityTypeComponent implements Component {
    public EntityType entityType;

    public EntityTypeComponent(EntityType entityType) {
        this.entityType = entityType;
    }
}
