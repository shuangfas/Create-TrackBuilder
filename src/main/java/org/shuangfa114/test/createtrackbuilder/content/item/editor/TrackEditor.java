package org.shuangfa114.test.createtrackbuilder.content.item.editor;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.world.item.Item;

public class TrackEditor extends Item {

    public TrackEditor(Properties pProperties) {
        super(pProperties);
    }

    @Override
    public void verifyTagAfterLoad(CompoundTag pTag) {
        if (!pTag.contains("Segments")) {
            pTag.put("Segments", new ListTag());
        }
        if (!pTag.contains("TemplateID")) {
            pTag.putString("TemplateID", String.valueOf(System.currentTimeMillis()));
        }
        super.verifyTagAfterLoad(pTag);
    }
}
