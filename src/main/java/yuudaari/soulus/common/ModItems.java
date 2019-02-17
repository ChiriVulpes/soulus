package yuudaari.soulus.common;

import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.oredict.OreDictionary;
import net.minecraftforge.registries.IForgeRegistry;
import yuudaari.soulus.common.compat.JeiDescriptionRegistry;
import yuudaari.soulus.common.item.*;
import yuudaari.soulus.common.util.IModThing;
import yuudaari.soulus.common.util.IProvidesJeiDescription;
import yuudaari.soulus.common.util.ModItem;

public class ModItems {

	// @formatter:off
	public static Barket BARKET = new Barket();
	public static CrystalBlood CRYSTAL_BLOOD = new CrystalBlood();
	public static ModItem ASH = new ModItem("ash").setHasDescription();
	public static ModItem BARK = new ModItem("bark").setBurnTime(40).setHasDescription();
	public static ModItem BONE_CHUNK_DRY = new BoneChunk("bone_chunk_dry");
	public static ModItem BONE_CHUNK_ENDER = new BoneChunkEnder();
	public static ModItem BONE_CHUNK_FROZEN = new BoneChunk("bone_chunk_frozen");
	public static ModItem BONE_CHUNK_FUNGAL = new BoneChunk("bone_chunk_fungal");
	public static ModItem BONE_CHUNK_NETHER = new BoneChunkNether();
	public static ModItem BONE_CHUNK_NORMAL = new BoneChunk("bone_chunk_normal");
	public static ModItem BONE_CHUNK_SCALE = new BoneChunk("bone_chunk_scale");
	public static ModItem BONE_DRY = new Bone("bone_dry");
	public static ModItem BONE_ENDER = new BoneEnder();
	public static ModItem BONE_FROZEN = new Bone("bone_frozen");
	public static ModItem BONE_FUNGAL = new Bone("bone_fungal");
	public static ModItem BONE_NETHER = new ModItem("bone_nether").setHasDescription();
	public static ModItem BONE_NORMAL = new Bone("bone_normal");
	public static ModItem BONE_SCALE = new Bone("bone_scale").removeOreDict("bone");
	public static ModItem BONEMEAL_ENDER = new BonemealEnder();
	public static ModItem BONEMEAL_NETHER = new BonemealNether();
	public static ModItem CRYSTAL_BLOOD_BROKEN = new ModItem("crystal_blood_broken").setHasDescription();
	public static ModItem CRYSTAL_DARK = new CrystalDark();
	public static ModItem DUST_NIOBIUM = new ModItem("dust_niobium").setHasGlint().setHasDescription();
	public static ModItem DUST_ENDER_IRON = new ModItem("dust_ender_iron").setHasGlint().setHasDescription();
	public static ModItem DUST_ENDER_IRON_ASHEN = new ModItem("dust_ender_iron_ashen").setHasDescription();
	public static ModItem DUST_IRON = new ModItem("dust_iron").addOreDict("dustIron").setHasDescription();
	public static ModItem DUST_GOLD = new ModItem("dust_gold").addOreDict("dustGold").setHasDescription();
	public static ModItem DUST_STONE = new ModItem("dust_stone").addOreDict("dustStone").setHasDescription();
	public static ModItem DUST_WOOD = new ModItem("dust_wood").addOreDict("dustWood").setHasDescription();
	public static ModItem EMERALD_BURNT = new ModItem("emerald_burnt").setHasDescription();
	public static ModItem EMERALD_COATED = new ModItem("emerald_coated").setHasDescription();
	public static ModItem ESSENCE = new Essence();
	public static ModItem ESSENCE_PERFECT = new EssencePerfect();
	public static ModItem GEAR_BONE = new GearBone("gear_bone");
	public static ModItem GEAR_BONE_DRY = new GearBone("gear_bone_dry");
	public static ModItem GEAR_BONE_ENDER = new GearBoneEnder();
	public static ModItem GEAR_BONE_FROZEN = new GearBone("gear_bone_frozen");
	public static ModItem GEAR_BONE_FUNGAL = new GearBone("gear_bone_fungal");
	public static ModItem GEAR_BONE_NETHER = new GearBone("gear_bone_nether");
	public static ModItem GEAR_OSCILLATING = (ModItem) new ModItem("gear_oscillating").setHasGlint().setHasDescription().setMaxStackSize(16);
	public static ModItem GEAR_NIOBIUM = (ModItem) new ModItem("gear_niobium").setHasGlint().setHasDescription().setMaxStackSize(16);
	public static ModItem GLUE = new Glue();
	public static ModItem INGOT_ENDERSTEEL = new ModItem("ingot_endersteel").setHasDescription();
	public static ModItem INGOT_ENDERSTEEL_DARK = new ModItem("ingot_endersteel_dark").setHasDescription();
	public static ModItem INGOT_NIOBIUM = new ModItem("ingot_niobium").setHasGlint().setHasDescription();
	public static ModItem NUGGET_ENDERSTEEL = new ModItem("nugget_endersteel").setHasGlint().setHasDescription();
	public static ModItem NUGGET_ENDERSTEEL_DARK = new ModItem("nugget_endersteel_dark").setHasDescription();
	public static ModItem NUGGET_NIOBIUM = new ModItem("nugget_niobium").setHasGlint().setHasDescription();
	public static ModItem SLEDGEHAMMER = new Sledgehammer();
	public static ModItem SOULBOOK = new Soulbook();
	public static OrbMurky ORB_MURKY = new OrbMurky();
	public static SoulCatalyst SOUL_CATALYST = new SoulCatalyst();
	// @formatter:on

