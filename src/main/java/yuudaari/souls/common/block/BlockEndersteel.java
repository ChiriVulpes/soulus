package yuudaari.souls.common.block;

import yuudaari.souls.common.util.Material;
import yuudaari.souls.common.util.ModBlock;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.MapColor;

public class BlockEndersteel extends ModBlock {
	public BlockEndersteel() {
		super("block_endersteel", new Material(MapColor.GRASS));
		setHasItem();
		setHardness(5F);
		setResistance(30F);
		setHarvestLevel("pickaxe", 1);
		setSoundType(SoundType.METAL);
	}
}
