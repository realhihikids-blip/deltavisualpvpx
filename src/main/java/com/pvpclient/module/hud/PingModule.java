package com.pvpclient.module.hud;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.network.PlayerListEntry;

public final class PingModule extends HudModule {
    public PingModule() {
        super("Ping Counter", "Latency to the server", 1.5, 13.5);
    }

    @Override
    protected void draw(DrawContext c) {
        String text = "Ping: --";
        if (mc.isInSingleplayer()) {
            text = "Ping: local";
        } else if (mc.getNetworkHandler() != null && mc.player != null) {
            PlayerListEntry entry = mc.getNetworkHandler().getPlayerListEntry(mc.player.getUuid());
            if (entry != null) text = "Ping: " + entry.getLatency() + " ms";
        }
        textBox(c, text);
    }
}
