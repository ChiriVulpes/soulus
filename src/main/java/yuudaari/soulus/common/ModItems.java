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

	public static Barket BARKET = new Barket();
	public static BoneChunkDry BONE_CHUNK_DRY = new BoneChunkDry();
	public static BoneChunkEnder BONE_CHUNK_ENDER = new BoneChunkEnder();
	public static BoneChunkFrozen BONE_CHUNK_FROZEN = new BoneChunkFrozen();
	public static BoneChunkFungal BONE_CHUNK_FUNGAL = new BoneChunkFungal();
	public static BoneChunkNether BONE_CHUNK_NETHER = new BoneChunkNether();
	public static BoneChunkNormal BONE_CHUNK_NORMAL = new BoneChunkNormal();
	public static BoneChunkScale BONE_CHUNK_SCALE = new BoneChunkScale();
	public static BoneEnder BONE_ENDER = new BoneEnder();
	public static Bonemeal BONEMEAL_NETHER = new BonemealNether();
	public static BonemealEnder BONEMEAL_ENDER = new BonemealEnder();
	public static CrystalBlood CRYSTAL_BLOOD = new CrystalBlood();
	public static DustEnderIron DUST_ENDER_IRON = new DustEnderIron();
	public static Essence ESSENCE = Essence.INSTANCE;
	public static GearBone GEAR_BONE = new GearBone("gear_bone");
	public static GearBone GEAR_BONE_DRY = new GearBone("gear_bone_dry");
	public static GearBone GEAR_BONE_FROZEN = new GearBone("gear_bone_frozen");
	public static GearBone GEAR_BONE_FUNGAL = new GearBone("gear_bone_fungal");
	public static GearBone GEAR_BONE_NETHER = new GearBone("gear_bone_nether");
	public static GearBoneEnder GEAR_BONE_ENDER = new GearBoneEnder();
	public static GearOscillating GEAR_OSCILLATING = new GearOscillating();
	public static Glue GLUE = new Glue();
	public static ModItem ASH = new ModItem("ash").setHasDescription();
	public static ModItem BARK = new ModItem("bark").setBurnTime(40).setHasDescription();
	public static Bone BONE_DRY = new Bone("bone_dry");
	public static Bone BONE_FROZEN = new Bone("bone_frozen");
	public static Bone BONE_FUNGAL = new Bone("bone_fungal");
	public static ModItem BONE_NETHER = new ModItem("bone_nether").setHasDescription();
	public static Bone BONE_NORMAL = new Bone("bone_normal");
	public static Bone BONE_SCALE = new Bone("bone_scale");
	public static CrystalDark CRYSTAL_DARK = new CrystalDark();
	public static ModItem CRYSTAL_BLOOD_BROKEN = new ModItem("crystal_blood_broken").setHasDescription();
	public static ModItem DUST_ENDER_IRON_ASHEN = new ModItem("dust_ender_iron_ashen").setHasDescription();
	public static ModItem DUST_IRON = new ModItem("dust_iron").addOreDict("dustIron").setHasDescription();
	public static ModItem DUST_STONE = new ModItem("dust_stone").addOreDict("dustStone").setHasDescription();
	public static ModItem DUST_WOOD = new ModItem("dust_wood").addOreDict("dustWood").setHasDescription();
	public static ModItem EMERALD_BURNT = new ModItem("emerald_burnt").setHasDescription();
	public static ModItem EMERALD_COATED = new ModItem("emerald_coated").setHasDescription();
	public static ModItem INGOT_ENDERSTEEL = new ModItem("ingot_endersteel").setHasDescription();
	public static ModItem INGOT_ENDERSTEEL_DARK = new ModItem("ingot_endersteel_dark").setHasDescription();
	public static ModItem NUGGET_ENDERSTEEL_DARK = new ModItem("nugget_endersteel_dark").setHasDescription();
	public static NuggetEndersteel NUGGET_ENDERSTEEL = new NuggetEndersteel();
	public static OrbMurky ORB_MURKY = new OrbMurky();
	public static Sledgehammer SLEDGEHAMMER = new Sledgehammer();
	public static Soulbook SOULBOOK = Soulbook.INSTANCE;
	public static SoulCatalyst SOUL_CATALYST = new SoulCatalyst();

	public static Item[] items = new Item[] {
		// @formatter:off
		ASH,
		BARK,
		BARKET,
		BONE_CHUNK_DRY, 
		BONE_CHUNK_ENDER,
		BONE_CHUNK_FROZEN,
		BONE_CHUNK_FUNGAL,
		BONE_CHUNK_NETHER,
		BONE_CHUNK_NORMAL,
		BONE_CHUNK_SCALE,
		BONE_DRY,
		BONE_ENDER,
		BONE_FROZEN, 
		BONE_FUNGAL,
		BONE_NETHER, 
		BONE_NORMAL,
		BONE_SCALE,
		BONEMEAL_ENDER,
		BONEMEAL_NETHER,
		CRYSTAL_BLOOD_BROKEN,
		CRYSTAL_BLOOD,
		CRYSTAL_DARK,
		DUST_ENDER_IRON_ASHEN,
		DUST_ENDER_IRON, 
		DUST_IRON,
		DUST_STONE,
		DUST_WOOD,
		EMERALD_BURNT,
		EMERALD_COATED, 
		ESSENCE, 
		GEAR_BONE_DRY, 
		GEAR_BONE_ENDER,
		GEAR_BONE_FROZEN, 
		GEAR_BONE_FUNGAL,
		GEAR_BONE_NETHER, 
		GEAR_BONE, 
		GEAR_OSCILLATING,
		GLUE,
		INGOT_ENDERSTEEL_DARK,
		INGOT_ENDERSTEEL,
		NUGGET_ENDERSTEEL_DARK,
		NUGGET_ENDERSTEEL,
		ORB_MURKY,
		SLEDGEHAMMER,
		SOUL_CATALYST,
		SOULBOOK
		// @formatter:on
	};

	public static void registerItems (IForgeRegistry<Item> registry) {
		OreDictionary.registerOre("bonemeal", new ItemStack(Items.DYE, 1, 15));

		for (Item item : items) {
			registry.register(item);
			if (item instanceof IModThing) {
				for (String dict : ((IModThing) item).getOreDicts()) {
					OreDictionary.registerOre(dict, item);
				}
			}
		}
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
