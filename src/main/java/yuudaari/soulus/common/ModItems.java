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
import yuudaari.soulus.common.util.IModThing;
import yuudaari.soulus.common.util.ModItem;

public class ModItems {
	public static final Barket BARKET = new Barket();
	public static final BloodCrystal BLOOD_CRYSTAL = BloodCrystal.INSTANCE;
	public static final BoneChunkDry BONE_CHUNK_DRY = new BoneChunkDry();
	public static final BoneChunkEnder BONE_CHUNK_ENDER = new BoneChunkEnder();
	public static final BoneChunkFrozen BONE_CHUNK_FROZEN = new BoneChunkFrozen();
	public static final BoneChunkFungal BONE_CHUNK_FUNGAL = new BoneChunkFungal();
	public static final BoneChunkNether BONE_CHUNK_NETHER = new BoneChunkNether();
	public static final BoneChunkNormal BONE_CHUNK_NORMAL = new BoneChunkNormal();
	public static final BoneChunkScale BONE_CHUNK_SCALE = new BoneChunkScale();
	public static final BoneEnder BONE_ENDER = new BoneEnder();
	public static final Bonemeal BONEMEAL_NETHER = BonemealNether.INSTANCE;
	public static final BonemealEnder BONEMEAL_ENDER = new BonemealEnder();
	public static final BoneNether BONE_NETHER = new BoneNether();
	public static final DustEnderIron DUST_ENDER_IRON = new DustEnderIron();
	public static final Essence ESSENCE = new Essence();
	public static final GearBone GEAR_BONE = new GearBone("gear_bone");
	public static final GearBone GEAR_BONE_DRY = new GearBone("gear_bone_dry");
	public static final GearBone GEAR_BONE_FROZEN = new GearBone("gear_bone_frozen");
	public static final GearBone GEAR_BONE_FUNGAL = new GearBone("gear_bone_fungal");
	public static final GearBone GEAR_BONE_NETHER = new GearBone("gear_bone_nether");
	public static final GearBoneEnder GEAR_BONE_ENDER = new GearBoneEnder();
	public static final GearOscillating GEAR_OSCILLATING = new GearOscillating();
	public static final Glue GLUE = new Glue();
	public static final ModItem ASH = new ModItem("ash");
	public static final ModItem BARK = new ModItem("bark").setBurnTime(40);
	public static final ModItem BLOOD_CRYSTAL_BROKEN = new ModItem("blood_crystal_broken");
	public static final ModItem BONE_DRY = new ModItem("bone_dry");
	public static final ModItem BONE_FROZEN = new ModItem("bone_frozen");
	public static final ModItem BONE_FUNGAL = new ModItem("bone_fungal");
	public static final ModItem BONE_NORMAL = new ModItem("bone_normal");
	public static final ModItem BONE_SCALE = new ModItem("bone_scale");
	public static final ModItem DUST_IRON = new ModItem("dust_iron").addOreDict("dustIron");
	public static final ModItem DUST_STONE = new ModItem("dust_stone").addOreDict("dustStone");
	public static final ModItem DUST_WOOD = new ModItem("dust_wood").addOreDict("dustWood");
	public static final ModItem EMERALD_BURNT = new ModItem("emerald_burnt");
	public static final ModItem EMERALD_COATED = new ModItem("emerald_coated");
	public static final ModItem INGOT_ENDERSTEEL = new ModItem("ingot_endersteel");
	public static final NuggetEndersteel NUGGET_ENDERSTEEL = new NuggetEndersteel();
	public static final OrbMurky ORB_MURKY = OrbMurky.INSTANCE;
	public static final Sledgehammer SLEDGEHAMMER = new Sledgehammer();
	public static final Soulbook SOULBOOK = Soulbook.INSTANCE;

	public static Item[] items = new Item[] { //
			ASH, //
			BARK, // 
			BARKET, //  
			BLOOD_CRYSTAL_BROKEN, // 
			BLOOD_CRYSTAL, // 
			BONE_CHUNK_DRY, // 
			BONE_CHUNK_ENDER, // 
			BONE_CHUNK_FROZEN, // 
			BONE_CHUNK_FUNGAL, // 
			BONE_CHUNK_NETHER, // 
			BONE_CHUNK_NORMAL, // 
			BONE_CHUNK_SCALE, //
			BONE_DRY, //  
			BONE_ENDER, //  
			BONE_FROZEN, //  
			BONE_FUNGAL, //  
			BONE_NETHER, //  
			BONE_NORMAL, // 
			BONE_SCALE, //  
			BONEMEAL_ENDER, // 
			BONEMEAL_NETHER, // 
			DUST_ENDER_IRON, // 
			DUST_IRON, // 
			DUST_STONE, // 
			DUST_WOOD, // 
			EMERALD_BURNT, //  
			EMERALD_COATED, // 
			ESSENCE, //  
			GEAR_BONE_DRY, // 
			GEAR_BONE_ENDER, // 
			GEAR_BONE_FROZEN, // 
			GEAR_BONE_FUNGAL, // 
			GEAR_BONE_NETHER, // 
			GEAR_BONE, // 
			GEAR_OSCILLATING, // 
			GLUE, //  
			INGOT_ENDERSTEEL, // 
			NUGGET_ENDERSTEEL, //
			ORB_MURKY, // 
			SLEDGEHAMMER, // 
			SOULBOOK //
	};

	public static void registerItems(IForgeRegistry<Item> registry) {
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
	public static void registerModels() {
		for (Item item : items) {
			if (item instanceof IModThing)
				((IModThing) item).registerModels();
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