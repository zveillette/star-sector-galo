package com.zveillette.galo.encounters;

import java.awt.Color;
import java.util.Map;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.CampaignFleetAPI;
import com.fs.starfarer.api.campaign.InteractionDialogAPI;
import com.fs.starfarer.api.campaign.InteractionDialogPlugin;
import com.fs.starfarer.api.campaign.LocationAPI;
import com.fs.starfarer.api.campaign.OptionPanelAPI;
import com.fs.starfarer.api.campaign.SectorEntityToken;
import com.fs.starfarer.api.campaign.rules.MemoryAPI;
import com.fs.starfarer.api.combat.EngagementResultAPI;
import com.fs.starfarer.api.combat.ShieldAPI.ShieldType;
import com.fs.starfarer.api.fleet.FleetMemberAPI;
import com.fs.starfarer.api.impl.campaign.RuleBasedInteractionDialogPluginImpl;
import com.fs.starfarer.api.impl.campaign.ExplosionEntityPlugin.ExplosionFleetDamage;
import com.fs.starfarer.api.impl.campaign.ExplosionEntityPlugin.ExplosionParams;
import com.fs.starfarer.api.impl.campaign.ids.Commodities;
import com.fs.starfarer.api.impl.campaign.ids.Entities;
import com.fs.starfarer.api.impl.campaign.ids.Factions;
import com.fs.starfarer.api.impl.campaign.ids.Sounds;
import com.fs.starfarer.api.impl.campaign.rulecmd.SetStoryOption;
import com.fs.starfarer.api.util.Misc;
import com.zveillette.galo.utilities.Utils;

import org.lwjgl.util.vector.Vector2f;

public class GaloTrapDialog implements InteractionDialogPlugin {
    protected InteractionDialogAPI dialog;
    private SectorEntityToken target;
    private boolean flagShipHit = false;

    private enum OptionId {
        INIT,
        SEND_TEAM,
        BRING_BACK_HA,
        EVASIVE_ACTIONS,
        SHIELD_UP,
        TARGET_FIRE,
        FIRE_ALL_BATTERIES,
        BRING_BACK_TEAM,
        BEFORE_LEAVE,
        SALVAGE_OPERATION,
        COMPLETE,
        LEAVE
    }

    public GaloTrapDialog(SectorEntityToken target) {
        this.target = target;
    }

    @Override
    public void init(InteractionDialogAPI dialog) {
        this.dialog = dialog;
        this.dialog.setInteractionTarget(this.target);
        this.optionSelected(null, OptionId.INIT);
    }

