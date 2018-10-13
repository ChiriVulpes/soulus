package yuudaari.soulus.common;

import yuudaari.soulus.common.block.fossil.fossil_dirt.FossilDirt;
import yuudaari.soulus.common.block.fossil.fossil_dirt.FossilDirtEnder;
import yuudaari.soulus.common.block.fossil.fossil_dirt.FossilDirtFrozen;
import yuudaari.soulus.common.block.fossil.fossil_dirt.FossilDirtFungal;
import yuudaari.soulus.common.block.fossil.fossil_end.FossilEndStone;
import yuudaari.soulus.common.block.fossil.fossil_gravel.FossilGravel;
import yuudaari.soulus.common.block.fossil.fossil_gravel.FossilGravelDry;
import yuudaari.soulus.common.block.fossil.fossil_gravel.FossilGravelFungal;
import yuudaari.soulus.common.block.fossil.fossil_gravel.FossilGravelScale;
import yuudaari.soulus.common.block.fossil.fossil_ice.FossilIce;
import yuudaari.soulus.common.block.fossil.fossil_ice.FossilIceEnder;
import yuudaari.soulus.common.block.fossil.fossil_ice.FossilIceFrozen;
import yuudaari.soulus.common.block.fossil.fossil_ice.FossilIceScale;
import yuudaari.soulus.common.block.fossil.fossil_netherrack.FossilNetherrack;
import yuudaari.soulus.common.block.fossil.fossil_netherrack.FossilNetherrackEnder;
import yuudaari.soulus.common.block.fossil.fossil_red_sand.FossilSandRed;
import yuudaari.soulus.common.block.fossil.fossil_red_sand.FossilSandRedScale;
import yuudaari.soulus.common.block.fossil.fossil_sand.FossilSand;
import yuudaari.soulus.common.block.fossil.fossil_sand.FossilSandEnder;
import yuudaari.soulus.common.block.fossil.fossil_sand.FossilSandScale;
import yuudaari.soulus.common.util.IBlock;
import yuudaari.soulus.common.util.IModThing;
import yuudaari.soulus.common.util.IProvidesJeiDescription;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.oredict.OreDictionary;
import net.minecraftforge.registries.IForgeRegistry;
import yuudaari.soulus.common.block.*;
import yuudaari.soulus.common.block.composer.*;
import yuudaari.soulus.common.block.enderlink.Enderlink;
import yuudaari.soulus.common.block.summoner.Summoner;
import yuudaari.soulus.common.compat.JeiDescriptionRegistry;
import yuudaari.soulus.common.block.skewer.Skewer;
import yuudaari.soulus.common.block.soul_totem.SoulTotem;

public class ModBlocks {

	public static final AshBlock ASH = new AshBlock();
	public static final BarsEndersteel BARS_ENDERSTEEL = new BarsEndersteel();
	public static final BlockEndersteel BLOCK_ENDERSTEEL = new BlockEndersteel();
	public static final BlockEndersteelDark BLOCK_ENDERSTEEL_DARK = new BlockEndersteelDark();
	public static final BlockNiobium BLOCK_NIOBIUM = new BlockNiobium();
	public static final Composer COMPOSER = new Composer();
	public static final ComposerCell COMPOSER_CELL = new ComposerCell();
	public static final DustEnderBlock DUST_ENDER = new DustEnderBlock();
	public static final Enderlink ENDERLINK = new Enderlink();
	private static final FossilDirt FOSSIL_DIRT = new FossilDirt();
	private static final FossilDirtEnder FOSSIL_DIRT_ENDER = new FossilDirtEnder();
	private static final FossilDirtFrozen FOSSIL_DIRT_FROZEN = new FossilDirtFrozen();
	private static final FossilDirtFungal FOSSIL_DIRT_FUNGAL = new FossilDirtFungal();
	private static final FossilEndStone FOSSIL_END_STONE = new FossilEndStone();
	private static final FossilGravelScale FOSSIL_GRAVEL_SCALE = new FossilGravelScale();
	private static final FossilGravelFungal FOSSIL_GRAVEL_FUNGAL = new FossilGravelFungal();
	private static final FossilGravelDry FOSSIL_GRAVEL_DRY = new FossilGravelDry();
	private static final FossilNetherrack FOSSIL_NETHERRACK = new FossilNetherrack();
	private static final FossilNetherrackEnder FOSSIL_NETHERRACK_ENDER = new FossilNetherrackEnder();
	private static final FossilSand FOSSIL_SAND = new FossilSand();
	private static final FossilSandEnder FOSSIL_SAND_ENDER = new FossilSandEnder();
	private static final FossilSandScale FOSSIL_SAND_SCALE = new FossilSandScale();
	private static final FossilSandRed FOSSIL_SAND_RED = new FossilSandRed();
	private static final FossilSandRedScale FOSSIL_SAND_RED_SCALE = new FossilSandRedScale();
	private static final FossilIce FOSSIL_ICE = new FossilIce();
	private static final FossilIceEnder FOSSIL_ICE_ENDER = new FossilIceEnder();
	private static final FossilIceFrozen FOSSIL_ICE_FROZEN = new FossilIceFrozen();
	private static final FossilIceScale FOSSIL_ICE_SCALE = new FossilIceScale();
	public static final Skewer SKEWER = new Skewer();
	public static final Summoner SUMMONER = new Summoner();
	private static final Unloader UNLOADER = new Unloader();
	public static final SoulTotem SOUL_TOTEM = new SoulTotem();

