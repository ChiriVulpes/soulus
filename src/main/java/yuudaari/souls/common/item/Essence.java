package yuudaari.souls.common.item;

import yuudaari.souls.common.Config;
import yuudaari.souls.common.util.MobTarget;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.EntityList;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.ForgeRegistries;

import javax.annotation.Nonnull;

public class Essence extends SoulsItem {
	public Essence() {
		super("essence");
		setMaxStackSize(64);
		setCreativeTab(null);
	}

	public ItemStack getStack(String mobTarget) {
		return getStack(mobTarget, 1);
	}

	public ItemStack getStack(String mobTarget, Integer count) {
		ItemStack stack = new ItemStack(this, count);
		NBTTagCompound entityTag = new NBTTagCompound();
		entityTag.setString("id", mobTarget);
		NBTTagCompound stackData = new NBTTagCompound();
		stackData.setTag("EntityTag", entityTag);
		stack.setTagCompound(stackData);
		return stack;
	}

	@Nonnull
	@Override
	public String getUnlocalizedNameInefficiently(@Nonnull ItemStack stack) {
		String mobTarget = MobTarget.getMobTarget(stack);
		if (mobTarget == null)
			mobTarget = "unfocused";
		return super.getUnlocalizedNameInefficiently(stack).replace("essence", "essence." + mobTarget);
	}

	@Override
	public void init() {
		Minecraft.getMinecraft().getItemColors().registerItemColorHandler((ItemStack stack, int tintIndex) -> {
			String mobTarget = MobTarget.getMobTarget(stack);
			if (mobTarget == null)
				return -1;
			Config.SoulInfo soulInfo = Config.getSoulInfo(mobTarget, false);
			if (soulInfo == null)
				return -1;
			Config.ColourInfo colourInfo = soulInfo.colourInfo;
			if (colourInfo == null) {
				EntityList.EntityEggInfo eggInfo = ForgeRegistries.ENTITIES
						.getValue(new ResourceLocation("minecraft", mobTarget)).getEgg();
				if (eggInfo == null)
					return -1;
				colourInfo = new Config.ColourInfo(eggInfo);
			}
			return tintIndex == 0 ? colourInfo.primaryColour : colourInfo.secondaryColour;
		}, this);
	}
}