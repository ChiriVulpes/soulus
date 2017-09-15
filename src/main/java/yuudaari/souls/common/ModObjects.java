package yuudaari.souls.common;

import yuudaari.souls.Souls;
import yuudaari.souls.client.render.TileEntitySummonerRenderer;
import yuudaari.souls.common.block.*;
import yuudaari.souls.common.block.Summoner.Summoner;
import yuudaari.souls.common.block.Summoner.SummonerEmpty;
import yuudaari.souls.common.item.*;
import yuudaari.souls.common.util.IModItem;
import yuudaari.souls.common.util.IModObject;
import yuudaari.souls.common.world.FossilGenerator;
import yuudaari.souls.common.world.WorldGenerator;
import java.util.Hashtable;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.crafting.IRecipe;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.oredict.OreDictionary;

@Mod.EventBusSubscriber(modid = Souls.MODID)
public final class ModObjects {

	static Hashtable<String, IModObject> objects;
	static IModObject[] arrayObjects;

	// items
	public static final SoulsItem BLOOD_CRYSTAL = new BloodCrystal();
	public static final SoulsItem BONE_CHUNK_ENDER = new BoneChunkEnder();
	public static final SoulsItem BONE_CHUNK_NETHER = new BoneChunkNether();
	public static final SoulsItem BONE_CHUNK_NORMAL = new BoneChunkNormal();
	public static final SoulsItem BONE_ENDER = new BoneEnder();
	public static final SoulsItem BONE_NETHER = new BoneNether();
	public static final SoulsItem BONE_MEAL_NETHER = new BoneMealNether();
	public static final SoulsItem DUST_ENDER = new DustEnder();
	public static final SoulsItem DUST_ENDER_IRON = new DustEnderIron();
	public static final SoulsItem DUST_IRON = new DustIron();
	public static final SoulsItem EMERALD_BURNT = new EmeraldBurnt();
	public static final SoulsItem EMERALD_DRAINED = new EmeraldDrained();
	public static final SoulsItem EMERALD_COATED = new EmeraldCoated();
	public static final SoulsItem ESSENCE = new Essence();
	public static final SoulsItem GLUE = new Glue();
	public static final SoulsItem INGOT_ENDERSTEEL = new IngotEndersteel();
	public static final SoulsItem NUGGET_ENDERSTEEL = new NuggetEndersteel();
	public static final SoulsItem SLEDGEHAMMER = new Sledgehammer();
	public static final SoulsItem SOULBOOK = new Soulbook();

	// blocks
	public static final SoulsBlock BLOCK_ENDERSTEEL = new BlockEndersteel();
	public static final SoulsBlock FOSSIL_DIRT = new FossilDirt();
	public static final SoulsBlock FOSSIL_DIRT_ENDER = new FossilDirtEnder();
	public static final SoulsBlock FOSSIL_NETHERRACK = new FossilNetherrack();
	public static final SoulsBlock FOSSIL_NETHERRACK_ENDER = new FossilNetherrackEnder();
	public static final SoulsBlock SUMMONER = new Summoner();
	public static final SoulsBlock SUMMONER_EMPTY = new SummonerEmpty();

	public static final SoulsBlockPane BARS_ENDERSTEEL = new BarsEndersteel();

	// generation
	public static final WorldGenerator FOSSIL_GENERATOR = new FossilGenerator();

	// renderers
	public static final TileEntitySummonerRenderer SUMMONER_RENDERER = new TileEntitySummonerRenderer();

