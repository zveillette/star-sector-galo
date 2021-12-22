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

public class SalvageGen {
    public static List<SectorEntityToken> addShipDerelicts(StarSystemAPI system, SectorEntityToken focus, List<String> variantIds,
    ShipCondition condition, float orbitRadius, float orbitRange) {
        List<SectorEntityToken> derelicts = new ArrayList<SectorEntityToken>();
        for (String variant : variantIds) {
            // Calculate orbit radius based on range (to avoid having all ships on the same orbit)
            float radius = orbitRadius + (((float) Math.random() * orbitRange) - orbitRange);
            boolean isRecoverable = Math.random() < 0.5;
            derelicts.add(addShipDerelict(system, focus, variant, condition, radius, isRecoverable));
        }
        return derelicts;
    }

    public static SectorEntityToken addShipDerelict(StarSystemAPI system, SectorEntityToken focus, String variantId,
            ShipCondition condition, float orbitRadius, boolean recoverable) {

        DerelictShipData params = new DerelictShipData(new PerShipData(variantId, condition), false);
        SectorEntityToken ship = BaseThemeGenerator.addSalvageEntity(system, Entities.WRECK, Factions.NEUTRAL, params);
        ship.setDiscoverable(true);

        float orbitDays = orbitRadius / (10f + (float) Math.random() * 5f);
        ship.setCircularOrbit(focus, (float) Math.random() * 360f, orbitRadius, orbitDays);

        if (recoverable) {
            ShipRecoverySpecialCreator creator = new ShipRecoverySpecialCreator(null, 0, 0, false, null, null);
            Misc.setSalvageSpecial(ship, creator.createSpecial(ship, null));
        }
        return ship;
    }

    public static SectorEntityToken addGenericDerelict(StarSystemAPI system, SectorEntityToken focus, String entity, float orbitRadius) {
        SectorEntityToken derelict = BaseThemeGenerator.addSalvageEntity(system, entity, Factions.NEUTRAL);
        derelict.setDiscoverable(true);

        float orbitDays = orbitRadius / (10f + (float) Math.random() * 5f);
        derelict.setCircularOrbit(focus, (float) Math.random() * 360f, orbitRadius, orbitDays);

        return derelict;
    }
}
