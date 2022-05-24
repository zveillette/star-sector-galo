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
import com.zveillette.galo.story.GaloStoryCoordinator.STAGES;

public class GaloIntel extends BreadcrumbIntel {
    private STAGES _stage;

    public GaloIntel(SectorEntityToken foundAt, SectorEntityToken target, STAGES stage) {
        super(foundAt, target);
        this._stage = stage;
    }

    @Override
    public String getIcon() {
        return "graphics/icons/markets/gl_tomb.png";
    }

    @Override
    public String getName() {
        switch (this._stage) {
            case DERELICT_FOUND:
                return "Travel to Galo";
            default:
                return "";
        }
    }

    @Override
    public void createIntelInfo(TooltipMakerAPI info, ListInfoMode mode) {
        super.createIntelInfo(info, mode);
        info.addPara("Galo's quest", 3f);
    }

    @Override
    public void createSmallDescription(TooltipMakerAPI info, float width, float height) {
        switch (this._stage) {
            case DERELICT_FOUND:
                info.addPara(
                        "An odd derelict gave you these coordinates to this \"%s\" world. It remains to be trusted.",
                        3f, Misc.getHighlightColor(), "Galo");
                break;
            default:
                break;
        }
        super.createSmallDescription(info, width, height);
    }

    @Override
    public Set<String> getIntelTags(SectorMapAPI map) {
        return new HashSet<>(Arrays.asList(Tags.INTEL_STORY));
    }

    @Override
    public boolean shouldRemoveIntel() {
        return GaloStoryCoordinator.getStage() != this._stage;
    }
}
