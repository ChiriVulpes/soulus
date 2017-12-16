package yuudaari.soulus.common.item;

import yuudaari.soulus.common.util.BoneType;

public class BoneChunkEnder extends BoneChunk {
    public BoneChunkEnder() {
        super("bone_chunk_ender", BoneType.ENDER);
        glint = true;
        removeOreDict("boneChunk");
    }
}