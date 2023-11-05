package farn.dynamicLight.util;

import farn.dynamicLight.mixin.EntityCreeperAccessor;
import net.minecraft.client.Minecraft;
import net.minecraft.core.entity.EntityTNT;
import net.minecraft.core.entity.monster.EntityCreeper;
import net.minecraft.core.entity.player.EntityPlayer;
import net.minecraft.core.entity.Entity;
import net.minecraft.core.item.ItemStack;
import net.minecraft.core.world.World;
import net.minecraft.core.entity.EntityItem;

import java.util.*;

public class DynamicLightRenderer {

    public static final DynamicLightRenderer instance = new DynamicLightRenderer();
    long prevTime;

    private DynamicLightRenderer() {

    }

    public void OnTickInGame(Minecraft mc)
    {
        if (System.currentTimeMillis() >= prevTime + 50L)
        {
            updateEntity(mc.theWorld);
            tickEntity(mc);
            prevTime = System.currentTimeMillis();
        }
    }

    private void tickEntity(Minecraft mc)
    {
        for(int j = 0; j < DynamicLightDispatcher.itemArray.size(); j++) // loop the PlayerTorch List
        {
            DynamicLightSource torchLoopClass = (DynamicLightSource) DynamicLightDispatcher.itemArray.get(j);
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

    private void TickPlayerEntity(Minecraft mc, DynamicLightSource torchLoopClass, EntityPlayer entPlayer)
    {
        int oldbrightness = torchLoopClass.isTorchActive() ? torchLoopClass.GetTorchBrightness() : 0;

        if (GetPlayerArmorLightValue(torchLoopClass, entPlayer, oldbrightness) == 0 && !entPlayer.isOnFire()) // case no (more) shiny armor
        {
            torchLoopClass.canArmorLit = false;
        }

        int itembrightness = 0;
        if (entPlayer.inventory.mainInventory[entPlayer.inventory.currentItem] != null)
        {
            int ID = entPlayer.inventory.mainInventory[entPlayer.inventory.currentItem].itemID;
            if (ID != torchLoopClass.currentItemID
                    || (!torchLoopClass.canArmorLit))
            {
                torchLoopClass.currentItemID = ID;

                itembrightness = DynamicLightDispatcher.GetItemBrightnessValue(ID);
                if (itembrightness >= oldbrightness)
                {
                    if (torchLoopClass.canArmorLit)
                        torchLoopClass.canArmorLit = false;

                    torchLoopClass.SetTorchBrightness(itembrightness);
                    torchLoopClass.SetTorchRange(DynamicLightDispatcher.GetItemLightRangeValue(ID));
                    torchLoopClass.SetWorksUnderwater(DynamicLightDispatcher.GetItemWorksUnderWaterValue(ID));
                    torchLoopClass.setTorchState(entPlayer.world, true);
                }
                else if(!torchLoopClass.canArmorLit && GetPlayerArmorLightValue(torchLoopClass, entPlayer, oldbrightness) == 0)
                {
                    torchLoopClass.setTorchState(entPlayer.world, false);
                }
            }
        }
        else
        {
            torchLoopClass.currentItemID = 0;
            if (!torchLoopClass.canArmorLit && GetPlayerArmorLightValue(torchLoopClass, entPlayer, oldbrightness) == 0)
            {
                torchLoopClass.setTorchState(entPlayer.world, false);
            }
        }

        if (torchLoopClass.isTorchActive())
        {
            torchLoopClass.setTorchPos(entPlayer.world, (float)entPlayer.x, (float)entPlayer.y, (float)entPlayer.z);
        }
    }

    private int GetPlayerArmorLightValue(DynamicLightSource torchLoopClass, EntityPlayer entPlayer, int oldbrightness)
    {
        int armorbrightness = 0;
        int armorID;


        if(entPlayer.isOnFire())
        {
            torchLoopClass.canArmorLit = true;
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
                    armorbrightness = DynamicLightDispatcher.GetItemBrightnessValue(armorID);

                    if (armorbrightness > oldbrightness)
                    {
                        oldbrightness = armorbrightness;
                        torchLoopClass.canArmorLit = true;
                        torchLoopClass.SetTorchBrightness(armorbrightness);
                        torchLoopClass.SetTorchRange(DynamicLightDispatcher.GetItemLightRangeValue(armorID));
                        torchLoopClass.SetWorksUnderwater(DynamicLightDispatcher.GetItemWorksUnderWaterValue(armorID));
                        torchLoopClass.setTorchState(entPlayer.world, true);
                    }
                }
            }
        }

        return armorbrightness;
    }

