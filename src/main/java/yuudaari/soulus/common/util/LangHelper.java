package yuudaari.soulus.common.util;

import net.minecraft.entity.EntityList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextComponentTranslation;

public class LangHelper {

    public static String localize (String path, Object... params) {
        return new TextComponentTranslation(path, params).getUnformattedComponentText();
    }

    public static String localizeEntity (final String id) {
        return localizeEntity(new ResourceLocation(id));
    }

    public static String localizeEntity (final ResourceLocation id) {
        return localize("entity." + EntityList.getTranslationName(id) + ".name");
    }
}
