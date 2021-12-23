package com.zveillette.galo;

import java.util.Random;
import java.awt.Color;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.PlanetAPI;
import com.fs.starfarer.api.campaign.SectorAPI;
import com.fs.starfarer.api.campaign.StarSystemAPI;
import com.fs.starfarer.api.campaign.econ.MarketAPI;
import com.fs.starfarer.api.impl.campaign.ids.Conditions;
import com.fs.starfarer.api.impl.campaign.ids.Entities;
import com.fs.starfarer.api.impl.campaign.ids.Terrain;
import com.fs.starfarer.api.impl.campaign.procgen.PlanetConditionGenerator;
import com.fs.starfarer.api.impl.campaign.procgen.StarAge;
import com.fs.starfarer.api.impl.campaign.rulecmd.salvage.special.ShipRecoverySpecial.ShipCondition;

import data.scripts.campaign.econ.GL_Conditions;

public class GaloSystem {
    // Planets / moons
    private static final int MIN_STAR_RANGE = 10000;
    private static final String TROEL = "gl_troel";
    private static final String GALO_PRIME = "gl_galo_prime";
    private static final String GALO_PRIME_MOON = "gl_galo_prime_moon";

    // Miscs
    private static final String INNER_ICE_RING = "gl_inner_ice_ring";
    private static final String ASTEROID_BELT = "gl_asteroid_belt";

    private StarSystemAPI system = null;
    private PlanetAPI star = null;
    private PlanetAPI troel = null;
    private PlanetAPI galoPrime = null;
    private PlanetAPI galoPrimeMoon = null;
    private Random rng = new Random();

    public GaloSystem() {
        _createSystem();
        _createStar();
        _createPlanets();
        _createMisc();
        _createDerelicts();
    }

    private void _createSystem() {
        SectorAPI sector = Global.getSector();
        system = sector.createStarSystem("Galo");
        system.setAge(StarAge.OLD);
    }

    private void _createStar() {
        int x = rng.nextInt(MIN_STAR_RANGE) + MIN_STAR_RANGE;
        int y = rng.nextInt(MIN_STAR_RANGE) + MIN_STAR_RANGE;

        star = system.initStar("galo", "star_red_giant", 1800, x, y, 600);
    }

    private void _createPlanets() {
        troel = system.addPlanet(TROEL, star, "Troel", "barren", 0, 80, 2500f, 88);
        PlanetConditionGenerator.generateConditionsForPlanet(troel, StarAge.OLD);

        galoPrime = system.addPlanet(GALO_PRIME, star, "Galo Prime", "GL_tomb", 0, 150, 7000f, 360);
        MarketAPI galoPrimeMarket = Global.getFactory().createMarket(GALO_PRIME + "_market", galoPrime.getName(), 0);
        galoPrimeMarket.setPlanetConditionMarketOnly(true);
        galoPrimeMarket.addCondition(GL_Conditions.TOMB_WORLD);
        galoPrimeMarket.addCondition(Conditions.DENSE_ATMOSPHERE);
        galoPrimeMarket.addCondition(Conditions.ORE_ULTRARICH);
        galoPrimeMarket.addCondition(Conditions.RUINS_EXTENSIVE);
        galoPrimeMarket.addCondition(Conditions.FARMLAND_POOR);
        galoPrimeMarket.setPrimaryEntity(galoPrime);
        galoPrime.setMarket(galoPrimeMarket);

        galoPrimeMoon = system.addPlanet(GALO_PRIME_MOON, galoPrime, "Galo Prime Moon", "barren-bombarded", 0, 30, 600f,
                15);
        PlanetConditionGenerator.generateConditionsForPlanet(galoPrimeMoon, StarAge.OLD);
        galoPrimeMoon.getMarket().addCondition(Conditions.DECIVILIZED);
    }

    private void _createMisc() {
        system.addRingBand(star, "misc", "rings_dust0", 256f, 4, Color.white, 256f, 4000f, 295f,
                Terrain.ASTEROID_BELT, INNER_ICE_RING);
        system.addAsteroidBelt(star, 900, 10000, 500, 300, 300, Terrain.ASTEROID_BELT, ASTEROID_BELT);

        system.autogenerateHyperspaceJumpPoints(true, true);
    }

    private void _createDerelicts() {
        // Add mining station orbiting Troel
        SalvageGen.addSalvage(system, troel, Entities.STATION_MINING, 200f);

        // Generate cluster of derelicts around Galo Prime
        SalvageGen.addDerelicts(system, galoPrime, 3, SalvageGen.ShipRarity.COMMON, ShipCondition.BATTERED, 400f, 200f);
        SalvageGen.addDerelicts(system, galoPrime, 2, SalvageGen.ShipRarity.UNCOMMON, ShipCondition.WRECKED, 400f, 200f);
    }
}
