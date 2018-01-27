package yuudaari.soulus.common.item;

import net.minecraft.item.ItemStack;
import yuudaari.soulus.Soulus;
import yuudaari.soulus.common.config.ConfigInjected;
import yuudaari.soulus.common.config.ConfigInjected.Inject;
import yuudaari.soulus.common.config.item.ConfigSledgehammer;
import yuudaari.soulus.common.util.ModItem;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Random;

@ConfigInjected(Soulus.MODID)
public class Sledgehammer extends ModItem {

	@Inject public static ConfigSledgehammer CONFIG;

	public Sledgehammer () {
		super("sledgehammer");
		setMaxStackSize(1);
		setMaxDamage(256);
		setHasDescription();
	}

	@Override
	public boolean hasContainerItem (ItemStack stack) {
		return true;
	}

	private final Random random = new Random();

	@ParametersAreNonnullByDefault
	@Nonnull
	@Override
	public ItemStack getContainerItem (ItemStack stack) {
		stack.attemptDamageItem(1, random, null);
		ItemStack newStack = new ItemStack(stack.getItem());
		newStack.setItemDamage(stack.getItemDamage());
		return newStack;
	}

	@Override
	public int getMaxDamage (ItemStack stack) {
		return CONFIG.durability;
	}
}