	public static IBlock[] blocks = new IBlock[] {
		DUST_ENDER,

		FOSSIL_DIRT,
		FOSSIL_DIRT_FROZEN,
		FOSSIL_DIRT_FUNGAL,
		FOSSIL_DIRT_ENDER,

		FOSSIL_SAND,
		FOSSIL_SAND_SCALE,
		FOSSIL_SAND_ENDER,
		FOSSIL_SAND_RED,
		FOSSIL_SAND_RED_SCALE,

		FOSSIL_ICE,
		FOSSIL_ICE_ENDER,
		FOSSIL_ICE_FROZEN,
		FOSSIL_ICE_SCALE,

		FOSSIL_GRAVEL_SCALE,
		FOSSIL_GRAVEL_FUNGAL,
		FOSSIL_GRAVEL_DRY,

		FOSSIL_NETHERRACK,
		FOSSIL_NETHERRACK_ENDER,

		FOSSIL_END_STONE,

		ASH,

		BARS_ENDERSTEEL,

		BLOCK_ENDERSTEEL,
		BLOCK_ENDERSTEEL_DARK,
		BLOCK_NIOBIUM,

		SKEWER,

		UNLOADER,

		SUMMONER,

		COMPOSER,
		COMPOSER_CELL,

		ENDERLINK,

		SOUL_TOTEM
	};

	public static void registerBlocks (IForgeRegistry<Block> registry) {
		for (IBlock block : blocks) {
			registry.register((Block) block);
		}
	}

	public static void registerItems (IForgeRegistry<Item> registry) {
		for (IBlock block : blocks) {
			if (block.hasItem()) {
				for (ItemBlock item : block.getItemBlocks()) {
					registry.register(item);
					for (String dict : block.getOreDicts()) {
						OreDictionary.registerOre(dict, item);
					}
				}
			}

			Class<? extends TileEntity> te = block.getTileEntityClass();
			if (te != null) {
				GameRegistry.registerTileEntity(te, block.getRegistryName().toString());
			}
		}
	}

	@SideOnly(Side.CLIENT)
	public static void registerModels () {
		for (IBlock block : blocks) {
			if (block.hasItem()) {
				block.registerItemModel();
			}
		}
	}

	public static void registerRecipes (IForgeRegistry<IRecipe> registry) {
		for (IBlock block : blocks) {
			if (block instanceof IModThing)
				((IModThing) block).onRegisterRecipes(registry);
		}
	}

	public static void registerDescriptions (JeiDescriptionRegistry registry) {
		for (IBlock block : blocks) {
			if (block instanceof IProvidesJeiDescription)
				((IProvidesJeiDescription) block).onRegisterDescription(registry);
		}
	}
}
