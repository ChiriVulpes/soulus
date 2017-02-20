package com.kallgirl.souls.common.item;

import com.kallgirl.souls.common.BoneType;

public class BoneChunkNether extends BoneChunk {
    public BoneChunkNether () {
        super("boneChunkNether", BoneType.NETHER);
        addRecipeShapeless(3, "boneWither");
    }
}