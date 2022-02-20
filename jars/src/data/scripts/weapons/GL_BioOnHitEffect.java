package data.scripts.weapons;

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

    private final static String CUSTOM_DATA_CR_IMPACT = "GL_BIO_CR_IMPACT";
    private final static Logger logger = Global.getLogger(GL_BioOnHitEffect.class);

    private ShipAPI target;
    private Integer crImpact = 5;
    private IntervalUtil interval;

    public GL_BioOnHitEffect() {}

    public GL_BioOnHitEffect(ShipAPI target) {
        this.target = target;

        interval = new IntervalUtil(2f, 3f);
		interval.forceIntervalElapsed();
    }

    public void init(CombatEntityAPI entity) {
        super.init(entity);
    }

    public void advance(float amount) {
        if (Global.getCombatEngine().isPaused()) {
            return;
        }

        crImpact = (Integer) target.getCustomData().get(CUSTOM_DATA_CR_IMPACT);
        interval.advance(amount);
        if (interval.intervalElapsed()) {
            interval.setElapsed(0);
            target.setCustomData(CUSTOM_DATA_CR_IMPACT, --crImpact);
            logger.info("CR DEGRADE: " + crImpact);
		}
    }

    public boolean isExpired() {
        return crImpact == 0 || !target.isAlive() || !Global.getCombatEngine().isEntityInPlay(target);
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

        // Make sure we only have 1 watcher for bio cr degradation
        Integer crImpact = (Integer) target.getCustomData().get(CUSTOM_DATA_CR_IMPACT);
        if (crImpact != null) {
            target.setCustomData(CUSTOM_DATA_CR_IMPACT, crImpact + 5);
        }

        target.setCustomData(CUSTOM_DATA_CR_IMPACT, 5);
        GL_BioOnHitEffect effect = new GL_BioOnHitEffect((ShipAPI) target);
        CombatEntityAPI entity = engine.addLayeredRenderingPlugin(effect);
        entity.getLocation().set(proj.getLocation());
    }
}
