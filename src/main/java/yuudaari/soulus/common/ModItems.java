package yuudaari.soulus.common;

import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.oredict.OreDictionary;
import net.minecraftforge.registries.IForgeRegistry;
import yuudaari.soulus.common.item.*;
import yuudaari.soulus.common.util.IModItem;
import yuudaari.soulus.common.util.ModItem;

public class ModItems {
	public static final BloodCrystal BLOOD_CRYSTAL = BloodCrystal.INSTANCE;
	public static final BoneChunkDry BONE_CHUNK_DRY = new BoneChunkDry();
	public static final BoneChunkEnder BONE_CHUNK_ENDER = new BoneChunkEnder();
	public static final BoneChunkFrozen BONE_CHUNK_FROZEN = new BoneChunkFrozen();
	public static final BoneChunkFungal BONE_CHUNK_FUNGAL = new BoneChunkFungal();
	public static final BoneChunkNether BONE_CHUNK_NETHER = new BoneChunkNether();
	public static final BoneChunkNormal BONE_CHUNK_NORMAL = new BoneChunkNormal();
	public static final BoneChunkScale BONE_CHUNK_SCALE = new BoneChunkScale();
	public static final BoneEnder BONE_ENDER = new BoneEnder();
	public static final Bonemeal BONEMEAL_NETHER = new Bonemeal("bone_meal_nether");
	public static final BonemealEnder BONEMEAL_ENDER = new BonemealEnder();
	public static final BoneNether BONE_NETHER = new BoneNether();
	public static final DustEnderIron DUST_ENDER_IRON = new DustEnderIron();
	public static final ModItem DUST_IRON = new ModItem("dust_iron").addOreDict("dustIron");
	public static final ModItem DUST_WOOD = new ModItem("dust_wood").addOreDict("dustWood");
	public static final ModItem DUST_STONE = new ModItem("dust_stone").addOreDict("dustStone");
	public static final Essence ESSENCE = new Essence();
	public static final GearBoneEnder GEAR_BONE_ENDER = new GearBoneEnder();
	public static final GearOscillating GEAR_OSCILLATING = new GearOscillating();
	public static final ModItem BARK = new ModItem("bark").setBurnTime(40);
	public static final ModItem BONE_DRY = new ModItem("bone_dry");
	public static final ModItem BONE_FROZEN = new ModItem("bone_frozen");
	public static final ModItem BONE_FUNGAL = new ModItem("bone_fungal");
	public static final ModItem BONE_SCALE = new ModItem("bone_scale");
	public static final ModItem EMERALD_BURNT = new ModItem("emerald_burnt");
	public static final ModItem EMERALD_COATED = new ModItem("emerald_coated");
	public static final ModItem BLOOD_CRYSTAL_BROKEN = new ModItem("blood_crystal_broken");
	public static final GearBone GEAR_BONE = new GearBone("gear_bone");
	public static final GearBone GEAR_BONE_DRY = new GearBone("gear_bone_dry");
	public static final GearBone GEAR_BONE_FROZEN = new GearBone("gear_bone_frozen");
	public static final GearBone GEAR_BONE_FUNGAL = new GearBone("gear_bone_fungal");
	public static final GearBone GEAR_BONE_NETHER = new GearBone("gear_bone_nether");
	public static final Glue GLUE = new Glue();
	public static final ModItem INGOT_ENDERSTEEL = new ModItem("ingot_endersteel");
	public static final NuggetEndersteel NUGGET_ENDERSTEEL = new NuggetEndersteel();
	public static final Sledgehammer SLEDGEHAMMER = new Sledgehammer();
	public static final Soulbook SOULBOOK = Soulbook.INSTANCE;
	public static final OrbMurky ORB_MURKY = OrbMurky.INSTANCE;
	public static final Barket BARKET = new Barket();

	public static Item[] items = new Item[] { INGOT_ENDERSTEEL, NUGGET_ENDERSTEEL, BARK, GLUE, ESSENCE, SLEDGEHAMMER,
			BARKET, BONE_DRY, BONE_FROZEN, BONE_FUNGAL, BONE_SCALE, BONE_ENDER, BONE_NETHER, BONE_CHUNK_NORMAL,
			BONE_CHUNK_DRY, BONE_CHUNK_FROZEN, BONE_CHUNK_FUNGAL, BONE_CHUNK_SCALE, BONE_CHUNK_ENDER, BONE_CHUNK_NETHER,
			BONEMEAL_NETHER, BONEMEAL_ENDER, DUST_IRON, DUST_WOOD, DUST_STONE, DUST_ENDER_IRON, GEAR_BONE_ENDER,
			GEAR_BONE, GEAR_BONE_NETHER, GEAR_BONE_DRY, GEAR_BONE_FROZEN, GEAR_BONE_FUNGAL, GEAR_OSCILLATING,
			EMERALD_BURNT, EMERALD_COATED, BLOOD_CRYSTAL_BROKEN, BLOOD_CRYSTAL, ORB_MURKY, SOULBOOK };

	public static void registerItems(IForgeRegistry<Item> registry) {
		OreDictionary.registerOre("bonemeal", new ItemStack(Items.DYE, 1, 15));

		for (Item item : items) {
			registry.register(item);
			if (item instanceof IModItem) {
				for (String dict : ((IModItem) item).getOreDicts()) {
					OreDictionary.registerOre(dict, item);
				}
			}
		}
	}

	@SideOnly(Side.CLIENT)
	public static void registerModels() {
		for (Item item : items) {
			if (item instanceof IModItem)
				((IModItem) item).registerModels();
		}
	}

	public static void registerRecipes(IForgeRegistry<IRecipe> registry) {
		for (Item item : items) {
			if (item instanceof ModItem)
				for (IRecipe recipe : ((ModItem) item).getRecipes())
					registry.register(recipe);
		}
	}
}