package yuudaari.soulus.common.util;

import net.minecraft.util.text.TextComponentTranslation;

public class LangHelper {
    public static String localize(String path, Object... params) {
        return new TextComponentTranslation(path, params).getUnformattedComponentText();
    }
}