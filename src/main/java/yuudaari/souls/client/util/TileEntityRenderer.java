package yuudaari.souls.client.util;

import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;

public abstract class TileEntityRenderer<T extends TileEntity> extends TileEntitySpecialRenderer<T> {
	public abstract Class<T> getTileEntityClass();
}