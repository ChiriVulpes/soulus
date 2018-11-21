package yuudaari.soulus.common.util;

import java.util.List;
import org.lwjgl.input.Keyboard;
import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.Phase;
import net.minecraftforge.fml.common.gameevent.TickEvent.RenderTickEvent;
import net.minecraftforge.fml.relauncher.Side;
import yuudaari.soulus.client.exporter.Exporter;

@Mod.EventBusSubscriber
public class DebugHelper {

	private static KeyBinding bind;
	private static boolean bindHeld;

	public static void initialize () {
		if (!isInDebugContext()) return;

		bind = new KeyBinding("key.export", Keyboard.KEY_GRAVE, "key.categories.soulus");
		ClientRegistry.registerKeyBinding(bind);
	}

	public static boolean isInDebugContext () {
		return FMLCommonHandler.instance().getSide() == Side.CLIENT && //
			Minecraft.getMinecraft().getSession().getToken().equals("FML");
	}

	// We *must* call render code in pre-render. If we don't, it won't work right. Because we need to render some things as part
	// of the export, we therefore call all export code in pre-render.
	@SubscribeEvent(priority = EventPriority.HIGHEST)
	public static void onFrameStart (final RenderTickEvent e) {
		if (e.phase != Phase.START)
			return;

		final int code = bind.getKeyCode();
		if (code > 256) {
			return;
		}

		final boolean down = Keyboard.isKeyDown(code);
		if (down && !bindHeld) {
			final List<String> result = Exporter.export();
			for (final String message : result)
				Minecraft.getMinecraft().ingameGUI.getChatGUI().printChatMessage(new TextComponentString(message));
		}

		bindHeld = down;
	}
}
