package farn.dynamicLight.config;

import net.minecraft.client.gui.*;
import net.minecraft.core.lang.I18n;

import farn.dynamicLight.DynamicLight;

import java.awt.*;

public class GuiDynamicLightConfiguration extends GuiScreen {
    private int centerButtonY = this.height / 4 + 48;
    public GuiDynamicLightConfiguration(GuiScreen par) {
        super(par);
    }

    @Override
    public void drawScreen(int x, int y, float renderPartialTicks) {
        this.drawDefaultBackground();
        this.drawStringCentered(this.fontRenderer, I18n.getInstance().translateKey("dynamic_light.open_cfg"), this.width / 2, 20, 0xFFFFFF);
        super.drawScreen(x, y, renderPartialTicks);
    }

    @Override
    public void initGui() {
        I18n i18n = I18n.getInstance();
        this.controlList.add(new GuiButton(0, this.width / 2 - 100, centerButtonY + 72 + 30, i18n.translateKey("dynamic_light.open_cfg")));
        this.controlList.add(new GuiButton(1, this.width / 2 - 100, centerButtonY + 72 + 60, i18n.translateKey("dynamic_light.reset_cfg")));
        this.controlList.add(new GuiButton(2, this.width / 2 - 100, centerButtonY + 72 + 120, i18n.translateKey("gui.done")));
    }

    @Override
    protected void buttonPressed(GuiButton guibutton) {
        if(guibutton.id == 0) {
            try {
                if(DynamicLight.settingsFile.exists())
                Desktop.getDesktop().open(DynamicLight.settingsFile);
            } catch (Exception e) {
            }
        } else {
            if(guibutton.id == 1) {
                DynamicLight.writeDefaultSettingFile();
            }
            this.mc.displayGuiScreen(this.getParentScreen());
        }
    }

    public void onGuiClosed() {
        super.onGuiClosed();
        DynamicLight.initializeSettingsFile(false);
        this.mc.renderGlobal.loadRenderers();
    }
}
