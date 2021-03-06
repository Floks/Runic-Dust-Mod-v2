package com.runicdustmod.client.gui;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

import com.runicdustmod.core.DustContent;
import com.runicdustmod.item.ItemInk;

public class InscriptionGuiContainer extends Container
{

	public InscriptionGuiContainer(InventoryPlayer inventoryPlayer,
			IInventory inv)
	{

		this.addSlotToContainer(new Slot(inv, 0, 1000, 1000));
		bindPlayerInventory(inventoryPlayer);
		for (int i = 0; i < 16 * 16; i++)
		{
			this.addSlotToContainer(new Slot(inv, i + 10, 1000, 1000));
		}

		// Loop through player's hotbar for inks
		for (int i = 1; i < 10; i++)
		{
			ItemStack stack = inventoryPlayer.getStackInSlot(i - 1);
			if (stack != null && stack.itemID == DustContent.ink.itemID)
			{
				int id = ItemInk.getDustID(stack);
				this.putStackInSlot(0, new ItemStack(DustContent.ink.itemID,
						id, i - 1));
				break;
			}
		}
	}

	protected void bindPlayerInventory(InventoryPlayer inventoryPlayer)
	{
		for (int i = 0; i < 9; i++)
		{
			addSlotToContainer(new Slot(inventoryPlayer, i, 8 + i * 18, 142));
		}
	}

	@Override
	public boolean canInteractWith(EntityPlayer p)
	{
		if (p.getCurrentEquippedItem() == null)
			return false;
		return p.getCurrentEquippedItem().itemID == DustContent.inscription.itemID;
	}

	@Override
	protected boolean mergeItemStack(ItemStack par1ItemStack, int par2,
			int par3, boolean par4)
	{
		return false;
	}
}
