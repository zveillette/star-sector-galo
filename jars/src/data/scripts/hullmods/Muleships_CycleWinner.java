package jars.src.data.scripts.hullmods;

import com.fs.starfarer.api.combat.BaseHullMod;
import com.fs.starfarer.api.combat.MutableShipStatsAPI;
import com.fs.starfarer.api.combat.ShipAPI.HullSize;

public class Muleships_CycleWinner extends BaseHullMod {

	private static final float SUPPLY_USE_MULT = 2f;
	private static final float SPEED_BOOST = 10f;
	
	public void applyEffectsBeforeShipCreation(HullSize hullSize, MutableShipStatsAPI stats, String id) {
		stats.getSuppliesPerMonth().modifyMult(id, SUPPLY_USE_MULT);
		stats.getMaxSpeed().modifyFlat(id, SPEED_BOOST);
	}
	
	public String getDescriptionParam(int index, HullSize hullSize) {
		if (index == 0) return "" + SPEED_BOOST;
		if (index == 1) return "" + (int)((SUPPLY_USE_MULT - 1f) * 100f) + "%";
		return null;
	}
}
