package data.scripts.weapons;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.combat.BaseCombatLayeredRenderingPlugin;
import com.fs.starfarer.api.combat.CombatEngineAPI;
import com.fs.starfarer.api.combat.CombatEntityAPI;
import com.fs.starfarer.api.combat.DamagingProjectileAPI;
import com.fs.starfarer.api.combat.OnHitEffectPlugin;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.combat.listeners.ApplyDamageResultAPI;

import org.apache.log4j.Logger;
import org.lwjgl.util.vector.Vector2f;

public class GL_BioOnHitEffect extends BaseCombatLayeredRenderingPlugin implements OnHitEffectPlugin {

    private final static Logger logger = Global.getLogger(GL_BioOnHitEffect.class);

    private DamagingProjectileAPI proj;
    private ShipAPI target;
    private int test = 10;

    public GL_BioOnHitEffect() {}

    public GL_BioOnHitEffect(DamagingProjectileAPI proj, ShipAPI target) {
        this.proj = proj;
        this.target = target;
    }

    public void init(CombatEntityAPI entity) {
        super.init(entity);
    }

    public void advance(float amount) {
        if (Global.getCombatEngine().isPaused()) {
            return;
        }

        logger.info("hello");
        --test;
    }

    public boolean isExpired() {
        return test == 0;
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

        GL_BioOnHitEffect effect = new GL_BioOnHitEffect(proj, (ShipAPI) target);
        CombatEntityAPI entity = engine.addLayeredRenderingPlugin(effect);
        entity.getLocation().set(proj.getLocation());
    }
}
