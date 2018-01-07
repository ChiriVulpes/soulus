package yuudaari.soulus.client.render;

import yuudaari.soulus.client.util.TileEntityRenderer;
import yuudaari.soulus.common.block.composer.HasRenderItemTileEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ComposerRenderer<T extends HasRenderItemTileEntity> extends TileEntityRenderer<T> {
	private Class<T> cls;

	public ComposerRenderer(Class<T> cls) {
		this.cls = cls;
	}

	public Class<T> getTileEntityClass() {
		return cls;
	}

	@Override
	public void render(T te, double x, double y, double z, float partialTicks, int destroyStage, float alpha) {

		if (te.getStoredItem() == null)
			return;

		GlStateManager.pushAttrib();
		GlStateManager.pushMatrix();

		GlStateManager.translate(x, y, z);
		GlStateManager.disableRescaleNormal();

		RenderHelper.enableStandardItemLighting();
		GlStateManager.enableLighting();

		GlStateManager.translate(.5, 1.25, .5);

		float rotate = (float) (te.getPrevItemRotation()
				+ (te.getItemRotation() - te.getPrevItemRotation()) * partialTicks);
		GlStateManager.translate(0, Math.sin(rotate / 5) / 20, 0);

		GlStateManager.rotate(rotate * 10.0F, 0.0F, 1.0F, 0.0F);
		if (te.shouldComplexRotate()) {
			GlStateManager.rotate(rotate * 4.0F, 0.0F, 0.0F, 1.0F);
			GlStateManager.rotate(rotate * 0.5F, 1.0F, 0.0F, 0.0F);
		}

		ItemStack storedItem = te.getStoredItem();
		double scale = storedItem.getItem() instanceof ItemBlock ? .25 : .4;
		GlStateManager.scale(scale, scale, scale);

		Minecraft.getMinecraft().getRenderItem().renderItem(storedItem, ItemCameraTransforms.TransformType.NONE);

		GlStateManager.popMatrix();
		GlStateManager.popAttrib();
	}
}