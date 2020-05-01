package com.mygdx.game.enginePackage.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class HealthComponent implements Component {
    public float maxHealth;
    public float health = maxHealth;
    public float healthWidth = 60;
    public float healthHeight = 10;
    public float healthReg;
    public boolean alwaysHidden = false;
    public boolean hidden = false;

    public TextureRegion region = createTexture((int) healthWidth, (int) healthHeight, Color.RED);

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
