package com.dustcore.tileentity;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.INetworkManager;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.Packet132TileEntityData;
import net.minecraft.tileentity.TileEntity;

import com.dustcore.DustMod;
import com.dustcore.api.DustItemManager;
import com.dustcore.block.BlockDust;
import com.dustcore.config.DustContent;
import com.dustcore.entity.EntityDust;
import com.dustcore.util.PacketHandler;

public class TileEntityDust extends TileEntity implements IInventory
{
	public static final int size = 4;
	public boolean active = false;
	private int[][] pattern;
	private boolean[] dusts;
	private int toDestroy = -1;
	private int ticksExisted = 0;
	private EntityDust entityDust = null;
	private boolean isPowered = false;
	private boolean hasMadeFirstPoweredCheck = false;

	public int dustEntID;

	private boolean hasFlame = false;
	private int fr, fg, fb; // flame rgb

	public TileEntityDust()
	{
		pattern = new int[size][size];
	}

	public void setEntityDust(EntityDust ed)
	{
		this.entityDust = ed;
		this.dustEntID = ed.entityId;
	}

	public void writeToNBT(NBTTagCompound tag)
	{
		super.writeToNBT(tag);

		for (int i = 0; i < size; i++)
		{
			for (int j = 0; j < size; j++)
			{
				tag.setInteger(i + "dust" + j, pattern[i][j]);
			}
		}

		tag.setInteger("toDestroy", toDestroy);
		tag.setInteger("ticks", ticksExisted);

		tag.setBoolean("flame", hasFlame);
		tag.setInteger("flameR", fr);
		tag.setInteger("flameG", fg);
		tag.setInteger("flameB", fb);
	}

	public void readFromNBT(NBTTagCompound tag)
	{
		super.readFromNBT(tag);

		for (int i = 0; i < size; i++)
		{
			for (int j = 0; j < size; j++)
			{
				int dust = tag.getInteger(i + "dust" + j);
				if (dust < 5 && dust > 0)
					dust *= 100;
				pattern[i][j] = dust;
			}
		}

		if (tag.hasKey("toDestroy"))
		{
			toDestroy = tag.getInteger("toDestroy");
		}

		if (tag.hasKey("ticks"))
		{
			ticksExisted = tag.getInteger("ticks");
		}

		if (tag.hasKey("flame"))
		{
			this.hasFlame = tag.getBoolean("flame");
			fr = tag.getInteger("flameR");
			fg = tag.getInteger("flameG");
			fb = tag.getInteger("flameB");
		}
	}

	public void setDust(EntityPlayer p, int i, int j, int dust)
	{
		if (p != null
				&& !worldObj.canMineBlock(p, this.xCoord, this.yCoord,
						this.zCoord))
			return;
		int last = getDust(i, j);
		if (dust >= 1000)
			dust = 999;
		pattern[i][j] = dust;
		dusts = null;

		if (dust != 0 && last != dust)
		{
			int[] color = DustItemManager.getFloorColorRGB(dust);
			java.awt.Color c = new java.awt.Color(color[0], color[1], color[2]);
			c = c.darker();
			float r = (float) c.getRed() / 255F;
			float g = (float) c.getGreen() / 255F;
			float b = (float) c.getBlue() / 255F;
			if (r == 0)
				r -= 1;

			if (Math.random() < 0.75)
				for (int d = 0; d < Math.random() * 3; d++)
				{
					worldObj.spawnParticle("reddust", xCoord + (double) i / 4D
							+ Math.random() * 0.15, yCoord, zCoord + (double) j
							/ 4D + Math.random() * 0.15, r, g, b);
				}
		}
		worldObj.notifyBlockChange(xCoord, yCoord, zCoord, 0);
		worldObj.markBlockForRenderUpdate(xCoord, yCoord, zCoord);
		this.onInventoryChanged();
	}

	public int[][] getPattern()
	{
		return pattern;
	}

	public int getDust(int i, int j)
	{
		int rtn = pattern[i][j];
		if (rtn >= 1000)
		{
			return 999;
		}
		return rtn;
	}

