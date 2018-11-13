/**
 * The following class was an experiment in the hopes that I could dynamically generate advancements based on the
 * current config files. It didn't work because of the way advancement loading works. If there was a way to register
 * advancements via code it would work fine. Alas.
 */

// package yuudaari.soulus.common.advancement;

// import java.util.Map;
// import net.minecraft.advancements.Advancement;
// import net.minecraft.advancements.AdvancementManager;
// import net.minecraft.advancements.AdvancementRewards;
// import net.minecraft.advancements.Criterion;
// import net.minecraft.advancements.DisplayInfo;
// import net.minecraft.util.ResourceLocation;
// import yuudaari.soulus.Soulus;

// public class AdvancementRegistrar {

// public static Advancement create (String id, String parent, DisplayInfo display, Map<String, Criterion> criteria) {
// return create(id, parent, display, criteria, new String[][] {}, AdvancementRewards.EMPTY);
// }

// public static Advancement create (String id, String parent, DisplayInfo display, Map<String, Criterion> criteria,
// String[][] requirements) {
// return create(id, parent, display, criteria, requirements, AdvancementRewards.EMPTY);
// }

// public static Advancement create (String id, String parent, DisplayInfo display, Map<String, Criterion> criteria,
// String[][] requirements, AdvancementRewards rewards) {
// ResourceLocation resourceId = new ResourceLocation(Soulus.MODID, id);
// Advancement parentAdvancement = AdvancementManager.ADVANCEMENT_LIST
// .getAdvancement(new ResourceLocation(Soulus.MODID, parent));
// return new Advancement(resourceId, parentAdvancement, display, rewards, criteria, requirements);
// }

// public static void register (Advancement advancement) {
// AdvancementManager.ADVANCEMENT_LIST.nonRoots.add(advancement);
// if (AdvancementManager.ADVANCEMENT_LIST.listener != null) {
// AdvancementManager.ADVANCEMENT_LIST.listener.nonRootAdvancementAdded(advancement);
// }
// }
// }
