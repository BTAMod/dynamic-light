package farn.dynamicLight.config;

import net.minecraft.client.gui.*;
import net.minecraft.client.render.tessellator.Tessellator;
import net.minecraft.core.lang.I18n;
import org.lwjgl.opengl.GL11;

import java.awt.*;

public class GuiDynamicLightConfiguration extends GuiScreen {
    public GuiDynamicLightConfiguration(GuiScreen par) {
        super(par);
    }

    @Override
    public void drawScreen(int x, int y, float renderPartialTicks) {
        drawBackground();
        this.drawStringCentered(this.fontRenderer, I18n.getInstance().translateKey("dynamic_light.cfg_title"), this.width / 2, 20, 0xFFFFFF);
        //this.overlayBackground();
        super.drawScreen(x, y, renderPartialTicks);
    }
    @Override
    public void init() {
        I18n i18n = I18n.getInstance();
        this.controlList.add(new GuiButton(0, this.width / 2 - 100, this.height - 224, i18n.translateKey("dynamic_light.open_cfg")));
        this.controlList.add(new GuiButton(1, this.width / 2 - 100,  this.height - 188, i18n.translateKey("dynamic_light.reset_cfg")));
        this.controlList.add(new GuiButton(2, this.width / 2 - 100, this.height - 28, i18n.translateKey("gui.done")));
    }

    @Override
    protected void buttonPressed(GuiButton guibutton) {
        if(guibutton.id == 0) {
            try {
                if(DynamicLightConfig.settingsFile.exists())
                Desktop.getDesktop().open(DynamicLightConfig.settingsFile);
            } catch (Exception e) {
            }
        } else {
            if(guibutton.id == 1) {
                DynamicLightConfig.writeDefaultSettingFile();
            }
            this.mc.displayGuiScreen(this.getParentScreen());
        }
    }

    @Override
    public void onClosed() {
        super.onClosed();
        DynamicLightConfig.initializeSettingsFile(false);
        this.mc.renderGlobal.loadRenderers();
    }

    @Override
    public void drawBackground() {
        super.drawBackground();
    }
}
