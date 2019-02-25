/**
 * The following class (this entire package, actually) is based on https://github.com/elytra/BlockRenderer
 * 
 * But Soulus needs to export more information than that for its website, so it needed to make some changes!
 */

package yuudaari.soulus.client.exporter;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.gson.JsonArray;
import org.apache.commons.io.FileUtils;
import org.lwjgl.input.Keyboard;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiIngameMenu;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import scala.Tuple2;
import yuudaari.soulus.client.exporter.exports.*;
import yuudaari.soulus.common.CreativeTab;
import yuudaari.soulus.common.ModBlocks;
import yuudaari.soulus.common.ModItems;
import yuudaari.soulus.common.util.IBlock;
import yuudaari.soulus.common.util.JSON;
import yuudaari.soulus.common.util.Translation;
import yuudaari.soulus.common.util.Logger;
import yuudaari.soulus.common.util.serializer.Serializer;

public class Exporter {

	// private static final DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd_HH.mm.ss");
	private static final Map<String, File> modFolders = Maps.newHashMap();

	private static File getModFolder (final String modid, final File exportFolder) {
		File modFolder = modFolders.get(modid);
		if (modFolder == null) modFolders.put(modid, modFolder = new File(exportFolder.getPath() + "/" + modid + "/"));
		return modFolder;
	}

	private static String sanitize (String str) {
		return str.replaceAll("[^A-Za-z0-9-_ ]", "_");
	}

	@SideOnly(Side.CLIENT)
	public static List<String> export () {
		Minecraft.getMinecraft().displayGuiScreen(new GuiIngameMenu());

		modFolders.clear();

		// final File exportFolder = new File("exports/" + dateFormat.format(new Date()) + "/");
		final File exportFolder = new File("export/");

		try {
			FileUtils.deleteDirectory(exportFolder);
		} catch (IOException e) {
			Logger.error("Unable to clear export folder");
			Logger.error(e);
		}

		// construct a list of which items need to be rendered
		final Tuple2<List<ItemStack>, List<IRecipe>> toExport = getExports();
		final List<ItemStack> itemsToExport = toExport._1;
		final List<IRecipe> recipesToExport = toExport._2;
		final Map<Item, Tuple2<List<Tuple2<ItemStack, String>>, Integer>> renderedItems = Maps.newHashMap();

		long lastUpdate = 0;
		int exportCount = 0;

		Renderer.setUpRenderState();

		for (final ItemStack stack : toExport._1) {
			if (Keyboard.isKeyDown(Keyboard.KEY_ESCAPE))
				break;

			final ResourceLocation registryName = stack.getItem().getRegistryName();
			final String modid = registryName.getResourceDomain();
			final File modFolder = getModFolder(modid, exportFolder);

			final String imagePath = Renderer.render(stack, sanitize(registryName.getResourcePath()), modFolder);

			if (imagePath != null) {
				Tuple2<List<Tuple2<ItemStack, String>>, Integer> renders = renderedItems.get(stack.getItem());
				if (renders == null) renderedItems.put(stack.getItem(), renders = new Tuple2<>(Lists.newArrayList(), exportCount));
				renders._1.add(new Tuple2<>(stack, imagePath));
			}

			exportCount++;

			// only update the exporting GUI every once in a while
			if (Minecraft.getSystemTime() - lastUpdate > 33) {
				Renderer.tearDownRenderState();

				final String rendering = Translation.localize("gui.rendering", itemsToExport.size(), modid);
				final String progress = Translation.localize("gui.progress", exportCount, itemsToExport.size(), itemsToExport.size() - exportCount);
				GuiExporterLoading.renderLoadingScreen(rendering, progress, stack, (float) exportCount / itemsToExport.size());

				lastUpdate = Minecraft.getSystemTime();

				Renderer.setUpRenderState();
			}
		}

		writeItemsJson(renderedItems, exportFolder);
		writeRecipesJson(recipesToExport, exportFolder);

		final List<String> result = Lists.newArrayList();

		// specify in the GUI whether the rendering was cancelled or succeeded
		if (exportCount < itemsToExport.size()) {
			final String cancelled = Translation.localize("gui.renderCancelled");
			final String progress = Translation.localize("gui.progress", exportCount, itemsToExport.size(), itemsToExport.size() - exportCount);
			result.add(cancelled);
			result.add(progress);

			GuiExporterLoading.renderLoadingScreen(cancelled, progress, null, (float) exportCount / itemsToExport.size());

		} else {
			final String rendered = Translation.localize("gui.rendered", exportCount);
			result.add(rendered);

			GuiExporterLoading.renderLoadingScreen(rendered, "", null, 1);
		}

		Renderer.tearDownRenderState();

		// wait a lil while
		try {
			Thread.sleep(1500);
		} catch (InterruptedException e) {
		}

		return result;
	}

