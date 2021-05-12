package jars.src.data.scripts.hullmods;

import com.fs.starfarer.api.combat.BaseHullMod;
import com.fs.starfarer.api.combat.MutableShipStatsAPI;
import com.fs.starfarer.api.combat.ShipAPI.HullSize;

public class Muleships_DsRacerFreighter extends BaseHullMod {
	private static final float CARGO_CAPACITY = 20f;
	private static final float FUEL_CAPACITY = 10f;
	private static final float MINIMUM_CREW = 14f;
	private static final float MAX_CREW = 15f;
	private static final float BURN_LEVEL = -1f;
	private static final float MAX_SPEED_MULT = 0.8f;
	private static final float MAX_TURN_RATE = 0.8f;
	
	public void applyEffectsBeforeShipCreation(HullSize hullSize, MutableShipStatsAPI stats, String id) {
		stats.getFuelMod().modifyFlat(id, FUEL_CAPACITY);
		stats.getCargoMod().modifyFlat(id, CARGO_CAPACITY);
		stats.getMinCrewMod().modifyFlat(id, MINIMUM_CREW);
		stats.getMaxCrewMod().modifyFlat(id, MAX_CREW);
		stats.getMaxBurnLevel().modifyFlat(id, BURN_LEVEL);
		stats.getMaxSpeed().modifyMult(id, MAX_SPEED_MULT);
		stats.getMaxTurnRate().modifyMult(id, MAX_TURN_RATE);
	}
	
	public String getDescriptionParam(int index, HullSize hullSize) {
		return null;
	}
}
