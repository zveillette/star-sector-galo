package com.zveillette.galo;

import com.fs.starfarer.api.PluginPick;
import com.fs.starfarer.api.campaign.BaseCampaignPlugin;
import com.fs.starfarer.api.campaign.InteractionDialogPlugin;
import com.fs.starfarer.api.campaign.SectorEntityToken;
import com.zveillette.galo.encounters.GaloEncountersCoordinator;
import com.zveillette.galo.encounters.GaloTrapDialog;
import com.zveillette.galo.story.GaloStoryCoordinator;
import com.zveillette.galo.story.GaloUniqueDerelictDialog;

public class GaloCampaignPlugin extends BaseCampaignPlugin {
    @Override
    public String getId() {
        return "GL_CampaignPlugin";
    }

    @Override
    public boolean isTransient() {
        return true;
    }

    @Override
    public PluginPick<InteractionDialogPlugin> pickInteractionDialogPlugin(SectorEntityToken interactionTarget) {
        // Galo story
        final SectorEntityToken uniqueDerelict = GaloStoryCoordinator.getUniqueDerelict();
        if (uniqueDerelict != null && interactionTarget.equals(uniqueDerelict)
                && GaloStoryCoordinator.getStage() == GaloStoryCoordinator.STAGES.NOT_STARTED) {
            return new PluginPick<InteractionDialogPlugin>(new GaloUniqueDerelictDialog(), PickPriority.MOD_SPECIFIC);
        }

        // Encounters
        final SectorEntityToken conquestEncounter = GaloEncountersCoordinator.getEncounter(GaloEncountersCoordinator.CONQUEST_ENCOUNTER);
        if (conquestEncounter != null && interactionTarget.equals(conquestEncounter)
                && GaloEncountersCoordinator.getEncounterStage(GaloEncountersCoordinator.CONQUEST_ENCOUNTER) == GaloEncountersCoordinator.STAGES.NOT_STARTED) {
            return new PluginPick<InteractionDialogPlugin>(new GaloTrapDialog(interactionTarget), PickPriority.MOD_SPECIFIC);
        }

        return null;
    }
}
