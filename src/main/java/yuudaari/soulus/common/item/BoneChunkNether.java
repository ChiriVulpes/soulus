package yuudaari.soulus.common.item;

import net.minecraft.item.crafting.Ingredient;
import yuudaari.soulus.common.compat.JeiDescriptionRegistry;
import yuudaari.soulus.common.util.BoneType;

public class BoneChunkNether extends BoneChunk {
    public BoneChunkNether() {
        super("bone_chunk_nether", BoneType.NETHER);
        removeOreDict("boneChunk");
    }

    @Override
    public void onRegisterDescription(JeiDescriptionRegistry registry) {
        registry.add(Ingredient.fromItem(this), getRegistryName());
    }
}