package yuudaari.souls.common.util;

import yuudaari.souls.Souls;
import yuudaari.souls.common.CreativeTab;
import yuudaari.souls.common.util.IModItem;
import java.util.ArrayList;
import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.color.IItemColor;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ModItem extends Item implements IModItem {
	protected Boolean glint = false;
	private String name;
	private List<String> oreDicts = new ArrayList<>();

	public ModItem(String name) {
		setName(name);
		setCreativeTab(CreativeTab.INSTANCE);
	}

	public ModItem(String name, Integer maxStackSize) {
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

	public void registerColorHandler(IItemColor itemColor) {
		Minecraft.getMinecraft().getItemColors().registerItemColorHandler(itemColor, this);
	}
}