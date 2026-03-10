package org.shuangfa114.test.createtrackbuilder.content.item.editor.client.tools;

import com.simibubi.create.AllKeys;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import org.shuangfa114.test.createtrackbuilder.content.item.editor.packet.SegmentInitPacket;
import org.shuangfa114.test.createtrackbuilder.foundation.util.ModLang;
import org.shuangfa114.test.createtrackbuilder.foundation.util.algorithm.Segment;

public class SelectionTool extends EditorToolBase {
    public static boolean enableAxis = false;

    @Override
    public void init() {
        super.init();
        selectionRange = 20;
    }

    @Override
    public boolean handleClick(boolean left) {
        if (!left) {
            if (AllKeys.ctrlDown()) {
                if (editorHandler.segments.size() < 2) {
                    ModLang.translate("selection.not_enough").sendStatus(Minecraft.getInstance().player);
                    return false;
                }
                ItemStack stack = editorHandler.getActiveEditorItem();
                CompoundTag tag = stack.getOrCreateTag();
                Segment.listToTag(editorHandler.segments, tag);
                tag.putBoolean("Initialized", true);
                editorHandler.sync(new SegmentInitPacket(editorHandler.segments, editorHandler.getActiveHotbarSlot()));
                editorHandler.deploy();
                ModLang.translate("selection.connection_init").sendStatus(Minecraft.getInstance().player);
                return true;
            }
            Segment seg = new Segment(selectedPos);
            if (!editorHandler.segments.contains(seg)) {
                Minecraft.getInstance().player.displayClientMessage(ModLang.translateDirect("selection.points", editorHandler.segments.size()), true);
                editorHandler.segments.add(seg);
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean shouldShowSelection() {
        return true;
    }
}
