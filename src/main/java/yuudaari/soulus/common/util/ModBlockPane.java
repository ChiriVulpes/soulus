package yuudaari.soulus.common.util;

import yuudaari.soulus.Soulus;
import yuudaari.soulus.common.CreativeTab;
import yuudaari.soulus.common.compat.JeiDescriptionRegistry;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.block.BlockPane;
import net.minecraft.block.material.Material;
import net.minecraft.item.ItemBlock;
import net.minecraft.tileentity.TileEntity;

public class ModBlockPane extends BlockPane implements IBlock {

	private String name;
	private List<String> oreDicts = new ArrayList<>();
	private Boolean hasItem = false;
	private ItemBlock itemBlock;

	public ModBlockPane (String name, Material material) {
		super(material, true);
		setName(name);
		setCreativeTab(CreativeTab.INSTANCE);
	}

	public String getName () {
		return name;
	}

	public void setName (String name) {
		this.name = name;
		setRegistryName(Soulus.MODID, name);
		setUnlocalizedName(getRegistryName().toString());
	}

	public ModBlockPane addOreDict (String... name) {
		for (String dict : name)
			oreDicts.add(dict);

		return this;
	}

	public List<String> getOreDicts () {
		return oreDicts;
	}

	public void setHasItem () {
		if (!hasItem) {
			hasItem = true;
			itemBlock = new ItemBlock(this);
			itemBlock.setRegistryName(getRegistryName());
		}
	}

	public boolean hasItem () {
		return hasItem;
	}

	public ItemBlock getItemBlock () {
		if (!hasItem)
			throw new IllegalArgumentException("This block has no registered item");
		return itemBlock;
	}

	public Class<? extends TileEntity> getTileEntityClass () {
		return null;
	}

	@Override
	public CreativeTab getCreativeTabToDisplayOn () {
		return CreativeTab.INSTANCE;
	}

	public boolean hasDescription = false;

	public ModBlockPane setHasDescription () {
		hasDescription = true;
		return this;
	}

	@Override
	public void onRegisterDescription (JeiDescriptionRegistry registry) {
		if (hasDescription)
			registry.add(this.getItemStack());
	}
}
