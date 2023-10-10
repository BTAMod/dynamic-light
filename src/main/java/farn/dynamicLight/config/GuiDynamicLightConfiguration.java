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
        this.drawStringCentered(this.fontRenderer, "Dynamic Light Configuration", this.width / 2, 20, 0xFFFFFF);
        this.drawStringCentered(this.fontRenderer, "More thing will be add here in full 1.2", this.width / 2, 80, 0xFFFFFF);
        super.drawScreen(x, y, renderPartialTicks);
    }

    @Override
    public void initGui() {
        I18n i18n = I18n.getInstance();
        this.controlList.add(new GuiButton(0, this.width / 2 - 100, centerButtonY + 72 + 90, "Open config file"));
        this.controlList.add(new GuiButton(1, this.width / 2 - 100, centerButtonY + 72 + 120, "Done"));
    }

    @Override
    protected void buttonPressed(GuiButton guibutton) {
        if(guibutton.id == 0) {
            try {
                Desktop.getDesktop().open(DynamicLight.settingsFile);
            } catch (Exception e) {
                System.err.println("Error opening '" + DynamicLight.settingsFile.getAbsolutePath() + "'!");
                e.printStackTrace();
            }
        } else {
            DynamicLight.writeSettingFile(true);
            this.mc.renderGlobal.loadRenderers();
            this.mc.displayGuiScreen(this.getParentScreen());
        }

    }
}
