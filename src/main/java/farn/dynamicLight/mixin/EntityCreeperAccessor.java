package farn.dynamicLight.mixin;

import net.minecraft.core.entity.monster.EntityCreeper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(value = EntityCreeper.class, remap = false)
public interface EntityCreeperAccessor {

	@Accessor("timeSinceIgnited")
	int ignitedTime();

}
