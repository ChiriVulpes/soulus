package yuudaari.soulus.common.util;

import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.MobEffects;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.GameType;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import yuudaari.soulus.common.util.serializer.Serializable;
import yuudaari.soulus.common.util.serializer.Serialized;

@Serializable
public class ModPotionEffect {

	@Serialized public String effect;
	@Serialized public int duration;
	@Serialized public float chance;
	@Serialized public boolean tiered = false;

	/**
	 * Do not use, for serialization only
	 */
	public ModPotionEffect () {
	}

	public ModPotionEffect (final String which) {
		this(which, 200);
	}

	public ModPotionEffect (final String which, final int duration) {
		this(which, duration, 1);
	}

	public ModPotionEffect (final String which, final int duration, final float chance) {
		this.effect = which;
		this.duration = duration;
		this.chance = chance;
	}

	public ModPotionEffect setIsTiered () {
		tiered = true;
		return this;
	}

	@Nullable
	public PotionEffect get (final Random rand, final int amplifier) {
		return rand.nextFloat() < chance ? new PotionEffect(getPotion(), (int) (duration * (tiered ? Math.pow(2, amplifier) : 1)), amplifier) : null;
	}

	private Potion getPotion () {
		return ForgeRegistries.POTIONS.getValue(new ResourceLocation(effect));
	}

	public void apply (final EntityLivingBase entity) {
		final PotionEffect existingEffect = entity.getActivePotionEffect(getPotion());
		if (existingEffect != null)
			entity.removePotionEffect(existingEffect.getPotion());

		final PotionEffect effect = get(entity.world.rand, existingEffect == null ? 0 : existingEffect.getAmplifier() + (tiered ? 1 : 0));
		if (effect != null) {
			entity.addPotionEffect(effect);
			if (entity instanceof EntityPlayer && effect.getPotion() == MobEffects.MINING_FATIGUE && effect.getAmplifier() > 3) {
				entity.sendMessage(new TextComponentTranslation("gameMode.changed", new Object[] {
					new TextComponentTranslation("gameMode." + GameType.ADVENTURE.getName())
				}));
			}
		}
	}
}
