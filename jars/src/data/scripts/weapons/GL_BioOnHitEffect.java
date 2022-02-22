package data.scripts.weapons;

import java.util.Date;

import com.fs.starfarer.api.Global;
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
    private final static Logger logger = Global.getLogger(GL_BioOnHitEffect.class);

    private ShipAPI target;
    private Integer ticks;
    private float crLossPerTick;
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
        Integer tickAmount = (int) Math.floor(shipMinCrew / shipHullPoints * projDamage * 2);

        // Make sure we only have 1 watcher for bio cr degradation
        Integer crTicks = (Integer) target.getCustomData().get(CUSTOM_DATA_CR_TICKS);
        if (crTicks != null && crTicks != 0) {
            target.setCustomData(CUSTOM_DATA_CR_TICKS, crTicks + tickAmount);
            return;
        }

        // Calculate cr loss for each tick
        float crLossPerTick = (float) Math.floor((projDamage / shipHullPoints) * 200) / 100;
        if (crLossPerTick > 0.15f) {
            crLossPerTick = 0.15f;
        }

        // Create effect
        target.setCustomData(CUSTOM_DATA_CR_TICKS, tickAmount);
        GL_BioOnHitEffect effect = new GL_BioOnHitEffect(ship, crLossPerTick);
        CombatEntityAPI entity = engine.addLayeredRenderingPlugin(effect);
        entity.getLocation().set(proj.getLocation());
    }

    // ----------------------------------------------------------------------------------------------
    // HANDLE LASTING EFFECT
    // ----------------------------------------------------------------------------------------------
    public GL_BioOnHitEffect(ShipAPI target, float crLossPerTick) {
        this.target = target;
        this.crLossPerTick = crLossPerTick;

        interval = new IntervalUtil(3f, 5f);
    }

    public void init(CombatEntityAPI entity) {
        super.init(entity);
    }

    public void advance(float amount) {
        if (Global.getCombatEngine().isPaused()) {
            return;
        }

        ticks = (Integer) target.getCustomData().get(CUSTOM_DATA_CR_TICKS);
        interval.advance(amount);
        if (interval.intervalElapsed()) {
            target.setCustomData(CUSTOM_DATA_CR_TICKS, --ticks);
            target.setCurrentCR(target.getCurrentCR() - crLossPerTick);
            logger.info("CR DEGRADE: " + new Date().toString() + " - " + ticks + " - current cr: "
                    + target.getCurrentCR() + " CR DEGRADATION BY: " + crLossPerTick);
        }
    }

    public boolean isExpired() {
        return ticks == 0 || !target.isAlive() || !Global.getCombatEngine().isEntityInPlay(target);
    }
}
