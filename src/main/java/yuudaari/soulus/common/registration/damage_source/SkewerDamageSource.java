package yuudaari.soulus.common.registration.damage_source;

import com.mojang.authlib.GameProfile;
import net.minecraft.entity.Entity;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.event.entity.living.LootingLevelEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import yuudaari.soulus.Soulus;
import yuudaari.soulus.common.block.skewer.Skewer;
import yuudaari.soulus.common.block.skewer.SkewerTileEntity;

@Mod.EventBusSubscriber(modid = Soulus.MODID)
public class SkewerDamageSource extends DamageSource {

	public final SkewerTileEntity skewer;


	public SkewerDamageSource (final SkewerTileEntity skewer) {
		super(Soulus.MODID + ":skewer");
		this.skewer = skewer;
	}

	private FakePlayer FAKE_PLAYER = null;

	private FakePlayer getFakePlayer (final WorldServer world) {
		if (FAKE_PLAYER == null)
			FAKE_PLAYER = new FakePlayer(world, new GameProfile(null, Soulus.MODID + ":skewer_damage_source")) {

				@Override
				public float getLuck () {
					return 300;
				}
			};

		return FAKE_PLAYER;
	}

	@Override
	public Entity getTrueSource () {
		final World world = skewer.getWorld();
		if (!world.isRemote && skewer.upgrades.get(Skewer.Upgrade.PLAYER) > 0)
			return getFakePlayer((WorldServer) world);

		return null;
	}


	////////////////////////////////////
	// Events
	//

	@SubscribeEvent
	public static void onLooting (final LootingLevelEvent event) {
		if (!(event.getDamageSource() instanceof SkewerDamageSource))
			return;

		final SkewerTileEntity skewer = ((SkewerDamageSource) event.getDamageSource()).skewer;
		event.setLootingLevel(skewer.getLootingLevel());
	}
}