	public void updateEntity()
	{
		super.updateEntity();

		if (ticksExisted > 2 && isEmpty()
				&& worldObj.getBlockMetadata(xCoord, yCoord, zCoord) != 10)
		{
			worldObj.setBlock(xCoord, yCoord, zCoord, 0, 0, 3);
			this.invalidate();
			return;
		}

		if (Math.random() < 0.12
				&& worldObj.getBlockMetadata(xCoord, yCoord, zCoord) == BlockDust.ACTIVE_DUST)
		{
			worldObj.spawnParticle("reddust", xCoord + Math.random(), yCoord,
					zCoord + Math.random(), 0, 0, 0);
		}

		ticksExisted++;
		if (toDestroy == 0)
		{
			if (worldObj.getBlockMetadata(xCoord, yCoord, zCoord) != BlockDust.DEAD_DUST)
			{
				toDestroy = -1;
				return;
			}

			for (int i1 = 0; (double) i1 < Math.random() * 2D + 2D; i1++)
			{
				worldObj.spawnParticle("smoke",
						(double) xCoord + Math.random(),
						(double) yCoord + Math.random() / 2D, (double) zCoord
								+ Math.random(), 0.07, 0.01D, 0.07D);
			}

			List<Integer> d = new ArrayList<Integer>();

			for (int i = 0; i < size; i++)
			{
				for (int j = 0; j < size; j++)
				{
					if (getDust(i, j) != 0)
					{
						d.add(i * 4 + j);
					}
				}
			}

			Random rand = new Random();

			if (d.size() == 0)
			{
			}

			int ind = d.get(rand.nextInt(d.size()));
			this.setDust(null, (int) Math.floor(ind / size), ind % size, 0);
			toDestroy = (int) Math.round(Math.random() * 200D + 100D);
			worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
		} else if (toDestroy > 0)
		{
			toDestroy--;
		}

		if (ticksExisted % 100 == 0)
		{
			if (toDestroy <= -1
					&& this.getBlockMetadata() == BlockDust.DEAD_DUST)
			{
				toDestroy = (int) Math.round(Math.random() * 200D + 100D);
			}
		}

		if (!this.hasMadeFirstPoweredCheck)
		{
			this.isPowered = worldObj.isBlockIndirectlyGettingPowered(xCoord,
					yCoord, zCoord);
			this.hasMadeFirstPoweredCheck = true;
		}
	}

	public void onRightClick(EntityPlayer p)
	{
		if (this.entityDust != null)
		{
			entityDust.onRightClick(this, p);
		}
	}

	public void onNeighborBlockChange()
	{
		this.isPowered = worldObj.isBlockIndirectlyGettingPowered(xCoord,
				yCoord, zCoord);
	}

	public boolean isPowered()
	{
		return this.isPowered;
	}

	public int[][][] getRendArrays()
	{
		int[][][] rtn = new int[3][size + 1][size + 1];
		int[][] n = new int[size + 2][size + 2]; // neighbors

		for (int x = 0; x < size; x++)
		{
			for (int z = 0; z < size; z++)
			{
				n[x + 1][z + 1] = getDust(x, z);
				rtn[0][x][z] = getDust(x, z);
			}
		}

		if (DustMod.isDust(worldObj.getBlockId(xCoord - 1, yCoord, zCoord)))
		{
			TileEntityDust ted = (TileEntityDust) worldObj.getBlockTileEntity(
					xCoord - 1, yCoord, zCoord);

			for (int i = 0; i < size; i++)
			{
				n[0][i + 1] = ted.getDust(size - 1, i);
			}
		}

		if (DustMod.isDust(worldObj.getBlockId(xCoord + 1, yCoord, zCoord)))
		{
			TileEntityDust ted = (TileEntityDust) worldObj.getBlockTileEntity(
					xCoord + 1, yCoord, zCoord);

			for (int i = 0; i < size; i++)
			{
				n[size + 1][i + 1] = ted.getDust(0, i);
			}
		}

		if (DustMod.isDust(worldObj.getBlockId(xCoord, yCoord, zCoord - 1)))
		{
			TileEntityDust ted = (TileEntityDust) worldObj.getBlockTileEntity(
					xCoord, yCoord, zCoord - 1);

			for (int i = 0; i < size; i++)
			{
				n[i + 1][0] = ted.getDust(i, size - 1);
			}
		}

		if (DustMod.isDust(worldObj.getBlockId(xCoord, yCoord, zCoord + 1)))
		{
			TileEntityDust ted = (TileEntityDust) worldObj.getBlockTileEntity(
					xCoord, yCoord, zCoord + 1);

			for (int i = 0; i < size; i++)
			{
				n[i + 1][size + 1] = ted.getDust(i, 0);
			}
		}

		// horiz
		for (int x = 0; x < size; x++)
		{
			for (int y = 0; y < size + 1; y++)
			{
				if (n[x + 1][y] == n[x + 1][y + 1])
				{
					rtn[1][x][y] = n[x + 1][y];
				}
			}
		}

		// vert
		for (int x = 0; x < size + 1; x++)
		{
			for (int y = 0; y < size; y++)
			{
				if (n[x][y + 1] == n[x + 1][y + 1])
				{
					rtn[2][x][y] = n[x][y + 1];
				}
			}
		}

		return rtn;
	}

