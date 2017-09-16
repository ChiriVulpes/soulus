package yuudaari.souls.common;

import yuudaari.souls.common.util.IBlock;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.crafting.IRecipe;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.oredict.OreDictionary;
import net.minecraftforge.registries.IForgeRegistry;
import yuudaari.souls.Souls;
import yuudaari.souls.common.block.*;
import yuudaari.souls.common.block.Summoner.Summoner;
import yuudaari.souls.common.block.Summoner.SummonerEmpty;

public class ModBlocks {
	public static final BlockEndersteel BLOCK_ENDERSTEEL = new BlockEndersteel();
	public static final FossilDirt FOSSIL_DIRT = new FossilDirt();
	public static final FossilDirtEnder FOSSIL_DIRT_ENDER = new FossilDirtEnder();
	public static final FossilNetherrack FOSSIL_NETHERRACK = new FossilNetherrack();
	public static final FossilNetherrackEnder FOSSIL_NETHERRACK_ENDER = new FossilNetherrackEnder();
	public static final Summoner SUMMONER = new Summoner();
	public static final SummonerEmpty SUMMONER_EMPTY = new SummonerEmpty();
	public static final BarsEndersteel BARS_ENDERSTEEL = new BarsEndersteel();

	public static IBlock[] blocks = new IBlock[] { BARS_ENDERSTEEL, BLOCK_ENDERSTEEL, FOSSIL_DIRT, FOSSIL_DIRT_ENDER,
			FOSSIL_NETHERRACK, FOSSIL_NETHERRACK_ENDER, SUMMONER, SUMMONER_EMPTY };

	public static void registerBlocks(IForgeRegistry<Block> registry) {
		for (IBlock block : blocks) {
			registry.register((Block) block);
		}
	}

	public static void registerItems(IForgeRegistry<Item> registry) {
		Souls.LOGGER.info(blocks);
		for (IBlock block : blocks) {
			if (block.hasItem()) {
				ItemBlock item = block.getItemBlock();
				registry.register(item);
				for (String dict : block.getOreDicts()) {
					OreDictionary.registerOre(dict, item);
				}
			}
			if (block.hasTileEntity()) {
				GameRegistry.registerTileEntity(block.getTileEntityClass(), "souls:summoner");
			}
		}
		Souls.LOGGER.info(blocks);
	}

	public static void registerModels() {
		for (IBlock block : blocks) {
			if (block.hasItem()) {
				ModelLoader.setCustomModelResourceLocation(block.getItemBlock(), 0,
						new ModelResourceLocation(block.getRegistryName(), "inventory"));
			}
		}
	}

	public static void registerRecipes(IForgeRegistry<IRecipe> registry) {
		for (IBlock block : blocks) {
			for (IRecipe recipe : block.getRecipes()) {
				registry.register(recipe);
			}
		}
	}
}