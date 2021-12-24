package com.zveillette.galo.utilities;

public class Utils {
    public static float getOrbitDays(float orbitRadius) {
        return orbitRadius / (10f + (float) Math.random() * 5f);
    }
}
