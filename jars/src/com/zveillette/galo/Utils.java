package com.zveillette.galo;

public class Utils {
    static float getOrbitDays(float orbitRadius) {
        return orbitRadius / (10f + (float) Math.random() * 5f);
    }
}
