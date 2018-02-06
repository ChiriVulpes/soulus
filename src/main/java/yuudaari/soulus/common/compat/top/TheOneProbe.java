package yuudaari.soulus.common.compat.top;

import mcjty.theoneprobe.Tools;
import mcjty.theoneprobe.api.*;
import mcjty.theoneprobe.config.Config;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.event.FMLInterModComms;
import yuudaari.soulus.Soulus;
import yuudaari.soulus.common.util.IBlock;
import yuudaari.soulus.common.util.Logger;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.Nullable;

public class TheOneProbe {

	private static boolean registered;

	public static void register () {
		if (registered)
			return;
		registered = true;
		FMLInterModComms.sendFunctionMessage("theoneprobe", "getTheOneProbe", GetTheOneProbe.class.getName());
	}


	public static class GetTheOneProbe implements com.google.common.base.Function<ITheOneProbe, Void> {

		public static final String ID = Soulus.MODID + ":probe";
		public static ITheOneProbe probe;

		@Nullable
		@Override
		public Void apply (ITheOneProbe theOneProbe) {
			probe = theOneProbe;
			Logger.info("Enabled support for The One Probe");

			probe.registerBlockDisplayOverride(new IBlockDisplayOverride() {

				@Override
				public boolean overrideStandardInfo (ProbeMode mode, IProbeInfo probeInfo, EntityPlayer player, World world, IBlockState blockstate, IProbeHitData data) {
					if (!(blockstate.getBlock() instanceof IBlock)) return false;

					IBlock block = (IBlock) blockstate.getBlock();
					IBlock.DataAccessorTOP accessor = new IBlock.DataAccessorTOP(player, data);

					ItemStack stack = block.getWailaStack(accessor);
					if (stack == null) return false;

					if (Tools.show(mode, Config.getRealConfig().getShowModName())) {
						probeInfo.horizontal()
							.item(stack)
							.vertical()
							.itemLabel(stack)
							.text(TextStyleClass.MODNAME + Soulus.NAME);
					} else {
						probeInfo
							.horizontal(probeInfo.defaultLayoutStyle().alignment(ElementAlignment.ALIGN_CENTER))
							.item(stack)
							.itemLabel(stack);
					}

					return true;
				}
			});

			probe.registerProvider(new IProbeInfoProvider() {

				@Override
				public String getID () {
					return ID;
				}

				@Override
				public void addProbeInfo (ProbeMode mode, IProbeInfo probeInfo, EntityPlayer player, World world, IBlockState blockState, IProbeHitData data) {

					if (blockState.getBlock() instanceof IBlock) {
						IBlock block = (IBlock) blockState.getBlock();
						IBlock.DataAccessorTOP accessor = new IBlock.DataAccessorTOP(player, data);

						List<String> currentTooltip = new ArrayList<>();
						currentTooltip = block.getWailaTooltip(currentTooltip, accessor);

						for (String str : currentTooltip) {
							probeInfo.text(str);
						}
					}
				}
			});

			return null;
		}
	}
}
