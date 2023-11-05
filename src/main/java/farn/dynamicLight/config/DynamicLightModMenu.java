package farn.dynamicLight.config;

import farn.dynamicLight.DynamicLight;
import io.github.prospector.modmenu.api.ModMenuApi;
import io.github.prospector.modmenu.util.TriConsumer;
import net.minecraft.client.gui.GuiScreen;

import java.awt.*;
import java.util.function.Function;

public class DynamicLightModMenu implements ModMenuApi {

    @Override
    public String getModId() {
        return DynamicLight.MOD_ID;
    }

    @Override
    public Function<GuiScreen, ? extends GuiScreen> getConfigScreenFactory() {
        return screen -> new GuiDynamicLightConfiguration(screen);
    }
}
