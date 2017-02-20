package com.kallgirl.souls.common.world;

import com.kallgirl.souls.common.ModObjects;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.event.terraingen.PopulateChunkEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Mod.EventBusSubscriber
public class SummonerReplacer {
	@SubscribeEvent
	public static void populateChunkPost(PopulateChunkEvent.Post event) {
		World world = event.getWorld();
		int cX = event.getChunkX() << 4;
		int cZ = event.getChunkZ() << 4;
		int worldHeight = world.getHeight();
		for (int y = 0; y < worldHeight; y++) {
			for (int iX = 0; iX < 16; iX++) {
				for (int iZ = 0; iZ < 16; iZ++) {
					BlockPos pos = new BlockPos(cX + iX, y, cZ + iZ);
					IBlockState blockState = world.getBlockState(pos);
					if (blockState.getBlock() == Blocks.MOB_SPAWNER) {
						world.setBlockState(pos, ModObjects.getBlock("summonerEmpty").getDefaultState());
					}
				}
			}
		}
	}
}
