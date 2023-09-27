package farn.dynamicLight;

import net.fabricmc.api.ModInitializer;
import farn.dynamicLight.thing.*;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.Minecraft;
import net.minecraft.core.block.Block;
import net.minecraft.core.entity.player.EntityPlayer;
import net.minecraft.core.entity.Entity;
import net.minecraft.core.item.Item;
import net.minecraft.core.item.ItemStack;
import net.minecraft.core.world.World;
import net.minecraft.core.entity.EntityItem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.*;


public class Main implements ModInitializer {
    public static final String MOD_ID = "dynamic_light";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
    private static int nextFrameTime;
    private static long prevFrameTimeForAvg;
    private static long[] tFrameTimes = new long[60];
    public static final Main instance = new Main();
    long time;
    static boolean areSettingsLoaded;
    static File settingsFile;

    @Override
    public void onInitialize() {
        LOGGER.info("Dynamic Light Mod initialized.");
        time = System.currentTimeMillis();
        if (!areSettingsLoaded)
            initializeSettingsFile();
    }
    public void OnTickInGame(net.minecraft.client.Minecraft mc)
    {
        


        if (System.currentTimeMillis() >= time + 250L)
        {
            UpdateTorchEntities(mc.theWorld);
            TorchEntitiesDoTick(mc);
            UpdateBurningEntities(mc);
            time = System.currentTimeMillis();
        }

    }

    private void TorchEntitiesDoTick(net.minecraft.client.Minecraft mc)
    {
        for(int j = 0; j < PlayerTorchArray.torchArray.size(); j++) // loop the PlayerTorch List
        {
            PlayerTorch torchLoopClass = (PlayerTorch)PlayerTorchArray.torchArray.get(j);
            Entity torchent = torchLoopClass.GetTorchEntity();

            if(torchent instanceof EntityPlayer)
            {
                EntityPlayer entPlayer = (EntityPlayer)torchent;
                TickPlayerEntity(mc, torchLoopClass, entPlayer);
            }

            else if(torchent instanceof EntityItem)
            {
                TickItemEntity(mc, torchLoopClass, torchent);
            }

            else
            {
                torchLoopClass.setTorchPos(mc.theWorld, (float)torchent.x, (float)torchent.y, (float)torchent.z);
            }
        }
    }

    private void TickPlayerEntity(net.minecraft.client.Minecraft mc, PlayerTorch torchLoopClass, EntityPlayer entPlayer)
    {
        int oldbrightness = torchLoopClass.isTorchActive() ? torchLoopClass.GetTorchBrightness() : 0;

            if (GetPlayerArmorLightValue(torchLoopClass, entPlayer, oldbrightness) == 0 && !entPlayer.isOnFire()) // case no (more) shiny armor
            {
                torchLoopClass.IsArmorTorch = false;
            }

        int itembrightness = 0;
        if (entPlayer.inventory.mainInventory[entPlayer.inventory.currentItem] != null)
        {
            int ID = entPlayer.inventory.mainInventory[entPlayer.inventory.currentItem].itemID;
            if (ID != torchLoopClass.currentItemID
                    || (!torchLoopClass.IsArmorTorch))
            {
                torchLoopClass.currentItemID = ID;

                // this is a debug function for modder use.
                //mc.ingameGUI.addChatMessage("Player Item changed, item now: " + ID);

                itembrightness = PlayerTorchArray.GetItemBrightnessValue(ID);
                if (itembrightness >= oldbrightness)
                {
                    if (torchLoopClass.IsArmorTorch)
                        torchLoopClass.IsArmorTorch = false;

                    torchLoopClass.SetTorchBrightness(itembrightness);
                    torchLoopClass.SetTorchRange(PlayerTorchArray.GetItemLightRangeValue(ID));
                    torchLoopClass.SetWorksUnderwater(PlayerTorchArray.GetItemWorksUnderWaterValue(ID));
                    torchLoopClass.setTorchState(entPlayer.world, true);
                }
                else if(!torchLoopClass.IsArmorTorch && GetPlayerArmorLightValue(torchLoopClass, entPlayer, oldbrightness) == 0)
                {
                    torchLoopClass.setTorchState(entPlayer.world, false);
                }
            }
        }
        else
        {
            torchLoopClass.currentItemID = 0;
            if (!torchLoopClass.IsArmorTorch && GetPlayerArmorLightValue(torchLoopClass, entPlayer, oldbrightness) == 0)
            {
                torchLoopClass.setTorchState(entPlayer.world, false);
            }
        }

        if (torchLoopClass.isTorchActive())
        {
            torchLoopClass.setTorchPos(entPlayer.world, (float)entPlayer.x, (float)entPlayer.y, (float)entPlayer.z);
        }
    }

