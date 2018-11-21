/**
 * The following class (this entire package, actually) is based on https://github.com/elytra/BlockRenderer
 * 
 * But Soulus needs to export more information than that for its website, so it needed to make some changes!
 */

package yuudaari.soulus.client.exporter;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import org.lwjgl.input.Keyboard;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiIngameMenu;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import scala.Tuple2;
import yuudaari.soulus.client.exporter.ItemExport.StackExport;
import yuudaari.soulus.common.CreativeTab;
import yuudaari.soulus.common.ModBlocks;
import yuudaari.soulus.common.ModItems;
import yuudaari.soulus.common.util.IBlock;
import yuudaari.soulus.common.util.JSON;
import yuudaari.soulus.common.util.LangHelper;
import yuudaari.soulus.common.util.Logger;
import yuudaari.soulus.common.util.serializer.DefaultFieldSerializer;
import yuudaari.soulus.common.util.serializer.SerializationHandlers.IClassSerializationHandler;

public class Exporter {

	private static final DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd_HH.mm.ss");
	private static final Map<String, File> modFolders = Maps.newHashMap();

	private static File getModFolder (final String modid, final File exportFolder) {
		File modFolder = modFolders.get(modid);
		if (modFolder == null) modFolders.put(modid, modFolder = new File(exportFolder.getPath() + "/" + modid + "/"));
		return modFolder;
	}

	private static String sanitize (String str) {
		return str.replaceAll("[^A-Za-z0-9-_ ]", "_");
	}

	public static List<String> export () {
		Minecraft.getMinecraft().displayGuiScreen(new GuiIngameMenu());

		modFolders.clear();
		final File exportFolder = new File("exports/" + dateFormat.format(new Date()) + "/");

		// construct a list of which items need to be rendered
		final List<ItemStack> itemsToExport = getExportItems();
		final Map<Item, List<Tuple2<ItemStack, String>>> renderedItems = Maps.newHashMap();

		long lastUpdate = 0;
		int exportCount = 0;

		Renderer.setUpRenderState();

		for (final ItemStack stack : itemsToExport) {
			if (Keyboard.isKeyDown(Keyboard.KEY_ESCAPE))
				break;

			final ResourceLocation registryName = stack.getItem().getRegistryName();
			final String modid = registryName.getResourceDomain();
			final File modFolder = getModFolder(modid, exportFolder);

			final String imagePath = Renderer.render(stack, sanitize(registryName.getResourcePath()), modFolder);

			if (imagePath != null) {
				List<Tuple2<ItemStack, String>> renders = renderedItems.get(stack.getItem());
				if (renders == null) renderedItems.put(stack.getItem(), renders = Lists.newArrayList());
				renders.add(new Tuple2<>(stack, imagePath));
			}

			exportCount++;

			// only update the exporting GUI every once in a while
			if (Minecraft.getSystemTime() - lastUpdate > 33) {
				Renderer.tearDownRenderState();

				final String rendering = LangHelper.localize("gui.rendering", itemsToExport.size(), modid);
				final String progress = LangHelper.localize("gui.progress", exportCount, itemsToExport.size(), itemsToExport.size() - exportCount);
				GuiExporterLoading.renderLoadingScreen(rendering, progress, stack, (float) exportCount / itemsToExport.size());

				lastUpdate = Minecraft.getSystemTime();

				Renderer.setUpRenderState();
			}
		}

		// create a JSON representation of the items exported, and data applicable to them
		final JsonArray itemExportsJson = new JsonArray();
		for (final Map.Entry<Item, List<Tuple2<ItemStack, String>>> renderedItem : renderedItems.entrySet()) {
			final Item item = renderedItem.getKey();

			final List<StackExport> stackExports = Lists.newArrayList();
			for (final Tuple2<ItemStack, String> renderedStack : renderedItem.getValue()) {
				final String imagePath = item.getRegistryName().getResourceDomain() + "/" + renderedStack._2;
				stackExports.add(new StackExport(imagePath, renderedStack._1));
			}

			final ItemExport export = new ItemExport(item, stackExports);
			final JsonObject json = new JsonObject();

			final IClassSerializationHandler<Object> deserializer = DefaultFieldSerializer.getClassSerializer(ItemExport.class);
			DefaultFieldSerializer.serializeClass(deserializer, export, json);

			itemExportsJson.add(json);
		}

		// write a JSON file for the exported/rendered item data
		final File itemsFile = new File(exportFolder, "items.json");
		final String itemsJsonString = JSON.getString(itemExportsJson, "\t");
		try {
			Files.write(itemsFile.toPath(), itemsJsonString.getBytes());
		} catch (IOException e) {
			Logger.error(e);
			Logger.error("Unable to export items.json file");
		}

		final List<String> result = Lists.newArrayList();

		// specify in the GUI whether the rendering was cancelled or succeeded
		if (exportCount < itemsToExport.size()) {
			final String cancelled = LangHelper.localize("gui.renderCancelled");
			final String progress = LangHelper.localize("gui.progress", exportCount, itemsToExport.size(), itemsToExport.size() - exportCount);
			result.add(cancelled);
			result.add(progress);

			GuiExporterLoading.renderLoadingScreen(cancelled, progress, null, (float) exportCount / itemsToExport.size());

		} else {
			final String rendered = LangHelper.localize("gui.rendered", exportCount);
			result.add(rendered);

			GuiExporterLoading.renderLoadingScreen(rendered, "", null, 1);
		}

		Renderer.tearDownRenderState();

		// wait a lil while
		try {
			Thread.sleep(1500);
		} catch (InterruptedException e) {}

		return result;
	}

