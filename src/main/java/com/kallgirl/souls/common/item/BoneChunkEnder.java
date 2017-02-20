package com.kallgirl.souls.common.item;

import com.kallgirl.souls.common.BoneType;

public class BoneChunkEnder extends BoneChunk {
    public BoneChunkEnder () {
        super("boneChunkEnder", BoneType.ENDER);
        glint = true;
        addRecipeShapeless(3, "boneEnder");
    }
}