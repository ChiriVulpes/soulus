package yuudaari.soulus.common.item;

import net.minecraft.item.crafting.Ingredient;
import yuudaari.soulus.common.compat.JeiDescriptionRegistry;
import yuudaari.soulus.common.util.BoneType;

public class BoneChunkEnder extends BoneChunk {

    public BoneChunkEnder () {
        super("bone_chunk_ender", BoneType.ENDER);
        glint = true;
        removeOreDict("boneChunk");
    }

    @Override
    public void onRegisterDescription (JeiDescriptionRegistry registry) {
        registry.add(Ingredient.fromItem(this), getRegistryName());
    }
}
