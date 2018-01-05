package yuudaari.soulus.client.util;

import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.IParticleFactory;
import yuudaari.soulus.client.particle.ParticleBlood;
import yuudaari.soulus.client.particle.ParticleBlackenedBonemeal;

public enum ParticleType {
	BLOOD(0, new ParticleBlood.Factory()), BLACKENED_BONEMEAL(1, new ParticleBlackenedBonemeal.Factory());

	private int id = 621785000;
	private IParticleFactory factory;

	private ParticleType(int id, IParticleFactory factory) {
		this.id += id;
		this.factory = factory;

		Minecraft.getMinecraft().effectRenderer.registerParticle(this.id, this.factory);
	}

	public int getId() {
		return id;
	}

	public IParticleFactory getFactory() {
		return factory;
	}
}