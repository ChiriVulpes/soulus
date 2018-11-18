package yuudaari.soulus.client.compat.patchouli;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;
import vazkii.patchouli.api.ICustomComponent;
import vazkii.patchouli.client.book.gui.GuiBook;
import vazkii.patchouli.common.base.Patchouli;
import yuudaari.soulus.common.util.LangHelper;

public class ShapelessIconComponent implements ICustomComponent {

	private transient int x, y;
	private transient GuiBook parent;

	@Override
	public void build (int x, int y, int page) {
		this.x = x;
		this.y = y;
	}

	@Override
	public void onDisplayed (GuiScreen parent) {
		this.parent = (GuiBook) parent;
	}

	@Override
	public void render (float ticks, int mouseX, int mouseY) {
		Minecraft.getMinecraft().renderEngine.bindTexture(new ResourceLocation(Patchouli.MOD_ID, "textures/gui/crafting.png"));
		GlStateManager.enableBlend();

		int iconX = x;
		int iconY = y;
		Gui.drawModalRectWithCustomSizedTexture(iconX, iconY, 0, 64, 11, 11, 128, 128);

		if (isMouseInRelativeRange(mouseX, mouseY, iconX, iconY, 11, 11)) {
			parent.setTooltip(LangHelper.localize("patchouli.gui.lexicon.shapeless"));
		}
	}

	private boolean isMouseInRelativeRange (int absMx, int absMy, int x, int y, int w, int h) {
		int mx = absMx - parent.bookLeft;
		int my = absMy - parent.bookTop;
		return mx > x && my > y && mx <= (x + w) && my <= (y + h);
	}
}
