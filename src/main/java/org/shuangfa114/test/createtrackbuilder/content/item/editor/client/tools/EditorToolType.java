package org.shuangfa114.test.createtrackbuilder.content.item.editor.client.tools;

import com.simibubi.create.foundation.gui.AllIcons;
import net.createmod.catnip.lang.Lang;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import org.shuangfa114.test.createtrackbuilder.foundation.util.ModLang;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public enum EditorToolType {
    SELECTION_INIT(new SelectionTool(),AllIcons.I_CENTERED),
    SEGMENT_MOVE(new SegmentMoveTool(),AllIcons.I_ACTIVE);
    private IEditorTool tool;
    private AllIcons icon;

    EditorToolType(IEditorTool tool, AllIcons icon) {
        this.tool = tool;
        this.icon = icon;
    }

    public IEditorTool getTool() {
        return tool;
    }

    public MutableComponent getDisplayName() {
        return ModLang.translate("schematic.tool." + Lang.asId(name())).component();
    }

    public AllIcons getIcon() {
        return icon;
    }

    public static List<EditorToolType> getTools(boolean creative) {
        List<EditorToolType> tools = new ArrayList<>();
        Collections.addAll(tools, SEGMENT_MOVE);
//        if (creative)
//            tools.add(PRINT);
        return tools;
    }
    public static List<EditorToolType> getToolsBeforeInit() {
        List<EditorToolType> tools = new ArrayList<>();
        Collections.addAll(tools, SELECTION_INIT);
        return tools;
    }


    public List<Component> getDescription() {
        return ModLang.translatedOptions("schematic.tool." + Lang.asId(name()) + ".description", "0", "1", "2", "3");
    }
}
