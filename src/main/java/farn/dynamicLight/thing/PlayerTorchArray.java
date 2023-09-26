package farn.dynamicLight.thing;

import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.core.block.Block;
import net.minecraft.core.entity.Entity;
import net.minecraft.core.item.Item;
import net.minecraft.core.world.World;
import java.util.*;
import java.io.*;

public class PlayerTorchArray
{
	public static java.util.List torchArray = new ArrayList();
	public static java.util.List torchEntityArray = new ArrayList();
	public static int [][] lightdata = new int[Item.itemsList.length][5];

	static boolean areSettingsLoaded;
	static boolean disableAllLights;
	static File settingsFile;
    public PlayerTorchArray()
    {
    }
	
	public static int GetItemBrightnessValue(int ID)
	{
		if (!areSettingsLoaded)
			initializeSettingsFile();
	
		int i = 0;
		while (lightdata[i][0] != 0)
		{
			if(lightdata[i][0] == ID)
			{
				return lightdata[i][1];
			}
			i++;
		}
		
		return 0;
	}
	
	public static int GetItemLightRangeValue(int ID)
	{
		int i = 0;
		while (lightdata[i][0] != 0)
		{
			if(lightdata[i][0] == ID)
			{
				return lightdata[i][2];
			}
			i++;
		}
		
		return 0;
	}
	
	public static int GetItemDeathAgeValue(int ID)
	{
		int i = 0;
		while (lightdata[i][0] != 0)
		{
			if(lightdata[i][0] == ID)
			{
				return lightdata[i][3];
			}
			i++;
		}
		
		return -1;
	}
	
	public static boolean GetItemWorksUnderWaterValue(int ID)
	{
		int i = 0;
		while (lightdata[i][0] != 0)
		{
			if(lightdata[i][0] == ID)
			{
				return (lightdata[i][4] != 0);
			}
			i++;
		}
		
		return false;
	}
	
	public static float getLightBrightness(World world, int i, int j, int k)
	{	
		float torchLight = 0.0F;
		if (disableAllLights) return torchLight;
		
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
	
	public static void initializeSettingsFile()
	{
		settingsFile = new File(FabricLoader.getInstance().getConfigDir() + "config/dynamicLight/dynamicLight_itemWhiteList.settings");
		
		try
		{
			if(!settingsFile.exists()) {
				BufferedWriter configWriter = new BufferedWriter(new FileWriter(settingsFile));
				configWriter.write("#Format (note: ItemLightTimeLimit and WorksUnderwater are optional)" + newLine());
				configWriter.write("#ItemID:MaximumBrightness:LightRange:ItemLightTimeLimit:WorksUnderwater" + newLine());
				configWriter.write("#" + newLine());
				configWriter.write("#" + newLine());
				configWriter.write("#Torch" + newLine());
				configWriter.write(Block.torchCoal.id + ":15:31:-1:0" + newLine());
				configWriter.write("#Glowstone dust" + newLine());
				configWriter.write(Item.dustGlowstone.id + ":10:21" + newLine());
				configWriter.write("#Glowstone dust" + newLine());
				configWriter.write(Block.glowstone.id + ":12:25" + newLine());
				configWriter.write("#Jack o Lantern" + newLine());
				configWriter.write(Block.pumpkinCarvedActive.id + ":15:31" + newLine());
				configWriter.write("#Bucket of Lava" + newLine());
				configWriter.write(Item.bucketLava.id + ":15:31" + newLine());
				configWriter.write("#Redstone Torch" + newLine());
				configWriter.write(Block.torchRedstoneActive.id + ":10:21" + newLine());
				configWriter.write("#Redstone Ore (Stone)" + newLine());
				configWriter.write(Block.oreRedstoneGlowingStone.id + ":10:21" + newLine());
				configWriter.write("#Redstone Ore (Basalt)" + newLine());
				configWriter.write(Block.oreRedstoneGlowingBasalt.id + ":10:21" + newLine());
				configWriter.write("#Redstone Ore (Granite)" + newLine());
				configWriter.write(Block.oreRedstoneGlowingGranite.id + ":10:21" + newLine());
				configWriter.write("#Redstone Ore (Lime Stone)" + newLine());
				configWriter.write(Block.oreRedstoneLimestone.id + ":10:21" + newLine());
				configWriter.write("#Nether coal" + newLine());
				configWriter.write(Item.nethercoal.id + ":10:21" + newLine());
				configWriter.write("#Green lantern jar" + newLine());
				configWriter.write(Item.lanternFireflyGreen.id + ":11:23" + newLine());
				configWriter.write("#Blue lantern jar" + newLine());
				configWriter.write(Item.lanternFireflyBlue.id + ":11:23" + newLine());
				configWriter.write("#Orange lantern jar" + newLine());
				configWriter.write(Item.lanternFireflyOrange.id + ":11:23" + newLine());
				configWriter.write("#Red lantern jar" + newLine());
				configWriter.write(Item.lanternFireflyRed.id + ":11:23" + newLine());
				configWriter.close();
			}
				BufferedReader in = new BufferedReader(new FileReader(settingsFile));
				String sCurrentLine;
				int i = 0;
				
				while((sCurrentLine = in.readLine()) != null)
				{
					if(sCurrentLine.startsWith("#")) continue;
					
					String[] curLine = sCurrentLine.split(":");

					lightdata[i][0] = Integer.parseInt(curLine[0]); // Item ID
					lightdata[i][1] = Integer.parseInt(curLine[1]); // Max Brightness
					lightdata[i][2] = Integer.parseInt(curLine[2]); // Range
					lightdata[i][3] = -1;
					lightdata[i][4] = 1;
					
					if (curLine.length > 3)
						lightdata[i][3] = Integer.parseInt(curLine[3]); // Death Age
						
					if (curLine.length > 4)
						lightdata[i][4] = Integer.parseInt(curLine[4]); // Work UnderWater

					
					++i;
				}
				lightdata[i][0] = 0; // Just to make sure we have an ending marked
				
				in.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		areSettingsLoaded = true;
	}
	public static String newLine() {
		return System.getProperty("line.separator");
	}
}
