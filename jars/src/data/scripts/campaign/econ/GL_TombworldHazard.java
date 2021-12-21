package data.scripts.campaign.econ;

import com.fs.starfarer.api.impl.campaign.econ.BaseHazardCondition;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import com.fs.starfarer.api.util.Misc;

public class GL_TombworldHazard extends BaseHazardCondition {
    public static final float HAZARD_PENALTY = 0.5f;

    @Override
    public void apply(String id) {
        super.apply(id);
        this.market.getHazard().modifyFlat(id, HAZARD_PENALTY, "hazard rating");
    }

    @Override
    public void unapply(String id) {
        super.unapply(id);
        this.market.getHazard().unmodifyFlat(id);
    }

    @Override
    protected void createTooltipAfterDescription(TooltipMakerAPI tooltip, boolean expanded) {
        super.createTooltipAfterDescription(tooltip, expanded);
        tooltip.addPara("%s hazard rating", 10f, Misc.getHighlightColor(), "+" + ((int) (HAZARD_PENALTY * 100f)) + "%");
    }
}
