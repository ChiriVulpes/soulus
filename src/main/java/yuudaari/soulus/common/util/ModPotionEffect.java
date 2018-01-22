package yuudaari.soulus.common.util;

import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import yuudaari.soulus.common.util.serializer.Serializable;
import yuudaari.soulus.common.util.serializer.Serialized;

@Serializable
public class ModPotionEffect {

	@Serialized public String effect;
	@Serialized public int duration;
	@Serialized public float chance;

	/**
	 * Do not use, for serialization only
	 */
	public ModPotionEffect () {}

	public ModPotionEffect (String which) {
		this(which, 200);
	}

	public ModPotionEffect (String which, int duration) {
		this(which, duration, 1);
	}

	public ModPotionEffect (String which, int duration, float chance) {
		this.effect = which;
		this.duration = duration;
		this.chance = chance;
	}

	@Nullable
	public PotionEffect get (Random rand) {
		return rand.nextFloat() < chance ? new PotionEffect(ForgeRegistries.POTIONS
			.getValue(new ResourceLocation(effect)), duration) : null;
	}

	public void apply (EntityLivingBase entity) {
		PotionEffect effect = get(entity.world.rand);

		if (effect != null) {
			entity.addPotionEffect(effect);
		}
	}
}
