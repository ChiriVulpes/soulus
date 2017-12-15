package yuudaari.souls.client.util;

import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.IParticleFactory;
import yuudaari.souls.client.particle.ParticleBlood;

public enum ParticleType {
	BLOOD(0, new ParticleBlood.Factory());

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