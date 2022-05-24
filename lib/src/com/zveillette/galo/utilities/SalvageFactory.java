package com.zveillette.galo.utilities;

import com.fs.starfarer.api.campaign.SectorEntityToken;
import com.fs.starfarer.api.campaign.StarSystemAPI;
import com.fs.starfarer.api.impl.campaign.DerelictShipEntityPlugin.DerelictShipData;
import com.fs.starfarer.api.impl.campaign.ids.Entities;
import com.fs.starfarer.api.impl.campaign.ids.Factions;
import com.fs.starfarer.api.impl.campaign.ids.Tags;
import com.fs.starfarer.api.impl.campaign.procgen.themes.BaseThemeGenerator;
import com.fs.starfarer.api.impl.campaign.procgen.themes.SalvageSpecialAssigner.ShipRecoverySpecialCreator;
import com.fs.starfarer.api.impl.campaign.rulecmd.salvage.special.ShipRecoverySpecial.PerShipData;
import com.fs.starfarer.api.impl.campaign.rulecmd.salvage.special.ShipRecoverySpecial.ShipCondition;

import com.fs.starfarer.api.util.Misc;

/**
 * Salvage helper for derelicts and other entities
 */
public class SalvageFactory {
    public enum RECOVER {
        NO,
        WITH_STORY_P,
        YES
    }
    /**
     * Add one specific derelict ship to targeted system
     */
    public static SectorEntityToken addDerelict(StarSystemAPI system, SectorEntityToken focus, String variantId,
            ShipCondition condition, float orbitRadius, RECOVER recoverable) {

        DerelictShipData params = new DerelictShipData(new PerShipData(variantId, condition, Factions.INDEPENDENT, 0f), false);
        SectorEntityToken ship = BaseThemeGenerator.addSalvageEntity(system, Entities.WRECK, Factions.NEUTRAL, params);
        ship.setDiscoverable(true);
        ship.setCircularOrbit(focus, (float) Math.random() * 360f, orbitRadius, OrbitUtils.getOrbitDays(orbitRadius));

        if (recoverable.equals(RECOVER.YES)) {
            ShipRecoverySpecialCreator creator = new ShipRecoverySpecialCreator(null, 0, 0, false, null, null);
            Misc.setSalvageSpecial(ship, creator.createSpecial(ship, null));
        }

        if (recoverable.equals(RECOVER.NO)) {
            ship.addTag(Tags.UNRECOVERABLE);
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
        derelict.setCircularOrbit(focus, (float) Math.random() * 360f, orbitRadius, OrbitUtils.getOrbitDays(orbitRadius));

        return derelict;
    }

    public static SectorEntityToken addCustomEntity(StarSystemAPI system, SectorEntityToken focus, String id,
            String name, String entity, String faction, float orbitRadius) {
        SectorEntityToken newEntity = system.addCustomEntity(id, name, entity, faction);
        newEntity.setDiscoverable(true);
        newEntity.setCircularOrbit(focus, (float) Math.random() * 360f, orbitRadius, OrbitUtils.getOrbitDays(orbitRadius));

        return newEntity;
    }
}
