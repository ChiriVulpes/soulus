package yuudaari.soulus.common.registration;

import java.util.List;
import com.google.common.collect.Lists;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.oredict.OreDictionary;
import net.minecraftforge.registries.IForgeRegistry;
import yuudaari.soulus.common.compat.jei.JeiDescriptionRegistry;
import yuudaari.soulus.common.item.*;
import yuudaari.soulus.common.registration.Registration.Item;

public class ItemRegistry {

	// @formatter:off
	public static Barket BARKET = new Barket();
	public static CrystalBlood CRYSTAL_BLOOD = new CrystalBlood();
	public static Item ASH = new Item("ash").setHasDescription();
	public static Item BARK = new Item("bark").setBurnTime(40).setHasDescription();
	public static BoneChunk BONE_CHUNK_DRY = new BoneChunk("bone_chunk_dry");
	public static BoneChunkEnder BONE_CHUNK_ENDER = new BoneChunkEnder();
	public static BoneChunk BONE_CHUNK_FROZEN = new BoneChunk("bone_chunk_frozen");
	public static BoneChunk BONE_CHUNK_FUNGAL = new BoneChunk("bone_chunk_fungal");
	public static BoneChunkNether BONE_CHUNK_NETHER = new BoneChunkNether();
	public static BoneChunk BONE_CHUNK_NORMAL = new BoneChunk("bone_chunk_normal");
	public static BoneChunk BONE_CHUNK_SCALE = new BoneChunk("bone_chunk_scale");
	public static Bone BONE_DRY = new Bone("bone_dry");
	public static BoneEnder BONE_ENDER = new BoneEnder();
	public static Bone BONE_FROZEN = new Bone("bone_frozen");
	public static Bone BONE_FUNGAL = new Bone("bone_fungal");
	public static Item BONE_NETHER = new Item("bone_nether").setHasDescription();
	public static Bone BONE_NORMAL = new Bone("bone_normal");
	public static Item BONE_SCALE = new Item("bone_scale").setHasDescription();
	public static BonemealEnder BONEMEAL_ENDER = new BonemealEnder();
	public static BonemealNether BONEMEAL_NETHER = new BonemealNether();
	public static Item CRYSTAL_BLOOD_BROKEN = new Item("crystal_blood_broken").setHasDescription();
	public static CrystalDark CRYSTAL_DARK = new CrystalDark();
	public static Item DUST_NIOBIUM = new Item("dust_niobium").addOreDict("dustSoulusNiobium").setHasGlint().setHasDescription();
	public static Item DUST_END_STONE = new Item("dust_end_stone").addOreDict("dustEndStone").setHasDescription();
	public static Item DUST_ENDER_IRON = new Item("dust_ender_iron").addOreDict("dustSoulusEndersteel").setHasGlint().setHasDescription();
	public static Item DUST_ENDER_IRON_ASHEN = new Item("dust_ender_iron_ashen").addOreDict("dustSoulusEndersteelDark").setHasDescription();
	public static Item DUST_IRON = new Item("dust_iron").addOreDict("dustIron").setHasDescription();
	public static Item DUST_GOLD = new Item("dust_gold").addOreDict("dustGold").setHasDescription();
	public static Item DUST_STONE = new Item("dust_stone").addOreDict("dustStone").setHasDescription();
	public static Item DUST_WOOD = new Item("dust_wood").addOreDict("dustWood").setHasDescription();
	public static Item EMERALD_BURNT = new Item("emerald_burnt").setHasDescription();
	public static Item EMERALD_COATED = new Item("emerald_coated").setHasDescription();
	public static Essence ESSENCE = new Essence();
	public static EssencePerfect ESSENCE_PERFECT = new EssencePerfect();
	public static GearBone GEAR_BONE = new GearBone("gear_bone");
	public static GearBone GEAR_BONE_DRY = new GearBone("gear_bone_dry");
	public static Item GEAR_BONE_ENDER = new Item("gear_bone_ender").setHasGlint().setHasDescription();
	public static GearBone GEAR_BONE_FROZEN = new GearBone("gear_bone_frozen");
	public static GearBone GEAR_BONE_FUNGAL = new GearBone("gear_bone_fungal");
	public static GearBone GEAR_BONE_NETHER = new GearBone("gear_bone_nether");
	public static GearOscillating GEAR_OSCILLATING = new GearOscillating();
	public static GearNiobium GEAR_NIOBIUM = new GearNiobium();
	public static Glue GLUE = new Glue();
	public static Item INGOT_ENDERSTEEL = new Item("ingot_endersteel").addOreDict("ingotSoulusEndersteel").setHasDescription();
	public static Item INGOT_ENDERSTEEL_DARK = new Item("ingot_endersteel_dark").addOreDict("ingotSoulusEndersteelDark").setHasDescription();
	public static Item INGOT_NIOBIUM = new Item("ingot_niobium").addOreDict("ingotSoulusNiobium").setHasGlint().setHasDescription();
	public static Item NUGGET_ENDERSTEEL = new Item("nugget_endersteel").addOreDict("nuggetSoulusEndersteel").setHasGlint().setHasDescription();
	public static Item NUGGET_ENDERSTEEL_DARK = new Item("nugget_endersteel_dark").addOreDict("nuggetSoulusEndersteelDark").setHasDescription();
	public static Item NUGGET_NIOBIUM = new Item("nugget_niobium").addOreDict("nuggetSoulusNiobium").setHasGlint().setHasDescription();
	public static Sledgehammer SLEDGEHAMMER = new Sledgehammer();
	public static Soulbook SOULBOOK = new Soulbook();
	public static OrbMurky ORB_MURKY = new OrbMurky();
	public static SoulCatalyst SOUL_CATALYST = new SoulCatalyst();
	public static DustMidnight DUST_MIDNIGHT = new DustMidnight();
	// @formatter:on