	public static void preinit() {
		arrayObjects = new IModObject[] {
				// items	
				ESSENCE, BONE_ENDER, BONE_CHUNK_ENDER, SOULBOOK, SLEDGEHAMMER, BONE_CHUNK_NORMAL, BONE_NETHER,
				BONE_CHUNK_NETHER, BONE_MEAL_NETHER, DUST_ENDER, DUST_IRON, DUST_ENDER_IRON, INGOT_ENDERSTEEL,
				BLOOD_CRYSTAL, NUGGET_ENDERSTEEL, EMERALD_BURNT, EMERALD_DRAINED, EMERALD_COATED, GLUE,

				// blocks
				BLOCK_ENDERSTEEL, SUMMONER_EMPTY, SUMMONER, FOSSIL_DIRT, FOSSIL_DIRT_ENDER, FOSSIL_NETHERRACK,
				FOSSIL_NETHERRACK_ENDER, BARS_ENDERSTEEL,

				// generation
				FOSSIL_GENERATOR,

				// renderers
				SUMMONER_RENDERER };

		objects = new Hashtable<>();

		for (IModObject modObject : arrayObjects) {
			objects.put(modObject.getName(), modObject);
		}

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
		if (result instanceof IModItem)
			return (IModItem) result;
		else
			throw new IllegalArgumentException(String.format("'%s' is not a valid item or block", name));
	}

	public static SoulsItem getItem(String name) {
		IModObject item = objects.get(name);
		if (item instanceof SoulsItem)
			return (SoulsItem) item;
		else
			throw new IllegalArgumentException(String.format("'%s' is not a valid item", name));
	}

	public static SoulsBlock getBlock(String name) {
		IModObject block = objects.get(name);
		if (block instanceof SoulsBlock)
			return (SoulsBlock) block;
		else
			throw new IllegalArgumentException(String.format("'%s' is not a valid block", name));
	}

	@SubscribeEvent
	public static void registerBlocks(RegistryEvent.Register<net.minecraft.block.Block> event) {
		for (IModObject object : arrayObjects) {
			if (object instanceof Block) {
				event.getRegistry().register((Block) object);
			}
		}
	}

	@SubscribeEvent
	public static void registerItems(RegistryEvent.Register<net.minecraft.item.Item> event) {
		for (IModObject object : arrayObjects) {
			if (object instanceof IBlock) {
				IBlock block = (IBlock) object;
				if (block.hasItem()) {
					event.getRegistry().register(block.getItemBlock());
				}
				if (block.hasTileEntity()) {
					GameRegistry.registerTileEntity(block.getTileEntityClass(), "souls:summoner");
				}
			} else if (object instanceof SoulsItem) {
				SoulsItem item = (SoulsItem) object;
				event.getRegistry().register(item);
			}
			if (object instanceof IModItem) {
				IModItem modItem = (IModItem) object;
				modItem.getOreDicts().forEach((name) -> {
					OreDictionary.registerOre(name, modItem.getItem());
				});
			}
		}
	}

	@SubscribeEvent
	public static void registerRecipes(RegistryEvent.Register<IRecipe> event) {
		for (IModObject object : arrayObjects) {
			if (object instanceof IModItem) {
				IModItem modItem = (IModItem) object;
				modItem.registerRecipes();
			}
		}
	}

	@SubscribeEvent
	@SideOnly(Side.CLIENT)
	public static void registerModels(ModelRegistryEvent event) {
		for (IModObject object : arrayObjects) {
			if (object instanceof SoulsBlock) {
				SoulsBlock block = (SoulsBlock) object;
				if (block.hasItem()) {
					ModelLoader.setCustomModelResourceLocation(block.getItemBlock(), 0,
							new ModelResourceLocation(block.getRegistryName(), "inventory"));
				}
			} else if (object instanceof SoulsBlockPane) {
				SoulsBlockPane block = (SoulsBlockPane) object;
				if (block.hasItem()) {
					ModelLoader.setCustomModelResourceLocation(block.getItemBlock(), 0,
							new ModelResourceLocation(block.getRegistryName(), "inventory"));
				}
			} else if (object instanceof SoulsItem) {
				SoulsItem item = (SoulsItem) object;
				ModelLoader.setCustomModelResourceLocation(item, 0,
						new ModelResourceLocation(item.getRegistryName(), "inventory"));
			}
		}
	}

}