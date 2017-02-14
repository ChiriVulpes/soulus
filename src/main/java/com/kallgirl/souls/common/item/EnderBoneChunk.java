package com.kallgirl.souls.common.item;

public class EnderBoneChunk extends Item {
    public EnderBoneChunk () {
        super("enderBoneChunk");
        glint = true;
        addRecipeShapeless(3, "boneEnder");
    }
}