package com.dustcore.util;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

import com.dustcore.client.gui.GuiInscription;
import com.dustcore.client.gui.InscriptionGuiContainer;
import com.dustcore.config.DustContent;

import cpw.mods.fml.common.network.IGuiHandler;

public class GuiHandler implements IGuiHandler
{

	@Override
	public Object getServerGuiElement(int ID, EntityPlayer player, World world,
			int x, int y, int z)
	{

		if (ID == 0)
		{
			ItemStack item = player.getCurrentEquippedItem();
			if (item != null && item.itemID == DustContent.inscription.itemID)
			{
				return new InscriptionGuiContainer(player.inventory,
						DustContent.inscription.getInventory(item));
			}
		}
		return null;
	}

	@Override
	public Object getClientGuiElement(int ID, EntityPlayer player, World world,
			int x, int y, int z)
	{

		if (ID == 0)
		{
			ItemStack item = player.getCurrentEquippedItem();
			if (item != null && item.itemID == DustContent.inscription.itemID)
			{
				return new GuiInscription(player,
						DustContent.inscription.getInventory(item));
			}
		}
		return null;
	}

}
