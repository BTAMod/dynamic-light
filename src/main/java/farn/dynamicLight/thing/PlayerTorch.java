package farn.dynamicLight.thing;

import net.minecraft.core.entity.Entity;
import net.minecraft.core.world.World;
import net.minecraft.core.block.Block;
import farn.dynamicLight.Main;


public class PlayerTorch
{
	boolean bIsTorchActive = false;
	float posX;
	float posY;
	float posZ;
	int iX;
	int iY;
	int iZ;
	private int torchBrightness = 15;
	private int range = torchBrightness * 2 + 1;
	float[] cache = new float[range * range * range];
	private Entity torchentity;
	public int currentItemID = 0;
	private boolean worksUnderwater = true;
	public int deathAge = -1;

	private boolean IsCustomTorch = false;

	public boolean IsArmorTorch = false;
    public PlayerTorch(Entity entity)
    {
		torchentity = entity;
    }

    public boolean isTorchActive()
    {
        return (bIsTorchActive && torchentity.isAlive() && !IsPutOutByWater());
    }

    public void setTorchState(World world, boolean flag)
    {
        if(bIsTorchActive != flag)
        {
            bIsTorchActive = flag;
        }
		markBlocksDirty(world);
    }

    public void setTorchPos(World world, float x, float y, float z)
    {
		long avgTime = Main.getAvgFrameTime();

		int updateRate = 1;
		if (avgTime > 33333333L)
		{
			updateRate = 3;
		}
		else if (avgTime > 16666666L)
		{
			updateRate = 2;
		}
		
		if ((world.getWorldTime() % updateRate == 0L)
		&& ((posX != x) || (posY != y) || (posZ != z))
		&& !IsPutOutByWater())
		{
            posX = x;
            posY = y;
            posZ = z;
            iX = (int)posX;
            iY = (int)posY;
            iZ = (int)posZ;
			markBlocksDirty(world);
        }
    }

	public float getTorchLight(World world, int x, int y, int z)
	{
		if (bIsTorchActive && !IsPutOutByWater())
		{		
			int diffX = x - iX + torchBrightness;
			int diffY = y - iY + torchBrightness;
			int diffZ = z - iZ + torchBrightness;
			
			if ((diffX >= 0) && (diffX < range) && (diffY >= 0) && (diffY < range) && (diffZ >= 0) && (diffZ < range))
			{
				return cache[(diffX * range * range + diffY * range + diffZ)];
			}
		}
		return 0.0F;
	}
	
	private boolean IsPutOutByWater()
	{
		return (!worksUnderwater && torchentity.isInWater());
	}

    private void markBlocksDirty(World world)
    {
        float XDiff = posX - iX;
        float YDiff = posY - iY;
        float ZDiff = posZ - iZ;
        int index = 0;
        for(int i = -torchBrightness; i <= torchBrightness; i++)
        {
            int blockX = i + iX;
            for(int j = -torchBrightness; j <= torchBrightness; j++)
            {
                int blockY = j + iY;
                for(int k = -torchBrightness; k <= torchBrightness; k++)
                {
                    int blockZ = k + iZ;
                    int blockID = world.getBlockId(blockX, blockY, blockZ);
                    if(blockID != 0 && Block.blocksList[blockID].renderAsNormalBlock())
                    {
                        cache[index++] = 0.0F;
                        continue;
                    }
                    float distance = (float)(Math.abs((i + 0.5D) - XDiff) + Math.abs((j + 0.5D) - YDiff) + Math.abs((k + 0.5D) - ZDiff));
                    if(distance <= (float)torchBrightness)
                    {
                        if((float)torchBrightness - distance > (float)world.getBlockLightValue(blockX, blockY, blockZ))
                        {
                            world.markBlockNeedsUpdate(blockX, blockY, blockZ);
                        }
                        cache[index++] = (float)torchBrightness - distance;
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
		torchBrightness = i;
	}
	
	public int GetTorchBrightness()
	{
		return torchBrightness;
	}
	
	public void SetTorchRange(int i)
	{
		range = i;
	}
	
	public Entity GetTorchEntity()
	{
		return torchentity;
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
	
	public boolean IsTorchCustom()
	{
		return IsCustomTorch;
	}
}
