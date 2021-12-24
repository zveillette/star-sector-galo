package com.zveillette.galo;
import com.fs.starfarer.api.campaign.BaseCampaignEventListener;
import com.fs.starfarer.api.campaign.CampaignFleetAPI;
import com.fs.starfarer.api.campaign.SectorEntityToken;
import com.fs.starfarer.api.campaign.StarSystemAPI;
import com.fs.starfarer.api.campaign.JumpPointAPI.JumpDestination;
import com.zveillette.galo.world.GaloSystem;

public class GaloCampaignListener extends BaseCampaignEventListener {
    private GaloSystem galoSystem = null;

    public GaloCampaignListener(boolean permaRegister, GaloSystem sys) {
        super(permaRegister);
        galoSystem = sys;
    }

    @Override
    public void reportFleetJumped(CampaignFleetAPI fleet, SectorEntityToken from, JumpDestination to) {
        super.reportFleetJumped(fleet, from, to);
        if (to == null) {
            return;
        }
        SectorEntityToken  destination = to.getDestination();

        if (destination == null) {
            return;
        }

        StarSystemAPI sys = destination.getStarSystem();

        if (sys == null) {
            return;
        }

        galoSystem.spawnFleetOnSystemEntered(fleet, from, to, sys);
    }
}
