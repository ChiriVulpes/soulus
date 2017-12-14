package yuudaari.souls.common.item;

import yuudaari.souls.common.util.BoneType;

public class BoneChunkNether extends BoneChunk {
    public BoneChunkNether() {
        super("bone_chunk_nether", BoneType.NETHER);
        removeOreDict("boneChunk");
    }
}