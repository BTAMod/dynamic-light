package farn.dynamicLight.config;

import net.minecraft.client.gui.*;
import net.minecraft.client.render.Tessellator;
import net.minecraft.core.lang.I18n;
import org.lwjgl.opengl.GL11;

import java.awt.*;

public class GuiDynamicLightConfiguration extends GuiScreen {
    public GuiDynamicLightConfiguration(GuiScreen par) {
        super(par);
    }

    @Override
    public void drawScreen(int x, int y, float renderPartialTicks) {
        this.drawDefaultBackground();
        this.drawStringCentered(this.fontRenderer, I18n.getInstance().translateKey("dynamic_light.cfg_title"), this.width / 2, 20, 0xFFFFFF);
        this.overlayBackground();
        super.drawScreen(x, y, renderPartialTicks);
    }

    @Override
    public void initGui() {
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

    public void onGuiClosed() {
        super.onGuiClosed();
        DynamicLightConfig.initializeSettingsFile(false);
        this.mc.renderGlobal.loadRenderers();
    }

    private void overlayBackground() {
        if(this.mc.theWorld == null) {
            Tessellator tessellator = Tessellator.instance;
            GL11.glBindTexture(3553, this.mc.renderEngine.getTexture("/gui/background.png"));
            GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
            float f1 = 32.0f;
            tessellator.startDrawingQuads();
            tessellator.setColorOpaque_I(0x202020);
            tessellator.addVertexWithUV(0.0, this.height - 64, 0.0, 0.0f / f1, (float) (this.height - 64 + 32.0F / f1));
            tessellator.addVertexWithUV(this.width, this.height - 64, 0.0, (float) this.width / f1, (float) (this.height - 64 + 32.0F) / f1);
            tessellator.addVertexWithUV(this.width, 32, 0.0, (float) this.width / f1, 32.0F / f1);
            tessellator.addVertexWithUV(0.0, 32, 0.0, 0.0f / f1, 32.0F / f1);
            tessellator.draw();
        } else {
            this.drawRect(0, 0, this.width, 32, 0x5F000000);
            this.drawRect(0, this.height - 64, this.width, this.height, 0x5F000000);
        }
    }
}
