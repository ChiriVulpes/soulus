package yuudaari.soulus.client.render;

import yuudaari.soulus.client.util.TileEntityRenderer;
import yuudaari.soulus.common.block.soul_totem.SoulTotemTileEntity;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class SoulTotemRenderer extends TileEntityRenderer<SoulTotemTileEntity> {

	private static final ResourceLocation TEXTURES = new ResourceLocation("soulus", "textures/blocks/soul_totem/catalyst.png");
	private final ModelSoulCatalyst model = new ModelSoulCatalyst();

	public Class<SoulTotemTileEntity> getTileEntityClass () {
		return SoulTotemTileEntity.class;
	}

	@Override
	public void render (SoulTotemTileEntity te, double x, double y, double z, float partialTicks, int destroyStage, float alpha) {
		float f = (float) te.lastRotation + (te.rotation - te.lastRotation) * partialTicks;
		float s = (float) te.lastScale + (te.scale - te.lastScale) * partialTicks;
		GlStateManager.pushMatrix();
		GlStateManager.translate((float) x + 0.5F, (float) y + 1.5F, (float) z + 0.5F);
		this.bindTexture(TEXTURES);
		float f1 = MathHelper.sin(f * 0.1F) / 2.0F + 0.5F;
		f1 = f1 * f1 + f1;

		this.model.render(f * 3.0F, f1 * 0.05F, s);

		GlStateManager.popMatrix();
	}


	public static class ModelSoulCatalyst extends ModelBase {

		/** The cube model for the Ender Crystal. */
		private final ModelRenderer cube = new ModelRenderer(this, "cube");
		/** The glass model for the Ender Crystal. */
		private final ModelRenderer glass = new ModelRenderer(this, "glass");

		public ModelSoulCatalyst () {
			this.glass.setTextureOffset(0, 0).addBox(-4.0F, -4.0F, -4.0F, 8, 8, 8);
			this.cube.setTextureOffset(32, 0).addBox(-4.0F, -4.0F, -4.0F, 8, 8, 8);
		}

		/**
		 * Sets the models various rotation angles then renders the model.
		 */
		public void render (float rotation, float yOffset, float scale) {
			GlStateManager.pushMatrix();
			GlStateManager.scale(2.0F * scale, 2.0F * scale, 2.0F * scale);
			GlStateManager.translate(0.0F, -0.5F, 0.0F);

			GlStateManager.rotate(rotation, 0.0F, 1.0F, 0.0F);
			GlStateManager.translate(0.0F, 0.8F + yOffset, 0.0F);
			GlStateManager.rotate(60.0F, 0.7071F, 0.0F, 0.7071F);
			this.glass.render(0.0625F);
			GlStateManager.scale(0.875F, 0.875F, 0.875F);
			GlStateManager.rotate(60.0F, 0.7071F, 0.0F, 0.7071F);
			GlStateManager.rotate(rotation, 0.0F, 1.0F, 0.0F);
			this.glass.render(0.0625F);
			GlStateManager.scale(0.875F, 0.875F, 0.875F);
			GlStateManager.rotate(60.0F, 0.7071F, 0.0F, 0.7071F);
			GlStateManager.rotate(rotation, 0.0F, 1.0F, 0.0F);
			this.cube.render(0.0625F);
			GlStateManager.popMatrix();
		}
	}
}
