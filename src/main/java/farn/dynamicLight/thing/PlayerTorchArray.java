package farn.dynamicLight.thing;

import net.minecraft.core.entity.Entity;
import net.minecraft.core.entity.player.EntityPlayer;
import net.minecraft.core.world.World;
import java.util.*;

public class PlayerTorchArray
{
	public static java.util.List torchArray = new ArrayList();
	public static java.util.List torchEntityArray = new ArrayList();
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
		
		for(int x = 0; x < torchArray.size(); x++)
        {
			PlayerTorch torchLoopClass = (PlayerTorch)torchArray.get(x);
			lightBuffer = torchLoopClass.getTorchLight(world, i, j, k);
			if(lightBuffer > torchLight)
			{
				torchLight = lightBuffer;
			}
		}
		
		return torchLight;
	}

	
	public static void AddTorchToArray(PlayerTorch playertorch)
    {		
        torchArray.add(playertorch);
		torchEntityArray.add(playertorch.GetTorchEntity());
    }
	
	public static void RemoveTorchFromArray(World world, PlayerTorch playertorch)
	{
		playertorch.setTorchState(world, false);
		torchArray.remove(playertorch);
		torchEntityArray.remove(playertorch.GetTorchEntity());
	}
	
	public static PlayerTorch GetTorchForEntity(Entity ent)
	{
		if(torchEntityArray.contains(ent))
		{
			for(int x = 0; x < torchArray.size(); x++)
			{
				PlayerTorch torchLoopClass = (PlayerTorch)torchArray.get(x);
				if (torchLoopClass.GetTorchEntity() == ent)
				{
					return torchLoopClass;
				}
			}
		}
		
		return null;
	}
}
