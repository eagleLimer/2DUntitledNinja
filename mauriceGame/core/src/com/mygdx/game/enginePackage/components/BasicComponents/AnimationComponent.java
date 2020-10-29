package com.mygdx.game.enginePackage.components.BasicComponents;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.utils.IntMap;

public class AnimationComponent implements Component {
    public IntMap<Animation> animationMap = new IntMap<Animation>();

}
