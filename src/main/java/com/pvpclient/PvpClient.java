package com.pvpclient;

import com.pvpclient.config.ConfigManager;
import com.pvpclient.gui.ClickGuiScreen;
import com.pvpclient.module.ModuleManager;
import com.pvpclient.module.SettingsModule;
import com.pvpclient.module.combat.KillAuraModule;
import com.pvpclient.module.combat.TriggerBotModule;
import com.pvpclient.module.hud.CpsModule;
import com.pvpclient.module.hud.FpsModule;
import com.pvpclient.module.hud.KeystrokesModule;
import com.pvpclient.module.hud.PingModule;
import com.pvpclient.module.visual.PlayerEspModule;
import com.pvpclient.module.visual.PlayerInfoModule;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import org.lwjgl.glfw.GLFW;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class PvpClient implements ClientModInitializer {
    public static final String MOD_ID = "pvpclient";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    private static ModuleManager modules;
    private static SettingsModule settings;
    private static KeyBinding openGuiKey;
    private static final java.util.List<KeyBinding> OPEN_KEYS = new java.util.ArrayList<>();

    public static ModuleManager modules() { return modules; }
    public static SettingsModule settings() { return settings; }
    public static KeyBinding openGuiKey() { return openGuiKey; }

    /** True if this key press matches any of the menu keys (Right Shift, F3, =). */
    public static boolean isOpenKey(int keyCode, int scanCode) {
        for (KeyBinding k : OPEN_KEYS) if (k.matchesKey(keyCode, scanCode)) return true;
        return false;
    }

    @Override
    public void onInitializeClient() {
        modules = new ModuleManager();
        settings = new SettingsModule();

        // Combat (practice mode, private worlds only)
        modules.register(new KillAuraModule());
        modules.register(new TriggerBotModule());
        // Visuals
        modules.register(new PlayerEspModule());
        modules.register(new PlayerInfoModule());
        // HUD
        modules.register(new CpsModule());
        modules.register(new FpsModule());
        modules.register(new PingModule());
        modules.register(new KeystrokesModule());
        // Settings
        modules.register(settings);

        ConfigManager.load(modules);

        openGuiKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.pvpclient.open_gui", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_RIGHT_SHIFT, "category.pvpclient"));
        OPEN_KEYS.add(openGuiKey);
        OPEN_KEYS.add(KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.pvpclient.open_gui_f3", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_F3, "category.pvpclient")));
        OPEN_KEYS.add(KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.pvpclient.open_gui_equal", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_EQUAL, "category.pvpclient")));

        modules.init();

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            boolean pressed = false;
            for (KeyBinding k : OPEN_KEYS) {
                while (k.wasPressed()) pressed = true;
            }
            if (pressed && client.currentScreen == null && client.world != null) {
                client.setScreen(new ClickGuiScreen());
            }
        });
        ClientLifecycleEvents.CLIENT_STOPPING.register(client -> ConfigManager.save(modules));

        LOGGER.info("PvP Client loaded. Press RIGHT SHIFT, F3 or = in a world to open the menu.");
    }
}
