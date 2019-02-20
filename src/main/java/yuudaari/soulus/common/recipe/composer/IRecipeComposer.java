package yuudaari.soulus.common.recipe.composer;

import java.util.Map;
import java.util.Set;
import net.minecraft.item.crafting.IRecipe;

public interface IRecipeComposer extends IRecipe {

	public float getTime ();

	public Map<String, Integer> getMobsRequired ();

	public Set<String> getMobWhitelist ();

	public Set<String> getMobBlacklist ();

}