	public static List<IItemRegistration> items = Lists.newArrayList(new IItemRegistration[] {

		BARK,
		GLUE,

		BARKET,
		SLEDGEHAMMER,

		BONE_NORMAL,
		BONE_DRY,
		BONE_FUNGAL,
		BONE_FROZEN,
		BONE_SCALE,
		BONE_ENDER,
		BONE_NETHER,

		BONE_CHUNK_NORMAL,
		BONE_CHUNK_DRY,
		BONE_CHUNK_FUNGAL,
		BONE_CHUNK_FROZEN,
		BONE_CHUNK_SCALE,
		BONE_CHUNK_ENDER,
		BONE_CHUNK_NETHER,

		GEAR_BONE,
		GEAR_BONE_DRY,
		GEAR_BONE_FUNGAL,
		GEAR_BONE_FROZEN,
		GEAR_BONE_ENDER,
		GEAR_BONE_NETHER,

		BONEMEAL_ENDER,
		BONEMEAL_NETHER,

		DUST_WOOD,
		DUST_END_STONE,
		DUST_STONE,
		DUST_IRON,
		DUST_GOLD,
		DUST_ENDER_IRON,
		ASH,
		DUST_ENDER_IRON_ASHEN,
		DUST_MIDNIGHT,
		DUST_NIOBIUM,

		INGOT_ENDERSTEEL,
		NUGGET_ENDERSTEEL,
		INGOT_ENDERSTEEL_DARK,
		NUGGET_ENDERSTEEL_DARK,
		INGOT_NIOBIUM,
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

		ESSENCE_PERFECT,
		ESSENCE,

		SOULBOOK,
	});

	public static void registerItems (IForgeRegistry<net.minecraft.item.Item> registry) {
		for (IItemRegistration item : items) {
			registry.register(item.getItem());
			for (String dict : item.getOreDicts()) {
				OreDictionary.registerOre(dict, item.getItem());
			}
		}

		OreDictionary.registerOre("bonemeal", new ItemStack(Items.DYE, 1, 15));
	}

	@SideOnly(Side.CLIENT)
	public static void registerModels () {
		for (IItemRegistration item : items) {
			item.registerModels();
		}
	}

	public static void registerRecipes (IForgeRegistry<IRecipe> registry) {
		for (IItemRegistration item : items) {
			item.onRegisterRecipes(registry);
		}
	}

	public static void registerDescriptions (JeiDescriptionRegistry registry) {
		for (IItemRegistration item : items) {
			item.onRegisterDescription(registry);
		}
	}
}
