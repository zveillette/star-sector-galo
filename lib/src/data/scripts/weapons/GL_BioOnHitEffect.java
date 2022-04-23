package data.scripts.weapons;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.CampaignFleetAPI;
import com.fs.starfarer.api.combat.BaseCombatLayeredRenderingPlugin;
import com.fs.starfarer.api.combat.CombatEngineAPI;
import com.fs.starfarer.api.combat.CombatEntityAPI;
import com.fs.starfarer.api.combat.DamagingProjectileAPI;
import com.fs.starfarer.api.combat.OnHitEffectPlugin;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.combat.listeners.ApplyDamageResultAPI;
import com.fs.starfarer.api.util.IntervalUtil;

import org.apache.log4j.Logger;
import org.lwjgl.util.vector.Vector2f;

public class GL_BioOnHitEffect extends BaseCombatLayeredRenderingPlugin implements OnHitEffectPlugin {

    private final static String CUSTOM_DATA_CR_TICKS = "GL_BIO_CR_TICKS";
    private final static String CUSTOM_DATA_CR_HIT = "GL_BIO_CR_HIT";
    private final static Logger logger = Global.getLogger(GL_BioOnHitEffect.class);

    private ShipAPI target;
    private Integer ticks;
    private float crHit;
    private IntervalUtil interval;

    public GL_BioOnHitEffect() {
    }

    @Override
    public void onHit(DamagingProjectileAPI proj, CombatEntityAPI target, Vector2f point, boolean isShield,
            ApplyDamageResultAPI damageResult, CombatEngineAPI engine) {

        if (target == null || point == null || isShield == true) {
            return;
        }

        if (!(target instanceof ShipAPI)) {
            return;
        }

        ShipAPI ship = (ShipAPI) target;
        if (!ship.isAlive()) {
            return;
        }

        // Calculate amount of tick for cr degradation
        float projDamage = proj.getDamageAmount();
        float shipHullPoints = ship.getMutableStats().getHullBonus()
                .computeEffective(ship.getHullSpec().getHitpoints());
        float shipMinCrew = ship.getMutableStats().getMinCrewMod().computeEffective(ship.getHullSpec().getMinCrew());
        Integer tickAmount = (int) Math.floor(shipMinCrew / shipHullPoints * projDamage * 1.5);

        // Calculate cr loss for each tick
        float crLossPerTick = (float) Math.floor((projDamage / shipHullPoints) * 150) / 100;
        if (crLossPerTick > 0.10f) {
            crLossPerTick = 0.10f;
        }

        // Make sure we only have 1 watcher for bio cr degradation
        Integer currentCrTicks = (Integer) target.getCustomData().get(CUSTOM_DATA_CR_TICKS);
        if (currentCrTicks != null && currentCrTicks != 0) {
            // Every hit, increase degradation length by half calculation
            target.setCustomData(CUSTOM_DATA_CR_TICKS, currentCrTicks + (tickAmount / 2));

            // Every hit, increase degradation strength divided by nbr of hits
            float currentCrHit = (float) target.getCustomData().get(CUSTOM_DATA_CR_HIT);
            float nbrTimeHit = 1;
            if (currentCrHit != 0) {
                nbrTimeHit = currentCrHit / crLossPerTick;
            }

            target.setCustomData(CUSTOM_DATA_CR_HIT, currentCrHit + (crLossPerTick / nbrTimeHit / 2));
            return;
        }

        // Create effect
        target.setCustomData(CUSTOM_DATA_CR_TICKS, tickAmount);
        target.setCustomData(CUSTOM_DATA_CR_HIT, crLossPerTick);

        GL_BioOnHitEffect effect = new GL_BioOnHitEffect(ship);
        CombatEntityAPI entity = engine.addLayeredRenderingPlugin(effect);
        entity.getLocation().set(proj.getLocation());
    }

    // ----------------------------------------------------------------------------------------------
    // HANDLE LASTING EFFECT
    // ----------------------------------------------------------------------------------------------
    public GL_BioOnHitEffect(ShipAPI target) {
        this.target = target;
        logger.info("Created " + GL_BioOnHitEffect.class.getName() + " on target: " + target.getName());

        if (target.isFrigate()) {
            interval = new IntervalUtil(3f, 5f);
        } else if (target.isDestroyer()) {
            interval = new IntervalUtil(5f, 7f);
        } else {
            interval = new IntervalUtil(7f, 9f);
        }
    }

    public void init(CombatEntityAPI entity) {
        super.init(entity);
    }

    public void advance(float amount) {
        if (Global.getCombatEngine().isPaused()) {
            return;
        }

        ticks = (Integer) target.getCustomData().get(CUSTOM_DATA_CR_TICKS);
        crHit = (float) target.getCustomData().get(CUSTOM_DATA_CR_HIT);
        interval.advance(amount);
        if (interval.intervalElapsed()) {
            target.setCustomData(CUSTOM_DATA_CR_TICKS, --ticks);
            target.setCurrentCR(target.getCurrentCR() - crHit);

            // Kill ship crew from inventory
            CampaignFleetAPI playerFleet = Global.getSector().getPlayerFleet();
            if (!Global.getCombatEngine().isSimulation()
                    && playerFleet.getFleetData().getMembersListCopy().contains(target.getFleetMember())) {
                float shipMinCrew = target.getMutableStats().getMinCrewMod()
                        .computeEffective(target.getHullSpec().getMinCrew());
                int deadCrew = (int) (shipMinCrew * crHit);
                playerFleet.getCargo().removeCrew(deadCrew);
            }
        }

        if (ticks == 0) {
            target.setCustomData(CUSTOM_DATA_CR_HIT, 0);
        }
    }

    public boolean isExpired() {
        return ticks == 0 || target.getCurrentCR() <= 0f || !target.isAlive()
                || !Global.getCombatEngine().isEntityInPlay(target);
    }
}
