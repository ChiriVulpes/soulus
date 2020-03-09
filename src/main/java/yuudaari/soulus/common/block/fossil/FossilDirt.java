package yuudaari.soulus.common.block.fossil;

import yuudaari.soulus.common.util.Material;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.MapColor;
import yuudaari.soulus.common.registration.Registration;

public class FossilDirt extends Registration.Block {

	public FossilDirt () {
		this("fossil_dirt");
	}

	public FossilDirt (String name) {
		super(name, new Material(MapColor.DIRT).setToolNotRequired());
		setHasItem();
		setHardness(0.5F);
		setResistance(2.5F);
		setTool(Tool.SHOVEL, 0);
		setSoundType(SoundType.GROUND);
	}
}