    private void TickItemEntity(Minecraft mc, DynamicLightSource torchLoopClass, Entity torchent)
    {
        torchLoopClass.setTorchPos(mc.theWorld, (float)torchent.x, (float)torchent.y, (float)torchent.z);

        if (torchLoopClass.hasDeathAge())
        {
            if (torchLoopClass.hasReachedDeathAge())
            {
                torchent.remove();
                DynamicLightDispatcher.RemoveTorch(mc.theWorld, torchLoopClass);
            }
            else
            {
                torchLoopClass.doAgeTick();
            }
        }
    }

    private void updateEntity(World worldObj)
    {
        List tempList = new ArrayList();

        for(int k = 0; k < worldObj.loadedEntityList.size(); k++)
        {
            Entity tempent = (Entity)worldObj.loadedEntityList.get(k);

            if(tempent instanceof EntityPlayer || shouldEmitLight(tempent)) {
                tempList.add(tempent);
            }
            else if(tempent instanceof EntityItem)
            {
                EntityItem helpitem = (EntityItem)tempent;
                int brightness = DynamicLightDispatcher.GetItemBrightnessValue(helpitem.item.itemID);
                if (brightness > 0)
                {
                    tempList.add(tempent);
                }
            }

        }
        // tempList is now a fresh list of all Entities that can have a PlayerTorch

        for(int j = 0; j < DynamicLightDispatcher.itemArray.size(); j++) // loop the old PlayerTorch List
        {
            DynamicLightSource torchLoopClass = (DynamicLightSource) DynamicLightDispatcher.itemArray.get(j);
            Entity torchent = torchLoopClass.GetTorchEntity();

            if (tempList.contains(torchent)) // check if the old entities are still in the world
            {
                tempList.remove(torchent); // if so remove them from the fresh list
            }
            else if ((!shouldEmitLight(torchent)) // exclude foreign modded torches and burning stuff
                    || torchent != null && !torchent.isAlive()) // but do delete dead stuff
            {
                DynamicLightDispatcher.RemoveTorch(worldObj, torchLoopClass); // else remove them from the PlayerTorch list
            }
        }

        for(int l = 0; l < tempList.size(); l++) // now to loop the remainder of the fresh list, the NEW lights
        {
            Entity newent = (Entity)tempList.get(l);

            DynamicLightSource newtorch = new DynamicLightSource(newent);
            DynamicLightDispatcher.AddEntity(newtorch);

            if(newent instanceof EntityItem)
            {
                EntityItem institem = (EntityItem)newent;
                newtorch.SetTorchBrightness(DynamicLightDispatcher.GetItemBrightnessValue(institem.item.itemID));
                newtorch.SetTorchRange(DynamicLightDispatcher.GetItemLightRangeValue(institem.item.itemID));
                newtorch.setDeathAge(DynamicLightDispatcher.GetItemDeathAgeValue(institem.item.itemID));
                newtorch.SetWorksUnderwater(DynamicLightDispatcher.GetItemWorksUnderWaterValue(institem.item.itemID));
                newtorch.setTorchState(worldObj, true);
            } else if(shouldEmitLight(newent) && !(newent instanceof EntityPlayer)) {
                newtorch.SetTorchBrightness(15);
                newtorch.SetTorchRange(31);
                newtorch.setTorchState(worldObj, true);
            }
        }
    }

    private boolean shouldEmitLight(Entity ent) {
        if(ent instanceof EntityCreeper) {
            return ((EntityCreeperAccessor)ent).ignitedTime() > 0;
        }
        return ent.isOnFire() || ent instanceof EntityTNT;
    }
}
