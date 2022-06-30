package com.zveillette.galo.encounters;

import java.awt.Color;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.CampaignFleetAPI;
import com.fs.starfarer.api.campaign.InteractionDialogAPI;
import com.fs.starfarer.api.campaign.InteractionDialogPlugin;
import com.fs.starfarer.api.campaign.LocationAPI;
import com.fs.starfarer.api.campaign.OptionPanelAPI;
import com.fs.starfarer.api.campaign.SectorEntityToken;
import com.fs.starfarer.api.campaign.TextPanelAPI;
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

import data.scripts.hullmods.GL_Hullmods;

import org.apache.log4j.Logger;
import org.lwjgl.util.vector.Vector2f;

public class GaloTrapDialog implements InteractionDialogPlugin {
    protected InteractionDialogAPI dialog;
    private SectorEntityToken target;
    private Logger logger = Global.getLogger(GaloTrapDialog.class);

    private enum OptionId {
        INIT,
        INVESTIGATE,
        BRING_BACK_THE_GOODS,
        TAKE_EVASIVE_MANEUVER,
        LEAVE
    }

    public GaloTrapDialog(SectorEntityToken target) {
        logger.warn("target: " + target.getName());
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
            final TextPanelAPI text = dialog.getTextPanel();
            final CampaignFleetAPI playerFleet = Global.getSector().getPlayerFleet();

            options.clearOptions();

            // TODO:
            //dialog.setOptionOnEscape("", null);
            switch ((OptionId) optionData) {
                case INIT:
                    text.addPara("You approach a conquest-class derelict. Almost good as new.");
                    options.addOption("Investigate", OptionId.INVESTIGATE);
                    options.addOption("Leave", OptionId.LEAVE);
                    dialog.setOptionOnEscape("Leave", OptionId.LEAVE);
                    break;
                case INVESTIGATE:
                    int heavyWeapons = (int) target.getCargo().getCommodityQuantity(Commodities.HAND_WEAPONS);
                    text.addPara("You send a few shuttles to investigate the derelict." +
                            " The first report comes in and %s were found.", Misc.getHighlightColor(),
                            heavyWeapons + " heavy armaments");
                    options.addOption("Bring them back", OptionId.BRING_BACK_THE_GOODS);
                    break;
                case BRING_BACK_THE_GOODS:
                    text.addPara("Your salvage team loads the first wave of cargo." +
                            " Right before the first shuttle is out, multiple warhead launches are detected across the derelict."
                            +
                            " \"They are targeting us!\"");
                    options.addOption("Take evasive maneuver!", OptionId.TAKE_EVASIVE_MANEUVER);
                    break;
                case TAKE_EVASIVE_MANEUVER:
                    text.addPara(
                            "Your whole fleet starts breaking off in an attempt to blindly escape the incoming missiles.");

                    // Find the slowest ship and hit the slowest one
                    List<FleetMemberAPI> fleetMembers = playerFleet.getFleetData().getMembersInPriorityOrder();
                    Collections.sort(fleetMembers, new Comparator<FleetMemberAPI>() {
                        @Override
                        public int compare(FleetMemberAPI o1, FleetMemberAPI o2) {
                            return (int) (o1.getStats().getMaxSpeed().modified - o2.getStats().getMaxSpeed().modified);
                        };
                    });

                    FleetMemberAPI slowShip;
                    for (int i = 0; i < fleetMembers.size(); i++) {
                        slowShip = fleetMembers.get(i);
                        slowShip.getShipName();
                        if (slowShip.getStats().getMaxSpeed().modified > 50) {
                            break;
                        }

                        // TODO: check if shielded or phased
                        if (slowShip.getStats().getFluxDissipation().modified > 350) {
                            continue;
                        }
                    }

                    break;
                /*
                 * case BRING_BACK_HA:
                 * dialog.getTextPanel().addPara("/// ALERT SHIP LOCKED ///");
                 * dialog.getTextPanel().addPara(
                 * "\"Captain! The derelict has targeted us! Multiple warheads incoming!\"");
                 * 
                 * options.addOption("Take evasive actions!", OptionId.EVASIVE_ACTIONS);
                 * options.addOption("Shield up!", OptionId.SHIELD_UP);
                 * break;
                 * case EVASIVE_ACTIONS:
                 * dialog.getTextPanel().addPara(
                 * "Your fleet starts evasive actions. Unfortunately, all incoming shuttles were not able to survive."
                 * );
                 * 
                 * crews = Utils.removeCrew(playerFleet, (float)
                 * playerFleet.getCargo().getCrew(), 0.05f, 2f, 100f);
                 * dialog.getTextPanel().addPara("Your fleet lost %s crewmembers.",
                 * Misc.getHighlightColor(),
                 * "" + (int) crews);
                 * dialog.getTextPanel().
                 * addPara("\"We won't be able to evade these missiles for long!\"");
                 * 
                 * setFireOptions(options);
                 * break;
                 * case SHIELD_UP:
                 * if (flagship.getHullSpec().getShieldType().equals(ShieldType.NONE)) {
                 * dialog.getTextPanel().addPara("\"We don't have shield Capt.. We're hit!\"");
                 * dialog.getTextPanel().
                 * addPara("\"Damages are minimal, we should target the derelict!\"");
                 * flagShipHit = true;
                 * } else {
                 * dialog.getTextPanel().addPara(
                 * "Your fleet shields up and all your incoming shuttle were able to hide behind them. Saving their precious cargo and lives."
                 * );
                 * 
                 * float heavyWeapons = (float)
                 * Math.floor(playerFleet.getCargo().getMaxCapacity() / 12f);
                 * if (heavyWeapons < 10f) {
                 * heavyWeapons = 10f;
                 * }
                 * 
                 * if (heavyWeapons > 214f) {
                 * heavyWeapons = 214f;
                 * }
                 * 
                 * dialog.getTextPanel().addPara("%s added to cargo.", Misc.getHighlightColor(),
                 * (int) heavyWeapons + " Heavy Armaments");
                 * playerFleet.getCargo().addCommodity(Commodities.HAND_WEAPONS, heavyWeapons);
                 * }
                 * 
                 * setFireOptions(options);
                 * break;
                 * case BRING_BACK_TEAM:
                 * dialog.getTextPanel().
                 * addPara("You order to bring back all of your shuttles. Leaving the sweet heavy armaments behind."
                 * );
                 * dialog.getTextPanel().
                 * addPara("Unfortunately, one of your ship gets hit by their unidentified warhead. It suffers minimal damage."
                 * );
                 * fleetShipHit = true;
                 * 
                 * // Set randomly one ship with the bio hazard
                 * List<FleetMemberAPI> fleetMembers =
                 * playerFleet.getFleetData().getMembersInPriorityOrder();
                 * FleetMemberAPI damagedShip = fleetMembers.get((int) Math.random() *
                 * fleetMembers.size());
                 * damagedShip.getVariant().addPermaMod(GL_Hullmods.BIO_HAZARD, false);
                 * 
                 * dialog.getTextPanel().addPara(
                 * "%s Its combat readiness is diminished.", Misc.getHighlightColor(),
                 * damagedShip.getShipName() + " now suffers from a rempant bio-hazard.");
                 * 
                 * break;
                 * case FIRE_ALL_BATTERIES:
                 * dialog.getTextPanel().
                 * addPara("\"Captain, We still have some crews onboard!\"");
                 * dialog.getTextPanel().addPara("\"Do it.\"");
                 * 
                 * dialog.getTextPanel().addPara(
                 * "All ships quickly target the derelict ship in a non-orderely manner. "
                 * +
                 * "It only takes a few seconds until a flash shadows the view. Leaving behind sole memories of your salvage team."
                 * );
                 * 
                 * crews = Utils.removeCrew(playerFleet, (float)
                 * playerFleet.getCargo().getCrew(), 0.125f, 5f, 220f);
                 * dialog.getTextPanel().addPara("Your fleet lost %s crewmembers.",
                 * Misc.getHighlightColor(), "" + (int) crews);
                 * 
                 * options.addOption("I think.. It's time to leave",
                 * flagShipHit ? OptionId.BEFORE_LEAVE : OptionId.COMPLETE);
                 * break;
                 * case TARGET_FIRE:
                 * dialog.getTextPanel().addPara("\"All missile pods locked, firing weapons!\""
                 * );
                 * dialog.getTextPanel().addPara("By an astonishing luck, all targets were hit."
                 * +
                 * " Your salvage team is able to locate the command room and shutdown the ship completely."
                 * );
                 * options.addOption("That was close. Proceed with the salvage operations",
                 * OptionId.SALVAGE_OPERATION);
                 * break;
                 * case SALVAGE_OPERATION:
                 * GaloEncountersCoordinator.completeEncounter(GaloEncountersCoordinator.
                 * CONQUEST_ENCOUNTER);
                 * RuleBasedInteractionDialogPluginImpl plugin = new
                 * RuleBasedInteractionDialogPluginImpl();
                 * dialog.setPlugin(plugin);
                 * plugin.init(dialog);
                 * break;
                 * case BEFORE_LEAVE:
                 * dialog.getTextPanel().addPara("/// BIO HAZARD DETECTED ///");
                 * dialog.getTextPanel().addPara(
                 * "The automated systems quickly shut off all doors, but it was already too late."
                 * +
                 * " Most of your ship becomes crippled by an unknown hazard." +
                 * " It is as if this ambush was not meant to destroy, but to kill.");
                 * 
                 * crews = Utils.removeCrew(playerFleet, (float) flagship.getMinCrew(), 0.5f,
                 * 2f,
                 * playerFleet.getCargo().getCrew() * 0.5f);
                 * dialog.getTextPanel().addPara("Your fleet lost %s crewmembers.",
                 * Misc.getHighlightColor(), "" + (int) crews);
                 * 
                 * flagship.getVariant().addPermaMod(GL_Hullmods.BIO_HAZARD, false);
                 * dialog.getTextPanel().addPara(
                 * "%s Your combat readiness is diminished.", Misc.getHighlightColor(),
                 * "Your flagship now suffers from a rempant bio-hazard.");
                 * 
                 * options.addOption("Leave", OptionId.COMPLETE);
                 * break;
                 * case COMPLETE:
                 * GaloEncountersCoordinator.completeEncounter(GaloEncountersCoordinator.
                 * CONQUEST_ENCOUNTER);
                 * 
                 * // Create explosion and remove derelict
                 * LocationAPI cloc = target.getContainingLocation();
                 * Vector2f loc = target.getLocation();
                 * ExplosionParams params = new ExplosionParams(new Color(255, 165, 100),
                 * cloc, loc, target.getRadius() + 25f, 1f);
                 * params.damage = ExplosionFleetDamage.LOW;
                 * 
                 * SectorEntityToken explosion = cloc.addCustomEntity(Misc.genUID(),
                 * "Conquest explosion",
                 * Entities.EXPLOSION, Factions.NEUTRAL, params);
                 * explosion.setLocation(loc.x, loc.y);
                 * target.getStarSystem().removeEntity(target);
                 * 
                 * dialog.dismiss();
                 * break;
                 */
                case LEAVE:
                    dialog.dismiss();
                    break;
                default:
                    break;
            }
        }
    }
/*
    private void setFireOptions(final OptionPanelAPI options) {
        options.addOption("Fire all batteries!",
                OptionId.FIRE_ALL_BATTERIES);
        options.addOption("Target their missile pods!", OptionId.TARGET_FIRE);
        SetStoryOption.set(dialog, 1, OptionId.TARGET_FIRE, null, Sounds.STORY_POINT_SPEND_COMBAT,
                "Targeted all missile pods");
    }
*/
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
