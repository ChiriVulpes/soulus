package yuudaari.souls.common.item;

import yuudaari.souls.Souls;
import yuudaari.souls.common.config.SoulConfig;
import yuudaari.souls.common.config.ColourConfig;
import yuudaari.souls.common.util.MobTarget;
import yuudaari.souls.common.util.ModItem;
import net.minecraft.entity.EntityList;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.ForgeRegistries;

import javax.annotation.Nonnull;

public class Essence extends ModItem {
	public Essence() {
		super("essence");
		setMaxStackSize(64);
		setCreativeTab(null);
		registerColorHandler((ItemStack stack, int tintIndex) -> {
			String mobTarget = MobTarget.getMobTarget(stack);
			if (mobTarget == null)
				return -1;
			SoulConfig soulInfo = Souls.getSoulInfo(mobTarget, false);
			if (soulInfo == null)
				return -1;
			ColourConfig colourInfo = soulInfo.colourInfo;
			if (colourInfo == null) {
				EntityList.EntityEggInfo eggInfo = ForgeRegistries.ENTITIES.getValue(new ResourceLocation(mobTarget))
						.getEgg();
				if (eggInfo == null)
					return -1;
				colourInfo = new ColourConfig(eggInfo);
			}
			return tintIndex == 0 ? colourInfo.primaryColour : colourInfo.secondaryColour;
		});
	}

	public ItemStack getStack(String mobTarget) {
		return getStack(mobTarget, 1);
	}

	public ItemStack getStack(String mobTarget, Integer count) {
		ItemStack stack = new ItemStack(this, count);
		MobTarget.setMobTarget(stack, mobTarget);
		return stack;
	}

	@Nonnull
	@Override
	public String getUnlocalizedNameInefficiently(@Nonnull ItemStack stack) {
		String mobTarget = MobTarget.getMobTarget(stack);
		if (mobTarget == null)
			mobTarget = "unfocused";
		return super.getUnlocalizedNameInefficiently(stack) + "." + mobTarget;
	}
}