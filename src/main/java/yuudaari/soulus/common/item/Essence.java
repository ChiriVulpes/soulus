package yuudaari.soulus.common.item;

import yuudaari.soulus.common.config.ColourConfig;
import yuudaari.soulus.common.util.EssenceType;
import yuudaari.soulus.common.util.ModItem;
import net.minecraft.entity.EntityList;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.fml.relauncher.Side;

import javax.annotation.Nonnull;

public class Essence extends ModItem {
	public Essence() {
		super("essence");
		setMaxStackSize(64);
		setCreativeTab(null);

		if (FMLCommonHandler.instance().getSide() == Side.CLIENT) {
			registerColorHandler((ItemStack stack, int tintIndex) -> {
				String essenceType = EssenceType.getEssenceType(stack);
				if (essenceType == null)
					return -1;

				EntityList.EntityEggInfo eggInfo = ForgeRegistries.ENTITIES.getValue(new ResourceLocation(essenceType))
						.getEgg();
				if (eggInfo == null)
					return -1;
				ColourConfig colourInfo = new ColourConfig(eggInfo);
				/*
				SoulConfig soulInfo = Soulus.getSoulInfo(mobTarget, false);
				if (soulInfo == null)
					return -1;
				ColourConfig colourInfo = soulInfo.colourInfo;
				if (colourInfo == null) {
				}
				*/
				return tintIndex == 0 ? colourInfo.primaryColour : colourInfo.secondaryColour;
			});
		}
	}

	public ItemStack getStack(String mobTarget) {
		return getStack(mobTarget, 1);
	}

	public ItemStack getStack(String mobTarget, Integer count) {
		ItemStack stack = new ItemStack(this, count);
		EssenceType.setEssenceType(stack, mobTarget);
		return stack;
	}

	@Nonnull
	@Override
	public String getUnlocalizedNameInefficiently(@Nonnull ItemStack stack) {
		String mobTarget = EssenceType.getEssenceType(stack);
		if (mobTarget == null)
			mobTarget = "unfocused";
		return super.getUnlocalizedNameInefficiently(stack) + "." + mobTarget;
	}
}