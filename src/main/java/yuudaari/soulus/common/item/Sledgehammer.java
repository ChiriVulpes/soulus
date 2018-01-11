package yuudaari.soulus.common.item;

import net.minecraft.item.ItemStack;
import yuudaari.soulus.common.config.Serializer;
import yuudaari.soulus.common.util.ModItem;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Random;

public class Sledgehammer extends ModItem {

	public static Serializer<Sledgehammer> serializer = new Serializer<>(Sledgehammer.class, "durability");

	public static Sledgehammer INSTANCE = new Sledgehammer();

	public int durability = 256;

	public Sledgehammer () {
		super("sledgehammer");
		setMaxStackSize(1);
		setMaxDamage(durability);
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
		return durability;
	}
}
