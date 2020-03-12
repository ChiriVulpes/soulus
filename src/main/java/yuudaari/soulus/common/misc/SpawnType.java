package yuudaari.soulus.common.misc;

import java.util.Arrays;
import net.minecraft.entity.EntityLivingBase;
import yuudaari.soulus.common.network.SoulsPacketHandler;
import yuudaari.soulus.common.network.packet.client.ApplySpawnType;
import yuudaari.soulus.common.util.nbt.NBTObject;

public enum SpawnType {

	ALL ((byte) 0, "all"),
	SPAWNED ((byte) 1, "spawned"),
	SUMMONED ((byte) 2, "summoned"),
	SUMMONED_MALICE ((byte) 3, "summoned_malice"),
	SPAWNED_FROM_EGG ((byte) 4, "spawned_from_egg");

	private static final String SPAWN_TYPE_KEY = "soulus:spawn_whitelisted";

	public static SpawnType get (final EntityLivingBase entity) {
		final byte spawnType = entity.getEntityData().getByte(SPAWN_TYPE_KEY);
		if (spawnType == 0)
			return null;

		return Arrays.stream(SpawnType.values())
			.filter(type -> type.id == spawnType)
			.findFirst()
			.orElse(null);
	}

	public static SpawnType fromName (final String name) {
		return Arrays.stream(SpawnType.values())
			.filter(type -> type.name.equals(name))
			.findFirst()
			.orElse(null);
	}

	private byte id;
	private String name;

	private SpawnType (final byte id, final String name) {
		this.id = id;
		this.name = name;
	}

	public byte getId () {
		return id;
	}

	public String getName () {
		return name;
	}

	public void apply (final NBTObject entityNBT) {
		entityNBT.computeObject("ForgeData", key -> new NBTObject())
			.setByte(SPAWN_TYPE_KEY, (byte) id);
	}

	public void apply (final EntityLivingBase entity, final boolean sync) {
		entity.getEntityData().setByte(SPAWN_TYPE_KEY, (byte) id);
		if (sync)
			SoulsPacketHandler.INSTANCE.sendToAll(new ApplySpawnType(this, entity));
	}

	public boolean matches (final EntityLivingBase entity) {
		return this == SpawnType.ALL || this == get(entity);
	}
}
