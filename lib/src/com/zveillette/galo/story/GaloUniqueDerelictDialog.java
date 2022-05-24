package com.zveillette.galo.story;

import java.util.Map;

import com.fs.starfarer.api.campaign.InteractionDialogAPI;
import com.fs.starfarer.api.campaign.InteractionDialogPlugin;
import com.fs.starfarer.api.campaign.rules.MemoryAPI;
import com.fs.starfarer.api.combat.EngagementResultAPI;
import com.fs.starfarer.api.util.Misc;

public class GaloUniqueDerelictDialog implements InteractionDialogPlugin {
    protected InteractionDialogAPI dialog;

    private enum OptionId {
        INIT,
        TRAVEL_AROUND,
        SEND_SHUTTLE,
        SEND_SHUTTLE_2,
        COMPLETE_SEARCH,
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
                case INIT:
                    dialog.getTextPanel().addPara(
                            "An old cruiser lay upon your sight.");
                    dialog.getOptionPanel().addOption("Do a quick sweep, I don't trust that one",
                            OptionId.TRAVEL_AROUND);
                    dialog.getOptionPanel().addOption("Send a shuttle",
                            OptionId.SEND_SHUTTLE);
                    dialog.getOptionPanel().addOption("Better to stay away from it", OptionId.LEAVE);
                    break;
                case TRAVEL_AROUND:
                    dialog.getTextPanel().addPara(
                            "You move your ship slowly towards the derelict. Scanning it one more time... " +
                                    "Nothing. The ship is as inert as a white dwarf.");
                    dialog.getOptionPanel().addOption("Alright, send a shuttle", OptionId.SEND_SHUTTLE);
                    break;
                case SEND_SHUTTLE:
                    dialog.getTextPanel().addPara(
                            "The shuttle quickly dock with the ship. " +
                                    "Your team detonates a few charges and is able to break through. " +
                                    "While clearing each compartment, your chief engineer notices that the ship is 'somewhat' automated.");
                    dialog.getOptionPanel().addOption("Somewhat ?", OptionId.SEND_SHUTTLE_2);
                    break;
                case SEND_SHUTTLE_2:
                    dialog.getTextPanel().addPara(
                            "\"Yes. The ship was almost stripped out of any interface. " +
                                    "We located a socket, supposedly for an AI core, but it's obviously empty. " +
                                    " I don't see any reason why would someone go through this process on this kind of ship.\"");

                    dialog.getTextPanel().addPara(
                            "Quickly after, your team stumble upon a faint beacon in the middle of the ship. " +
                                    "It's emiting coordinates from this system. Planet %s.",
                            Misc.getHighlightColor(), "Galo");

                    dialog.getOptionPanel().addOption("Note the coordinates and disconnect the beacon", OptionId.COMPLETE_SEARCH);
                    break;
                case COMPLETE_SEARCH:
                    GaloStoryCoordinator.completeDerelictInvestigation();
                    dialog.dismiss();
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
