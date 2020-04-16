package com.mygdx.game.preferences;

public class Preffies {
    public static final boolean BASE_FULLSCREEN = false;
    public static final Resolution[] RESOLUTIONS = {new Resolution(640, 480), new Resolution(800, 600), new Resolution(960, 720),
            new Resolution(1024, 768), new Resolution(1280, 960), new Resolution(1400, 1050), new Resolution(1440, 1080),
            new Resolution(1600, 1200), new Resolution(1856, 1392), new Resolution(1920, 1440), new Resolution(2048, 1536),
            new Resolution(1280, 800), new Resolution(1440, 900), new Resolution(1680, 1050), new Resolution(1920, 1200), new Resolution(2560, 1600),
            new Resolution(1024, 576), new Resolution(1152, 648), new Resolution(1280, 720), new Resolution(1366, 768), new Resolution(1600, 900),
            new Resolution(1920, 1080), new Resolution(2560, 1440), new Resolution(3840, 2160), new Resolution(7680, 4320)};
    public static final Resolution BASE_RESOLUTION = RESOLUTIONS[0];
    public static final String FILE_NAME = "MyPreferences";

    public static final String WIDTH_KEY = "width";
    public static final String HEIGHT_KEY = "height";
    public static final String FULLSCREEN_KEY = "fullscreen";

}
