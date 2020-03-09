package yuudaari.soulus.common.registration;

import java.util.AbstractMap;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;
import net.minecraft.block.Block;
import net.minecraft.block.BlockDispenser;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.dispenser.IBlockSource;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.IForgeRegistryEntry;
import yuudaari.soulus.Soulus;
import yuudaari.soulus.common.CreativeTab;
import yuudaari.soulus.common.compat.jei.IProvidesJeiDescription;
import yuudaari.soulus.common.compat.jei.JeiDescriptionRegistry;
import yuudaari.soulus.common.registration.item.DispenserBehavior;
import yuudaari.soulus.common.registration.item.DispenserBehavior.IDispense;

@SuppressWarnings("unchecked")
public interface IRegistration<T extends IForgeRegistryEntry<T>> extends IProvidesJeiDescription, IForgeRegistryEntry<T>, IDispense {

	default void initialize () {
		try {
			if (this.getClass().getMethod("onDispense", IBlockSource.class, ItemStack.class, BlockPos.class).getDeclaringClass() != IRegistration.class) {
				BlockDispenser.DISPENSE_BEHAVIOR_REGISTRY.putObject(getItem(), new DispenserBehavior(this));
			}
		} catch (final NoSuchMethodException e) {
		}
	}

	////////////////////////////////////
	// Misc
	//

	abstract T setCreativeTab (final CreativeTabs tab);

	////////////////////////////////////
	// Name
	//

	abstract T setRegistryName (final String mod, final String name);

	abstract T setRegistryName (final String name);

	abstract T setRegistryName (final ResourceLocation name);

	abstract ResourceLocation getRegistryName ();

	abstract T setUnlocalizedName (final String name);

	default T setName (final String name) {
		setRegistryName(Soulus.MODID, name);
		setUnlocalizedName(getRegistryName().toString());
		setCreativeTab(CreativeTab.INSTANCE);
		return (T) this;
	}

	////////////////////////////////////
	// Ore Dictionary
	//

	// I would make this private if I could
	public static final Map<IRegistration<?>, Map<String, ItemStack>> ORE_DICTS = new HashMap<>();

	default T addOreDict (final String... dictionaries) {
		return addOreDict(ItemStack.EMPTY, dictionaries);
	}

	default T addOreDict (final ItemStack stack, final String... dictionaries) {
		Map<String, ItemStack> oreDicts = ORE_DICTS.get(this);
		if (oreDicts == null) {
			oreDicts = new HashMap<>();
			ORE_DICTS.put(this, oreDicts);
		}

		for (final String dictionary : dictionaries)
			oreDicts.put(dictionary, stack);

		return (T) this;
	}

	default T removeOreDict (final String... dictionaries) {
		Map<String, ItemStack> oreDicts = ORE_DICTS.get(this);
		if (oreDicts == null) {
			oreDicts = new HashMap<>();
			ORE_DICTS.put(this, oreDicts);
		}

		for (final String dictionary : dictionaries)
			oreDicts.remove(dictionary);

		return (T) this;
	}

	default Stream<Map.Entry<String, ItemStack>> getOreDicts () {
		Map<String, ItemStack> oreDicts = ORE_DICTS.get(this);
		if (oreDicts == null) {
			oreDicts = new HashMap<>();
			ORE_DICTS.put(this, oreDicts);
		}

		return oreDicts.entrySet()
			.stream()
			.map(entry -> new AbstractMap.SimpleEntry<>(entry.getKey(),
				// if the item stack registered for this ore dictionary is empty, we use this registration's default item stack
				entry.getValue().isEmpty() ? getItemStack() : entry.getValue()));
	}

	////////////////////////////////////
	// Descriptions
	//

	// I would make this private if I could
	public static final Set<IRegistration<?>> REGISTRATIONS_WITH_DESCRIPTIONS = new HashSet<>();

	default T setHasDescription () {
		REGISTRATIONS_WITH_DESCRIPTIONS.add(this);
		return (T) this;
	}

	@Override
	default void onRegisterDescription (JeiDescriptionRegistry registry) {
		if (!REGISTRATIONS_WITH_DESCRIPTIONS.contains(this))
			return;

		final Ingredient ing = getDescriptionIngredient();
		final String name = getDescriptionRegistryName();
		final Item item = getItem();
		if (name == null) {
			registry.add(item);
			return;
		}

		registry.add(ing == null ? Ingredient.fromItem(item) : ing, name);
	}

	default Ingredient getDescriptionIngredient () {
		return null;
	}

	default String getDescriptionRegistryName () {
		return null;
	}

	////////////////////////////////////
	// Stack
	//

	abstract Item getItem ();

	default ItemStack getItemStack () {
		if (this instanceof Item)
			return new ItemStack((Item) this);
		else if (this instanceof Block)
			return new ItemStack(((Block) this));
		else
			throw new IllegalArgumentException("Must be called on a valid Block or Item");
	}

	default ItemStack getItemStack (Integer count) {
		ItemStack result = getItemStack();
		result.setCount(count);
		return result;
	}

	default ItemStack getItemStack (Integer count, Integer meta) {
		ItemStack result = getItemStack();
		result.setCount(count);
		result.setItemDamage(meta);
		return result;
	}

	////////////////////////////////////
	// Events
	//

	default void onRegisterRecipes (IForgeRegistry<IRecipe> registry) {
	}

	@SideOnly(Side.CLIENT)
	default void registerModels () {
		ModelLoader.setCustomModelResourceLocation((Item) this, 0, new ModelResourceLocation(((Item) this)
			.getRegistryName(), "inventory"));
	}

	/**
	 * @return Whether to dispense the item.
	 */
	default boolean onDispense (final IBlockSource source, final ItemStack stack, final BlockPos targetPos) {
		return true;
	}
}
