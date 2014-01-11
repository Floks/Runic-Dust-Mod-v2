package com.runicdustmod.inscriptions.standard;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import com.runicdustmod.entity.EntityDust;
import com.runicdustmod.event.DustEvent;
import com.runicdustmod.event.InscriptionEvent;
import com.runicdustmod.util.VoidStorageManager;

public class InscriptionVoidStorage extends InscriptionEvent
{

	public InscriptionVoidStorage(int[][] design, String idName, String properName,
			int id)
	{
		super(design, idName, properName, id);
		this.setAuthor("billythegoat101");
		this.setDescription("Description:\n"
				+ "Automatically store all picked-up items into the void. Use the Void Storage rune to summon them back.");
		this.setNotes("Sacrifice:\n" + "-4xObsidianBlock + 2xEnderPearl + 5XP");
	}

	@Override
	public boolean callSacrifice(DustEvent rune, EntityDust e, ItemStack item)
	{
		ItemStack[] req = new ItemStack[] { new ItemStack(Block.obsidian, 4),
				new ItemStack(Item.enderPearl, 2) };
		req = rune.sacrifice(e, req);
		if (!rune.checkSacrifice(req))
			return false;
		if (!rune.takeXP(e, 5))
			;
		item.setItemDamage(0);
		return true;
	}

	@Override
	public ItemStack onItemPickup(EntityPlayer wearer, ItemStack insc,
			ItemStack pickedup)
	{
		VoidStorageManager.addItemToVoidInventory(
				((EntityPlayer) wearer).username, pickedup);
		ItemStack rtn = pickedup.copy();
		this.damage((EntityPlayer) wearer, insc, pickedup.stackSize);
		rtn.stackSize = 0;
		return rtn;
	}

}
