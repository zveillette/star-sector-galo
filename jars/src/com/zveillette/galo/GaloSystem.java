package com.zveillette.galo;
import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.PlanetAPI;
import com.fs.starfarer.api.campaign.SectorAPI;
import com.fs.starfarer.api.campaign.StarSystemAPI;
import com.fs.starfarer.api.impl.campaign.procgen.StarAge;

public class GaloSystem {
    private static final String GALO_I = "galoI";

    private static StarSystemAPI system = null;
    private static PlanetAPI star = null;

    public GaloSystem() {
        _createSystem();
        _createStar();
        _createPlanets();
    }

    private void _createSystem() {
        SectorAPI sector = Global.getSector();
        system = sector.createStarSystem("Galo");
        system.setAge(StarAge.OLD);
    }

    private void _createStar() {
        star = system.initStar("galo", "star_orange", 1500, 8500, -10500, 600);
    }

    private void _createPlanets() {
        PlanetAPI galoI = system.addPlanet("galoI", star, "Galo I", "barren-bombarded", 0, 30, 1500, 88);
        galoI.addTag(GALO_I);
    }
}
