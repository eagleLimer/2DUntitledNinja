package com.mygdx.game.preferences;

import java.util.Objects;

public class Resolution {
    public final int width;
    public final int height;


    @Override
    public String toString() {
        return width + "x" + height;
    }

    public Resolution(int width, int height) {
        this.width = width;
        this.height = height;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Resolution resolution = (Resolution) o;
        return width == resolution.width &&
                height == resolution.height;
    }

    @Override
    public int hashCode() {
        return Objects.hash(width, height);
    }
}