    @Override
    public void optionSelected(String optionText, Object optionData) {
        if (optionData instanceof OptionId) {
            final OptionPanelAPI options = dialog.getOptionPanel();
            final CampaignFleetAPI playerFleet = Global.getSector().getPlayerFleet();
            final FleetMemberAPI flagship = playerFleet.getFlagship();
            float crews = 0f;

            options.clearOptions();

            dialog.setOptionOnEscape("", null);
            switch ((OptionId) optionData) {
                case INIT:
                    dialog.getTextPanel()
                            .addPara("A conquest-class lays upon your sight. You smell the profits from here...");
                    options.addOption("Send the salvage team right away", OptionId.SEND_TEAM);
                    options.addOption("Leave", OptionId.LEAVE);
                    dialog.setOptionOnEscape("Leave", OptionId.LEAVE);
                    break;
                case SEND_TEAM:
                    dialog.getTextPanel().addPara(
                            "\"Captain! I'm happy to say that there's a big haul of heavy armaments! We will start sending back a few pods full of these precious weapons.\"");
                    options.addOption("Bring them back", OptionId.BRING_BACK_HA);
                    break;
                case BRING_BACK_HA:
                    dialog.getTextPanel().addPara("/// ALERT SHIP LOCKED ///");
                    dialog.getTextPanel().addPara(
                            "\"Captain! The derelict has targeted us! Multiple warheads incoming!\"");

                    options.addOption("Take evasive actions!", OptionId.EVASIVE_ACTIONS);
                    options.addOption("Shield up!", OptionId.SHIELD_UP);
                    break;
                case EVASIVE_ACTIONS:
                    dialog.getTextPanel().addPara(
                            "Your fleet starts evasive actions. Unfortunately, all incoming shuttles were not able to survive.");

                    crews = Utils.removeCrew(playerFleet, (float) playerFleet.getCargo().getCrew(), 0.05f, 2f, 100f);
                    dialog.getTextPanel().addPara("Your fleet lost %s crewmembers.", Misc.getHighlightColor(),
                            "" + (int) crews);
                    dialog.getTextPanel().addPara("\"We won't be able to evade these missiles for long!\"");

                    setFireOptions(options);
                    break;
                case SHIELD_UP:
                    if (flagship.getHullSpec().getShieldType().equals(ShieldType.NONE)) {
                        dialog.getTextPanel().addPara("\"We don't have shield Capt.. We're hit!\"");
                        dialog.getTextPanel().addPara("\"Damages are minimal, we should target the derelict!\"");
                        flagShipHit = true;
                    } else {
                        dialog.getTextPanel().addPara(
                                "Your fleet shields up and all your incoming shuttle were able to hide behind them. Saving their precious cargo and lives.");

                        float heavyWeapons = (float) Math.floor(playerFleet.getCargo().getMaxCapacity() / 12f);
                        if (heavyWeapons < 10f) {
                            heavyWeapons = 10f;
                        }

                        if (heavyWeapons > 214f) {
                            heavyWeapons = 214f;
                        }

                        dialog.getTextPanel().addPara("%s added to cargo.", Misc.getHighlightColor(),
                                (int) heavyWeapons + " Heavy Armaments");
                        playerFleet.getCargo().addCommodity(Commodities.HAND_WEAPONS, heavyWeapons);
                    }

                    setFireOptions(options);
                    break;
                case BRING_BACK_TEAM:
                    // TODO:
                    break;
                case FIRE_ALL_BATTERIES:
                    dialog.getTextPanel().addPara("\"Captain, We still have some crews onboard!\"");
                    dialog.getTextPanel().addPara("\"Do it.\"");

                    dialog.getTextPanel().addPara(
                            "All ships quickly target the derelict ship in a non-orderely manner. "
                                    + "It only takes a few seconds until a flash shadows the view. Leaving behind sole memories of your salvage team.");

                    crews = Utils.removeCrew(playerFleet, (float) playerFleet.getCargo().getCrew(), 0.125f, 5f, 220f);
                    dialog.getTextPanel().addPara("Your fleet lost %s crewmembers.",
                            Misc.getHighlightColor(), "" + (int) crews);

                    options.addOption("I think.. It's time to leave",
                            flagShipHit ? OptionId.BEFORE_LEAVE : OptionId.COMPLETE);
                    break;
                case TARGET_FIRE:
                    dialog.getTextPanel().addPara("\"All missile pods locked, firing weapons!\"");
                    dialog.getTextPanel().addPara("By an astonishing luck, all targets were hit." +
                            " Your salvage team is able to locate the command room and shutdown the ship completely.");
                    options.addOption("That was close. Proceed with the salvage operations",
                            OptionId.SALVAGE_OPERATION);
                    break;
                case SALVAGE_OPERATION:
                    GaloEncountersCoordinator.completeEncounter(GaloEncountersCoordinator.CONQUEST_ENCOUNTER);
                    RuleBasedInteractionDialogPluginImpl plugin = new RuleBasedInteractionDialogPluginImpl();
                    dialog.setPlugin(plugin);
                    plugin.init(dialog);
                    break;
                case BEFORE_LEAVE:
                    dialog.getTextPanel().addPara("/// BIO HAZARD DETECTED ///");
                    dialog.getTextPanel().addPara(
                            "The automated systems quickly shut off all doors, but it was already too late." +
                                    " Most of your ship becomes crippled by an unknown hazard." +
                                    " It is as if this ambush was not meant to destroy, but to kill.");

                    crews = Utils.removeCrew(playerFleet, (float) flagship.getMinCrew(), 0.5f, 2f,
                            playerFleet.getCargo().getCrew() * 0.5f);
                    dialog.getTextPanel().addPara("Your fleet lost %s crewmembers.",
                            Misc.getHighlightColor(), "" + (int) crews);

                    flagship.getVariant().addPermaMod("gl_biohazard", false);
                    dialog.getTextPanel().addPara(
                            "%s Your combat readiness is diminished.", Misc.getHighlightColor(),
                            "Your flagship now suffers from a rempant bio-hazard.");

                    options.addOption("Leave", OptionId.COMPLETE);
                    break;
                case COMPLETE:
                    GaloEncountersCoordinator.completeEncounter(GaloEncountersCoordinator.CONQUEST_ENCOUNTER);

                    // Create explosion and remove derelict
                    LocationAPI cloc = target.getContainingLocation();
                    Vector2f loc = target.getLocation();
                    ExplosionParams params = new ExplosionParams(new Color(255, 165, 100),
                            cloc, loc, target.getRadius() + 25f, 1f);
                    params.damage = ExplosionFleetDamage.LOW;

                    SectorEntityToken explosion = cloc.addCustomEntity(Misc.genUID(), "Conquest explosion",
                            Entities.EXPLOSION, Factions.NEUTRAL, params);
                    explosion.setLocation(loc.x, loc.y);
                    target.getStarSystem().removeEntity(target);

                    dialog.dismiss();
                    break;
                case LEAVE:
                    dialog.dismiss();
                    break;
                default:
                    break;
            }
        }
    }

    private void setFireOptions(final OptionPanelAPI options) {
        options.addOption("Fire all batteries!",
                OptionId.FIRE_ALL_BATTERIES);
        options.addOption("Target their missile pods!", OptionId.TARGET_FIRE);
        SetStoryOption.set(dialog, 1, OptionId.TARGET_FIRE, null, Sounds.STORY_POINT_SPEND_COMBAT,
                "Targeted all missile pods");
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
