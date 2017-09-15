package yuudaari.souls.common.item;

import yuudaari.souls.Souls;
import yuudaari.souls.common.CreativeTab;
import yuudaari.souls.common.util.IModItem;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class SoulsItem extends net.minecraft.item.Item implements IModItem {
	protected Boolean glint = false;
	private String name;
	private List<String> oreDicts = new ArrayList<>();

	public SoulsItem(String name) {
		setName(name);
		setCreativeTab(CreativeTab.INSTANCE);
	}

	public SoulsItem(String name, Integer maxStackSize) {
		this(name);
		setMaxStackSize(maxStackSize);
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
		setRegistryName(Souls.MODID, name);
		setUnlocalizedName(getRegistryName().toString());
	}

	public void addOreDict(String name) {
		oreDicts.add(name);
	}

	public List<String> getOreDicts() {
		return oreDicts;
	}

	@SideOnly(Side.CLIENT)
	@Override
	public boolean hasEffect(ItemStack stack) {
		return glint;
	}
}