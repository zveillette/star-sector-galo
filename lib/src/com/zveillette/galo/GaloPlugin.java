package com.zveillette.galo;

import com.fs.starfarer.api.BaseModPlugin;
import com.fs.starfarer.api.Global;
import com.zveillette.galo.encounters.GaloEncountersCoordinator;
import com.zveillette.galo.story.GaloStoryCoordinator;

import org.apache.log4j.Logger;

public class GaloPlugin extends BaseModPlugin {
    private final static Logger logger = Global.getLogger(GaloPlugin.class);

    @Override
    public void onNewGameAfterProcGen() {
        GaloStoryCoordinator.init();
        GaloEncountersCoordinator.init();
    }

    @Override
    public void onGameLoad(boolean newGame) {
        Global.getSector().registerPlugin(new GaloCampaignPlugin());
        logger.info("Galo story stage = [" + GaloStoryCoordinator.getStage().toString() + "]");
    }
}
