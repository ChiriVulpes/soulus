package yuudaari.soulus.common.block.fossil;

import yuudaari.soulus.common.registration.Registration;
import yuudaari.soulus.common.util.Material;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.MapColor;

public class FossilSand extends Registration.Block {

	public FossilSand () {
		this("fossil_sand");
	}

	public FossilSand (String name) {
		super(name, new Material(MapColor.SAND).setToolNotRequired());
		setHasItem();
		setHardness(0.5F);
		setTool(Tool.SHOVEL, 0);
		setSoundType(SoundType.SAND);
	}
}
