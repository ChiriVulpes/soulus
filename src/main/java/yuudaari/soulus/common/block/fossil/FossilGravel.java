package yuudaari.soulus.common.block.fossil;

import yuudaari.soulus.common.registration.Registration;
import yuudaari.soulus.common.util.Material;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.MapColor;

public class FossilGravel extends Registration.Block {

	public FossilGravel () {
		this("fossil_gravel_scale");
	}

	public FossilGravel (String name) {
		super(name, new Material(MapColor.STONE).setToolNotRequired());
		setHasItem();
		setHardness(0.6F);
		setHarvestLevel("shovel", 0);
		setSoundType(SoundType.GROUND);
	}
}
