package com.kallgirl.souls.client.render;

import com.kallgirl.souls.common.util.IModObject;
import com.kallgirl.souls.common.block.Summoner.SummonerLogic;
import com.kallgirl.souls.common.block.Summoner.SummonerTileEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.entity.Entity;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class TileEntitySummonerRenderer extends TileEntitySpecialRenderer<SummonerTileEntity> implements IModObject {

	@Override
	public void init () {
		TileEntityRendererDispatcher.instance.mapSpecialRenderers.put(SummonerTileEntity.class, new TileEntitySummonerRenderer());
	}

	public void renderTileEntityAt(SummonerTileEntity tileEntity, double x, double y, double z, float partialTicks, int destroyStage) {
		GlStateManager.pushMatrix();
		GlStateManager.translate((float)x + 0.5F, (float)y, (float)z + 0.5F);
		renderMob(tileEntity.getLogic(), x, y, z, partialTicks);
		GlStateManager.popMatrix();
	}

	public static void renderMob(SummonerLogic logic, double posX, double posY, double posZ, float partialTicks) {
		Entity entity = logic.getCachedMob();

		float f = 0.53125F;
		float f1 = Math.max(entity.width, entity.height);

		if (f1 > 1.0D) {
			f /= f1;
		}

		GlStateManager.translate(0.0F, 0.4F, 0.0F);
		GlStateManager.rotate((float)(logic.getPrevMobRotation() + (logic.getMobRotation() - logic.getPrevMobRotation()) * partialTicks) * 10.0F, 0.0F, 1.0F, 0.0F);
		GlStateManager.translate(0.0F, -0.2F, 0.0F);
		GlStateManager.rotate(-30.0F, 1.0F, 0.0F, 0.0F);
		GlStateManager.scale(f, f, f);
		entity.setLocationAndAngles(posX, posY, posZ, 0.0F, 0.0F);
		Minecraft.getMinecraft().getRenderManager().doRenderEntity(entity, 0.0D, 0.0D, 0.0D, 0.0F, partialTicks, false);
	}
}