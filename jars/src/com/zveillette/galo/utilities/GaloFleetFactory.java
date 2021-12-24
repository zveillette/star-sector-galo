package com.zveillette.galo.utilities;

import com.fs.starfarer.api.campaign.CampaignFleetAPI;
import com.fs.starfarer.api.impl.campaign.fleets.FleetFactoryV3;
import com.fs.starfarer.api.impl.campaign.fleets.FleetParamsV3;
import com.fs.starfarer.api.impl.campaign.ids.FleetTypes;

import org.lwjgl.util.vector.Vector2f;

public class GaloFleetFactory {
    public static CampaignFleetAPI createFleet(Vector2f loc, String faction) {
        FleetParamsV3 params = new FleetParamsV3(loc, faction, 1f, FleetTypes.PATROL_SMALL, 60, 0, 0, 0, 0, 0, 0);
        
        return FleetFactoryV3.createFleet(params);
    }
}
