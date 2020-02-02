package yuudaari.soulus.client.util;

import org.lwjgl.input.Keyboard;
import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class Sneak {

	/**
	 * Note: Client-side only
	 */
	public static boolean isSneaking () {
		return Keyboard.isKeyDown(Minecraft.getMinecraft().gameSettings.keyBindSneak.getKeyCode());
	}
}
