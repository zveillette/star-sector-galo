package com.zveillette.galo;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.PlanetAPI;
import com.fs.starfarer.api.campaign.StarSystemAPI;
import com.fs.starfarer.api.campaign.econ.MarketAPI;
import com.fs.starfarer.api.impl.campaign.ids.Conditions;
import com.fs.starfarer.api.impl.campaign.rulecmd.salvage.special.ShipRecoverySpecial.ShipCondition;
import com.zveillette.galo.utilities.OrbitUtils;
import com.zveillette.galo.utilities.SalvageFactory;
import com.zveillette.galo.utilities.Utils;

import com.fs.starfarer.api.BaseModPlugin;
import org.apache.log4j.Logger;
import data.scripts.campaign.econ.GL_Conditions;

public class GaloPlugin extends BaseModPlugin {
    private final static Logger logger = Global.getLogger(GaloPlugin.class);
    private final String GALO_ID = "gl_galo";
    private final String GALO_MARKET_ID = "gl_galo_market";
    private final String GALO_NAME = "Galo";
    private final String TOMB_WORLD_PLANET_TYPE = "GL_tomb";

    @Override
    public void onNewGameAfterProcGen() {
        setGaloPlanet();
    }

    /**
     * Pick a planet to replace with Galo
     */
    private void setGaloPlanet() {
        // Get a system that isn't the core worlds
        StarSystemAPI randomSys = Utils.getRandomStarSystem(true, false, null);

        // Spawn planet
        final float planetSize = 135f;
        final float orbitRadius = OrbitUtils.getAvailableOrbitRadius(randomSys, planetSize);
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

        // Add some derelicts around it
        float planetRadius = galoPlanet.getRadius() + 100f;
        SalvageFactory.addDerelict(randomSys, galoPlanet, "brawler_Assault", ShipCondition.BATTERED,
                planetRadius + 50f, false);
        SalvageFactory.addDerelict(randomSys, galoPlanet, "conquest_Standard", ShipCondition.AVERAGE,
                planetRadius + 75f, false);
        SalvageFactory.addDerelict(randomSys, galoPlanet, "condor_Strike", ShipCondition.WRECKED,
                planetRadius + 90f, false);

        logger.info("Tombworld '" + galoPlanet.getName() + "' created in '" + randomSys.getName() + "'");
    }
}
