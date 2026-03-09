package org.shuangfa114.test.createtrackbuilder.content.item.editor.client.tools;

import com.mojang.blaze3d.vertex.PoseStack;
import net.createmod.catnip.render.SuperRenderTypeBuffer;
import net.minecraft.client.Minecraft;
import net.minecraft.world.phys.Vec3;
import org.shuangfa114.test.createtrackbuilder.foundation.util.ModLang;
import org.shuangfa114.test.createtrackbuilder.foundation.util.Segment;
import org.shuangfa114.test.createtrackbuilder.foundation.util.VisualUtil;

import java.util.ArrayList;
import java.util.List;

public class SelectionTool extends EditorToolBase {
    public List<Segment> segments;
    public static boolean enableAxis=false;
    @Override
    public void init() {
        super.init();
        segments= new ArrayList<>();
        selectionRange = 20;
    }

    @Override
    public void renderTool(PoseStack ms, SuperRenderTypeBuffer buffer, Vec3 camera) {
        super.renderTool(ms, buffer, camera);
        if(selectedPos!=null){
            VisualUtil.chaseAABB("chaseAABB", selectedPos);
        }
    }

    @Override
    public boolean handleClick(boolean left) {
        if(!left){
            if(Minecraft.getInstance().player.isShiftKeyDown()){
                if(segments.size()<2){
                    ModLang.translate("selection.not_enough");
                    return false;
                }

                editorHandler.deploy();
                return true;
            }
            segments.add(new Segment(selectedPos));
            return true;
        }
        return false;
    }
}
