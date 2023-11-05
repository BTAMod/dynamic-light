package farn.dynamicLight.util;

import net.minecraft.core.block.material.Material;
import net.minecraft.core.entity.Entity;
import net.minecraft.core.world.World;
import net.minecraft.core.block.Block;


public class DynamicLightSource
{
	boolean isLit = false;
	float posX;
	float posY;
	float posZ;
	int iX;
	int iY;
	int iZ;
	private int brightness = 15;
	private int range = brightness * 2 + 1;
	float[] cache = new float[range * range * range];
	private Entity target;
	public int currentItemID = 0;
	private boolean worksUnderwater = true;
	public int deathAge = -1;
	public boolean canArmorLit = false;
    public DynamicLightSource(Entity entity)
    {
		target = entity;
    }

    public boolean isTorchActive()
    {
        return (isLit && target.isAlive() && !IsPutOutByWater());
    }

    public void setTorchState(World world, boolean flag)
    {
        if(isLit != flag)
        {
            isLit = flag;
        }
		markBlocksDirty(world);
    }

    public void setTorchPos(World world, float x, float y, float z)
    {
            posX = x;
            posY = y;
            posZ = z;
            iX = (int)posX;
            iY = (int)posY;
            iZ = (int)posZ;
			markBlocksDirty(world);
    }

	public float getTorchLight(World world, int x, int y, int z)
	{
		if (isLit && !IsPutOutByWater())
		{		
			int diffX = x - iX + brightness;
			int diffY = y - iY + brightness;
			int diffZ = z - iZ + brightness;
			
			if ((diffX >= 0) && (diffX < range) && (diffY >= 0) && (diffY < range) && (diffZ >= 0) && (diffZ < range))
			{
				return cache[(diffX * range * range + diffY * range + diffZ)];
			}
		}
		return 0.0F;
	}
	
	private boolean IsPutOutByWater()
	{
		return (!worksUnderwater && target.isUnderLiquid(Material.water));
	}

    private void markBlocksDirty(World world)
    {
        float XDiff = posX - iX;
        float YDiff = posY - iY;
        float ZDiff = posZ - iZ;
        int index = 0;
        for(int i = -brightness; i <= brightness; i++)
        {
            for(int j = -brightness; j <= brightness; j++)
            {
                for(int k = -brightness; k <= brightness; k++)
                {
					int blockX = i + iX;
					int blockY = j + iY;
                    int blockZ = k + iZ;
                    int blockID = world.getBlockId(blockX, blockY, blockZ);
                    if(blockID != 0 && Block.blocksList[blockID].renderAsNormalBlock())
                    {
                        cache[index++] = 0.0F;
                        continue;
                    }
                    float distance = (float)(Math.abs((i + 0.5D) - XDiff) + Math.abs((j + 0.5D) - YDiff) + Math.abs((k + 0.5D) - ZDiff));
                    if(distance <= (float) brightness)
                    {
                        if((float) brightness - distance > (float)world.getBlockLightValue(blockX, blockY, blockZ))
                        {
                            world.markBlockNeedsUpdate(blockX, blockY, blockZ);
                        }
                        cache[index++] = (float) brightness - distance;
                    }
					else
                    {
                        cache[index++] = 0.0F;
                    }
                }
            }
        }
    }
	
	public void SetTorchBrightness(int i)
	{
		brightness = i;
	}
	
	public int GetTorchBrightness()
	{
		return brightness;
	}
	
	public void SetTorchRange(int i)
	{
		range = i;
	}
	
	public Entity GetTorchEntity()
	{
		return target;
	}
	
	public void SetWorksUnderwater(boolean works)
	{
		worksUnderwater = works;
	}
	
	public void setDeathAge(int age)
	{
		deathAge = age;
	}
	
	public void doAgeTick()
	{
		deathAge--;
	}
	
	public boolean hasDeathAge()
	{
		return (deathAge != -1);
	}
	
	public boolean hasReachedDeathAge()
	{
		return (deathAge == 0);
	}
}
