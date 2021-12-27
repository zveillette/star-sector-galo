package com.zveillette.galo;

import com.fs.starfarer.api.BaseModPlugin;
import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.FactionAPI;
import com.fs.starfarer.api.campaign.SectorAPI;
import com.fs.starfarer.api.impl.campaign.ids.Factions;
import com.zveillette.galo.world.GaloFleetManager;
import com.zveillette.galo.world.GaloSystem;

public class GaloPlugin extends BaseModPlugin {
    public static final String WARLORD_FACTION_ID = "gl_warlord";

    @Override
    public void onNewGame() {
        SectorAPI sector = Global.getSector();
        _setNewFactionRelations(sector);

        new GaloSystem();
        sector.addScript(GaloFleetManager.getInstance());
    }

    private void _setNewFactionRelations(SectorAPI sector) {
        FactionAPI warlord = sector.getFaction(WARLORD_FACTION_ID);

        warlord.setRelationship(Factions.TRITACHYON, 0f);
        warlord.setRelationship(Factions.REMNANTS, -0.9f);
        warlord.setRelationship(Factions.PIRATES, -0.2f);
        warlord.setRelationship(Factions.PERSEAN, -0.6f);
        warlord.setRelationship(Factions.HEGEMONY, -0.6f);
        warlord.setRelationship(Factions.LIONS_GUARD, -0.6f);
        warlord.setRelationship(Factions.DIKTAT, -0.6f);
        warlord.setRelationship(Factions.LUDDIC_PATH, -0.2f);
		warlord.setRelationship(Factions.INDEPENDENT, -0.4f);
		warlord.setRelationship(Factions.PLAYER, -0.6f);
    }
}
