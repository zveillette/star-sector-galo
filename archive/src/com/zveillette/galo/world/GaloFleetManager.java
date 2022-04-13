package com.zveillette.galo.world;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.CampaignFleetAPI;
import com.fs.starfarer.api.campaign.FleetAssignment;
import com.fs.starfarer.api.campaign.SectorEntityToken;
import com.fs.starfarer.api.campaign.StarSystemAPI;
import com.fs.starfarer.api.impl.campaign.fleets.DisposableFleetManager;
import com.fs.starfarer.api.impl.campaign.fleets.FleetFactoryV3;
import com.fs.starfarer.api.impl.campaign.fleets.FleetParamsV3;
import com.fs.starfarer.api.impl.campaign.ids.FleetTypes;
import com.fs.starfarer.api.impl.campaign.tutorial.TutorialMissionIntel;
import com.fs.starfarer.api.util.WeightedRandomPicker;
import com.zveillette.galo.GaloPlugin;
import com.zveillette.galo.utilities.Utils;

import org.apache.log4j.Logger;

public class GaloFleetManager extends DisposableFleetManager {
    private static GaloFleetManager instance = null;
    private static final Logger log = Global.getLogger(GaloFleetManager.class);
    private WeightedRandomPicker<String> fleetTypePicker = new WeightedRandomPicker<String>();
    private WeightedRandomPicker<String> planetFocusPicker = new WeightedRandomPicker<String>();

    public static GaloFleetManager getInstance() {
        if (instance != null) {
            return instance;
        }

        instance = new GaloFleetManager();
        return instance;
    }

    private GaloFleetManager() {
        _initPickers();
    }

    private void _initPickers() {
        fleetTypePicker.add(FleetTypes.PATROL_SMALL, 2f);
        fleetTypePicker.add(FleetTypes.PATROL_MEDIUM, 1f);
        fleetTypePicker.add(FleetTypes.PATROL_LARGE, 0.5f);

        planetFocusPicker.add(GaloSystem.GALO_PRIME_MOON, 1f);
        planetFocusPicker.add(GaloSystem.TROEL, 0.5f);
    }

    @Override
    protected int getDesiredNumFleetsForSpawnLocation() {
        return 3;
    }

    @Override
    protected String getSpawnId() {
        return "gl_galo_patrol";
    }

    @Override
    protected CampaignFleetAPI spawnFleetImpl() {
        StarSystemAPI system = currSpawnLoc;
        if (system == null)
            return null;

        // Only spawn galo fleet in-system
        if (system.getEntityById(GaloSystem.GALO) == null)
            return null;

        return _createFleet(system, fleetTypePicker.pick(), (SectorEntityToken) system.getEntityById(planetFocusPicker.pick()));
    }

    @Override
    public float getSpawnRateMult() {
        return super.getSpawnRateMult() * 5f;
    }

    @Override
    public void advance(float amount) {
        if (TutorialMissionIntel.isTutorialInProgress()) {
            return;
        }

        super.advance(amount);

        CampaignFleetAPI player = Global.getSector().getPlayerFleet();
        if (player == null)
            return;

        float days = Global.getSector().getClock().convertToDays(amount);
        if (DEBUG) {
            days *= 100f;
        }

        tracker2.advance(days);
        if (tracker2.intervalElapsed()) {
            StarSystemAPI closest = _getCurrentSystem();
            if (closest != currSpawnLoc) {
                currSpawnLoc = closest;
            }
            updateSpawnRateMult();
        }
    }

    private StarSystemAPI _getCurrentSystem() {
        if (Global.getSector().isInNewGameAdvance())
            return null;
        CampaignFleetAPI player = Global.getSector().getPlayerFleet();
        return player.getStarSystem();
    }

    private CampaignFleetAPI _createFleet(StarSystemAPI system, String type, SectorEntityToken focus) {
        boolean isPatrol = false;
        float combat = 50f;
        float freight = 0f;
        switch (type) {
            case FleetTypes.PATROL_SMALL:
                combat *= Utils.getFloatBetween(0.2f, 0.8f);
                isPatrol = true;
                break;
            case FleetTypes.PATROL_MEDIUM:
                combat *= Utils.getFloatBetween(1f, 2.5f);
                freight = combat * 0.1f;
                isPatrol = true;
                break;
            case FleetTypes.PATROL_LARGE:
                combat *= Utils.getFloatBetween(2f, 5f);
                freight = combat * 0.1f;
                isPatrol = true;
                break;
        }

        FleetParamsV3 params = new FleetParamsV3(
                null,
                focus.getLocation(),
                GaloPlugin.WARLORD_FACTION_ID,
                null, // quality override
                type,
                combat, // combatPts
                freight, // freighterPts
                combat * 0.1f, // tankerPts
                0f, // transportPts
                0f, // linerPts
                0f, // utilityPts
                0f // qualityMod
        );

        CampaignFleetAPI fleet = FleetFactoryV3.createFleet(params);

        if (fleet == null)
            return null;

        if (isPatrol) {
            fleet.addAssignment(FleetAssignment.ORBIT_AGGRESSIVE, focus, 5f);
            fleet.addAssignment(FleetAssignment.PATROL_SYSTEM, system.getStar(), 15f);
            fleet.addAssignment(FleetAssignment.GO_TO_LOCATION_AND_DESPAWN, focus, 5f);
        }

        fleet.setLocation(focus.getLocation().x, focus.getLocation().y);
        currSpawnLoc.addEntity(fleet);

        log.info("Galo \"" + type + "\" fleet spawned.");

        return null;
    }
}
