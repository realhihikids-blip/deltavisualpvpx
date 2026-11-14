package com.pvpclient.module.visual;

import com.pvpclient.module.Category;
import com.pvpclient.module.ClientModule;
import com.pvpclient.setting.BooleanSetting;
import com.pvpclient.setting.ColorSetting;
import com.pvpclient.setting.NumberSetting;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.VertexRendering;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.scoreboard.AbstractTeam;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

/** Outline boxes around players. Depth-tested, so they follow normal line of sight. */
public final class PlayerEspModule extends ClientModule {
    private final ColorSetting color = add(new ColorSetting("ESP Color", 0x8B5CF6));
    private final BooleanSetting teamColors = add(new BooleanSetting("Use Team Colors", true));
    private final BooleanSetting highlight = add(new BooleanSetting("Highlight", true));
    private final ColorSetting highlightColor = add(new ColorSetting("Highlight Color", 0x3B82F6));
    private final NumberSetting distance = add(new NumberSetting("Distance", 64, 8, 128, 1));

    public PlayerEspModule() {
        super("Player ESP", "Outline boxes around players (private worlds only)", Category.VISUALS);
        markRestricted();
    }

    @Override
    public void onWorldRender(WorldRenderContext ctx) {
        if (mc.world == null || mc.player == null) return;
        MatrixStack ms = ctx.matrixStack();
        VertexConsumerProvider vcp = ctx.consumers();
        if (ms == null || vcp == null) return;

        Vec3d cam = ctx.camera().getPos();
        float td = mc.getRenderTickCounter().getTickDelta(false);
        double max = distance.get();

        ms.push();
        ms.translate(-cam.x, -cam.y, -cam.z);
        VertexConsumer lines = vcp.getBuffer(RenderLayer.getLines());

        for (AbstractClientPlayerEntity p : mc.world.getPlayers()) {
            if (p == mc.player || p.isSpectator() || !p.isAlive()) continue;
            if (p.distanceTo(mc.player) > max) continue;

            double dx = MathHelper.lerp(td, p.prevX, p.getX()) - p.getX();
            double dy = MathHelper.lerp(td, p.prevY, p.getY()) - p.getY();
            double dz = MathHelper.lerp(td, p.prevZ, p.getZ()) - p.getZ();
            Box box = p.getBoundingBox().offset(dx, dy, dz);

            float r = color.r(), g = color.g(), b = color.b();
            AbstractTeam team = p.getScoreboardTeam();
            if (teamColors.get() && team != null && team.getColor().getColorValue() != null) {
                int tc = team.getColor().getColorValue();
                r = ((tc >> 16) & 255) / 255f;
                g = ((tc >> 8) & 255) / 255f;
                b = (tc & 255) / 255f;
            }
            VertexRendering.drawBox(ms, lines, box, r, g, b, 1.0f);

            if (highlight.get()) {
                VertexRendering.drawBox(ms, lines, box.expand(0.04), highlightColor.r(), highlightColor.g(), highlightColor.b(), 1.0f);
            }
        }
        ms.pop();

        if (vcp instanceof VertexConsumerProvider.Immediate immediate) {
            immediate.draw(RenderLayer.getLines());
        }
    }
}
