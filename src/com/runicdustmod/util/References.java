package com.runicdustmod.util;

import net.minecraft.item.Item;

import com.runicdustmod.api.DustItemManager;
import com.runicdustmod.core.DustContent;
import com.runicdustmod.item.ItemWornInscription;

public class References
{
	public static final int warpVer = 1;
	public static boolean debug = false;

	public static int plantDID = 1;
	public static int gunDID = 2;
	public static int lapisDID = 3;
	public static int blazeDID = 4;

	public static String path = "runicdustmod:textures";
	public static String spritePath = "runicdustmod:";
	public static int[] tex;
	public static int groundTex;
	public static boolean allTex = true;

	public static int DustMetaDefault = 0;
	public static int DustMetaUsing = 1;
	public static int DustMetaUsed = 2;

	/** Mod ids **/
	public static int BLOCK_DustID = 3465;
	public static int BLOCK_DustTableID = 3466;
	public static int BLOCK_RutID = 3467;
	public static int ITEM_DustID = 21850;
	public static int ITEM_RunicTomeID = 21851;
	public static int ITEM_DustScrollID = 21852;
	public static int ITEM_SpiritSwordID = 21853;
	public static int ITEM_SpiritPickID = 21854;
	public static int ITEM_ChiselID = 21855;
	public static int ITEM_SacrificeNegationID = 21856;
	public static int ITEM_RunicPaperID = 21857;
	public static int ITEM_InscriptionID = 21858;
	public static int ITEM_InkID = 21859;
	public static int ITEM_WornInscriptionID = 21860;
	public static int ITEM_PouchID = 21861;
	public static int ENTITY_FireSpriteID = 149;
	public static int ENTITY_BlockEntityID = 150;
	public static boolean Enable_Render_Flames_On_Dust = true;
	public static boolean Enable_Render_Flames_On_Ruts = true;
	public static boolean Enable_Decorative_Ruts = false;
	public static boolean verbose = false;
	
	//Module Activation booleans
	public static boolean Enable_Default_Dusts = true;
	public static boolean Enable_Decor_Dusts = true;
	public static boolean Enable_Thaumcraft_Dusts = false;
	public static boolean Enable_Beta_Dusts = false;
	
	//Reference methods that should be here
	public static ItemWornInscription getWornInscription()
	{
		return DustContent.wornInscription;
	}

	public static Item getItemDust()
	{
		return DustItemManager.idust;
	}

	public static Item getNegator()
	{
		return DustContent.negateSacrifice;
	}
}
