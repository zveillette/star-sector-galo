package com.zveillette.galo.story;

import java.util.List;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.PlanetAPI;
import com.fs.starfarer.api.campaign.SectorEntityToken;
import com.fs.starfarer.api.campaign.StarSystemAPI;
import com.fs.starfarer.api.campaign.econ.MarketAPI;
import com.fs.starfarer.api.impl.campaign.ids.Conditions;
import com.fs.starfarer.api.impl.campaign.rulecmd.salvage.special.ShipRecoverySpecial.ShipCondition;
import com.zveillette.galo.GaloPlugin;
import com.zveillette.galo.utilities.OrbitUtils;
import com.zveillette.galo.utilities.SalvageFactory;
import com.zveillette.galo.utilities.Utils;
import com.zveillette.galo.utilities.SalvageFactory.RECOVER;

import org.apache.log4j.Logger;

import data.scripts.campaign.econ.GL_Conditions;

public class GaloStoryCoordinator {
    private final static Logger logger = Global.getLogger(GaloPlugin.class);
    private final static String GALO_ID = "gl_galo";
    private final static String GALO_MARKET_ID = "gl_galo_market";
    private final static String GALO_NAME = "Galo";
    private final static String TOMB_WORLD_PLANET_TYPE = "GL_tomb";
    private final static String UNIQUE_DERELICT_TAG = "gl_unique_derelict";

    private final static String STAGE_MEM_KEY = "gl_stage";

    public static enum STAGES {
        NOT_STARTED,
        DERELICT_FOUND
    }

    /**
     * Add Galo planet and unique derelict that starts the story
     */
    public static void init() {
        // Get a system that isn't the core worlds
        StarSystemAPI randomSys = Utils.getRandomStarSystem(true, false, null);

        // Spawn planet
        final float planetSize = 135f;
        final float orbitRadius = OrbitUtils.getAvailableOrbitRadius(randomSys, planetSize, null);
        PlanetAPI galoPlanet = randomSys.addPlanet(GALO_ID, randomSys.getCenter(), GALO_NAME,
                TOMB_WORLD_PLANET_TYPE, 12f, planetSize, orbitRadius,
                OrbitUtils.getOrbitDays(orbitRadius));

        // Create market
        MarketAPI galoMarket = Global.getFactory().createMarket(GALO_MARKET_ID, GALO_NAME, 0);
        galoMarket.setPlanetConditionMarketOnly(true);
        galoMarket.addCondition(GL_Conditions.TOMB_WORLD);
        galoMarket.addCondition(Conditions.DENSE_ATMOSPHERE);
        galoMarket.addCondition(Conditions.ORE_ULTRARICH);
        galoMarket.addCondition(Conditions.RUINS_EXTENSIVE);
        galoMarket.setPrimaryEntity(galoPlanet);
        galoPlanet.setMarket(galoMarket);

        logger.info("Tombworld '" + galoPlanet.getName() + "' created in '" + randomSys.getName() + "'");

        // Add some derelicts around it
        float planetRadius = galoPlanet.getRadius() + 100f;
        SalvageFactory.addDerelict(randomSys, galoPlanet, "brawler_Assault", ShipCondition.BATTERED,
                planetRadius + 50f, RECOVER.YES);
        SalvageFactory.addDerelict(randomSys, galoPlanet, "conquest_Standard", ShipCondition.AVERAGE,
                planetRadius + 75f, RECOVER.WITH_STORY_P);
        SalvageFactory.addDerelict(randomSys, galoPlanet, "condor_Strike", ShipCondition.WRECKED,
                planetRadius + 90f, RECOVER.YES);

        // Spawn the unique derelict that prompts the story
        float uniqueDerelictOrbitRadius = OrbitUtils.getAvailableOrbitRadius(randomSys, 25f, 3000f);
        SectorEntityToken uniqueDerelict = SalvageFactory.addDerelict(randomSys, randomSys.getStar(),
                "gl_endeavour_battle_Overdriven",
                ShipCondition.WRECKED, OrbitUtils.getAvailableOrbitRadius(randomSys, 25f, 3000f), RECOVER.NO);

        uniqueDerelict.addTag(GaloStoryCoordinator.UNIQUE_DERELICT_TAG);
        logger.info(
                "Unique derelict spawn at '" + randomSys.getName() + "' at radius '" + uniqueDerelictOrbitRadius + "'");

        // Set initial quest stage
        Global.getSector().getPersistentData().put(STAGE_MEM_KEY, STAGES.NOT_STARTED);
    }

    /**
     * Player found the unknown derelict -> add intel to galo planet
     */
    public static void completeDerelictInvestigation() {
        Global.getSector().getPersistentData().put(STAGE_MEM_KEY, STAGES.DERELICT_FOUND);
        Global.getSector().getIntelManager()
                .addIntel(new GaloIntel(getUniqueDerelict(), getGaloPlanet(), STAGES.DERELICT_FOUND));
    }

    // ---------------------------------------------------------------
    // GETTERS
    // ---------------------------------------------------------------
    public static STAGES getStage() {
        return (STAGES) Global.getSector().getPersistentData().get(STAGE_MEM_KEY);
    }

    public static SectorEntityToken getUniqueDerelict() {
        final List<SectorEntityToken> derelicts = Global.getSector().getEntitiesWithTag(UNIQUE_DERELICT_TAG);
        if (derelicts.isEmpty()) {
            return null;
        }
        return Global.getSector().getEntitiesWithTag(UNIQUE_DERELICT_TAG).get(0);
    }

    public static SectorEntityToken getGaloPlanet() {
        return Global.getSector().getEntityById(GALO_ID);
    }
}
