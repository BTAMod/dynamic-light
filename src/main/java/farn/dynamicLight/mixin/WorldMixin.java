package farn.dynamicLight.mixin;

import net.minecraft.core.world.World;
import farn.dynamicLight.util.*;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = World.class, remap = false)
public class WorldMixin {

    public World world = (World)(Object)this;

    /**
     * @author AtomicStryker
     * @reason farnfarn02
     */
    @Overwrite
    public float getBrightness(int i, int j, int k, int l)
    {
        float lc = LightCache.cache.getLightValue(i, j, k);
        if(lc > l)
        {
            return lc;
        }

        int lightValue = world.getBlockLightValue(i, j, k);
        float torchLight = PlayerTorchArray.getLightBrightness(world, i, j, k);
        if(lightValue < torchLight)
        {
            int floorValue = (int)java.lang.Math.floor(torchLight);
            if(floorValue==15)
            {
                return world.getWorldType().getBrightnessRamp()[15];
            }
            else
            {
                int ceilValue = (int)java.lang.Math.ceil(torchLight);
                float lerpValue = torchLight-floorValue;
                return (1.0f-lerpValue)*world.getWorldType().getBrightnessRamp()[floorValue]+lerpValue*world.getWorldType().getBrightnessRamp()[ceilValue];
            }
        }

        lc = world.getWorldType().getBrightnessRamp()[lightValue];
        LightCache.cache.setLightValue(i, j, k, lc);
        return lc;
    }

    /**
     * @author AtomicStryker
     * @reason farnfarn02
     */
    @Overwrite
    public float getLightBrightness(int i, int j, int k)
    {
        float lc = LightCache.cache.getLightValue(i, j, k);
        if(lc >= 0)
        {
            return lc;
        }

        int lightValue = world.getBlockLightValue(i, j, k);
        float torchLight = PlayerTorchArray.getLightBrightness(world, i, j, k);
        if(lightValue < torchLight)
        {
            int floorValue = (int)java.lang.Math.floor(torchLight);
            if(floorValue==15)
            {
                return world.getWorldType().getBrightnessRamp()[15];
            }
            else
            {
                int ceilValue = (int)java.lang.Math.ceil(torchLight);
                float lerpValue = torchLight-floorValue;
                return (1.0f-lerpValue)*world.getWorldType().getBrightnessRamp()[floorValue]+lerpValue*world.getWorldType().getBrightnessRamp()[ceilValue];
            }
        }

        lc = world.getWorldType().getBrightnessRamp()[lightValue];
        LightCache.cache.setLightValue(i, j, k, lc);
        return lc;
    }


    @Inject(at = @At(value = "TAIL"), method = "notifyBlockOfNeighborChange")
    private void injected(int i, int j, int k, int blockID, CallbackInfo ci) {
        world.markBlockNeedsUpdate(i, j, k);
    }



}
