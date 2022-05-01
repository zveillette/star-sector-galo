package com.zveillette.galo.utilities;

import java.util.ArrayList;
import java.util.List;

import com.fs.starfarer.api.campaign.SectorEntityToken;
import com.fs.starfarer.api.campaign.StarSystemAPI;
import com.fs.starfarer.api.util.Pair;

public class OrbitUtils {
    public static float getOrbitDays(float orbitRadius) {
        return orbitRadius / (10f + (float) Math.random() * 5f);
    }

    public static float getFloatBetween(float min, float max) {
        return (float) Math.random() * (max - min) + min;
    }

    public static List<SectorEntityToken> getEntitiesWithFocus(SectorEntityToken focus) {
        List<SectorEntityToken> entities = new ArrayList<SectorEntityToken>();
        for (SectorEntityToken entity : focus.getStarSystem().getAllEntities()) {
            if (entity.getOrbitFocus() == null
                    || !entity.getOrbitFocus().equals(entity)) {
                continue;
            }
            entities.add(entity);
        }
        return entities;
    }

    public static float getAvailableOrbitRadius(StarSystemAPI sys, float planetSize) {
        List<Pair<Float, Float>> orbitingEntities = new ArrayList<>();

        final float margin = 250f;
        float orbitRadius = 800f;

        // Account for star
        if (!sys.isNebula()) {
            orbitRadius = sys.getStar().getRadius() * 3f + (margin * 2f);
        }

        // Add some randomness to where the orbit should start
        orbitRadius += Math.random() * 3000f;

        // Build list of all entities orbiting in the system
        for (SectorEntityToken entity : sys.getAllEntities()) {
            if (entity.getOrbit() == null
                    || (!entity.getOrbitFocus().isStar() && !sys.isNebula())
                    || sys.getStar().equals(entity)) {
                continue;
            }

            List<SectorEntityToken> satellites = getEntitiesWithFocus(entity);
            if (satellites.isEmpty()) {
                orbitingEntities.add(new Pair<>(
                        entity.getCircularOrbitRadius() - entity.getRadius() - margin,
                        entity.getCircularOrbitRadius() + entity.getRadius() + margin));
            } else {
                float farthestEntity = 0f;
                float satelliteRadius = 0f;
                for (SectorEntityToken satellite : satellites) {
                    satelliteRadius = satellite.getCircularOrbitRadius() + satellite.getRadius();
                    if (satelliteRadius > farthestEntity) {
                        farthestEntity = satelliteRadius;
                    }
                }

                orbitingEntities.add(new Pair<>(
                        entity.getCircularOrbitRadius() - satelliteRadius - margin,
                        entity.getCircularOrbitRadius() + satelliteRadius + margin));
            }
        }

        // Increase orbit until we find a suitable spot that doesn't collide with any others
        float minOrbit = 0f;
        float maxOrbit = 0f;
        int maxIteration = 0;
        boolean orbitFound = false;
        while (!orbitFound && maxIteration++ < 10) {
            minOrbit = orbitRadius - planetSize;
            maxOrbit = orbitRadius + planetSize;

            orbitFound = true;
            for (Pair<Float, Float> entityRadius : orbitingEntities) {
                if (maxOrbit >= entityRadius.one
                        && minOrbit <= entityRadius.two) {
                    orbitRadius = entityRadius.two + planetSize + margin;
                    orbitFound = false;
                    break;
                }
            }
        }

        return orbitRadius;
    }
}
