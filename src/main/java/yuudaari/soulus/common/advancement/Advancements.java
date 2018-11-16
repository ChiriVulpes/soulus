package yuudaari.soulus.common.advancement;

import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import yuudaari.soulus.Soulus;
import yuudaari.soulus.common.config.ConfigInjected;
import yuudaari.soulus.common.config.ConfigInjected.Inject;
import yuudaari.soulus.common.config.essence.ConfigEssences;

@ConfigInjected(Soulus.MODID)
@Mod.EventBusSubscriber(modid = Soulus.MODID)
public class Advancements {

	@Inject public static ConfigEssences CONFIG;

	public static BreakBlockTrigger BREAK_BLOCK;
	public static SummonCreatureTrigger SUMMON_CREATURE;
	public static BreakSummonerMaliceTrigger BREAK_SUMMONER_MALICE;
	public static SkewerKillTrigger SKEWER_KILL;
	public static UpgradeTrigger UPGRADE;
	public static CrystalBloodPrickTrigger CRYSTAL_BLOOD_PRICK;
	public static ComposeTrigger COMPOSE;
	public static TeleportTrigger TELEPORT;
	public static StyleSummonerTrigger STYLE_SUMMONER;
	public static ConstructTrigger CONSTRUCT;

	public static void registerTriggers () {
		CriteriaTriggers.register(BREAK_BLOCK = new BreakBlockTrigger());
		CriteriaTriggers.register(SUMMON_CREATURE = new SummonCreatureTrigger());
		CriteriaTriggers.register(BREAK_SUMMONER_MALICE = new BreakSummonerMaliceTrigger());
		CriteriaTriggers.register(SKEWER_KILL = new SkewerKillTrigger());
		CriteriaTriggers.register(UPGRADE = new UpgradeTrigger());
		CriteriaTriggers.register(CRYSTAL_BLOOD_PRICK = new CrystalBloodPrickTrigger());
		CriteriaTriggers.register(COMPOSE = new ComposeTrigger());
		CriteriaTriggers.register(TELEPORT = new TeleportTrigger());
		CriteriaTriggers.register(STYLE_SUMMONER = new StyleSummonerTrigger());
		CriteriaTriggers.register(CONSTRUCT = new ConstructTrigger());
	}

	@SubscribeEvent
	public void onBlockBreak (BlockEvent.BreakEvent event) {
		EntityPlayer player = event.getPlayer();
		if (player instanceof EntityPlayerMP) {
			BREAK_BLOCK.trigger((EntityPlayerMP) player, event.getState());
		}
	}

	// public static Advancement SUMMON_ALL_CREATURES;

	// @SubscribeEvent
	// public void registerAdvancements (WorldEvent.Load event) {
	// AdvancementRegistrar.register(SUMMON_ALL_CREATURES = summonAllCreaturesAdvancement());
	// }

	// private static Advancement summonAllCreaturesAdvancement () {
	// ItemStack icon = Essence.getStack("minecraft:creeper");
	// ITextComponent title = new TextComponentTranslation("advancement.soulus:summon_creature_all.title");
	// ITextComponent description = new TextComponentTranslation("advancement.soulus:summon_creature_all.description");
	// DisplayInfo display = new DisplayInfo(icon, title, description, null, FrameType.GOAL, true, true, false);

	// Map<String, Criterion> criteria = new HashMap<>();

	// for (ConfigEssence essence : CONFIG.essences) {
	// criteria.put("summon_" + essence, new Criterion(new SummonCreatureTrigger.Instance(essence.essence)));
	// }

	// return AdvancementRegistrar.create("summon_creature_all", "summon_creature", display, criteria);
	// }

}
