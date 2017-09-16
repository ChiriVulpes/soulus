package yuudaari.souls.common.recipe;

import net.minecraft.item.crafting.IRecipe;
import net.minecraft.util.ResourceLocation;
import yuudaari.souls.Souls;

public abstract class Recipe implements IRecipe {
	protected ResourceLocation name;
	protected ResourceLocation group;

	public IRecipe setRegistryName(ResourceLocation name) {
		// the base game calls this, but we don't want the base game to be able to change the registry name if we've already set it
		return setRegistryName(name, false);
	}

	public IRecipe setRegistryName(ResourceLocation name, boolean overwrite) {
		if (this.name == null || overwrite)
			this.name = name;
		return this;
	}

	public IRecipe setRegistryName(String name) {
		return setRegistryName(Souls.getRegistryName(name), true);
	}

	public IRecipe setRegistryName(String prefix, String name) {
		return setRegistryName(new ResourceLocation(prefix, name), true);
	}

	public ResourceLocation getRegistryName() {
		return name;
	}

	public Class<IRecipe> getRegistryType() {
		return IRecipe.class;
	}

	public boolean canFit(int w, int h) {
		return w > 1 || h > 1;
	}
}