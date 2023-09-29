package farn.dynamicLight.mixin;

import net.minecraft.core.entity.EntityTNT;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = EntityTNT.class, remap = false)
public class EntityTNTMixin {

	public EntityTNT tnt = (EntityTNT)(Object)this;

	@Inject(method = "init()V", at = @At("TAIL"))
	public void onEntityCreate(float partialTicks, CallbackInfo ci) {
		tnt.entityBrightness = 1.0F;

	}

}