	public boolean isEmpty()
	{
		for (int i = 0; i < size; i++)
		{
			for (int j = 0; j < size; j++)
			{
				if (getDust(i, j) != 0)
				{
					return false;
				}
			}
		}

		return true;
	}

	public int getAmount()
	{
		int amt = 0;

		for (int i = 0; i < size; i++)
		{
			for (int j = 0; j < size; j++)
			{
				if (getDust(i, j) != 0)
				{
					amt++;
				}
			}
		}

		return amt;
	}

	public boolean[] getDusts()
	{
		if (dusts == null)
		{
			dusts = new boolean[1000];

			for (int i = 0; i < size; i++)
			{
				for (int j = 0; j < size; j++)
				{
					if (getDust(i, j) >= 0)
					{
						dusts[getDust(i, j)] = true;
					}
				}
			}
		}

		return dusts;
	}

	public int getRandomDustColor()
	{
		int s = 0;
		int[] dusts = new int[1000];
		boolean[] bdusts = getDusts();

		for (int i = 1; i < 1000; i++)
		{
			if (bdusts[i])
			{
				dusts[s] = i;
				s++;
			}
		}

		if (s <= 0)
		{
			return 0;
		}

		Random rand = new Random();
		int[] rgb = DustItemManager.getFloorColorRGB(dusts[rand.nextInt(s)]);
		int hex = 0;
		return new Color(rgb[0], rgb[1], rgb[2]).getRGB();
	}

	public void empty()
	{
		for (int i = 0; i < size; i++)
		{
			for (int j = 0; j < size; j++)
			{
				setDust(null, i, j, 0);
			}
		}
	}

	public void copyTo(TileEntityDust ted)
	{
		ted.dusts = Arrays.copyOf(dusts, dusts.length);

		for (int i = 0; i < pattern.length; i++)
		{
			ted.pattern[i] = Arrays.copyOf(pattern[i], pattern[i].length);
		}

		ted.toDestroy = toDestroy;
		ted.ticksExisted = ticksExisted;
		int tx = ted.xCoord;
		int ty = ted.yCoord;
		int tz = ted.zCoord;
		ted.worldObj.setBlock(tx, ty, tz,
				worldObj.getBlockMetadata(xCoord, yCoord, zCoord), 0, 3);
	}

	@Override
	public void onDataPacket(INetworkManager net, Packet132TileEntityData pkt)
	{
		super.onDataPacket(net, pkt);
		// System.out.println("DataPacket");
	}

	@Override
	public int getSizeInventory()
	{
		return size * size;
	}

	@Override
	public ItemStack getStackInSlot(int loc)
	{
		int y = loc % size;
		int x = (loc - size) / size;

		if (getDust(x, y) == 0)
		{
			return null;
		} else
		{
			return new ItemStack(DustContent.idust.itemID, 1, pattern[x][y]);
		}
	}

	@Override
	public ItemStack decrStackSize(int loc, int amt)
	{
		int y = loc % size;
		int x = (loc - size) / size;
		pattern[x][y] = 0;
		return null;
	}

	@Override
	public ItemStack getStackInSlotOnClosing(int loc)
	{
		return null;
	}

	@Override
	public void setInventorySlotContents(int loc, ItemStack item)
	{
		int y = loc % size;
		int x = (loc - size) / size;
		int size = item.stackSize;
		int meta = item.getItemDamage();
		int id = item.itemID;

		if (id == DustContent.idust.itemID && size > 0)
		{
			pattern[x][y] = meta;
		}
	}

	@Override
	public String getInvName()
	{
		return "dusttileentity";
	}

	@Override
	public int getInventoryStackLimit()
	{
		return 1;
	}

	@Override
	public boolean isUseableByPlayer(EntityPlayer var1)
	{
		return false;
	}

	@Override
	public void openChest()
	{
	}

	@Override
	public void closeChest()
	{
	}

	public void setRenderFlame(boolean val, int r, int g, int b)
	{
		this.hasFlame = val;
		this.fr = r;
		this.fg = g;
		this.fb = b;
		worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
	}

	public boolean hasFlame()
	{
		return hasFlame;
	}

	public int[] getFlameColor()
	{
		return new int[] { fr, fg, fb };
	}

	@Override
	public Packet getDescriptionPacket()
	{
		return PacketHandler.getTEDPacket(this);
	}

	@Override
	public boolean isInvNameLocalized()
	{
		return false;
	}

	@Override
	public boolean isItemValidForSlot(int i, ItemStack itemstack)
	{
		return false;
	}
}
