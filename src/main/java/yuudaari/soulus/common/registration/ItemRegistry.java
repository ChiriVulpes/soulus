package yuudaari.soulus.common.registration;

import java.util.List;
import com.google.common.collect.Lists;
import net.minecraft.entity.EntityList;
import net.minecraft.init.Items;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.oredict.OreDictionary;
import net.minecraftforge.registries.IForgeRegistry;
import yuudaari.soulus.Soulus;
import yuudaari.soulus.common.config.ConfigInjected;
import yuudaari.soulus.common.config.ConfigInjected.Inject;
import yuudaari.soulus.common.config.essence.ConfigEssences;
import yuudaari.soulus.common.item.Barket;
import yuudaari.soulus.common.item.Bone;
import yuudaari.soulus.common.item.BoneChunk;
import yuudaari.soulus.common.item.BoneChunkEnder;
import yuudaari.soulus.common.item.BoneChunkNether;
import yuudaari.soulus.common.item.BoneEnder;
import yuudaari.soulus.common.item.BoneNether;
import yuudaari.soulus.common.item.BoneScale;
import yuudaari.soulus.common.item.Bonemeal;
import yuudaari.soulus.common.item.BonemealEnder;
import yuudaari.soulus.common.item.BonemealNether;
import yuudaari.soulus.common.item.CrystalBlood;
import yuudaari.soulus.common.item.CrystalDark;
import yuudaari.soulus.common.item.DustMidnight;
import yuudaari.soulus.common.item.EmeraldBloody;
import yuudaari.soulus.common.item.Essence;
import yuudaari.soulus.common.item.EssencePerfect;
import yuudaari.soulus.common.item.GearBone;
import yuudaari.soulus.common.item.GearNiobium;
import yuudaari.soulus.common.item.GearOscillating;
import yuudaari.soulus.common.item.Glue;
import yuudaari.soulus.common.item.OrbMurky;
import yuudaari.soulus.common.item.Sledgehammer;
import yuudaari.soulus.common.item.SoulCatalyst;
import yuudaari.soulus.common.item.Soulbook;
import yuudaari.soulus.common.registration.Registration.Item;

@Mod.EventBusSubscriber(modid = Soulus.MODID)
@ConfigInjected(Soulus.MODID)
public class ItemRegistry {

	@Inject public static ConfigEssences CONFIG_ESSENCES;

