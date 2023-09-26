package farn.dynamicLight.thing;

import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.Minecraft;
import net.minecraft.core.block.Block;
import net.minecraft.core.entity.Entity;
import net.minecraft.core.item.Item;
import net.minecraft.core.world.World;
import java.util.*;
import java.io.*;

public class PlayerTorchArray
{
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
	
	public static void ToggleDynamicLights()
	{
		disableAllLights = !disableAllLights;
	}
	
	public static boolean AreTorchesActive()
	{
		for(int x = 0; x < torchArray.size(); x++)
        {
			PlayerTorch torchLoopClass = (PlayerTorch)torchArray.get(x);
			if (torchLoopClass.isTorchActive())
				return true;
		}
		return false;
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
		settingsFile = new File(FabricLoader.getInstance().getConfigDir() + "dynamiclights.settings");
		
		try
		{
			if(!settingsFile.exists()) {
				BufferedWriter configWriter = new BufferedWriter(new FileWriter(settingsFile));
				configWriter.write("#Torch" + newLine());
				configWriter.write(Block.torchCoal.id + ":15:31:-1:0" + newLine());
				configWriter.write("#Glowstone" + newLine());
				configWriter.write(Block.glowstone.id + ":12:25" + newLine());
				configWriter.write("#Glowstone dust" + newLine());
				configWriter.write(Item.dustGlowstone.id + ":10:21" + newLine());
				configWriter.write("#Jack o Lantern" + newLine());
				configWriter.write(Block.pumpkinCarvedActive.id + ":15:31" + newLine());
				configWriter.write("#Bucket of Lava" + newLine());
				configWriter.write(Item.bucketLava.id + ":15:31" + newLine());
				configWriter.write("#Redstone Torch" + newLine());
				configWriter.write(Block.torchRedstoneActive.id + ":10:21" + newLine());
				configWriter.close();
			}
				BufferedReader in = new BufferedReader(new FileReader(settingsFile));
				String sCurrentLine;
				int i = 0;
				
				while ((sCurrentLine = in.readLine()) != null)
				{
					if(sCurrentLine.startsWith("#")) continue;
					
					String[] curLine = sCurrentLine.split(":");

					lightdata[i][0] = Integer.parseInt(curLine[0]); // Item ID
					lightdata[i][1] = Integer.parseInt(curLine[1]); // Max Brightness
					lightdata[i][2] = Integer.parseInt(curLine[2]); // Range
					
					if (curLine.length > 3)
						lightdata[i][3] = Integer.parseInt(curLine[3]); // Death Age
					else
						lightdata[i][3] = -1;
						
					if (curLine.length > 4)
						lightdata[i][4] = Integer.parseInt(curLine[4]); // Works Underwater
					else
						lightdata[i][4] = 1;
					
					i++;
					
					//System.out.println("read Torch item no " +i+ " of ID " + curLine[0] + ", brightness " + curLine[1] + ", range " + curLine[2]);
				}
				lightdata[i][0] = 0; // Just to make sure we have an ending marked
				
				in.close();
		}
		catch (Exception fuckdammit)
		{
		}
		
		areSettingsLoaded = true;
	}
	
    public static java.util.List torchArray = new ArrayList();
	public static java.util.List torchEntityArray = new ArrayList();
	public static int [][] lightdata = new int[32][5];
	
	static boolean areSettingsLoaded;
	static boolean disableAllLights;
	static File settingsFile;

	public static String newLine() {
		return System.getProperty("line.separator");
	}
}
