package com.pvpclient.module.combat;

import com.pvpclient.module.Category;
import com.pvpclient.module.ClientModule;
import com.pvpclient.setting.BooleanSetting;
import com.pvpclient.setting.ModeSetting;
import com.pvpclient.setting.NumberSetting;
import com.pvpclient.util.CombatUtil;
import net.minecraft.entity.LivingEntity;

/** Practice aura for private test worlds. Attacks only what is inside your own view cone; never rotates you. */
public final class KillAuraModule extends ClientModule {
    private final NumberSetting range = add(new NumberSetting("Range", 3.0, 2.0, 4.5, 0.1));
    private final NumberSetting fov = add(new NumberSetting("FOV", 100.0, 30.0, 360.0, 5.0));
    private final NumberSetting charge = add(new NumberSetting("Cooldown Charge", 0.95, 0.5, 1.0, 0.05));
    private final ModeSetting targets = add(new ModeSetting("Targets", 2, CombatUtil.TARGET_MODES));
    private final BooleanSetting lineOfSight = add(new BooleanSetting("Require Line of Sight", true));

    public KillAuraModule() {
        super("KillAura", "Practice mode: attacks targets inside your view cone", Category.COMBAT);
        markRestricted();
    }

    @Override
    public void onTick() {
        if (mc.player == null || mc.world == null || mc.interactionManager == null) return;
        if (mc.currentScreen != null || mc.player.isUsingItem()) return;
        if (mc.player.getAttackCooldownProgress(0.0f) < charge.get()) return;

        LivingEntity target = CombatUtil.findTarget(mc, range.get(), fov.get(), targets.get(), lineOfSight.get());
        if (target != null) CombatUtil.attack(mc, target);
    }
}
