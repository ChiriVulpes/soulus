package yuudaari.soulus.common.item;

import yuudaari.soulus.common.util.BoneType;

public class BoneChunkNether extends BoneChunk {
    public BoneChunkNether() {
        super("bone_chunk_nether", BoneType.NETHER);
        removeOreDict("boneChunk");
    }
}