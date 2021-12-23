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
    private static final String BWEDEL = "gl_bwedel";
    private static final String BWEDEL_I = "gl_bwedel_i";
    private static final String BWEDEL_II = "gl_bwedel_ii";
    private static final String BWEDEL_III = "gl_bwedel_iii";

    // Miscs
    private static final String INNER_ICE_RING = "gl_inner_ice_ring";
    private static final String BWEDEL_ICE_RING = "gl_bwedel_ice_ring";
    private static final String ASTEROID_BELT = "gl_asteroid_belt";

    // Orbit radiuses
    private static final float TROEL_RADIUS = 2500f;
    private static final float INNER_ICE_RING_RADIUS = 4000f;
    private static final float GALO_PRIME_RADIUS = 7000f;
    private static final float GALO_PRIME_MOON_RADIUS = 600f;
    private static final float ASTEROID_BELT_RADIUS = 10000f;
    private static final float BWEDEL_RADIUS = 14000f;
    private static final float BWEDEL_I_RADIUS = 500f;
    private static final float BWEDEL_ICE_RING_RADIUS = 500f;
    private static final float BWEDEL_II_RADIUS = 800f;
    private static final float BWEDEL_III_RADIUS = 1100f;

    private StarSystemAPI system = null;
    private PlanetAPI star = null;
    private PlanetAPI troel = null;
    private PlanetAPI galoPrime = null;
    private PlanetAPI galoPrimeMoon = null;
    private PlanetAPI bwedel = null;
    private PlanetAPI bwedelI = null;
    private PlanetAPI bwedelII = null;
    private PlanetAPI bwedelIII = null;
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

        star = system.initStar("galo", "star_red_giant", 1800f, x, y, 600f);
    }

    private void _createPlanets() {
        // Troel
        troel = system.addPlanet(TROEL, star, "Troel", "barren", 0f, 80f, TROEL_RADIUS,
                Utils.getOrbitDays(TROEL_RADIUS));
        PlanetConditionGenerator.generateConditionsForPlanet(troel, StarAge.OLD);

        // Prime + moon
        galoPrime = system.addPlanet(GALO_PRIME, star, "Galo Prime", "GL_tomb", 0f, 150f, GALO_PRIME_RADIUS,
                Utils.getOrbitDays(GALO_PRIME_RADIUS));
        MarketAPI galoPrimeMarket = Global.getFactory().createMarket(GALO_PRIME + "_market", galoPrime.getName(), 0);
        galoPrimeMarket.setPlanetConditionMarketOnly(true);
        galoPrimeMarket.addCondition(GL_Conditions.TOMB_WORLD);
        galoPrimeMarket.addCondition(Conditions.DENSE_ATMOSPHERE);
        galoPrimeMarket.addCondition(Conditions.ORE_ULTRARICH);
        galoPrimeMarket.addCondition(Conditions.RUINS_EXTENSIVE);
        galoPrimeMarket.addCondition(Conditions.FARMLAND_POOR);
        galoPrimeMarket.setPrimaryEntity(galoPrime);
        galoPrime.setMarket(galoPrimeMarket);

        galoPrimeMoon = system.addPlanet(GALO_PRIME_MOON, galoPrime, "Galo Prime Moon", "barren-bombarded", 0, 30,
                GALO_PRIME_MOON_RADIUS, Utils.getOrbitDays(GALO_PRIME_MOON_RADIUS));
        PlanetConditionGenerator.generateConditionsForPlanet(galoPrimeMoon, StarAge.OLD);
        galoPrimeMoon.getMarket().addCondition(Conditions.DECIVILIZED);

        // Bwedel + ring + moons
        bwedel = system.addPlanet(BWEDEL, star, "Bwedel", "gas_giant", 360f, 360f, BWEDEL_RADIUS,
                Utils.getOrbitDays(BWEDEL_RADIUS));
        PlanetConditionGenerator.generateConditionsForPlanet(bwedel, StarAge.OLD);

        system.addRingBand(bwedel, "misc", "rings_dust0", 256f, 4, Color.white, 256f, BWEDEL_ICE_RING_RADIUS,
                Utils.getOrbitDays(BWEDEL_ICE_RING_RADIUS),
                Terrain.ASTEROID_BELT, BWEDEL_ICE_RING);

        bwedelI = system.addPlanet(BWEDEL_I, bwedel, "Bwedel I", "barren2", 0f, 30f, BWEDEL_I_RADIUS,
                Utils.getOrbitDays(BWEDEL_I_RADIUS));
        PlanetConditionGenerator.generateConditionsForPlanet(bwedelI, StarAge.OLD);

        bwedelII = system.addPlanet(BWEDEL_II, bwedel, "Bwedel II", "rocky_ice", 0f, 30f, BWEDEL_II_RADIUS,
                Utils.getOrbitDays(BWEDEL_II_RADIUS));
        PlanetConditionGenerator.generateConditionsForPlanet(bwedelII, StarAge.OLD);

        bwedelIII = system.addPlanet(BWEDEL_III, bwedel, "Bwedel III", "rocky_unstable", 0f, 60f, BWEDEL_III_RADIUS,
                Utils.getOrbitDays(BWEDEL_III_RADIUS));
        PlanetConditionGenerator.generateConditionsForPlanet(bwedelIII, StarAge.OLD);
    }

    private void _createMisc() {
        // Star rings
        system.addRingBand(star, "misc", "rings_dust0", 256f, 4, Color.white, 256f, INNER_ICE_RING_RADIUS,
                Utils.getOrbitDays(INNER_ICE_RING_RADIUS),
                Terrain.ASTEROID_BELT, INNER_ICE_RING);

        system.addAsteroidBelt(star, 900, ASTEROID_BELT_RADIUS, Utils.getOrbitDays(ASTEROID_BELT_RADIUS), 300f, 300f,
                Terrain.ASTEROID_BELT, ASTEROID_BELT);

        // Jump points
        system.autogenerateHyperspaceJumpPoints(true, true);
    }

    private void _createDerelicts() {
        // Add mining station orbiting Troel
        SalvageGen.addSalvage(system, troel, Entities.STATION_MINING, 200f);

        // Generate cluster of derelicts around Galo Prime
        SalvageGen.addDerelicts(system, galoPrime, 3, SalvageGen.ShipRarity.COMMON, ShipCondition.BATTERED, 400f, 200f);
        SalvageGen.addDerelicts(system, galoPrime, 2, SalvageGen.ShipRarity.UNCOMMON, ShipCondition.WRECKED, 400f,
                200f);
    }
}
