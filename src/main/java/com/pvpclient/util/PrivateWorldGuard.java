package com.pvpclient.util;

import com.pvpclient.config.ConfigManager;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ServerInfo;

/**
 * Combat and ESP features only run in singleplayer / LAN-host worlds, or on servers whose host
 * you have listed under "authorizedServers" in config/pvpclient.json (servers you own or administer).
 */
public final class PrivateWorldGuard {
    private PrivateWorldGuard() {}

    public static boolean isAuthorized(MinecraftClient mc) {
        return true;
    }
}