    private int GetPlayerArmorLightValue(PlayerTorch torchLoopClass, EntityPlayer entPlayer, int oldbrightness)
    {
        int armorbrightness = 0;
        int armorID;


        if(entPlayer.isOnFire())
        {
            torchLoopClass.IsArmorTorch = true;
            torchLoopClass.SetTorchBrightness(15);
            torchLoopClass.SetTorchRange(31);
            torchLoopClass.setTorchState(entPlayer.world, true);
        }
        else
        {
            for(int l = 0; l < 4; l++)
            {
                ItemStack armorItem = entPlayer.inventory.armorItemInSlot(l);
                if(armorItem != null)
                {
                    armorID = armorItem.itemID;
                    armorbrightness = PlayerTorchArray.GetItemBrightnessValue(armorID);

                    if (armorbrightness > oldbrightness)
                    {
                        oldbrightness = armorbrightness;
                        torchLoopClass.IsArmorTorch = true;
                        torchLoopClass.SetTorchBrightness(armorbrightness);
                        torchLoopClass.SetTorchRange(PlayerTorchArray.GetItemLightRangeValue(armorID));
                        torchLoopClass.SetWorksUnderwater(PlayerTorchArray.GetItemWorksUnderWaterValue(armorID));
                        torchLoopClass.setTorchState(entPlayer.world, true);
                    }
                }
            }
        }

        return armorbrightness;
    }

    private void TickItemEntity(net.minecraft.client.Minecraft mc, PlayerTorch torchLoopClass, Entity torchent)
    {
        torchLoopClass.setTorchPos(mc.theWorld, (float)torchent.x, (float)torchent.y, (float)torchent.z);

        if (torchLoopClass.hasDeathAge())
        {
            if (torchLoopClass.hasReachedDeathAge())
            {
                torchent.remove();
                PlayerTorchArray.RemoveTorchFromArray(mc.theWorld, torchLoopClass);
            }
            else
            {
                torchLoopClass.doAgeTick();
            }
        }
    }

    private void UpdateBurningEntities(net.minecraft.client.Minecraft mc)
    {
        for(int k = 0; k < mc.theWorld.loadedEntityList.size(); k++) // we loop ALL entities
        {
            Entity tempent = (Entity)mc.theWorld.loadedEntityList.get(k);

            if (tempent.isOnFire())
            {
                //mc.ingameGUI.addChatMessage("Found burning entity: " + tempent);

                PlayerTorch torchent = PlayerTorchArray.GetTorchForEntity(tempent);

                if (torchent == null) // if it is on fire and not yet a playertorch...
                {
                    //mc.ingameGUI.addChatMessage("Burning ent has no Torch yet, adding");

                    PlayerTorch newtorch = new PlayerTorch(tempent); // add one with torch data!
                    PlayerTorchArray.AddTorchToArray(newtorch);
                    newtorch.SetTorchBrightness(15);
                    newtorch.SetTorchRange(31);
                    newtorch.setTorchState(mc.theWorld, true);
                }
            }
        }
    }

