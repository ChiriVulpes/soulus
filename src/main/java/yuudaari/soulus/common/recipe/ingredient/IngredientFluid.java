package yuudaari.soulus.common.recipe.ingredient;

import java.util.ArrayList;
import java.util.List;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.JsonUtils;
import net.minecraftforge.common.ForgeModContainer;
import net.minecraftforge.common.crafting.IIngredientFactory;
import net.minecraftforge.common.crafting.JsonContext;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.fluids.UniversalBucket;

public class IngredientFluid extends Ingredient {

	private static List<UniversalBucket> UNIVERSAL_BUCKETS;

	public static ItemStack[] getMatchingStacks1 (Fluid fluid) {
		if (UNIVERSAL_BUCKETS == null) {
			UNIVERSAL_BUCKETS = new ArrayList<>();

			for (Item item : Item.REGISTRY) {
				if (item instanceof UniversalBucket) {
					UNIVERSAL_BUCKETS.add((UniversalBucket) item);
				}
			}
		}

		List<ItemStack> stacks = new ArrayList<>();

		for (UniversalBucket bucket : UNIVERSAL_BUCKETS) {
			if (bucket.equals(ForgeModContainer.getInstance().universalBucket)) {
				stacks.add(FluidUtil.getFilledBucket(new FluidStack(fluid, Fluid.BUCKET_VOLUME)));
				continue;
			}

			ItemStack filledBucket = new ItemStack(bucket);
			FluidStack fluidContents = new FluidStack(fluid, bucket.getCapacity());

			FluidUtil.getFluidHandler(filledBucket).fill(fluidContents, true);

			stacks.add(filledBucket);
		}

		return stacks.toArray(new ItemStack[0]);
	}

	private final Fluid fluid;

	public IngredientFluid (Fluid fluid) {
		super(getMatchingStacks1(fluid));
		this.fluid = fluid;
	}

	@Override
	public boolean apply (ItemStack stack) {
		FluidStack contained = FluidUtil.getFluidContained(stack);
		return contained != null && contained.isFluidEqual(new FluidStack(fluid, Fluid.BUCKET_VOLUME));
	}

	@Override
	public boolean isSimple () {
		return false;
	}

	public static class Factory implements IIngredientFactory {

		@Override
		public Ingredient parse (JsonContext context, JsonObject json) {
			if (!json.has("fluid")) {
				throw new JsonParseException("IngredientFluid requires a fluid type");
			}

			String fluidName = JsonUtils.getString(json, "fluid");

			Fluid fluid = FluidRegistry.getFluid(fluidName);
			if (fluid == null) {
				throw new JsonParseException("Unknown fluid '" + fluidName + "'");
			}

			return new IngredientFluid(fluid);
		}
	}
}
