package yuudaari.soulus.common.advancement;

import net.minecraft.advancements.critereon.AbstractCriterionInstance;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.ResourceLocation;

public abstract class MatchableCriterionInstance<D> extends AbstractCriterionInstance {

	public MatchableCriterionInstance (ResourceLocation criterionIn) {
		super(criterionIn);
	}

	abstract boolean matches (EntityPlayerMP player, D data);
}
