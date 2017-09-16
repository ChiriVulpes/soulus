package yuudaari.souls.common.util;

import yuudaari.souls.Souls;
import yuudaari.souls.common.CreativeTab;
import yuudaari.souls.common.recipe.Recipe;
import yuudaari.souls.common.util.IModItem;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.color.IItemColor;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.EnumAction;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraft.stats.StatList;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ModItem extends Item implements IModItem {
	public interface ConsumeHandler {
		void consume(ItemStack item, World world, EntityLivingBase entity);
	}

	public interface CanConsumeHandler {
		boolean canConsume(World worldIn, EntityPlayer playerIn, EnumHand handIn);
	}

	protected Boolean glint = false;
	private String name;
	private List<String> oreDicts = new ArrayList<>();
	private boolean foodIsFood = false;
	private boolean foodAlwaysEdible;
	private int foodDuration = 32;
	private int foodAmount;
	private float foodSaturation;
	private ConsumeHandler foodHandler;
	private int foodQuantity = 1;
	private PotionEffect[] foodEffects;
	private CanConsumeHandler foodCanEatHandler;

	public ModItem(String name) {
		setName(name);
		setCreativeTab(CreativeTab.INSTANCE);
	}

	public ModItem(String name, Integer maxStackSize) {
		this(name);
		setMaxStackSize(maxStackSize);
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
		setRegistryName(Souls.MODID, name);
		setUnlocalizedName(getRegistryName().toString());
	}

	public void addOreDict(String name) {
		oreDicts.add(name);
	}

	public List<String> getOreDicts() {
		return oreDicts;
	}

	protected List<Recipe> recipes = new ArrayList<>();

	public List<Recipe> getRecipes() {
		return recipes;
	}

	public void addRecipe(Recipe recipe) {
		recipes.add(recipe);
	}

	@SideOnly(Side.CLIENT)
	@Override
	public boolean hasEffect(ItemStack stack) {
		return glint;
	}

	public void registerColorHandler(IItemColor itemColor) {
		Souls.onInit((FMLInitializationEvent event) -> {
			Minecraft.getMinecraft().getItemColors().registerItemColorHandler(itemColor, this);
		});
	}

	public boolean isFood() {
		return foodIsFood;
	}

	public void setFood(int amount, float saturation) {
		setFood(amount, saturation, false, null);
	}

	public void setFood(int amount, float saturation, boolean alwaysEdible) {
		setFood(amount, saturation, alwaysEdible, null);
	}

	public void setFood(int amount, float saturation, boolean alwaysEdible, ConsumeHandler handler) {
		foodIsFood = true;
		foodAmount = amount;
		foodSaturation = saturation;
		foodAlwaysEdible = alwaysEdible;
		foodHandler = handler;
	}

	public void setFoodQuantityToEat(int amount) {
		foodQuantity = amount;
	}

	public void setFoodPotionEffects(PotionEffect... effects) {
		foodEffects = effects;
	}

	public void setFoodCanEatHandler(CanConsumeHandler handler) {
		foodCanEatHandler = handler;
	}

	public void setFoodDuration(int duration) {
		foodDuration = duration;
	}

	@Override
	public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer playerIn, EnumHand handIn) {
		ItemStack itemstack = playerIn.getHeldItem(handIn);
		if (foodIsFood) {
			if ((foodCanEatHandler != null && foodCanEatHandler.canConsume(worldIn, playerIn, handIn))
					|| (playerIn.canEat(foodAlwaysEdible) && itemstack.getCount() >= foodQuantity)) {
				playerIn.setActiveHand(handIn);
				return new ActionResult<ItemStack>(EnumActionResult.SUCCESS, itemstack);
			}
		}
		return new ActionResult<ItemStack>(EnumActionResult.FAIL, itemstack);
	}

	@Override
	public EnumAction getItemUseAction(ItemStack stack) {
		return EnumAction.EAT;
	}

	@Override
	public int getMaxItemUseDuration(ItemStack stack) {
		return foodDuration;
	}

	@Override
	public ItemStack onItemUseFinish(ItemStack stack, World world, EntityLivingBase entityLiving) {
		if (entityLiving instanceof EntityPlayer) {
			EntityPlayer entityplayer = (EntityPlayer) entityLiving;
			entityplayer.getFoodStats().addStats(foodAmount, foodSaturation);
			world.playSound((EntityPlayer) null, entityplayer.posX, entityplayer.posY, entityplayer.posZ,
					SoundEvents.ENTITY_PLAYER_BURP, SoundCategory.PLAYERS, 0.5F, world.rand.nextFloat() * 0.1F + 0.9F);

			if (foodEffects != null) {
				for (PotionEffect effect : foodEffects) {
					entityLiving.addPotionEffect(new PotionEffect(effect));
				}
			}

			if (foodHandler != null)
				foodHandler.consume(stack, world, entityLiving);

			entityplayer.addStat(StatList.getObjectUseStats(this));

			if (entityplayer instanceof EntityPlayerMP) {
				CriteriaTriggers.CONSUME_ITEM.trigger((EntityPlayerMP) entityplayer, stack);
			}
		}

		stack.shrink(foodQuantity);
		return stack;
	}
}