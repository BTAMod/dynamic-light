package farn.dynamicLight;

import net.fabricmc.api.ClientModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import farn.dynamicLight.config.DynamicLightConfig;
public class DynamicLight implements ClientModInitializer {
    public static final String MOD_ID = "dynamic_light";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    @Override
    public void onInitializeClient() {
        LOGGER.info("Dynamic Light Mod initialized.");
        DynamicLightConfig.initializeSettingsFile(true);
    }
}
