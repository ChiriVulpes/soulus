package yuudaari.souls.common.item;

import yuudaari.souls.common.util.BoneType;

public class BoneChunkEnder extends BoneChunk {
    public BoneChunkEnder() {
        super("bone_chunk_ender", BoneType.ENDER);
        glint = true;
        removeOreDict("boneChunk");
    }
}