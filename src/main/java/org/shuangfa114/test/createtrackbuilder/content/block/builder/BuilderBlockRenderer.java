package org.shuangfa114.test.createtrackbuilder.content.block.builder;

import com.mojang.blaze3d.vertex.PoseStack;
import com.simibubi.create.content.kinetics.base.ShaftRenderer;
import com.simibubi.create.foundation.blockEntity.behaviour.filtering.FilteringRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import org.shuangfa114.test.createtrackbuilder.foundation.blockEntity.behaviour.MultiFilterRender;

public class BuilderBlockRenderer extends ShaftRenderer<BuilderBlockEntity> {

    public BuilderBlockRenderer(BlockEntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    protected void renderSafe(BuilderBlockEntity be, float partialTicks, PoseStack ms, MultiBufferSource buffer, int light, int overlay) {
        super.renderSafe(be, partialTicks, ms, buffer, light, overlay);
        FilteringRenderer.renderOnBlockEntity(be, partialTicks, ms, buffer, light, overlay);
        MultiFilterRender.renderOnBlockEntity(be, partialTicks, ms, buffer, light, overlay);
    }
}
