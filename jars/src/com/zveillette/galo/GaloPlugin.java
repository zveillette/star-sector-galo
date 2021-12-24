package com.zveillette.galo;

import com.fs.starfarer.api.BaseModPlugin;
import com.fs.starfarer.api.Global;
import com.zveillette.galo.world.GaloSystem;

public class GaloPlugin extends BaseModPlugin {
    @Override
    public void onNewGame() {
        GaloSystem galo = new GaloSystem();
        Global.getSector().addListener(new GaloCampaignListener(false, galo));
    }
}
