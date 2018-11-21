package yuudaari.soulus.client.exporter;

import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.File;
import java.nio.ByteBuffer;
import javax.imageio.ImageIO;
import com.google.common.io.Files;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.item.ItemStack;

// OpenGL is double-buffered. This means, where we draw to is /not/ on the screen.
// As such, we are free to do whatever we like before Minecraft renders, as long as we put everything back the way it was.
public class Renderer {

	private static final int SIZE = 512;

	private static float oldZLevel;

	public static void setUpRenderState () {
		final Minecraft mc = Minecraft.getMinecraft();
		final ScaledResolution res = new ScaledResolution(mc);
		/*
		 * As we render to the back-buffer, we need to cap our render size to be within
		 * the window's bounds. If we didn't do this, the results of our readPixels up
		 * ahead would be undefined. And nobody likes undefined behavior.
		 */
		final int size = Math.min(Math.min(mc.displayHeight, mc.displayWidth), SIZE);

		// Switches from 3D to 2D
		mc.entityRenderer.setupOverlayRendering();
		RenderHelper.enableGUIStandardItemLighting();
		/*
		 * The GUI scale affects us due to the call to setupOverlayRendering above. As
		 * such, we need to counteract this to always get a 512x512 render. We could
		 * manually switch to orthogonal mode, but it's just more convenient to leverage
		 * setupOverlayRendering.
		 */
		final float scale = size / (16f * res.getScaleFactor());
		GlStateManager.translate(0, 0, -(scale * 100));

		GlStateManager.scale(scale, scale, scale);

		oldZLevel = mc.getRenderItem().zLevel;
		mc.getRenderItem().zLevel = -50;

		GlStateManager.enableRescaleNormal();
		GlStateManager.enableColorMaterial();
		GlStateManager.enableDepth();
		GlStateManager.enableBlend();
		GlStateManager.tryBlendFuncSeparate(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, GL11.GL_SRC_ALPHA, GL11.GL_ONE);
		GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		GlStateManager.disableAlpha();
	}

	public static void tearDownRenderState () {
		GlStateManager.disableLighting();
		GlStateManager.disableColorMaterial();
		GlStateManager.disableDepth();
		GlStateManager.disableBlend();

		Minecraft.getMinecraft().getRenderItem().zLevel = oldZLevel;
	}

	public static String render (final ItemStack stack, final String baseFilename, final File folder) {
		final Minecraft mc = Minecraft.getMinecraft();

		// render the item
		GlStateManager.pushMatrix();
		GlStateManager.clearColor(0, 0, 0, 0);
		GlStateManager.clear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
		mc.getRenderItem().renderItemAndEffectIntoGUI(stack, 0, 0);
		GlStateManager.popMatrix();

		try {
			/*
			 * We need to flip the image over here, because again, GL Y-zero is the bottom,
			 * so it's "Y-up". Minecraft's Y-zero is the top, so it's "Y-down". Since
			 * readPixels is Y-up, our Y-down render is flipped. It's easier to do this
			 * operation on the resulting image than to do it with GL transforms. Not
			 * faster, just easier.
			 */
			final BufferedImage img = createFlipped(readPixels(SIZE, SIZE));

			String filename = baseFilename + ".png";
			File f = new File(folder, filename);
			int i = 2;
			while (f.exists()) {
				f = new File(folder, filename = baseFilename + "_" + i + ".png");
				i++;
			}

			Files.createParentDirs(f);
			f.createNewFile();
			ImageIO.write(img, "PNG", f);

			return filename;

		} catch (Exception ex) {
			ex.printStackTrace();
			return null;
		}
	}

	public static BufferedImage readPixels (final int width, final int height) throws InterruptedException {
		/*
		 * Make sure we're reading from the back buffer, not the front buffer. The front
		 * buffer is what is currently on-screen, and is useful for screenshots.
		 */
		GL11.glReadBuffer(GL11.GL_BACK);
		// Allocate a native data array to fit our pixels
		final ByteBuffer buf = BufferUtils.createByteBuffer(width * height * 4);
		// And finally read the pixel data from the GPU...
		GL11.glReadPixels(0, Minecraft.getMinecraft().displayHeight - height, width, height, GL12.GL_BGRA, GL11.GL_UNSIGNED_BYTE, buf);
		// ...and turn it into a Java object we can do things to.
		final BufferedImage img = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
		final int[] pixels = new int[width * height];
		buf.asIntBuffer().get(pixels);
		img.setRGB(0, 0, width, height, pixels, 0, width);
		return img;
	}

	private static BufferedImage createFlipped (final BufferedImage image) {
		final AffineTransform at = new AffineTransform();
		/*
		 * Creates a compound affine transform, instead of just one, as we need to
		 * perform two transformations.
		 * 
		 * The first one is to scale the image to 100% width, and -100% height. (That's
		 * *negative* 100%.)
		 */
		at.concatenate(AffineTransform.getScaleInstance(1, -1));
		/**
		 * We then need to translate the image back up by it's height, as flipping it
		 * over moves it off the bottom of the canvas.
		 */
		at.concatenate(AffineTransform.getTranslateInstance(0, -image.getHeight()));
		return createTransformed(image, at);
	}

	private static BufferedImage createTransformed (final BufferedImage image, final AffineTransform at) {
		// Create a blank image with the same dimensions as the old one...
		final BufferedImage newImage = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_INT_ARGB);
		// ...get its renderer...
		final Graphics2D g = newImage.createGraphics();
		// ...and draw the old image on top of it with our transform.
		g.transform(at);
		g.drawImage(image, 0, 0, null);
		g.dispose();
		return newImage;
	}
}
