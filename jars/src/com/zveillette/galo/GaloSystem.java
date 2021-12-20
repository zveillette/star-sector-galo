package com.zveillette.galo;

import java.util.Random;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.PlanetAPI;
import com.fs.starfarer.api.campaign.SectorAPI;
import com.fs.starfarer.api.campaign.StarSystemAPI;
import com.fs.starfarer.api.campaign.econ.MarketAPI;
import com.fs.starfarer.api.impl.campaign.ids.Conditions;
import com.fs.starfarer.api.impl.campaign.procgen.StarAge;
public class GaloSystem {
    private static final String TROEL = "gl_troel";
    private static final String GALO_PRIME = "gl_galo_prime";
    private static final int MIN_STAR_RANGE = 10000;

    private static StarSystemAPI system = null;
    private static PlanetAPI star = null;

    public GaloSystem() {
        _createSystem();
        _createStar();
        _createPlanets();
        _createMisc();
    }

    private void _createSystem() {
        SectorAPI sector = Global.getSector();
        system = sector.createStarSystem("Galo");
        system.setAge(StarAge.OLD);
    }

    private void _createStar() {
        Random rng = new Random();
        int x = rng.nextInt(MIN_STAR_RANGE) + MIN_STAR_RANGE;
        int y = rng.nextInt(MIN_STAR_RANGE) + MIN_STAR_RANGE;

        star = system.initStar("galo", "star_red_giant", 1800, x, y, 600);
    }

    private void _createPlanets() {
        PlanetAPI troel = system.addPlanet(TROEL, star, "Troel", "barren", 0, 150, 2500, 88);
        MarketAPI troelMarket = Global.getFactory().createMarket(TROEL + "_market", troel.getName(), 0);
        troelMarket.setPlanetConditionMarketOnly(true);
        troelMarket.addCondition(Conditions.VERY_HOT);
        troelMarket.addCondition(Conditions.IRRADIATED);
        troelMarket.addCondition(Conditions.NO_ATMOSPHERE);
        troelMarket.addCondition(Conditions.ORE_MODERATE);
        troelMarket.addCondition(Conditions.RARE_ORE_SPARSE);
        troelMarket.setPrimaryEntity(troel);
		troel.setMarket(troelMarket);
    }

    private void _createMisc() {
        system.autogenerateHyperspaceJumpPoints(true, true);
    }
}
