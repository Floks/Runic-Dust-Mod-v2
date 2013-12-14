/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.runicdust.dustdefault.runes;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import com.runicdust.dustcore.entity.EntityDust;
import com.runicdust.dustcore.event.PoweredEvent;
import com.runicdust.dustcore.tileentity.TileEntityDust;

/**
 * 
 * @author billythegoat101
 */
public class RuneHunterVision extends PoweredEvent
{
	public RuneHunterVision()
	{
		super();
	}

	@Override
	public void initGraphics(EntityDust e)
	{
		super.initGraphics(e);

	}

	@Override
	public void onInit(EntityDust e)
	{
		super.onInit(e);
		ItemStack[] req = new ItemStack[] { new ItemStack(Item.blazePowder, 3),
				new ItemStack(Item.eyeOfEnder, 1) };
		req = this.sacrifice(e, req);

		if (!checkSacrifice(req) || !takeXP(e, 12))
		{
			e.fizzle();
			return;
		}
	}

	@Override
	public void onTick(EntityDust e)
	{
		super.onTick(e);
		// DustModCore.hunterVisionActive = e.data[0] % 2 == 0;
	}

	@Override
	public void onRightClick(EntityDust e, TileEntityDust ted, EntityPlayer p)
	{
		super.onRightClick(e, ted, p);
		e.data[0]++;
	}

	@Override
	public void onUnload(EntityDust e)
	{
		super.onUnload(e);
		// DustModCore.hunterVisionActive = false;
	}

	@Override
	public int getStartFuel()
	{
		return dayLength;
	}

	@Override
	public int getMaxFuel()
	{
		return dayLength * 2;
	}

	@Override
	public int getStableFuelAmount(EntityDust e)
	{
		return dayLength;
	}

	@Override
	public boolean isPaused(EntityDust e)
	{
		return e.data[0] % 2 == 1;
	}
}
