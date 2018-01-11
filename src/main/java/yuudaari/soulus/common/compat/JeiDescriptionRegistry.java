package yuudaari.soulus.common.compat;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.ResourceLocation;
import scala.Tuple2;

public class JeiDescriptionRegistry {
	public List<Tuple2<List<ItemStack>, String>> ingredients = new ArrayList<>();

	public <T extends Ingredient> void add(T ing, String description) {
		ingredients.add(new Tuple2<>(new ArrayList<>(Arrays.asList(ing.getMatchingStacks())), description));
	}

	public <T extends Ingredient> void add(T ing, ResourceLocation name) {
		add(ing, name.toString());
	}

	public void add(Item item) {
		add(Ingredient.fromItem(item), item.getRegistryName());
	}

	public void add(ItemStack... items) {
		add(Ingredient.fromStacks(items), items[0].getItem().getRegistryName());
	}
}