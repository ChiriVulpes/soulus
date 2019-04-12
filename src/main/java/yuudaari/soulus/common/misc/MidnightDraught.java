package yuudaari.soulus.common.misc;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.init.PotionTypes;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.potion.PotionType;
import net.minecraft.potion.PotionUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.common.brewing.BrewingRecipeRegistry;
import net.minecraftforge.event.entity.player.PlayerWakeUpEvent;
import net.minecraftforge.event.entity.player.SleepingLocationCheckEvent;
import net.minecraftforge.event.entity.player.SleepingTimeCheckEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.Event;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import yuudaari.soulus.Soulus;
import yuudaari.soulus.common.registration.ItemRegistry;
import yuudaari.soulus.common.util.Translation;

@Mod.EventBusSubscriber
public class MidnightDraught {

	private static long startSleepingTime = -1;

	@SubscribeEvent
	public static void onTryWakeUp (final PlayerWakeUpEvent event) {
		final World world = event.getEntityPlayer().world;
		if (world.isRemote || startSleepingTime == -1)
			return;

		for (final EntityPlayer player : event.getEntityPlayer().world.playerEntities) {
			if (player.sleepTimer < 99 || !player.isPotionActive(PotionSleepy.INSTANCE)) {
				return;
			}
		}

		// if they're starting to sleep in the beginning of the night, allow them to sleep the whole night
		if (startSleepingTime > 12500L && startSleepingTime < 19000L)
			world.setWorldTime(23500L);

		// if they're starting to sleep in the beginning of the day, allow them to sleep the whole day
		else if ((startSleepingTime > 23500L || startSleepingTime > 0) && startSleepingTime < 6000L)
			world.setWorldTime(13000L);

		// if it's any other time, just add 8000 ticks
		else
			world.setWorldTime((startSleepingTime + 8000L) % 24000L);

		startSleepingTime = -1;

		for (final EntityPlayer player : event.getEntityPlayer().world.playerEntities) {
			player.removePotionEffect(PotionSleepy.INSTANCE);
			player.removePotionEffect(Potion.REGISTRY.getObject(new ResourceLocation("blindness")));
			player.removePotionEffect(Potion.REGISTRY.getObject(new ResourceLocation("slowness")));
		}
	}

	@SubscribeEvent
	public static void onSleepTimeCheck (final SleepingTimeCheckEvent event) {
		final EntityPlayer player = event.getEntityPlayer();
		if (player.isPotionActive(PotionSleepy.INSTANCE)) {
			event.setResult(Event.Result.ALLOW);
			startSleepingTime = player.world.getWorldTime() % 24000L;
		}
	}

	@SubscribeEvent
	public static void onSleepLocationCheck (final SleepingLocationCheckEvent event) {
		if (event.getEntityPlayer().isPotionActive(PotionSleepy.INSTANCE))
			event.setResult(Event.Result.ALLOW);
	}

	public static class PotionSleepy extends Potion {

		public static final ResourceLocation POTION_SLEEPY_ICON = new ResourceLocation(Soulus.MODID, "textures/gui/potion_effect/sleepy.png");

		public static final PotionSleepy INSTANCE = new PotionSleepy();

		public PotionSleepy () {
			super(false, 0x1A163E);
			setRegistryName(Soulus.MODID, "sleepy");
		}

		@Override
		public String getName () {
			return Translation.localize("potion.effect.soulus:sleepy.name");
		}

		@Override
		public void renderInventoryEffect (int x, int y, PotionEffect effect, Minecraft mc) {
			mc.getTextureManager().bindTexture(POTION_SLEEPY_ICON);
			Gui.drawModalRectWithCustomSizedTexture(x + 6, y + 7, 0, 0, 16, 16, 16, 16);
		}

		@Override
		public void renderHUDEffect (int x, int y, PotionEffect effect, Minecraft mc, float alpha) {
			mc.getTextureManager().bindTexture(POTION_SLEEPY_ICON);
			Gui.drawModalRectWithCustomSizedTexture(x + 3, y + 3, 0, 0, 16, 16, 16, 16);
		}
	}

	public static final PotionType POTION_TYPE_SLEEPY = new PotionType(Soulus.MODID + ":sleepy", new PotionEffect[] {
		new PotionEffect(Potion.REGISTRY.getObject(new ResourceLocation("blindness")), 1200),
		new PotionEffect(Potion.REGISTRY.getObject(new ResourceLocation("slowness")), 1200),
		new PotionEffect(PotionSleepy.INSTANCE, 1200),
	}).setRegistryName(Soulus.MODID, "sleepy");

	public static void register () {
		ForgeRegistries.POTIONS.register(PotionSleepy.INSTANCE);
		ForgeRegistries.POTION_TYPES.register(POTION_TYPE_SLEEPY);
		final ItemStack potionInput = PotionUtils.addPotionToItemStack(new ItemStack(Items.POTIONITEM), PotionTypes.AWKWARD);
		BrewingRecipeRegistry.addRecipe(potionInput, ItemRegistry.DUST_MIDNIGHT.getItemStack(), getItemStack());
	}

	public static ItemStack getItemStack () {
		final ItemStack stack = new ItemStack(Items.POTIONITEM);
		PotionUtils.addPotionToItemStack(stack, POTION_TYPE_SLEEPY);
		return stack;
	}
}
