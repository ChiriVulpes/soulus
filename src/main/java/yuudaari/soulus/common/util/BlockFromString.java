package yuudaari.soulus.common.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;

public class BlockFromString {
	private static Pattern regexMetadata = Pattern.compile("@(\\d+)$");

	@SuppressWarnings("deprecation")
	public static IBlockState get(String name) {
		Matcher matcher = regexMetadata.matcher(name);
		int metadata = -1;
		if (matcher.find()) {
			name = name.substring(0, name.length() - matcher.group().length());
			metadata = Integer.parseInt(matcher.group(1));
		}
		Block block = Block.getBlockFromName(name);

		if (block == null) {
			Logger.error("Can't find block for " + name);
			return null;
		}

		return metadata == -1 ? block.getDefaultState() : block.getStateFromMeta(metadata);
	}
}