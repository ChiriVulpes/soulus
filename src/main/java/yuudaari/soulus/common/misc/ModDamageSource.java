package yuudaari.soulus.common.misc;

import java.util.UUID;
import com.mojang.authlib.GameProfile;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EntityDamageSource;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.util.FakePlayer;
import yuudaari.soulus.Soulus;

public class ModDamageSource {

	public static final DamageSource CRYSTAL_BLOOD = new DamageSource(Soulus.MODID + ":crystal_blood");
	public static final DamageSource SKEWER = new DamageSource(Soulus.MODID + ":skewer");
	private static DamageSource skewerPlayerDamageSource = null;
	private static FakePlayer skewerFakePlayer;
	private static UUID skewerFakePlayerUuid;

	public static DamageSource getSkewerPlayer (WorldServer world) {
		if (skewerPlayerDamageSource == null) {
			skewerFakePlayerUuid = UUID.randomUUID();
			skewerFakePlayer = new FakePlayer(world, new GameProfile(skewerFakePlayerUuid, "composer_tile_entity"));
			skewerPlayerDamageSource = new EntityDamageSource(Soulus.MODID + ":skewer_player", skewerFakePlayer);
		}
		return skewerPlayerDamageSource;
	}
}
