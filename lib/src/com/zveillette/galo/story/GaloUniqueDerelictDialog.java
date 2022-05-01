package com.zveillette.galo.story;

import java.util.Map;

import com.fs.starfarer.api.campaign.InteractionDialogAPI;
import com.fs.starfarer.api.campaign.InteractionDialogPlugin;
import com.fs.starfarer.api.campaign.rules.MemoryAPI;
import com.fs.starfarer.api.combat.EngagementResultAPI;

public class GaloUniqueDerelictDialog implements InteractionDialogPlugin {
    protected InteractionDialogAPI dialog;
    
    private enum OptionId {
        INIT,
        SEND_A_TEAM,
        LEAVE
    }

    @Override
    public void init(InteractionDialogAPI dialog) {
        this.dialog = dialog;
        this.optionSelected(null, OptionId.INIT);
    }

    @Override
    public void optionSelected(String optionText, Object optionData) {
        if (optionData instanceof OptionId) {
            dialog.getOptionPanel().clearOptions();

            switch ((OptionId) optionData) {
                // The invisible "init" option was selected by the init method.
                case INIT:
                    dialog.getTextPanel().addPara("TODO: An old derelict of unfamiliar design...");
                    dialog.getOptionPanel().addOption("Send a boarding team", OptionId.SEND_A_TEAM);
                    dialog.getOptionPanel().addOption("Let it be", OptionId.LEAVE);
                    break;
                case SEND_A_TEAM:
                    dialog.getTextPanel().addPara("TODO: Your team quickly notices a weak beacon that transmitting some unknown coordinates.");
                    dialog.getTextPanel().addPara("TODO: It seems that no one noticed it before as it's contained within the ship.");

                    GaloStoryCoordinator.completeDerelictInvestigation();
                    dialog.getOptionPanel().addOption("Disconnect the beacon and note the coordinates", OptionId.LEAVE);
                break;
                case LEAVE:
                    dialog.dismiss();
                    break;
            }
        }
    }

    @Override
    public void advance(float arg0) {
    }

    @Override
    public void backFromEngagement(EngagementResultAPI arg0) {
    }

    @Override
    public Object getContext() {
        return null;
    }

    @Override
    public Map<String, MemoryAPI> getMemoryMap() {
        return null;
    }

    @Override
    public void optionMousedOver(String arg0, Object arg1) {
    }    
}
