package yuudaari.souls.common;

import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraft.item.crafting.IRecipe;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.oredict.OreDictionary;
import net.minecraftforge.registries.IForgeRegistry;
import yuudaari.souls.common.item.*;
import yuudaari.souls.common.util.ModItem;

public class ModItems {
	public static final BloodCrystal BLOOD_CRYSTAL = new BloodCrystal();
	public static final BoneChunkEnder BONE_CHUNK_ENDER = new BoneChunkEnder();
	public static final BoneChunkNether BONE_CHUNK_NETHER = new BoneChunkNether();
	public static final BoneChunkNormal BONE_CHUNK_NORMAL = new BoneChunkNormal();
	public static final BoneEnder BONE_ENDER = new BoneEnder();
	public static final BoneNether BONE_NETHER = new BoneNether();
	public static final ModItem BONE_MEAL_NETHER = new ModItem("bone_meal_nether");
	public static final DustEnder DUST_ENDER = new DustEnder();
	public static final DustEnderIron DUST_ENDER_IRON = new DustEnderIron();
	public static final DustIron DUST_IRON = new DustIron();
	public static final GearBoneEnder GEAR_BONE_ENDER = new GearBoneEnder();
	public static final GearOscillating GEAR_OSCILLATING = new GearOscillating();
	public static final ModItem EMERALD_BURNT = new ModItem("emerald_burnt");
	public static final ModItem EMERALD_COATED = new ModItem("emerald_coated");
	public static final Essence ESSENCE = new Essence();
	public static final ModItem GLUE = new Glue();
	public static final ModItem INGOT_ENDERSTEEL = new ModItem("ingot_endersteel");
	public static final ModItem NUGGET_ENDERSTEEL = new NuggetEndersteel();
	public static final OrbMurky ORB_MURKY = new OrbMurky();
	public static final ModItem SLEDGEHAMMER = new Sledgehammer();
	public static final ModItem SOULBOOK = new Soulbook();

	public static ModItem[] items = new ModItem[] { BLOOD_CRYSTAL, BONE_CHUNK_ENDER, BONE_CHUNK_NETHER,
			BONE_CHUNK_NORMAL, BONE_ENDER, BONE_NETHER, BONE_MEAL_NETHER, DUST_ENDER, DUST_ENDER_IRON, DUST_IRON,
			GEAR_BONE_ENDER, GEAR_OSCILLATING, EMERALD_BURNT, EMERALD_COATED, ESSENCE, GLUE, INGOT_ENDERSTEEL,
			NUGGET_ENDERSTEEL, ORB_MURKY, SLEDGEHAMMER, SOULBOOK };

	public static void registerItems(IForgeRegistry<Item> registry) {
		for (ModItem item : items) {
			registry.register(item);
			for (String dict : item.getOreDicts()) {
				OreDictionary.registerOre(dict, item);
			}
		}
	}

	public static void registerModels() {
		for (ModItem item : items) {
			ModelLoader.setCustomModelResourceLocation(item, 0,
					new ModelResourceLocation(item.getRegistryName(), "inventory"));
		}
	}

	public static void registerRecipes(IForgeRegistry<IRecipe> registry) {
		for (ModItem item : items) {
			for (IRecipe recipe : item.getRecipes()) {
				registry.register(recipe);
			}
		}
	}
}