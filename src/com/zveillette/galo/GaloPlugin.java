package com.zveillette.galo;

import com.fs.starfarer.api.BaseModPlugin;
import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.SectorAPI;

public class GaloPlugin extends BaseModPlugin {
    public static final String WARLORD_FACTION_ID = "gl_warlord";

    @Override
    public void onNewGame() {
        SectorAPI sector = Global.getSector();
    }
}
