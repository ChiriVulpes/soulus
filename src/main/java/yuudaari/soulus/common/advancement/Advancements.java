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
	// public static Advancement SUMMON_ALL_CREATURES;

	public static void registerTriggers () {
		CriteriaTriggers.register(BREAK_BLOCK = new BreakBlockTrigger());
		CriteriaTriggers.register(SUMMON_CREATURE = new SummonCreatureTrigger());
	}

	// @SubscribeEvent
	// public void registerAdvancements (WorldEvent.Load event) {
	// AdvancementRegistrar.register(SUMMON_ALL_CREATURES = summonAllCreaturesAdvancement());
	// }

	@SubscribeEvent
	public void onBlockBreak (BlockEvent.BreakEvent event) {
		EntityPlayer player = event.getPlayer();
		if (player instanceof EntityPlayerMP) {
			BREAK_BLOCK.trigger((EntityPlayerMP) player, event.getState());
		}
	}

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
