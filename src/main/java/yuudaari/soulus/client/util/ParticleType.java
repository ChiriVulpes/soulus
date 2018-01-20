package yuudaari.soulus.client.util;

import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.IParticleFactory;
import yuudaari.soulus.client.particle.*;

public enum ParticleType {
	BLOOD(0, new ParticleBlood.Factory()), //
	MOB_POOF(1, new ParticleMobPoof.Factory()), //
	BONEMEAL_NETHER(2, new ParticleBlackenedBonemeal.Factory()), //
	CRYSTAL_DARK(3, new ParticleCrystalDark.Factory());

	private int id = 611735000;
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
