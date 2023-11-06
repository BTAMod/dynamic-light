package farn.dynamicLight.util;

import farn.dynamicLight.config.DynamicLightConfig;
import net.minecraft.core.entity.Entity;
import net.minecraft.core.world.World;
import java.util.*;

public class DynamicLightDispatcher
{
	public static java.util.List itemArray = new ArrayList();
	public static java.util.List entityArray = new ArrayList();
	public static int [][] lightdata = new int[64][5];
	public static int GetItemBrightnessValue(int ID)
	{
		for(int i = 0; i < lightdata.length; ++i) {
			if(lightdata[i][0] == ID) {
				return lightdata[i][1];
			}
		}
		
		return 0;
	}
	
	public static int GetItemLightRangeValue(int ID)
	{
		for(int i = 0; i < lightdata.length; ++i) {
			if(lightdata[i][0] == ID) {
				return lightdata[i][2];
			}
		}
		
		return 0;
	}
	
	public static int GetItemDeathAgeValue(int ID)
	{

		for(int i = 0; i < lightdata.length; ++i) {
			if(lightdata[i][0] == ID) {
				return lightdata[i][3];
			}
		}
		
		return -1;
	}
	
	public static boolean GetItemWorksUnderWaterValue(int ID)
	{
		for(int i = 0; i < lightdata.length; ++i) {
			if(lightdata[i][0] == ID) {
				return lightdata[i][4] != 0;
			}
		}
		
		return false;
	}
	
	public static float getLightBrightness(World world, int i, int j, int k)
	{	
		float torchLight = 0.0F;
		
		float lightBuffer;
		
		for(int x = 0; x < itemArray.size(); x++)
        {
			DynamicLightSource torchLoopClass = (DynamicLightSource) itemArray.get(x);
			lightBuffer = torchLoopClass.getTorchLight(world, i, j, k);
			if(lightBuffer > torchLight)
			{
				torchLight = lightBuffer;
			}
		}
		
		return torchLight;
	}

	
	public static void AddEntity(DynamicLightSource playertorch)
    {
		if(!DynamicLightConfig.resetLight) {
			itemArray.add(playertorch);
			entityArray.add(playertorch.GetTorchEntity());
		}
    }
	
	public static void RemoveTorch(World world, DynamicLightSource playertorch)
	{
		if(!DynamicLightConfig.resetLight) {
			playertorch.setTorchState(world, false);
			itemArray.remove(playertorch);
			entityArray.remove(playertorch.GetTorchEntity());
		}
	}
	
	public static DynamicLightSource GetTorchForEntity(Entity ent)
	{
		if(entityArray.contains(ent))
		{
			for(int x = 0; x < itemArray.size(); x++)
			{
				DynamicLightSource torchLoopClass = (DynamicLightSource) itemArray.get(x);
				if (torchLoopClass.GetTorchEntity() == ent)
				{
					return torchLoopClass;
				}
			}
		}
		
		return null;
	}

	public static void clearCacheAndResetPool() {
		DynamicLightCache.cache.clear();
		DynamicLightBlockCoord.resetPool();
	}
}
