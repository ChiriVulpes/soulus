package com.kallgirl.souls.common;

import com.kallgirl.souls.client.render.TileEntitySummonerRenderer;
import com.kallgirl.souls.common.block.*;
import com.kallgirl.souls.common.block.Summoner.SummonerEmpty;
import com.kallgirl.souls.common.block.Summoner.Summoner;
import com.kallgirl.souls.common.item.*;
import com.kallgirl.souls.common.util.IModItem;
import com.kallgirl.souls.common.util.IModObject;
import com.kallgirl.souls.common.world.FossilGenerator;

import java.util.Hashtable;

public final class ModObjects {

	static Hashtable<String, IModObject> objects;

	public static void preinit() {
		objects = new Hashtable<>();

		// items
		objects.put("essence", new Essence());
		objects.put("boneEnder", new BoneEnder());
		objects.put("boneChunkEnder", new BoneChunkEnder());
		objects.put("soulbook", new Soulbook());
		objects.put("sledgehammer", new Sledgehammer());
		objects.put("boneChunkNormal", new BoneChunkNormal());
		objects.put("boneNether", new BoneNether());
		objects.put("boneChunkNether", new BoneChunkNether());
		objects.put("bonemealNether", new BonemealNether());
		objects.put("dustEnder", new DustEnder());
		objects.put("dustIron", new DustIron());
		objects.put("dustEnderIron", new DustEnderIron());
		objects.put("endersteel", new Endersteel());

		// blocks
		objects.put("endersteelBlock", new EndersteelBlock());
		objects.put("endersteelBars", new EndersteelBars());
		objects.put("summonerEmpty", new SummonerEmpty());
		objects.put("summoner", new Summoner());
		objects.put("fossilDirt", new FossilDirt());
		objects.put("fossilDirtEnder", new FossilDirtEnder());
		objects.put("fossilNetherrack", new FossilNetherrack());
		objects.put("fossilNetherrackEnder", new FossilNetherrackEnder());

		// generation
		objects.put("fossilGeneration", new FossilGenerator());

		// renderers
		objects.put("summonerRenderer", new TileEntitySummonerRenderer());

		objects.forEach((name, item) -> {
			item.preinit();
		});
	}

	public static void init() {
		objects.forEach((name, item) -> {
			item.init();
		});
	}

	public static void postinit() {
		objects.forEach((name, item) -> item.postinit());
	}

	public static IModItem get(String name) {
		IModObject result = objects.get(name);
		if (result instanceof IModItem) return (IModItem)result;
		else throw new IllegalArgumentException(String.format("'%s' is not a valid item or block", name));
	}
	public static Item getItem(String name) {
		IModObject item = objects.get(name);
		if (item instanceof Item) return (Item)item;
		else throw new IllegalArgumentException(String.format("'%s' is not a valid item", name));
	}
	public static Block getBlock(String name) {
		IModObject block = objects.get(name);
		if (block instanceof Block) return (Block)block;
		else throw new IllegalArgumentException(String.format("'%s' is not a valid block", name));
	}

}