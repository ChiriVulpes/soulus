package yuudaari.soulus.common.block.fossil;

import yuudaari.soulus.common.registration.Registration;
import yuudaari.soulus.common.util.Material;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.MapColor;

public class FossilSandRed extends Registration.Block {

	public FossilSandRed () {
		this("fossil_sand_red_dry");
	}

	public FossilSandRed (String name) {
		super(name, new Material(MapColor.RED).setToolNotRequired());
		setHasItem();
		setHardness(0.5F);
		setHarvestLevel("shovel", 0);
		setSoundType(SoundType.SAND);
	}
}