	/**
	 * Writes a JSON file representing each stack which was rendered.
	 */
	private static void writeItemsJson (final Map<Item, Tuple2<List<Tuple2<ItemStack, String>>, Integer>> renderedItems, final File exportFolder) {

		final JsonArray itemExportsJson = new JsonArray();

		// create a JSON representation of the items exported, and data applicable to them
		renderedItems.entrySet()
			.stream()
			.sorted( (a, b) -> a.getValue()._2 - b.getValue()._2)
			.forEach(renderedItem -> {
				final Item item = renderedItem.getKey();

				final List<ItemExport.StackExport> stackExports = Lists.newArrayList();
				for (final Tuple2<ItemStack, String> renderedStack : renderedItem.getValue()._1) {
					final String imagePath = item.getRegistryName().getResourceDomain() + "/" + renderedStack._2;
					stackExports.add(new ItemExport.StackExport(imagePath, renderedStack._1));
				}

				final ItemExport export = new ItemExport(item, stackExports);
				itemExportsJson.add(Serializer.serialize(export));
			});

		// write a JSON file for the exported/rendered item data
		JSON.writeFile(itemExportsJson, new File(exportFolder, "items.json"));
	}

	/**
	 * Writes a JSON file representing each recipe which contains an item from Soulus.
	 */
	private static void writeRecipesJson (final List<IRecipe> recipes, final File exportFolder) {

		// create a JSON representation of the items exported, and data applicable to them
		final JsonArray recipeExportsJson = new JsonArray();

		recipes.stream()
			.map(recipe -> Serializer.serialize(new RecipeExport(recipe)))
			.forEach(recipeExportsJson::add);

		// write a JSON file for the exported/rendered item data
		JSON.writeFile(recipeExportsJson, new File(exportFolder, "recipes.json"));
	}

	/**
	 * @return A list of {@link ItemStack}s which are either from Soulus or share a recipe with a Soulus item.
	 */
	@SuppressWarnings("deprecation")
	private static Tuple2<List<ItemStack>, List<IRecipe>> getExports () {
		final StackMap items = new StackMap();

		// add mod blocks to item map
		for (final IBlock block : ModBlocks.blocks) {
			final NonNullList<ItemStack> tabList = NonNullList.create();
			block.getSubBlocks(CreativeTab.INSTANCE, tabList);
			items.add(tabList);
		}

		// add mod items to item map
		for (final Item item : ModItems.items) {
			final NonNullList<ItemStack> tabList = NonNullList.create();
			item.getSubItems(CreativeTab.INSTANCE, tabList);
			items.add(tabList);
		}

		// go through recipes to see if any recipes contain items we're planning on exporting
		// if they do, we need to export those items as well
		final List<ItemStack> matchingRecipeItems = Lists.newArrayList();
		final List<IRecipe> matchingRecipes = Lists.newArrayList();
		for (final IRecipe recipe : ForgeRegistries.RECIPES.getValues()) {

			final ItemStack output = recipe.getRecipeOutput();
			final List<ItemStack> ingredients = recipe.getIngredients()
				.stream()
				.flatMap(ingredient -> Arrays.stream(ingredient.getMatchingStacks()))
				.collect(Collectors.toList());

			// check if any of the planned exports are the same items as in this recipe
			for (final Item item : items.items()) {
				if (output.getItem().equals(item) || ingredients.stream().anyMatch(stack -> stack.getItem().equals(item))) {
					matchingRecipes.add(recipe);
					matchingRecipeItems.add(output);
					matchingRecipeItems.addAll(ingredients);
					break;
				}
			}
		}

		// we add the items from the matching recipes after the recipe loop so that they don't cause a chain reaction of importing
		// every recipe that touched a recipe that touched a recipe that touched a recipe that touched a soulus recipe
		items.add(matchingRecipeItems);

		return new Tuple2<>(items.stacks(), matchingRecipes);
	}

	private static class StackMap {

		private final Map<Item, List<Tuple2<ItemStack, Integer>>> internalMap = Maps.newHashMap();
		private int index = 0;

		public void add (final List<ItemStack> stacks) {
			for (final ItemStack stack : stacks) {
				List<Tuple2<ItemStack, Integer>> itemList = internalMap.get(stack.getItem());
				if (itemList == null) internalMap.put(stack.getItem(), itemList = Lists.newArrayList());

				if (itemList.stream().anyMatch(existingStack -> areStacksEqual(stack, existingStack._1)))
					continue;

				// the stack isn't in this set yet
				itemList.add(new Tuple2<>(stack, index++));
				Logger.info(stack.getItem().getRegistryName() + " (" + stack.getDisplayName() + ")");
			}
		}

		public Set<Item> items () {
			return internalMap.keySet();
		}

		public List<ItemStack> stacks () {
			return internalMap.values()
				.stream()
				.flatMap(stackList -> stackList.stream())
				.sorted( (a, b) -> a._2 - b._2)
				.map(stack -> stack._1)
				.collect(Collectors.toList());
		}

		private boolean areStacksEqual (final ItemStack a, final ItemStack b) {
			return a.getItem().equals(b.getItem()) && //
				a.getMetadata() == b.getMetadata() && //
				(a.getTagCompound() == null) == (b.getTagCompound() == null) && //
				(a.getTagCompound() == null || a.getTagCompound().equals(b.getTagCompound())) && //
				a.areCapsCompatible(b);
		}
	}
}
