package farn.dynamicLight.config;

import farn.dynamicLight.DynamicLight;
import io.github.prospector.modmenu.api.ModMenuApi;
import net.minecraft.client.gui.GuiScreen;
import java.util.function.Function;

public class DynamicLightConfig  implements ModMenuApi {

    @Override
    public String getModId() {
        return DynamicLight.MOD_ID;
    }

    @Override
    public Function<GuiScreen, ? extends GuiScreen> getConfigScreenFactory() {
        return screen -> new GuiDynamicLightConfiguration(screen);
    }
}
