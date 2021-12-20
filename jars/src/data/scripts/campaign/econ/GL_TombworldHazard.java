package data.scripts.campaign.econ;

import com.fs.starfarer.api.impl.campaign.econ.BaseHazardCondition;
import com.fs.starfarer.api.ui.TooltipMakerAPI;

public class GL_TombworldHazard extends BaseHazardCondition {
    public static float HAZARD_PENALTY = 0.5f;

    @Override
    public void apply(String id) {
        super.apply(id);
        this.market.getHazard().modifyFlat(id, HAZARD_PENALTY, "Base station hazard");
    }

    @Override
    public void unapply(String id) {
        super.unapply(id);
        this.market.getHazard().unmodifyFlat(id);
    }

    @Override
    protected void createTooltipAfterDescription(TooltipMakerAPI tooltip, boolean expanded) {
        super.createTooltipAfterDescription(tooltip, expanded);
    }
}
