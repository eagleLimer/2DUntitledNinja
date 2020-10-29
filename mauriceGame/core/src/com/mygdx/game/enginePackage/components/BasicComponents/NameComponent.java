package com.mygdx.game.enginePackage.components.BasicComponents;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.graphics.Color;

public class NameComponent implements Component {
    public String name;
    public boolean hidden = false;
    public Color color = Color.BLACK;

    public NameComponent(String name) {
        this.name = name;
    }
}
