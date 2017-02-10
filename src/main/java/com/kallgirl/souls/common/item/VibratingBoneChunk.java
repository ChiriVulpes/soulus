package com.kallgirl.souls.common.item;

public class VibratingBoneChunk extends Item {
    public VibratingBoneChunk () {
        super("vibratingBoneChunk");
        glint = true;
        addRecipeShapeless(3, "boneEnder");
    }
}