    private void UpdateTorchEntities(World worldObj)
    {
        List tempList = new ArrayList();

        for(int k = 0; k < worldObj.loadedEntityList.size(); k++)
        {
            Entity tempent = (Entity)worldObj.loadedEntityList.get(k);

            if(tempent instanceof EntityPlayer)
            {
                tempList.add(tempent);
            }

            else if(tempent instanceof EntityItem)
            {
                EntityItem helpitem = (EntityItem)tempent;
                int brightness = PlayerTorchArray.GetItemBrightnessValue(helpitem.item.itemID);
                if (brightness > 0)
                {
                    tempList.add(tempent);
                }
            }
        }
        // tempList is now a fresh list of all Entities that can have a PlayerTorch

        for(int j = 0; j < PlayerTorchArray.torchArray.size(); j++) // loop the old PlayerTorch List
        {
            PlayerTorch torchLoopClass = (PlayerTorch)PlayerTorchArray.torchArray.get(j);
            Entity torchent = torchLoopClass.GetTorchEntity();

            if (tempList.contains(torchent)) // check if the old entities are still in the world
            {
                tempList.remove(torchent); // if so remove them from the fresh list
            }
            else if ((!torchLoopClass.IsTorchCustom() && !torchent.isOnFire()) // exclude foreign modded torches and burning stuff
                    || torchent != null && !torchent.isAlive()) // but do delete dead stuff
            {
                PlayerTorchArray.RemoveTorchFromArray(worldObj, torchLoopClass); // else remove them from the PlayerTorch list
            }
        }

        for(int l = 0; l < tempList.size(); l++) // now to loop the remainder of the fresh list, the NEW lights
        {
            Entity newent = (Entity)tempList.get(l);

            PlayerTorch newtorch = new PlayerTorch(newent);
            PlayerTorchArray.AddTorchToArray(newtorch);

            if(newent instanceof EntityItem)
            {
                EntityItem institem = (EntityItem)newent;
                newtorch.SetTorchBrightness(PlayerTorchArray.GetItemBrightnessValue(institem.item.itemID));
                newtorch.SetTorchRange(PlayerTorchArray.GetItemLightRangeValue(institem.item.itemID));
                newtorch.setDeathAge(PlayerTorchArray.GetItemDeathAgeValue(institem.item.itemID));
                newtorch.SetWorksUnderwater(PlayerTorchArray.GetItemWorksUnderWaterValue(institem.item.itemID));
                newtorch.setTorchState(worldObj, true);
            }
        }
    }
    public static void onPoolUnInitialize() {
        prevFrameTimeForAvg = System.nanoTime();
        tFrameTimes[nextFrameTime] = prevFrameTimeForAvg;
        nextFrameTime = ((nextFrameTime + 1) % 60);
    }

    public static long getAvgFrameTime()
    {
        if (tFrameTimes[nextFrameTime] != 0L)
        {
            return (prevFrameTimeForAvg - tFrameTimes[nextFrameTime]) / 60L;
        }
        return 23333333L;
    }

    public static void initializeSettingsFile()
    {
        settingsFile = new File(FabricLoader.getInstance().getConfigDir() + "dynamicLight_itemWhiteList.setting");

        try
        {
            if(!settingsFile.exists()) {
                BufferedWriter configWriter = new BufferedWriter(new FileWriter(settingsFile));
                configWriter.write("#Format (note: ItemLightTimeLimit and WorksUnderwater are optional)" + newLine());
                configWriter.write("#ItemID:MaximumBrightness:LightRange:ItemLightTimeLimit:WorksUnderwater(0 is false)" + newLine());
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

                PlayerTorchArray.lightdata[i][0] = Integer.parseInt(curLine[0]); // Item ID
                PlayerTorchArray.lightdata[i][1] = Integer.parseInt(curLine[1]); // Max Brightness
                PlayerTorchArray.lightdata[i][2] = Integer.parseInt(curLine[2]); // Range
                PlayerTorchArray.lightdata[i][3] = -1;
                PlayerTorchArray.lightdata[i][4] = 1;

                if (curLine.length > 3)
                    PlayerTorchArray.lightdata[i][3] = Integer.parseInt(curLine[3]); // Death Age

                if (curLine.length > 4)
                    PlayerTorchArray.lightdata[i][4] = Integer.parseInt(curLine[4]); // Work UnderWater


                ++i;
            }
            PlayerTorchArray.lightdata[i][0] = 0; // Just to make sure we have an ending marked

            in.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        areSettingsLoaded = true;
    }

    public static String newLine() {
        return System.getProperty("line.separator");
    }

    public static final EntityPlayer getPlayer() {
        return Minecraft.getMinecraft(Minecraft.class).thePlayer;
    }

}