	/**
	 * @return A list of {@link ItemStack}s which are either from Soulus or share a recipe with a Soulus item.
	 */
	@SuppressWarnings("deprecation")
	private static List<ItemStack> getExportItems () {
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
		for (final IRecipe recipe : ForgeRegistries.RECIPES.getValues()) {

			final ItemStack output = recipe.getRecipeOutput();
			final List<ItemStack> ingredients = recipe.getIngredients()
				.stream()
				.flatMap(ingredient -> Arrays.stream(ingredient.getMatchingStacks()))
				.collect(Collectors.toList());

			// check if any of the planned exports are the same items as in this recipe
			for (final Item item : items.items()) {
				if (output.getItem().equals(item) || ingredients.stream().anyMatch(stack -> stack.getItem().equals(item))) {
					matchingRecipeItems.add(output);
					matchingRecipeItems.addAll(ingredients);
					break;
				}
			}
		}

		// we add the items from the matching recipes after the recipe loop so that they don't cause a chain reaction of importing
		// every recipe that touched a recipe that touched a recipe that touched a recipe that touched a soulus recipe
		items.add(matchingRecipeItems);

		return items.stacks();
	}

	private static class StackMap {

		private final Map<Item, List<ItemStack>> internalMap = Maps.newHashMap();

		public void add (final List<ItemStack> stacks) {
			for (final ItemStack stack : stacks) {
				List<ItemStack> itemList = internalMap.get(stack.getItem());
				if (itemList == null) internalMap.put(stack.getItem(), itemList = Lists.newArrayList());

				if (itemList.stream().anyMatch(existingStack -> areStacksEqual(stack, existingStack)))
					continue;

				// the stack isn't in this set yet
				itemList.add(stack);
				Logger.info(stack.getItem().getRegistryName() + " (" + stack.getDisplayName() + ")");
			}
		}

		public Set<Item> items () {
			return internalMap.keySet();
		}

		public List<ItemStack> stacks () {
			return internalMap.values().stream().flatMap(stackList -> stackList.stream()).collect(Collectors.toList());
		}

		private boolean areStacksEqual (final ItemStack a, final ItemStack b) {
			return a.getItem().equals(b.getItem()) && //
				(a.getTagCompound() == null) == (b.getTagCompound() == null) && //
				(a.getTagCompound() == null || a.getTagCompound().equals(b.getTagCompound())) && //
				a.areCapsCompatible(b);
		}
	}
}
