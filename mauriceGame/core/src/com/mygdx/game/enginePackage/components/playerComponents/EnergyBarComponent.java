package com.mygdx.game.enginePackage.components.playerComponents;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class EnergyBarComponent implements Component {
    public float energyWidth = 60;

    public float energyHeight = 10;

    public TextureRegion region;
    public EnergyBarComponent(float energyWidth, float energyHeight) {
        this.energyWidth = energyWidth;
        this.energyHeight = energyHeight;
        region = createTexture((int) energyWidth, (int) energyHeight, Color.ORANGE);
    }

    private TextureRegion createTexture(int width, int height, Color color) {
        Pixmap pixmap = new Pixmap(width, height, Pixmap.Format.RGBA8888);
        pixmap.setColor(color);
        pixmap.fillRectangle(0, 0, width, height);
        Texture texture = new Texture(pixmap);
        TextureRegion region = new TextureRegion(texture, 0, 0, width, height);
        pixmap.dispose();
        return region;
    }
}
