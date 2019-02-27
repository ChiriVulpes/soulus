package yuudaari.soulus.common.registration;

import java.util.List;
import com.google.common.collect.Lists;
import net.minecraft.block.Block;
import net.minecraft.block.material.MapColor;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.oredict.OreDictionary;
import net.minecraftforge.registries.IForgeRegistry;
import yuudaari.soulus.common.block.AshBlock;
import yuudaari.soulus.common.block.BarsEndersteel;
import yuudaari.soulus.common.block.BlockEndersteel;
import yuudaari.soulus.common.block.BlockEndersteelDark;
import yuudaari.soulus.common.block.BlockNiobium;
import yuudaari.soulus.common.block.DustEnderBlock;
import yuudaari.soulus.common.block.Unloader;
import yuudaari.soulus.common.block.composer.Composer;
import yuudaari.soulus.common.block.composer.ComposerCell;
import yuudaari.soulus.common.block.enderlink.Enderlink;
import yuudaari.soulus.common.block.fossil.FossilDirt;
import yuudaari.soulus.common.block.fossil.FossilEndStone;
import yuudaari.soulus.common.block.fossil.FossilGravel;
import yuudaari.soulus.common.block.fossil.FossilNetherrack;
import yuudaari.soulus.common.block.fossil.FossilSand;
import yuudaari.soulus.common.block.fossil.FossilSandRed;
import yuudaari.soulus.common.block.skewer.Skewer;
import yuudaari.soulus.common.block.soul_inquirer.SoulInquirer;
import yuudaari.soulus.common.block.soul_totem.SoulTotem;
import yuudaari.soulus.common.block.summoner.Summoner;
import yuudaari.soulus.common.compat.jei.JeiDescriptionRegistry;
import yuudaari.soulus.common.registration.IBlockRegistration;
import yuudaari.soulus.common.util.Material;

public class BlockRegistry {

	public static final AshBlock ASH = new AshBlock();
	public static final BarsEndersteel BARS_ENDERSTEEL = new BarsEndersteel();
	public static final BlockEndersteel BLOCK_ENDERSTEEL = new BlockEndersteel();
	public static final BlockEndersteelDark BLOCK_ENDERSTEEL_DARK = new BlockEndersteelDark();
	public static final BlockNiobium BLOCK_NIOBIUM = new BlockNiobium();
	public static final Composer COMPOSER = new Composer();
	public static final ComposerCell COMPOSER_CELL = new ComposerCell();
	public static final DustEnderBlock DUST_ENDER = new DustEnderBlock();
	public static final Enderlink ENDERLINK = new Enderlink();
	public static final FossilDirt FOSSIL_DIRT = new FossilDirt();
	public static final FossilDirt FOSSIL_DIRT_ENDER = new FossilDirt("fossil_dirt_ender");
	public static final FossilDirt FOSSIL_DIRT_FROZEN = new FossilDirt("fossil_dirt_frozen");
	public static final FossilDirt FOSSIL_DIRT_FUNGAL = new FossilDirt("fossil_dirt_fungal");
	public static final FossilEndStone FOSSIL_END_STONE = new FossilEndStone();
	public static final FossilGravel FOSSIL_GRAVEL = new FossilGravel();
	public static final FossilNetherrack FOSSIL_NETHERRACK = new FossilNetherrack();
	public static final FossilNetherrack FOSSIL_NETHERRACK_ENDER = new FossilNetherrack("fossil_netherrack_ender");
	public static final FossilSand FOSSIL_SAND = new FossilSand();
	public static final FossilSand FOSSIL_SAND_ENDER = new FossilSand("fossil_sand_ender");
	public static final FossilSand FOSSIL_SAND_SCALE = new FossilSand("fossil_sand_scale");
	public static final FossilSandRed FOSSIL_SAND_RED = new FossilSandRed();
	public static final FossilSandRed FOSSIL_SAND_RED_ENDER = new FossilSandRed("fossil_sand_red_ender");
	public static final FossilSandRed FOSSIL_SAND_RED_SCALE = new FossilSandRed("fossil_sand_red_scale");
	public static final Skewer SKEWER = new Skewer();
	public static final Summoner SUMMONER = new Summoner();
	public static final Unloader UNLOADER = new Unloader();
	public static final SoulTotem SOUL_TOTEM = new SoulTotem();
	public static final SoulInquirer SOUL_INQUIRER = new SoulInquirer();
	public static final Registration.Block MIDNIGHT_BRICKS = new Registration.Block("midnight_bricks", new Material(MapColor.BLACK)).setHasItem();
	public static final Registration.BlockPillar MIDNIGHT_PILLAR = new Registration.BlockPillar("midnight_pillar", new Material(MapColor.BLACK)).setHasItem();

	public static List<IBlockRegistration> blocks = Lists.newArrayList(new IBlockRegistration[] {
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
		FOSSIL_SAND_RED_ENDER,

		FOSSIL_GRAVEL,

		FOSSIL_NETHERRACK,
		FOSSIL_NETHERRACK_ENDER,

		FOSSIL_END_STONE,


		ASH,

		BARS_ENDERSTEEL,

		BLOCK_ENDERSTEEL,
		BLOCK_ENDERSTEEL_DARK,
		BLOCK_NIOBIUM,

		MIDNIGHT_BRICKS,
		MIDNIGHT_PILLAR,

		SKEWER,

		UNLOADER,

		SUMMONER,

		COMPOSER,
		COMPOSER_CELL,

		ENDERLINK,

		SOUL_INQUIRER,

		SOUL_TOTEM
	});

	public static void registerBlocks (IForgeRegistry<Block> registry) {
		for (IBlockRegistration block : blocks) {
			registry.register((Block) block);
		}
	}

	public static void registerItems (IForgeRegistry<Item> registry) {
		for (IBlockRegistration block : blocks) {
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
				GameRegistry.registerTileEntity(te, block.getRegistryName());
			}
		}
	}

	@SideOnly(Side.CLIENT)
	public static void registerModels () {
		for (IBlockRegistration block : blocks) {
			if (block.hasItem()) {
				block.registerItemModel();
			}
		}
	}

	public static void registerRecipes (IForgeRegistry<IRecipe> registry) {
		for (IBlockRegistration block : blocks) {
			block.onRegisterRecipes(registry);
		}
	}

	public static void registerDescriptions (JeiDescriptionRegistry registry) {
		for (IBlockRegistration block : blocks) {
			block.onRegisterDescription(registry);
		}
	}
}
