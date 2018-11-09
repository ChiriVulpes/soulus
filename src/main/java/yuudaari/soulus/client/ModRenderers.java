package yuudaari.soulus.client;

import java.util.Arrays;
import java.util.List;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.tileentity.TileEntity;
import yuudaari.soulus.client.render.ComposerRenderer;
import yuudaari.soulus.client.render.SoulInquirerRenderer;
import yuudaari.soulus.client.render.SoulTotemRenderer;
import yuudaari.soulus.client.render.SummonerRenderer;
import yuudaari.soulus.client.util.TileEntityRenderer;
import yuudaari.soulus.common.block.composer.ComposerCellTileEntity;
import yuudaari.soulus.common.block.composer.ComposerTileEntity;

public class ModRenderers {

	public static final SummonerRenderer SUMMONER_RENDERER = new SummonerRenderer();
	public static final SoulInquirerRenderer SOUL_INQUIRER_RENDERER = new SoulInquirerRenderer();
	public static final ComposerRenderer<ComposerTileEntity> COMPOSER_RENDERER = new ComposerRenderer<>(ComposerTileEntity.class);
	public static final ComposerRenderer<ComposerCellTileEntity> COMPOSER_CELL_RENDERER = new ComposerRenderer<>(ComposerCellTileEntity.class);
	public static final SoulTotemRenderer SOUL_TOTEM_RENDERER = new SoulTotemRenderer();

	public static final List<TileEntityRenderer<? extends TileEntity>> renderers = Arrays
		.asList(SUMMONER_RENDERER, COMPOSER_RENDERER, COMPOSER_CELL_RENDERER, SOUL_TOTEM_RENDERER, SOUL_INQUIRER_RENDERER);

	public static void init () {
		for (TileEntityRenderer<? extends TileEntity> renderer : renderers) {
			TileEntityRendererDispatcher.instance.renderers.put(renderer.getTileEntityClass(), renderer);
			renderer.setRendererDispatcher(TileEntityRendererDispatcher.instance);
		}
	}
}
