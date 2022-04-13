package com.zveillette.galo;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.BaseModPlugin;
import org.apache.log4j.Logger;

public class GaloPlugin extends BaseModPlugin {
    private final static Logger logger = Global.getLogger(GaloPlugin.class);

    @Override
    public void onNewGame() {
        logger.info("Add Galo planet to system x");
    }
}
