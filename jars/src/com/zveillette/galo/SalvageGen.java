package com.zveillette.galo;

import java.util.ArrayList;
import java.util.List;

import com.fs.starfarer.api.campaign.SectorEntityToken;
import com.fs.starfarer.api.campaign.StarSystemAPI;
import com.fs.starfarer.api.impl.campaign.DerelictShipEntityPlugin.DerelictShipData;
import com.fs.starfarer.api.impl.campaign.ids.Entities;
import com.fs.starfarer.api.impl.campaign.ids.Factions;
import com.fs.starfarer.api.impl.campaign.procgen.themes.BaseThemeGenerator;
import com.fs.starfarer.api.impl.campaign.procgen.themes.SalvageSpecialAssigner.ShipRecoverySpecialCreator;
import com.fs.starfarer.api.impl.campaign.rulecmd.salvage.special.ShipRecoverySpecial.PerShipData;
import com.fs.starfarer.api.impl.campaign.rulecmd.salvage.special.ShipRecoverySpecial.ShipCondition;

import com.fs.starfarer.api.util.Misc;

/**
 * Salvage helper for derelicts and other entities
 */
public class SalvageGen {
    private static final String[] COMMON_SHIPS = {
            "condor_Attack",
            "buffalo_Standard",
            "cerberus_Standard",
            "cerberus_d_pirates_Standard",
            "enforcer_Balanced",
            "hound_Standard",
            "hound_d_pirates_Overdriven",
            "kite_Standard",
            "kite_pirates_Raider",
            "lasher_Standard",
            "lasher_Strike",
            "tarsus_Standard",
            "venture_Exploration",
            "mule_Standard",
            "gemini_Standard",
            "mudskipper_Standard",
            "dram_Light",
            "nebula_Standard"
    };

    private static final String[] UNCOMMON_SHIPS = {
            "centurion_Assault",
            "dominator_Assault",
            "falcon_Attack",
            "mora_Assault",
            "sunder_Assault",
            "wolf_Assault",
            "starliner_Standard",
            "colossus_Standard",
            "drover_Strike",
            "phaeton_Standard",
            "gremlin_Strike",
            "shrike_Attack",
            "brawler_Assault",
            "hammerhead_Balanced"
    };

    private static final String[] RARE_SHIPS = {
            "conquest_Standard",
            "champion_Assault",
            "onslaught_Standard",
            "gryphon_Standard",
            "eagle_Assault",
            "atlas_Standard",
            "tempest_Attack"
    };

    private static String getRandomShip(ShipRarity shipRarity) {
        String[] ships;
        switch (shipRarity) {
            case UNCOMMON:
                ships = UNCOMMON_SHIPS;
                break;
            case RARE:
                ships = RARE_SHIPS;
                break;
            case COMMON:
            default:
                ships = COMMON_SHIPS;
                break;
        }

        int randomIndex = (int) (Math.random() * ships.length);
        return ships[randomIndex];
    }

    public enum ShipRarity {
        COMMON,
        UNCOMMON,
        RARE
    }

    /**
     * Generate multiple derelict ships based on shipRarity and amount
     */
    public static List<SectorEntityToken> addDerelicts(StarSystemAPI system, SectorEntityToken focus, int amount,
            ShipRarity shipRarity, ShipCondition condition, float orbitRadius, float orbitRange) {
        List<SectorEntityToken> derelicts = new ArrayList<SectorEntityToken>();
        for (int i = 0; i < amount; i++) {
            // Calculate orbit radius based on range
            // (to avoid having all ships on the same orbit)
            float radius = orbitRadius + (((float) Math.random() * orbitRange) - orbitRange);
            boolean isRecoverable = Math.random() < 0.5;
            derelicts.add(addDerelict(system, focus, getRandomShip(shipRarity), condition, radius, isRecoverable));
        }
        return derelicts;
    }

    /**
     * Add one specific derelict ship based on his variantId
     */
    public static SectorEntityToken addDerelict(StarSystemAPI system, SectorEntityToken focus, String variantId,
            ShipCondition condition, float orbitRadius, boolean recoverable) {

        DerelictShipData params = new DerelictShipData(new PerShipData(variantId, condition), false);
        SectorEntityToken ship = BaseThemeGenerator.addSalvageEntity(system, Entities.WRECK, Factions.NEUTRAL, params);
        ship.setDiscoverable(true);
        ship.setCircularOrbit(focus, (float) Math.random() * 360f, orbitRadius, Utils.getOrbitDays(orbitRadius));

        if (recoverable) {
            ShipRecoverySpecialCreator creator = new ShipRecoverySpecialCreator(null, 0, 0, false, null, null);
            Misc.setSalvageSpecial(ship, creator.createSpecial(ship, null));
        }
        return ship;
    }

    /**
     * Add a generic salvage entity such as mining stations and domain era probes
     */
    public static SectorEntityToken addSalvage(StarSystemAPI system, SectorEntityToken focus, String entity,
            float orbitRadius) {
        SectorEntityToken derelict = BaseThemeGenerator.addSalvageEntity(system, entity.toString(), Factions.NEUTRAL);
        derelict.setDiscoverable(true);
        derelict.setCircularOrbit(focus, (float) Math.random() * 360f, orbitRadius, Utils.getOrbitDays(orbitRadius));

        return derelict;
    }

    public static SectorEntityToken addCustomEntity(StarSystemAPI system, SectorEntityToken focus, String id,
            String name, String entity, String faction, float orbitRadius) {
        SectorEntityToken newEntity = system.addCustomEntity(id, name, entity, faction);
        newEntity.setDiscoverable(true);
        newEntity.setCircularOrbit(focus, (float) Math.random() * 360f, orbitRadius, Utils.getOrbitDays(orbitRadius));

        return newEntity;
    }
}
