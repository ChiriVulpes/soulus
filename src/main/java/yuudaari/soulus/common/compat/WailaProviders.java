package yuudaari.soulus.common.compat;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;

public class WailaProviders {

	public static class Accessor {
		public TileEntity te;
		public EntityPlayer player;
		public int metadata;

		public Accessor(TileEntity te, EntityPlayer player, int metadata) {
			this.te = te;
			this.player = player;
			this.metadata = metadata;
		}
	}

	public static List<Class<? extends Block>> providers = new ArrayList<>();
}
