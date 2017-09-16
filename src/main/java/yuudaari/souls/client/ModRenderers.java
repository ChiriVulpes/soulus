package yuudaari.souls.client;

import java.util.Arrays;
import java.util.List;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.tileentity.TileEntity;
import yuudaari.souls.client.render.TileEntitySummonerRenderer;
import yuudaari.souls.client.util.TileEntityRenderer;

public class ModRenderers {
	public static final TileEntitySummonerRenderer SUMMONER_RENDERER = new TileEntitySummonerRenderer();

	public static final List<TileEntityRenderer<? extends TileEntity>> renderers = Arrays.asList(SUMMONER_RENDERER);

	public static void init() {
		for (TileEntityRenderer<? extends TileEntity> renderer : renderers) {
			TileEntityRendererDispatcher.instance.renderers.put(renderer.getTileEntityClass(), renderer);
		}
	}
}