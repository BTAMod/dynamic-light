package farn.dynamicLight;

import net.fabricmc.api.ModInitializer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.render.RenderGlobal;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import farn.dynamicLight.thing.*;
import net.minecraft.core.util.helper.MathHelper;
import net.minecraft.core.entity.player.EntityPlayer;
import net.minecraft.core.entity.Entity;
import net.minecraft.core.item.ItemStack;
import net.minecraft.core.world.World;
import net.minecraft.core.entity.EntityItem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.lwjgl.input.Keyboard;

import java.util.*;


public class Main implements ModInitializer {
    public static final String MOD_ID = "dynamic_light";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    private static int nextFrameTime;
    private static long prevFrameTimeForAvg;
    private static long[] tFrameTimes = new long[60];

    public static final Main instance = new Main();

    @Override
    public void onInitialize() {
        LOGGER.info("Dynamic Light Mod initialized.");
        time = System.currentTimeMillis();
    }

    private final String togglekey = "L";
    private final int itogglekey = Keyboard.getKeyIndex(togglekey);
    private int lastEntityListHash = 0;
    private List entityList;
    long time;
    public boolean OnTickInGame(net.minecraft.client.Minecraft mc)
    {
        if (mc.thePlayer == null || mc.theWorld == null) return false;

        boolean newsecond = false;
        if(System.currentTimeMillis() >= time + 1000L)
        {
            newsecond = true;
            time = System.currentTimeMillis();
        }

        if(lastEntityListHash != mc.theWorld.loadedEntityList.hashCode())
        {
            entityList = mc.theWorld.loadedEntityList;
            lastEntityListHash = mc.theWorld.loadedEntityList.hashCode();

            UpdateTorchEntities(mc.theWorld);
        }

        TorchEntitiesDoTick(mc, newsecond);

        if (newsecond)
        {
            UpdateBurningEntities(mc);
        }

        if (Keyboard.getEventKeyState()
                && Keyboard.getEventKey() == itogglekey
                && newsecond)
        {
            PlayerTorchArray.ToggleDynamicLights();
        }

        return true;
    }

    private int targetBlockID = 0;
    private int targetBlockX;
    private int targetBlockY;
    private int targetBlockZ;

    private void TorchEntitiesDoTick(net.minecraft.client.Minecraft mc, boolean newsecond)
    {
        for(int j = 0; j < PlayerTorchArray.torchArray.size(); j++) // loop the PlayerTorch List
        {
            PlayerTorch torchLoopClass = (PlayerTorch)PlayerTorchArray.torchArray.get(j);
            Entity torchent = torchLoopClass.GetTorchEntity();

            if(torchent instanceof EntityPlayer)
            {
                EntityPlayer entPlayer = (EntityPlayer)torchent;
                TickPlayerEntity(mc, newsecond, torchLoopClass, entPlayer);
            }

            else if(torchent instanceof EntityItem)
            {
                TickItemEntity(mc, newsecond, torchLoopClass, torchent);
            }

            else
            {
                torchLoopClass.setTorchPos(mc.theWorld, (float)torchent.x, (float)torchent.y, (float)torchent.z);
            }
        }
    }

    private void TickPlayerEntity(net.minecraft.client.Minecraft mc, boolean newsecond, PlayerTorch torchLoopClass, EntityPlayer entPlayer)
    {
        int oldbrightness = torchLoopClass.isTorchActive() ? torchLoopClass.GetTorchBrightness() : 0;

        if (newsecond)
        {
            if (GetPlayerArmorLightValue(torchLoopClass, entPlayer, oldbrightness) == 0 && !entPlayer.isOnFire()) // case no (more) shiny armor
            {
                torchLoopClass.IsArmorTorch = false;
            }
        }

        int itembrightness = 0;
        if (entPlayer.inventory.mainInventory[entPlayer.inventory.currentItem] != null)
        {
            int ID = entPlayer.inventory.mainInventory[entPlayer.inventory.currentItem].itemID;
            if (ID != torchLoopClass.currentItemID
                    || (newsecond && !torchLoopClass.IsArmorTorch))
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

    private void TickItemEntity(net.minecraft.client.Minecraft mc, boolean newsecond, PlayerTorch torchLoopClass, Entity torchent)
    {
        torchLoopClass.setTorchPos(mc.theWorld, (float)torchent.x, (float)torchent.y, (float)torchent.z);

        if (torchLoopClass.hasDeathAge())
        {
            if (torchLoopClass.hasReachedDeathAge())
            {
                torchent.remove();
                PlayerTorchArray.RemoveTorchFromArray(mc.theWorld, torchLoopClass);
            }
            else if (newsecond)
            {
                torchLoopClass.doAgeTick();
            }
        }
    }

    private void UpdateBurningEntities(net.minecraft.client.Minecraft mc)
    {
        for(int k = 0; k < entityList.size(); k++) // we loop ALL entities
        {
            Entity tempent = (Entity)entityList.get(k);

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

        for(int k = 0; k < entityList.size(); k++)
        {
            Entity tempent = (Entity)entityList.get(k);

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

    private boolean hasTargetBlockChanged(EntityPlayer p)
    {
        double x = -MathHelper.sin((p.yRot / 180F) * 3.141593F) * MathHelper.cos((p.xRot / 180F) * 3.141593F);
        double z = MathHelper.cos((p.yRot / 180F) * 3.141593F) * MathHelper.cos((p.xRot / 180F) * 3.141593F);
        int targetX = MathHelper.floor_double(p.x + x);
        int targetY = MathHelper.floor_double(p.y) - 1;
        int targetZ = MathHelper.floor_double(p.z + z);
        int blockID = p.world.getBlockId(targetX, targetY, targetZ);

        if (blockID != targetBlockID)
        {
            targetBlockID = blockID;
            return true;
        }

        return false;
    }

    public static void onABBBUninit() {
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

}
