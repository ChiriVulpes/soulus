package yuudaari.soulus.common.block.fossil;

import yuudaari.soulus.common.registration.Registration;
import yuudaari.soulus.common.util.Material;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.MapColor;

public class FossilEndStone extends Registration.Block {

	public FossilEndStone () {
		this("fossil_end_stone");
	}

	public FossilEndStone (String name) {
		super(name, new Material(MapColor.SAND));
		setHasItem();
		setHardness(3.0F);
		setResistance(15.0F);
		setHarvestLevel("pickaxe", 1);
		setSoundType(SoundType.STONE);
	}
}
