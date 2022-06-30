package com.zveillette.galo.encounters;

import java.util.List;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.CargoAPI;
import com.fs.starfarer.api.campaign.SectorEntityToken;
import com.fs.starfarer.api.impl.campaign.ids.Commodities;
import com.fs.starfarer.api.impl.campaign.rulecmd.salvage.special.BaseSalvageSpecial;
import com.fs.starfarer.api.impl.campaign.rulecmd.salvage.special.ShipRecoverySpecial.ShipCondition;
import com.zveillette.galo.story.GaloStoryCoordinator;
import com.zveillette.galo.utilities.SalvageFactory;
import com.zveillette.galo.utilities.SalvageFactory.RECOVER;

public class GaloEncountersCoordinator {
    public final static String CONQUEST_ENCOUNTER = "gl_conquest_encounter";
    private final static String CONQUEST_STAGE_MEM_KEY = "gl_conquest_encounter_stage";

    public static enum STAGES {
        NOT_STARTED,
        COMPLETED
    }

    public static void init() {
        spawnConquestEncounter();
    }

    /**
     * Create the conquest encounter around galo planet.
     * Spawn some derelicts around as bait
     */
    private static void spawnConquestEncounter() {
        SectorEntityToken galoPlanet = GaloStoryCoordinator.getGaloPlanet();
        float planetRadius = galoPlanet.getRadius() + 100f;

        // Spawn bait derelicts
        SalvageFactory.addDerelict(galoPlanet.getStarSystem(), galoPlanet, "brawler_Assault", ShipCondition.BATTERED,
                planetRadius + 50f, RECOVER.YES);
        SalvageFactory.addDerelict(galoPlanet.getStarSystem(), galoPlanet, "condor_Strike", ShipCondition.AVERAGE,
                planetRadius + 90f, RECOVER.WITH_STORY_P);

        // Spawn encounter
        SectorEntityToken conquestShip = SalvageFactory.addDerelict(galoPlanet.getStarSystem(), galoPlanet,
                "conquest_Standard", ShipCondition.BATTERED,
                planetRadius + 75f, RECOVER.YES);
        conquestShip.addTag(CONQUEST_ENCOUNTER);

        // Add some cargo
        CargoAPI conquestCargo = Global.getFactory().createCargo(true);
        SalvageFactory.addCommodity(conquestCargo, Commodities.HAND_WEAPONS, 100, 150);
        BaseSalvageSpecial.addExtraSalvage(conquestCargo, conquestShip.getMemory(), -1);
        conquestShip.getCargo().addAll(conquestCargo);

        Global.getSector().getPersistentData().put(CONQUEST_STAGE_MEM_KEY, STAGES.NOT_STARTED);
    }

    public static void completeEncounter(String name) {
        switch (name) {
            case CONQUEST_ENCOUNTER:
                Global.getSector().getPersistentData().put(CONQUEST_STAGE_MEM_KEY, STAGES.COMPLETED);
                break;
            default:
                break;
        }
    }

    // ---------------------------------------------------------------
    // GETTERS
    // ---------------------------------------------------------------
    public static SectorEntityToken getEncounter(String name) {
        final List<SectorEntityToken> encounters = Global.getSector().getEntitiesWithTag(name);
        if (encounters.isEmpty()) {
            return null;
        }
        return encounters.get(0);
    }

    public static STAGES getEncounterStage(String name) {
        switch (name) {
            case CONQUEST_ENCOUNTER:
                return (STAGES) Global.getSector().getPersistentData().get(CONQUEST_STAGE_MEM_KEY);
            default:
                return null;
        }
    }
}
