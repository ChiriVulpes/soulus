package yuudaari.soulus.client.render;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import yuudaari.soulus.client.util.TileEntityRenderer;
import yuudaari.soulus.common.block.composer.HasRenderItemTileEntity;

@SideOnly(Side.CLIENT)
public class ComposerRenderer<T extends HasRenderItemTileEntity> extends TileEntityRenderer<T> {

	private Class<T> cls;

	public ComposerRenderer (Class<T> cls) {
		this.cls = cls;
	}

	public Class<T> getTileEntityClass () {
		return cls;
	}

	@Override
	public void render (T te, double x, double y, double z, float partialTicks, int destroyStage, float alpha) {

		if (te.getStoredItem() == null)
			return;

		GlStateManager.pushAttrib();
		GlStateManager.pushMatrix();

		GlStateManager.translate(x, y, z);
		GlStateManager.disableRescaleNormal();

		RenderHelper.enableStandardItemLighting();
		GlStateManager.enableLighting();

		GlStateManager.translate(.5, 1.25, .5);

		float rotate = (float) (te.getPrevItemRotation() + (te.getItemRotation() - te.getPrevItemRotation()) * partialTicks);
		GlStateManager.translate(0, Math.sin(rotate / 5) / 20, 0);

		GlStateManager.rotate(rotate * 10.0F, 0.0F, 1.0F, 0.0F);

		final double spinSpeed = te.getSpinSpeed();

		if (spinSpeed > 0) {
			final Vec3d spin = rotate(new Vec3d(0, 0, 1), 10, 0, 0);
			GlStateManager.rotate(rotate * (float) spinSpeed, (float) spin.x, (float) spin.y, (float) spin.z);
		}

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

	public Vec3d rotate (Vec3d vec, float yaw, float pitch, float roll) {
		double cosa = Math.cos(yaw);
		double cosb = Math.cos(pitch);
		double cosc = Math.cos(roll);
		double sina = Math.sin(yaw);
		double sinb = Math.sin(pitch);
		double sinc = Math.sin(roll);

		double Axx = cosa * cosb;
		double Axy = cosa * sinb * sinc - sina * cosc;
		double Axz = cosa * sinb * cosc + sina * sinc;

		double Ayx = sina * cosb;
		double Ayy = sina * sinb * sinc + cosa * cosc;
		double Ayz = sina * sinb * cosc - cosa * sinc;

		double Azx = -sinb;
		double Azy = cosb * sinc;
		double Azz = cosb * cosc;

		double px = vec.x;
		double py = vec.y;
		double pz = vec.z;
		double x = Axx * px + Axy * py + Axz * pz;
		double y = Ayx * px + Ayy * py + Ayz * pz;
		double z = Azx * px + Azy * py + Azz * pz;
		return new Vec3d(x, y, z);
	}
}
