package com.zveillette.galo;

import com.fs.starfarer.api.BaseModPlugin;

public class GaloPlugin extends BaseModPlugin {
    @Override
    public void onNewGame() {
        new GaloSystem();
    }
}
