package yuudaari.soulus.common.util;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import net.minecraft.entity.EntityList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextComponentTranslation;

public class Translation {

	private static final DecimalFormat format = new DecimalFormat("#.#");

	public static String formatPercentage (double num) {
		return format.format(num * 100);
	}

	public static String localize (String path, Object... params) {
		return new TextComponentTranslation(path, params).getUnformattedComponentText();
	}

	public static String localizeEntity (final String id) {
		return localizeEntity(new ResourceLocation(id));
	}

	public static String localizeEntity (final ResourceLocation id) {
		return localize("entity." + EntityList.getTranslationName(id) + ".name");
	}

	private final String path;
	private final List<Object> args = new ArrayList<>();

	public Translation (final String path, final Object... args) {
		this.path = path;
		addArgs(args);
	}

	public Translation addArgs (final Object... args) {
		Arrays.stream(args).forEach(this.args::add);
		return this;
	}

	public String get (final Object... args) {
		addArgs(args);
		return localize(path, this.args.toArray(new Object[0]));
	}
}
