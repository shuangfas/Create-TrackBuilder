package org.shuangfa114.test.createtrackbuilder.foundation.blockEntity.behaviour;

import com.mojang.blaze3d.vertex.PoseStack;
import com.simibubi.create.AllSpecialTextures;
import com.simibubi.create.CreateClient;
import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.ValueBox;
import com.simibubi.create.foundation.blockEntity.behaviour.ValueBoxRenderer;
import com.simibubi.create.foundation.blockEntity.behaviour.ValueBoxTransform;
import dev.engine_room.flywheel.lib.transform.TransformStack;
import net.createmod.catnip.data.Pair;
import net.createmod.catnip.math.VecHelper;
import net.createmod.catnip.outliner.Outliner;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.List;

public class MultiFilterRender {
    public static void tick() {
        Minecraft mc = Minecraft.getInstance();
        HitResult target = mc.hitResult;
        if (!(target instanceof BlockHitResult result))
            return;

        ClientLevel world = mc.level;
        BlockPos pos = result.getBlockPos();
        BlockState state = world.getBlockState(pos);

        if (mc.player.isShiftKeyDown())
            return;
        if (!(world.getBlockEntity(pos) instanceof SmartBlockEntity sbe))
            return;

        ItemStack mainhandItem = mc.player.getItemInHand(InteractionHand.MAIN_HAND);
        MultiFilterBehaviour multiBehaviour = sbe.getBehaviour(MultiFilterBehaviour.TYPE);
        if (multiBehaviour == null) {
            return;
        }
        multiBehaviour.filters.forEach(behaviour -> {
            if (!behaviour.isActive())
                return;
            if (!behaviour.getSlotPositioning().shouldRender(world, pos, state))
                return;
            if (!behaviour.mayInteract(mc.player))
                return;
            boolean hit = behaviour.getSlotPositioning().testHit(world, pos, state, multiBehaviour.toLocalHit(target.getLocation()));
            if (!hit) {
                return;
            }
            ItemStack filter = behaviour.getFilter();
            Component label = behaviour.getLabel();
            AABB emptyBB = new AABB(Vec3.ZERO, Vec3.ZERO);
            AABB bb = emptyBB.inflate(.25f);
            ValueBox box = new ValueBox.ItemValueBox(label, bb, pos, filter, behaviour.getCountLabelForValueBox());
            box.passive(behaviour.bypassesInput(mainhandItem));
            Outliner.getInstance()
                    .showOutline(Pair.of("multiFilter" + behaviour.netId(), pos), box.transform(behaviour.getSlotPositioning()))
                    .lineWidth(1 / 64f)
                    .withFaceTexture(hit ? AllSpecialTextures.THIN_CHECKERED : null)
                    .highlightFace(result.getDirection());
            List<MutableComponent> tip = new ArrayList<>();
            tip.add(label.copy());
            tip.add(behaviour.getTip());
            CreateClient.VALUE_SETTINGS_HANDLER.showHoverTip(tip);
        });
    }

    public static void renderOnBlockEntity(SmartBlockEntity be, float partialTicks, PoseStack ms, MultiBufferSource buffer, int light, int overlay) {
        if (be == null || be.isRemoved()) {
            return;
        }
        Level level = be.getLevel();
        BlockPos blockPos = be.getBlockPos();
        MultiFilterBehaviour multiBehaviour = be.getBehaviour(MultiFilterBehaviour.TYPE);
        if (multiBehaviour == null) {
            return;
        }
        multiBehaviour.filters.forEach(behaviour -> {
            if (!be.isVirtual()) {
                Entity cameraEntity = Minecraft.getInstance().cameraEntity;
                if (cameraEntity != null && level == cameraEntity.level()) {
                    float max = behaviour.getRenderDistance();
                    if (cameraEntity.position()
                            .distanceToSqr(VecHelper.getCenterOf(blockPos)) > (max * max)) {
                        return;
                    }
                }
            }
            if (!behaviour.isActive())
                return;
            if (behaviour.getFilter().isEmpty())
                return;
            ValueBoxTransform slotPositioning = behaviour.getSlotPositioning();
            BlockState blockState = be.getBlockState();
            if (slotPositioning.shouldRender(level, blockPos, blockState)) {
                ms.pushPose();
                slotPositioning.transform(level, blockPos, blockState, ms);
                TransformStack.of(ms).rotateZDegrees(90);
                ValueBoxRenderer.renderItemIntoValueBox(behaviour.getFilter(), ms, buffer, light, overlay);
                ms.popPose();
            }
        });
    }
}
