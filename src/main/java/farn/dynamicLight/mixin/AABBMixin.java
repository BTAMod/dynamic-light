package farn.dynamicLight.mixin;

import java.util.List;


import net.minecraft.core.util.phys.AABB;
import farn.dynamicLight.Main;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(value = AABB.class, remap = false)
public class AABBMixin {
    @Shadow
    static List boundingBoxes;

    @Shadow
    static int numBoundingBoxesInUse;

    /**
     * @author AtomicStryker
     * @reason farnfarn02
     */
    @Overwrite
    public static void deinitializePool() {
        boundingBoxes.clear();
        numBoundingBoxesInUse = 0;
        Main.onPoolUnInitialize();
    }

}
