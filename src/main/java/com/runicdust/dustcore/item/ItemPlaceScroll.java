package com.runicdust.dustcore.item;

import java.util.List;

import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumAction;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumMovingObjectType;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;

import com.runicdust.dustcore.DustMod;
import com.runicdust.dustcore.DustShape;
import com.runicdust.dustcore.api.DustManager;
import com.runicdust.dustcore.config.DustContent;
import com.runicdust.dustcore.util.References;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

/**
 * 
 * @author billythegoat101
 */
public class ItemPlaceScroll extends Item
{
	private int blockID;

	public ItemPlaceScroll(int i)
	{
		super(i);
		blockID = DustContent.dust.blockID;
		setMaxDamage(0);
		setHasSubtypes(true);
		this.setMaxStackSize(4);
	}

	@Override
	public boolean onItemUse(ItemStack item, EntityPlayer wielder, World world,
			int i, int j, int k, int l, float x, float y, float z)
	{
		if (!world.canMineBlock(wielder, i, j, k))
			return false;

		if (!(wielder instanceof EntityPlayer))
			return false;
		boolean inUse = ((EntityPlayer) wielder).getCurrentEquippedItem() == item;

		DustShape ds = DustManager.getShapeFromID(item.getItemDamage());
		int r = (int) MathHelper
				.floor_double((double) ((wielder.rotationYaw * 4F) / 360F) + 0.5D) & 3;

		if (DustMod.isDust(world.getBlockId(i, j, k)))
		{
			j--;
		}

		try
		{
			ds.drawOnWorldWhole(world, i, j, k, (EntityPlayer) wielder, r);
		} catch (Exception e)
		{
			e.printStackTrace();
		}
		((EntityPlayer) wielder).inventory.onInventoryChanged();
		return true;
	}

	/**
	 * Called whenever this item is equipped and the right mouse button is
	 * pressed. Args: itemStack, world, entityPlayer
	 */
	public ItemStack onItemRightClick(ItemStack par1ItemStack, World par2World,
			EntityPlayer par3EntityPlayer)
	{
		par3EntityPlayer.setItemInUse(par1ItemStack,
				this.getMaxItemUseDuration(par1ItemStack));
		return par1ItemStack;
	}

	/**
	 * returns the action that specifies what animation to play when the items
	 * is being used
	 */
	@Override
	public EnumAction getItemUseAction(ItemStack par1ItemStack)
	{
		return EnumAction.block;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public boolean isFull3D()
	{
		return false;
	}

	public String getUnlocalizedName(ItemStack itemstack)
	{
		DustShape shape = DustManager.getShapeFromID(itemstack.getItemDamage());
		if (shape != null)
			return "tile.scroll" + shape.name;
		else
			return "tile.scroll.error";
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void addInformation(ItemStack item, EntityPlayer player, List list,
			boolean flag)
	{
		super.addInformation(item, player, list, flag);
		DustShape shape = DustManager.getShapeFromID(item.getItemDamage());
		if (shape == null)
			return;
		String sacr = shape.getNotes().replace("Sacrifice:\n", "");
		String[] split = sacr.split("\n");
		list.add("Requires:");
		for (String i : split)
		{
			if (!i.isEmpty())
			{
				if (i.charAt(0) != '-')
					break;
				if (i.length() > 25)
				{
					String temp = i;
					while (temp.length() > 25)
					{
						int index = temp.indexOf(' ', 25);
						if (index == -1)
							index = temp.length();

						String add = temp.substring(0, index);
						if (!add.isEmpty())
							list.add(add);
						temp = temp.substring(index);
					}
					list.add(temp);
				} else
				{
					list.add(i);
				}
			}
		}
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void getSubItems(int par1, CreativeTabs par2CreativeTabs, List list)
	{
		for (DustShape i : DustManager.getShapes())
		{
			list.add(new ItemStack(itemID, 1, i.id));
		}
	}

	/**
	 * How long it takes to use or consume an item
	 */
	@Override
	public int getMaxItemUseDuration(ItemStack par1ItemStack)
	{
		return 72000;
	}

	public int[] getClickedBlock(Entity wielder, ItemStack item)
	{
		MovingObjectPosition click = this.getMovingObjectPositionFromPlayer(
				wielder.worldObj, (EntityPlayer) wielder, false);
		if (click != null && click.typeOfHit == EnumMovingObjectType.TILE)
		{
			int tx = click.blockX;
			int ty = click.blockY;
			int tz = click.blockZ;
			return new int[] { tx, ty, tz };
		}
		return null;
	}

	protected MovingObjectPosition lastMOP = null;
	protected long lastCheck = 0;

	public MovingObjectPosition getMovingObjectPositionFromPlayer(World world,
			EntityPlayer par2EntityPlayer, boolean par3)
	{
		if (lastCheck > world.getWorldTime())
			lastCheck = world.getWorldTime();
		if (lastMOP != null && world.getWorldTime() - lastCheck < 0)
		{
			return lastMOP;
		}
		lastCheck = world.getWorldTime();
		float var4 = 1.0F;
		float var5 = par2EntityPlayer.prevRotationPitch
				+ (par2EntityPlayer.rotationPitch - par2EntityPlayer.prevRotationPitch)
				* var4;
		float var6 = par2EntityPlayer.prevRotationYaw
				+ (par2EntityPlayer.rotationYaw - par2EntityPlayer.prevRotationYaw)
				* var4;
		double var7 = par2EntityPlayer.prevPosX
				+ (par2EntityPlayer.posX - par2EntityPlayer.prevPosX)
				* (double) var4;
		double var9 = par2EntityPlayer.prevPosY
				+ (par2EntityPlayer.posY - par2EntityPlayer.prevPosY)
				* (double) var4 + 1.62D - (double) par2EntityPlayer.yOffset;
		double var11 = par2EntityPlayer.prevPosZ
				+ (par2EntityPlayer.posZ - par2EntityPlayer.prevPosZ)
				* (double) var4;
		Vec3 var13 = world.getWorldVec3Pool().getVecFromPool(var7, var9, var11);
		float var14 = MathHelper.cos(-var6 * 0.017453292F - (float) Math.PI);
		float var15 = MathHelper.sin(-var6 * 0.017453292F - (float) Math.PI);
		float var16 = -MathHelper.cos(-var5 * 0.017453292F);
		float var17 = MathHelper.sin(-var5 * 0.017453292F);
		float var18 = var15 * var16;
		float var20 = var14 * var16;
		double var21 = 65.0D;
		Vec3 var23 = var13.addVector((double) var18 * var21, (double) var17
				* var21, (double) var20 * var21);
		lastMOP = world.rayTraceBlocks_do_do(var13, var23, par3, !par3);
		return lastMOP;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerIcons(IconRegister iconRegister)
	{
		this.itemIcon = iconRegister.registerIcon(References.spritePath
				+ "dustScroll");
	}
}