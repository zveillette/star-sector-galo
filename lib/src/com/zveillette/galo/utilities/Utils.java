package com.zveillette.galo.utilities;

import java.util.ArrayList;
import java.util.List;
import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.CampaignFleetAPI;
import com.fs.starfarer.api.campaign.PlanetAPI;
import com.fs.starfarer.api.campaign.SectorAPI;
import com.fs.starfarer.api.campaign.StarSystemAPI;
import com.fs.starfarer.api.impl.campaign.procgen.StarSystemGenerator.StarSystemType;

public class Utils {
    public static Float removeCrew(CampaignFleetAPI fleet, Float total, Float mod) {
        return removeCrew(fleet, total, mod, null, null);
    }

    public static float removeCrew(CampaignFleetAPI fleet, Float total, Float mod, Float min, Float max) {
        float crews = (float) Math.floor(total * mod);
        if (min != null && crews < min) {
            crews = min;
        }

        if (max != null && crews > max) {
            crews = max;
        }

        fleet.getCargo().removeCrew((int) crews);
        return crews;
    }

    /**
     * Pick a random starsystem that follows given rules
     */
    public static StarSystemAPI getRandomStarSystem(boolean isProcgen, boolean hasBlackHole, StarSystemType sysType) {
        SectorAPI sector = Global.getSector();
        List<StarSystemAPI> starSystems = sector.getStarSystems();
        List<StarSystemAPI> possibleStarSystems = new ArrayList<StarSystemAPI>();

        for (final StarSystemAPI sys : starSystems) {
            if ((isProcgen && !sys.isProcgen())
                    || (!isProcgen && sys.isProcgen())) {
                continue;
            }

            if (sysType != null && !sys.getType().equals(sysType)) {
                continue;
            }

            if ((!hasBlackHole && sys.hasBlackHole())
                    || (hasBlackHole && !sys.hasBlackHole())) {
                continue;
            }

            possibleStarSystems.add(sys);
        }

        if (possibleStarSystems.isEmpty()) {
            return null;
        }

        return possibleStarSystems.get((int) (Math.random() * possibleStarSystems.size()));
    }

    /**
     * Pick a random planet that follows given rules
     */
    public static PlanetAPI getRandomPlanet(boolean isProcgen, boolean isGas, String type) {
        SectorAPI sector = Global.getSector();
        List<StarSystemAPI> starSystems = sector.getStarSystems();
        List<PlanetAPI> possiblePlanets = new ArrayList<PlanetAPI>();

        for (final StarSystemAPI sys : starSystems) {
            if (isProcgen && !sys.isProcgen()) {
                continue;
            }

            for (final PlanetAPI planet : sys.getPlanets()) {
                if (planet.isStar()) {
                    continue;
                }

                if (isGas && !planet.isGasGiant() ||
                        !isGas && planet.isGasGiant()) {
                    continue;
                }

                if (type != null && !planet.getTypeId().equals(type)) {
                    continue;
                }

                possiblePlanets.add(planet);
            }
        }

        if (possiblePlanets.isEmpty()) {
            return null;
        }

        return possiblePlanets.get((int) (Math.random() * possiblePlanets.size()));
    }
}