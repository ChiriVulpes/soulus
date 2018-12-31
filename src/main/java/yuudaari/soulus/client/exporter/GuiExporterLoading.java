package yuudaari.soulus.client.exporter;

import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class GuiExporterLoading {

	@SideOnly(Side.CLIENT)
	public static void renderLoadingScreen (final String title, final String subtitle, final ItemStack stack, final float progress) {
		final Minecraft mc = Minecraft.getMinecraft();
		final ScaledResolution res = new ScaledResolution(mc);

		mc.getFramebuffer().unbindFramebuffer();
		GlStateManager.pushMatrix();

		mc.entityRenderer.setupOverlayRendering();

		// Draw the dirt background and status text...
		StaticRenderer.drawBackground(res.getScaledWidth(), res.getScaledHeight());
		StaticRenderer.drawCenteredString(mc.fontRenderer, title, res.getScaledWidth() / 2, res.getScaledHeight() / 2 - 24, -1);
		StaticRenderer.drawRect(res.getScaledWidth() / 2 - 50, res.getScaledHeight() / 2 - 1, res.getScaledWidth() / 2 + 50, res.getScaledHeight() / 2 + 1, 0xFF001100);
		StaticRenderer.drawRect(res.getScaledWidth() / 2 - 50, res.getScaledHeight() / 2 - 1, (res.getScaledWidth() / 2 - 50) + (int) (progress * 100), res.getScaledHeight() / 2 + 1, 0xFF55FF55);

		// render a tooltip for the item
		tryRenderTooltip(stack, subtitle);

		GlStateManager.popMatrix();
		mc.updateDisplay();
		/*
		 * While OpenGL itself is double-buffered, Minecraft is actually *triple*-buffered.
		 * This is to allow shaders to work, as shaders are only available in "modern" GL.
		 * Minecraft uses "legacy" GL, so it renders using a separate GL context to this
		 * third buffer, which is then flipped to the back buffer with this call.
		 */
		mc.getFramebuffer().bindFramebuffer(false);
	}

	@SideOnly(Side.CLIENT)
	private static void tryRenderTooltip (final ItemStack stack, final String subtitle) {
		final Minecraft mc = Minecraft.getMinecraft();
		final ScaledResolution res = new ScaledResolution(mc);

		GlStateManager.pushMatrix();

		GlStateManager.scale(0.5f, 0.5f, 1);
		StaticRenderer.drawCenteredString(mc.fontRenderer, subtitle, res.getScaledWidth(), res.getScaledHeight() - 20, -1);

		// ...and draw the tooltip.
		try {
			if (stack == null) return;

			renderTooltip(stack, subtitle);

		} catch (Throwable t) {
			// oops!

		} finally {
			GlStateManager.popMatrix();
		}
	}

	@SideOnly(Side.CLIENT)
	private static void renderTooltip (final ItemStack stack, final String subtitle) {
		final Minecraft mc = Minecraft.getMinecraft();
		final ScaledResolution res = new ScaledResolution(mc);
		final List<String> list = stack.getTooltip(mc.player, ITooltipFlag.TooltipFlags.NORMAL);

		// This code is copied from the tooltip renderer, so we can properly center it
		for (int i = 0; i < list.size(); ++i) {
			if (i == 0) {
				list.set(i, stack.getRarity().rarityColor + list.get(i));
			} else {
				list.set(i, TextFormatting.GRAY + list.get(i));
			}
		}

		FontRenderer font = stack.getItem().getFontRenderer(stack);
		if (font == null) font = mc.fontRenderer;

		int width = 0;

		for (String s : list) {
			int j = font.getStringWidth(s);

			if (j > width) {
				width = j;
			}
		}
		// End copied code

		GlStateManager.translate((res.getScaledWidth() - width / 2) - 12, res.getScaledHeight() + 30, 0);
		StaticRenderer.drawHoveringText(list, 0, 0, font);
	}
}
