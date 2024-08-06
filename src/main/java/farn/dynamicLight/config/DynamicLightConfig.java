package farn.dynamicLight.config;

import farn.dynamicLight.util.DynamicLightDispatcher;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.core.block.Block;
import net.minecraft.core.item.Item;

import java.io.*;

public class DynamicLightConfig {

    public static File settingsFile;
    public static boolean resetLight = false;

    private static boolean init = false;

    public static void initializeSettingsFile(boolean doResetLight)
    {
        if(!init) {
            settingsFile = new File(FabricLoader.getInstance().getConfigDir() + "/dynamicLight_items.config");
            init = true;
        }

        if(!settingsFile.exists()) {
            writeDefaultSettingFile();
        }

        readSettingFile(doResetLight);
    }
    private static void readSettingFile(boolean doResetLight) {
        try {
            if(doResetLight) {
                DynamicLightDispatcher.clearCacheAndResetPool();
                DynamicLightDispatcher.itemArray.clear();
                DynamicLightDispatcher.entityArray.clear();
                resetLight = true;
            }

            BufferedReader in = new BufferedReader(new FileReader(settingsFile));
            String sCurrentLine;
            int[][] newLightData = new int[64][5];
            int i = 0;

            while ((sCurrentLine = in.readLine()) != null && i < 64) {
                if (sCurrentLine.startsWith("#")) continue;

                String[] curLine = sCurrentLine.split(":");

                if (curLine.length > 2) {
                    newLightData[i][0] = Integer.parseInt(curLine[0]); // Item ID
                    newLightData[i][1] = Integer.parseInt(curLine[1]); // Max Brightness
                    newLightData[i][2] = Integer.parseInt(curLine[2]); // Range
                }

                if (curLine.length > 3)
                    newLightData[i][3] = Integer.parseInt(curLine[3]); // Death Age

                if (curLine.length > 4)
                    newLightData[i][4] = Integer.parseInt(curLine[4]); // Work UnderWater


                ++i;
            }
            in.close();
            DynamicLightDispatcher.lightdata = newLightData;
            resetLight = false;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void writeDefaultSettingFile() {
        try {
            BufferedWriter configWriter = new BufferedWriter(new FileWriter(settingsFile));
            configWriter.write("#Format (note: ItemLightTimeLimit and WorksUnderwater are optional)" + newLine());
            configWriter.write("#ItemID:MaximumBrightness:LightRange:ItemLightTimeLimit:WorksUnderwater(0 is false)" + newLine());
            configWriter.write("#" + newLine());
            configWriter.write("#" + newLine());
            configWriter.write("#Torch" + newLine());
            configWriter.write(Block.torchCoal.id + ":15:31:-1:0" + newLine());
            configWriter.write("#Glowstone dust" + newLine());
            configWriter.write(Item.dustGlowstone.id + ":10:21" + newLine());
            configWriter.write("#Glowstone" + newLine());
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
            configWriter.write("#Nether coal ore" + newLine());
            configWriter.write(Block.oreNethercoalNetherrack.id + ":12:25" + newLine());
            configWriter.write("#Igneous netherRack" + newLine());
            configWriter.write(Block.netherrackIgneous.id + ":12:25" + newLine());
            configWriter.write("#Green lantern jar" + newLine());
            configWriter.write(Item.lanternFireflyGreen.id + ":11:23" + newLine());
            configWriter.write("#Blue lantern jar" + newLine());
            configWriter.write(Item.lanternFireflyBlue.id + ":11:23" + newLine());
            configWriter.write("#Orange lantern jar" + newLine());
            configWriter.write(Item.lanternFireflyOrange.id + ":11:23" + newLine());
            configWriter.write("#Red lantern jar" + newLine());
            configWriter.write(Item.lanternFireflyRed.id + ":11:23" + newLine());
            configWriter.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static String newLine() {
        return System.getProperty("line.separator");
    }

}
