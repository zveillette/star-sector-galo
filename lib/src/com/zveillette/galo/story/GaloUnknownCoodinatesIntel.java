package com.zveillette.galo.story;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import com.fs.starfarer.api.campaign.SectorEntityToken;
import com.fs.starfarer.api.impl.campaign.ids.Tags;
import com.fs.starfarer.api.impl.campaign.intel.misc.BreadcrumbIntel;
import com.fs.starfarer.api.ui.SectorMapAPI;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import com.fs.starfarer.api.util.Misc;

public class GaloUnknownCoodinatesIntel extends BreadcrumbIntel {

    public GaloUnknownCoodinatesIntel(SectorEntityToken foundAt, SectorEntityToken target) {
        super(foundAt, target);
    }

    @Override
    public String getIcon() {
        return "graphics/icons/intel/player.png";
    }

    @Override
    public String getName() {
        return "Unknown coordinates from TODO:";
    }

    @Override
    public void createIntelInfo(TooltipMakerAPI info, ListInfoMode mode) {
        super.createIntelInfo(info, mode);

        info.addPara("Unkown coordinates to %s",
                3f,
                super.getBulletColorForMode(mode),
                Misc.getHighlightColor(),
                "TODO:");
    }

    @Override
    public void createSmallDescription(TooltipMakerAPI info, float width, float height) {
        info.addPara("TODO:", 3f);
        super.createSmallDescription(info, width, height);
    }

    @Override
    public Set<String> getIntelTags(SectorMapAPI map) {
        return new HashSet<>(Arrays.asList(Tags.INTEL_STORY));
    }
}
