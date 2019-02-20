package yuudaari.soulus.common.util;

import yuudaari.soulus.Soulus;
import yuudaari.soulus.common.CreativeTab;
import yuudaari.soulus.common.compat.JeiDescriptionRegistry;
import yuudaari.soulus.common.config.item.ConfigFood;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.color.IItemColor;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.EnumAction;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.stats.StatList;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.SoundCategory;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ModItem extends Item implements IModThing {

	public interface ConsumeHandler {

		void consume (ItemStack item, World world, EntityLivingBase entity);
	}

	public interface CanConsumeHandler {

		boolean canConsume (World worldIn, EntityPlayer playerIn, EnumHand handIn);
	}

	protected Boolean glint = false;
	private String name;
	private List<String> oreDicts = new ArrayList<>();
	private IFoodConfigGetter foodConfigGetter = null;
	public ConsumeHandler foodHandler;
	public CanConsumeHandler foodCanEatHandler;

	public static interface IFoodConfigGetter {

		public ConfigFood get ();
	}

	public ModItem (String name) {
		setName(name);
	}

	public ModItem (String name, Integer maxStackSize) {
		this(name);
		setMaxStackSize(maxStackSize);
	}

	@Override
	public CreativeTabs getCreativeTab () {
		return CreativeTab.INSTANCE;
	}

	public String getName () {
		return name;
	}

	public void setName (String name) {
		this.name = name;
		setRegistryName(Soulus.MODID, name);
		setUnlocalizedName(getRegistryName().toString());
	}

	public ModItem addOreDict (String... name) {
		for (String dict : name)
			oreDicts.add(dict);

		return this;
	}

	public ModItem removeOreDict (String... name) {
		for (String dict : name)
			oreDicts.remove(dict);

		return this;
	}

	public List<String> getOreDicts () {
		return oreDicts;
	}

	public ModItem setHasGlint () {
		glint = true;
		return this;
	}

	@SideOnly(Side.CLIENT)
	@Override
	public boolean hasEffect (ItemStack stack) {
		return glint;
	}

	private int burnTime = 0;

	@Override
	public int getItemBurnTime (ItemStack itemStack) {
		return burnTime;
	}

	public ModItem setBurnTime (int burnTime) {
		this.burnTime = burnTime;
		return this;
	}

	@SideOnly(Side.CLIENT)
	public void registerColorHandler (IItemColor itemColor) {
		Soulus.onInit( (FMLInitializationEvent event) -> {
			Minecraft.getMinecraft().getItemColors().registerItemColorHandler(itemColor, this);
		});
	}

	public boolean isFood () {
		return foodConfigGetter != null;
	}

	public void setFood (final IFoodConfigGetter getter) {
		foodConfigGetter = getter;
	}

	@Override
	public ActionResult<ItemStack> onItemRightClick (World worldIn, EntityPlayer playerIn, EnumHand handIn) {
		ItemStack itemstack = playerIn.getHeldItem(handIn);
		if (isFood()) {
			if ((foodCanEatHandler != null && foodCanEatHandler.canConsume(worldIn, playerIn, handIn)) || (playerIn
				.canEat(foodConfigGetter.get().isAlwaysEdible()) && itemstack
					.getCount() >= foodConfigGetter.get().getQuantity())) {
				playerIn.setActiveHand(handIn);
				return new ActionResult<ItemStack>(EnumActionResult.SUCCESS, itemstack);
			}
		}
		return new ActionResult<ItemStack>(EnumActionResult.FAIL, itemstack);
	}

	@Override
	public EnumAction getItemUseAction (ItemStack stack) {
		return EnumAction.EAT;
	}

	@Override
	public int getMaxItemUseDuration (ItemStack stack) {
		return isFood() ? foodConfigGetter.get().getDuration() : super.getMaxItemUseDuration(stack);
	}

	@Override
	public ItemStack onItemUseFinish (ItemStack stack, World world, EntityLivingBase entity) {
		if (entity instanceof EntityPlayer) {
			EntityPlayer player = (EntityPlayer) entity;
			player.getFoodStats().addStats(foodConfigGetter.get().getAmount(), foodConfigGetter.get().getSaturation());
			world
				.playSound((EntityPlayer) null, player.posX, player.posY, player.posZ, SoundEvents.ENTITY_PLAYER_BURP, SoundCategory.PLAYERS, 0.5F, world.rand
					.nextFloat() * 0.1F + 0.9F);

			for (ModPotionEffect effect : foodConfigGetter.get().getEffects())
				effect.apply(player);

			if (foodHandler != null)
				foodHandler.consume(stack, world, entity);

			player.addStat(StatList.getObjectUseStats(this));

			if (player instanceof EntityPlayerMP) {
				CriteriaTriggers.CONSUME_ITEM.trigger((EntityPlayerMP) player, stack);
			}
		}

		stack.shrink(foodConfigGetter.get().getQuantity());
		return stack;
	}

	public final NonNullList<ItemStack> getSubItems () {
		final NonNullList<ItemStack> items = NonNullList.create();
		getSubItems(CreativeTab.INSTANCE, items);
		return items;
	}

	public boolean hasDescription = false;

	public ModItem setHasDescription () {
		hasDescription = true;
		return this;
	}

	@Override
	public void onRegisterDescription (final JeiDescriptionRegistry registry) {
		if (!hasDescription) return;

		final Ingredient ing = getDescriptionIngredient();
		final String name = getDescriptionRegistryName();
		if (name != null)
			registry.add(ing == null ? Ingredient.fromItem(this) : ing, name);
		else
			registry.add(this);
	}

	public Ingredient getDescriptionIngredient () {
		return null;
	}

	public String getDescriptionRegistryName () {
		return null;
	}
}