	public static Item[] items = new Item[] {

		BARK,
		GLUE,
		ASH,

		BARKET,
		SLEDGEHAMMER,

		BONE_DRY,
		BONE_ENDER,
		BONE_FROZEN,
		BONE_FUNGAL,
		BONE_NETHER,
		BONE_NORMAL,
		BONE_SCALE,

		BONE_CHUNK_DRY,
		BONE_CHUNK_ENDER,
		BONE_CHUNK_FROZEN,
		BONE_CHUNK_FUNGAL,
		BONE_CHUNK_NETHER,
		BONE_CHUNK_NORMAL,
		BONE_CHUNK_SCALE,

		GEAR_BONE,
		GEAR_BONE_DRY,
		GEAR_BONE_ENDER,
		GEAR_BONE_FROZEN,
		GEAR_BONE_FUNGAL,
		GEAR_BONE_NETHER,

		BONEMEAL_ENDER,
		BONEMEAL_NETHER,

		DUST_ENDER_IRON_ASHEN,
		DUST_ENDER_IRON,
		DUST_IRON,
		DUST_GOLD,
		DUST_STONE,
		DUST_WOOD,

		DUST_NIOBIUM,

		INGOT_ENDERSTEEL_DARK,
		INGOT_ENDERSTEEL,
		INGOT_NIOBIUM,
		NUGGET_ENDERSTEEL_DARK,
		NUGGET_ENDERSTEEL,
		NUGGET_NIOBIUM,

		EMERALD_BURNT,
		EMERALD_COATED,
		CRYSTAL_BLOOD,
		CRYSTAL_BLOOD_BROKEN,

		GEAR_OSCILLATING,
		GEAR_NIOBIUM,

		ORB_MURKY,

		CRYSTAL_DARK,
		SOUL_CATALYST,

		ESSENCE,
		ESSENCE_PERFECT,

		SOULBOOK
	};

	public static void registerItems (IForgeRegistry<Item> registry) {
		for (Item item : items) {
			registry.register(item);
			if (item instanceof IModThing) {
				for (String dict : ((IModThing) item).getOreDicts()) {
					OreDictionary.registerOre(dict, item);
				}
			}
		}

		OreDictionary.registerOre("bonemeal", new ItemStack(Items.DYE, 1, 15));
	}

	@SideOnly(Side.CLIENT)
	public static void registerModels () {
		for (Item item : items) {
			if (item instanceof IModThing)
				((IModThing) item).registerModels();
		}
	}

	public static void registerRecipes (IForgeRegistry<IRecipe> registry) {
		for (Item item : items) {
			if (item instanceof IModThing)
				((IModThing) item).onRegisterRecipes(registry);
		}
	}

	public static void registerDescriptions (JeiDescriptionRegistry registry) {
		for (Item item : items) {
			if (item instanceof IProvidesJeiDescription)
				((IProvidesJeiDescription) item).onRegisterDescription(registry);
		}
	}
}
