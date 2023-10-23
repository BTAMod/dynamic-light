package farn.dynamicLight.mixin;

import farn.dynamicLight.util.DynamicLightDispatcher;
import net.minecraft.client.render.ChunkRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = ChunkRenderer.class, remap = false)
public class ChunkRendererMixin {
    @Inject(method = "updateRenderer", at = @At("TAIL"))
    private void afterUpdateRenderer(CallbackInfo ci) {
        DynamicLightDispatcher.clearCacheAndResetPool();
    }

}
