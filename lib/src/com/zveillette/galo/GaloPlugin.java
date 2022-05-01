package com.zveillette.galo;
import com.fs.starfarer.api.BaseModPlugin;
import com.fs.starfarer.api.Global;
import com.zveillette.galo.story.GaloStoryCoordinator;

public class GaloPlugin extends BaseModPlugin {

    @Override
    public void onNewGameAfterProcGen() {
        GaloStoryCoordinator.init();
    }

    @Override
    public void onGameLoad(boolean newGame) {
        Global.getSector().registerPlugin(new GaloCampaignPlugin());
    }
}
