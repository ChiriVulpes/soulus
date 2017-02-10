package com.kallgirl.souls.common.block;

import com.kallgirl.souls.ModInfo;
import com.kallgirl.souls.client.ResourceMap;
import com.kallgirl.souls.client.render.IModelRegister;
import com.kallgirl.souls.common.CreativeTab;
import com.kallgirl.souls.common.IModItem;
import com.kallgirl.souls.common.item.ItemBlock;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nonnull;

public class Block extends net.minecraft.block.Block implements IModItem, IModelRegister {
	public String name;
	private Boolean hasItem = false;
	private ItemBlock itemBlock;

	public Block (String name, Material material) {
		super(material);
		this.name = name;
		setUnlocalizedName(name);
		setCreativeTab(CreativeTab.INSTANCE);
		GameRegistry.register(this, new ResourceLocation(ModInfo.MODID, name));
	}

	@Nonnull
	@Override
	public BlockRenderLayer getBlockLayer () {
		Material material = blockMaterial;
		if (material.isOpaque()) return BlockRenderLayer.SOLID;
		else if (material.blocksLight()) return BlockRenderLayer.TRANSLUCENT;
		else return BlockRenderLayer.CUTOUT;
	}

	@Override
	public boolean isOpaqueCube (IBlockState state) {
		return blockMaterial.isOpaque() && blockMaterial.blocksLight();
	}

	public void setHasItem() {
		if (!hasItem) {
			hasItem = true;
			ItemBlock itemBlock = new ItemBlock(this);
			itemBlock.setRegistryName(getRegistryName());
			this.itemBlock = itemBlock;
			GameRegistry.register(itemBlock);
		}
	}
	public ItemBlock getItemBlock() {
		if (!hasItem) throw new IllegalArgumentException("This block has no registered item");
		return itemBlock;
	}

	@Override
	public void preinit () {
		registerModels();
	}

	@Override
	public void addFurnaceRecipe (Object item) {
		if (!hasItem) throw new IllegalArgumentException("Can't add a recipe for this block as it has no ItemBlock");
		IModItem.super.addFurnaceRecipe(item);
	}
	@Override
	public void addRecipeShaped (Integer count, String[] recipe, Object... map) {
		if (!hasItem) throw new IllegalArgumentException("Can't add a recipe for this block as it has no ItemBlock");
		IModItem.super.addRecipeShaped(count, recipe, map);
	}
	@Override
	public void addRecipeShaped (String[] recipe, Object... map) {
		if (!hasItem) throw new IllegalArgumentException("Can't add a recipe for this block as it has no ItemBlock");
		IModItem.super.addRecipeShaped(recipe, map);
	}
	@Override
	public void addRecipeShapeless (Integer count, Object... items) {
		if (!hasItem) throw new IllegalArgumentException("Can't add a recipe for this block as it has no ItemBlock");
		IModItem.super.addRecipeShapeless(count, items);
	}
	@Override
	public void addRecipeShapeless (Object... items) {
		if (!hasItem) throw new IllegalArgumentException("Can't add a recipe for this block as it has no ItemBlock");
		IModItem.super.addRecipeShapeless(items);
	}

	@Nonnull
	@Override
	public String getUnlocalizedName() {
		return super.getUnlocalizedName()
			.replaceAll("tile\\.", "tile." + ResourceMap.PREFIX_MOD);
	}


	@SideOnly (Side.CLIENT)
	@Override
	public void registerModels() {
		if (hasItem) {
			System.out.println("Registered block model " + getRegistryName());
			ModelLoader.setCustomModelResourceLocation(this.getItemBlock(), 0,
				new ModelResourceLocation(getRegistryName(), "inventory")
			);
		}
	}
}