	public static Barket BARKET = new Barket();
	public static CrystalBlood CRYSTAL_BLOOD = new CrystalBlood();
	public static Item ASH = new Item("ash").setHasDescription().setRarity(EnumRarity.UNCOMMON);
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
	public static Item BONE_NETHER = new BoneNether();
	public static Bone BONE_NORMAL = new Bone("bone_normal");
	public static Item BONE_SCALE = new BoneScale();
	public static BonemealEnder BONEMEAL_ENDER = new BonemealEnder();
	public static BonemealNether BONEMEAL_NETHER = new BonemealNether();
	public static Item CRYSTAL_BLOOD_BROKEN = new Item("crystal_blood_broken").setHasDescription();
	public static CrystalDark CRYSTAL_DARK = new CrystalDark();
	public static Item CRYSTAL_DARK_BROKEN = new Item("crystal_dark_broken").setRarity(EnumRarity.UNCOMMON).setHasDescription();
	public static Item DUST_NIOBIUM = new Item("dust_niobium").addOreDict("dustSoulusNiobium").setHasGlint().setHasDescription().setRarity(EnumRarity.EPIC);
	public static Item DUST_END_STONE = new Item("dust_end_stone").addOreDict("dustEndStone").setHasDescription();
	public static Item DUST_ENDER_IRON = new Item("dust_ender_iron").addOreDict("dustSoulusEndersteel").setHasGlint().setHasDescription().setRarity(EnumRarity.UNCOMMON);
	public static Item DUST_ENDER_IRON_ASHEN = new Item("dust_ender_iron_ashen").addOreDict("dustSoulusEndersteelDark").setHasDescription().setRarity(EnumRarity.UNCOMMON);
	public static Item DUST_IRON = new Item("dust_iron").addOreDict("dustIron").setHasDescription();
	public static Item DUST_GOLD = new Item("dust_gold").addOreDict("dustGold").setHasDescription();
	public static Item DUST_STONE = new Item("dust_stone").addOreDict("dustStone").setHasDescription();
	public static Item DUST_WOOD = new Item("dust_wood").addOreDict("dustWood").setHasDescription();
	public static Item EMERALD_BURNT = new Item("emerald_burnt").setHasDescription();
	public static Item EMERALD_COATED = new EmeraldBloody();
	public static Essence ESSENCE = new Essence();
	public static EssencePerfect ESSENCE_PERFECT = new EssencePerfect();
	public static GearBone GEAR_BONE = new GearBone("gear_bone");
	public static GearBone GEAR_BONE_DRY = new GearBone("gear_bone_dry");
	public static Item GEAR_BONE_ENDER = new Item("gear_bone_ender").setHasGlint().setHasDescription().setRarity(EnumRarity.UNCOMMON);
	public static GearBone GEAR_BONE_FROZEN = new GearBone("gear_bone_frozen");
	public static GearBone GEAR_BONE_FUNGAL = new GearBone("gear_bone_fungal");
	public static GearBone GEAR_BONE_NETHER = new GearBone("gear_bone_nether");
	public static GearOscillating GEAR_OSCILLATING = new GearOscillating();
	public static GearNiobium GEAR_NIOBIUM = new GearNiobium();
	public static Glue GLUE = new Glue();
	public static Item INGOT_ENDERSTEEL = new Item("ingot_endersteel").addOreDict("ingotSoulusEndersteel").setHasDescription().setRarity(EnumRarity.UNCOMMON);
	public static Item INGOT_ENDERSTEEL_DARK = new Item("ingot_endersteel_dark").addOreDict("ingotSoulusEndersteelDark").setHasDescription().setRarity(EnumRarity.RARE);
	public static Item INGOT_NIOBIUM = new Item("ingot_niobium").addOreDict("ingotSoulusNiobium").setHasGlint().setHasDescription().setRarity(EnumRarity.EPIC);
	public static Item NUGGET_ENDERSTEEL = new Item("nugget_endersteel").addOreDict("nuggetSoulusEndersteel").setHasGlint().setHasDescription().setRarity(EnumRarity.UNCOMMON);
	public static Item NUGGET_ENDERSTEEL_DARK = new Item("nugget_endersteel_dark").addOreDict("nuggetSoulusEndersteelDark").setHasDescription().setRarity(EnumRarity.RARE);
	public static Item NUGGET_NIOBIUM = new Item("nugget_niobium").addOreDict("nuggetSoulusNiobium").setHasGlint().setHasDescription().setRarity(EnumRarity.EPIC);
	public static Sledgehammer SLEDGEHAMMER = new Sledgehammer(Sledgehammer.Tier.NORMAL);
	public static Sledgehammer SLEDGEHAMMER_ENDERSTEEL = new Sledgehammer(Sledgehammer.Tier.ENDERSTEEL);
	public static Sledgehammer SLEDGEHAMMER_ENDERSTEEL_DARK = new Sledgehammer(Sledgehammer.Tier.ENDERSTEEL_DARK);
	public static Sledgehammer SLEDGEHAMMER_NIOBIUM = new Sledgehammer(Sledgehammer.Tier.NIOBIUM);
	public static Soulbook SOULBOOK = new Soulbook();
	public static OrbMurky ORB_MURKY = new OrbMurky();
	public static SoulCatalyst SOUL_CATALYST = new SoulCatalyst();
	public static DustMidnight DUST_MIDNIGHT = new DustMidnight();

	public static List<IItemRegistration> items = Lists.newArrayList(new IItemRegistration[] {

		BARK,
		GLUE,

		BARKET,

		SLEDGEHAMMER,
		SLEDGEHAMMER_ENDERSTEEL,
		SLEDGEHAMMER_ENDERSTEEL_DARK,
		SLEDGEHAMMER_NIOBIUM,

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
		CRYSTAL_DARK_BROKEN,

		SOUL_CATALYST,

		ESSENCE_PERFECT,
		ESSENCE,

		SOULBOOK,
	});

	@SubscribeEvent
	@SideOnly(Side.CLIENT)
	public static void registerModels (ModelRegistryEvent event) {
		for (IItemRegistration item : items)
			item.registerModels();
	}

	@SubscribeEvent
	public static void registerRecipes (final RegistryEvent.Register<IRecipe> event) {
		for (IItemRegistration item : items)
			item.onRegisterRecipes(event.getRegistry());
	}

	@SubscribeEvent
	public static void registerItems (final RegistryEvent.Register<net.minecraft.item.Item> event) {
		final IForgeRegistry<net.minecraft.item.Item> registry = event.getRegistry();

		for (IItemRegistration item : items) {
			registry.register(item.getItem());
			item.getOreDicts()
				.forEach(dict -> OreDictionary.registerOre(dict.getKey(), dict.getValue()));
		}

		// add bonemeal ore dictionary
		OreDictionary.registerOre(Bonemeal.ORE_DICT, new ItemStack(Items.DYE, 1, 15));

		// create eggs for all essence types
		CONFIG_ESSENCES.getEssences()
			.filter(essence -> essence.colors != null && !EntityList.ENTITY_EGGS.containsKey(new ResourceLocation(essence.essence)))
			.forEach(essence -> EntityList.addSpawnInfo(essence.essence, essence.colors.primary, essence.colors.secondary));
	}
}
