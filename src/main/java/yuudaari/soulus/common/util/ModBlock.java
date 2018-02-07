package yuudaari.soulus.common.util;

import yuudaari.soulus.Soulus;
import yuudaari.soulus.common.CreativeTab;
import yuudaari.soulus.common.compat.JeiDescriptionRegistry;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.NonNullList;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class ModBlock extends Block implements IBlock {

	private String name;
	private List<String> oreDicts = new ArrayList<>();
	private Boolean hasItem = false;
	private ItemBlock itemBlock;

	public ModBlock (String name, Material material) {
		super(material);
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

	public ModBlock addOreDict (String... name) {
		for (String dict : name)
			oreDicts.add(dict);

		return this;
	}

	public List<String> getOreDicts () {
		return oreDicts;
	}

	protected List<IRecipe> recipes = new ArrayList<>();

	public List<IRecipe> getRecipes () {
		return recipes;
	}

	public void addRecipe (IRecipe recipe) {
		recipes.add(recipe);
	}

	public void setHasItem (boolean hasItem, @Nullable ItemBlock block) {
		if (hasItem) {
			if (!this.hasItem) {
				this.hasItem = true;
				itemBlock = block == null ? new ItemBlock(this) : block;
				itemBlock.setRegistryName(getRegistryName());
			}
		} else {
			this.hasItem = false;
			itemBlock = null;
		}
	}

	public void setHasItem (boolean hasItem) {
		setHasItem(hasItem, null);
	}

	public void setHasItem () {
		setHasItem(true);
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

	@Nonnull
	@Override
	public BlockRenderLayer getBlockLayer () {
		Material material = blockMaterial;
		if (material.isOpaque())
			return BlockRenderLayer.SOLID;
		else if (material.blocksLight())
			return BlockRenderLayer.TRANSLUCENT;
		else
			return BlockRenderLayer.CUTOUT;
	}

	@Override
	public boolean isOpaqueCube (IBlockState state) {
		return blockMaterial.isOpaque() && blockMaterial.blocksLight();
	}

	@Override
	public CreativeTab getCreativeTabToDisplayOn () {
		return CreativeTab.INSTANCE;
	}

	@Override
	public void getSubBlocks (CreativeTab itemIn, NonNullList<ItemStack> items) {
		items.add(new ItemStack(this));
	}

	public boolean hasDescription = false;

	public ModBlock setHasDescription () {
		hasDescription = true;
		return this;
	}

	@Override
	public void onRegisterDescription (JeiDescriptionRegistry registry) {
		if (hasDescription)
			registry.add(this.getItemStack());
	}
}
