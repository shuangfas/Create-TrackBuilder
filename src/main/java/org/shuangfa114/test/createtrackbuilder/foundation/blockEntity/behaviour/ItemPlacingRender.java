package org.shuangfa114.test.createtrackbuilder.foundation.blockEntity.behaviour;

import com.mojang.blaze3d.vertex.PoseStack;
import com.simibubi.create.AllSpecialTextures;
import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import com.simibubi.create.foundation.blockEntity.behaviour.ValueBox;
import com.simibubi.create.foundation.blockEntity.behaviour.ValueBoxRenderer;
import com.simibubi.create.foundation.blockEntity.behaviour.ValueBoxTransform;
import net.createmod.catnip.data.Iterate;
import net.createmod.catnip.data.Pair;
import net.createmod.catnip.math.VecHelper;
import net.createmod.catnip.outliner.Outliner;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
//todo use "showItem" instead of "ItemValueBox"
public class ItemPlacingRender {
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
        for (BlockEntityBehaviour b : sbe.getAllBehaviours()) {
            if (!(b instanceof ItemPlacingBehaviour behaviour))
                continue;
            if (!behaviour.isActive())
                continue;
            if (behaviour.slotPositioning instanceof ValueBoxTransform.Sided)
                ((ValueBoxTransform.Sided) behaviour.slotPositioning).fromSide(result.getDirection());
            if (!behaviour.slotPositioning.shouldRender(world, pos, state))
                continue;
            if (!behaviour.mayInteract(mc.player))
                continue;
            boolean hit = behaviour.slotPositioning.testHit(world, pos, state, target.getLocation()
                    .subtract(Vec3.atLowerCornerOf(pos)));
            AABB emptyBB = new AABB(Vec3.ZERO, Vec3.ZERO);
            AABB bb = emptyBB.inflate(.45f, .31f, .2f);
            Component label = behaviour.getLabel();

            ValueBox box = new ValueBox.ItemValueBox(label, bb, pos, behaviour.template, Component.empty());
            box.passive(!hit || behaviour.bypassesInput(behaviour.template));
            Outliner.getInstance()
                    .showOutline(Pair.of("filter" + behaviour.netId(), pos), box.transform(behaviour.slotPositioning))
                    .lineWidth(1 / 64f)
                    .withFaceTexture(hit ? AllSpecialTextures.THIN_CHECKERED : null)
                    .highlightFace(result.getDirection());
        }
    }

    public static void renderOnBlockEntity(SmartBlockEntity be, float partialTicks, PoseStack ms, MultiBufferSource buffer, int light, int overlay) {
        if (be == null || be.isRemoved())
            return;
        Level level = be.getLevel();
        BlockPos blockPos = be.getBlockPos();
        for (BlockEntityBehaviour b : be.getAllBehaviours()) {
            if (!(b instanceof ItemPlacingBehaviour behaviour))
                continue;

            if (!be.isVirtual()) {
                Entity cameraEntity = Minecraft.getInstance().cameraEntity;
                if (cameraEntity != null && level == cameraEntity.level()) {
                    float max = behaviour.getRenderDistance();
                    if (cameraEntity.position()
                            .distanceToSqr(VecHelper.getCenterOf(blockPos)) > (max * max)) {
                        continue;
                    }
                }
            }

            if (!behaviour.isActive())
                continue;
            if (behaviour.template.isEmpty())
                continue;

            ValueBoxTransform slotPositioning = behaviour.slotPositioning;
            BlockState blockState = be.getBlockState();

            if (slotPositioning instanceof ValueBoxTransform.Sided sided) {
                Direction side = sided.getSide();
                for (Direction d : Iterate.directions) {
                    ItemStack filter = behaviour.template;
                    if (filter.isEmpty())
                        continue;
                    sided.fromSide(d);
                    if (!slotPositioning.shouldRender(level, blockPos, blockState))
                        continue;
                    ms.pushPose();
                    slotPositioning.transform(level, blockPos, blockState, ms);
                    ValueBoxRenderer.renderItemIntoValueBox(filter, ms, buffer, light, overlay);
                    ms.popPose();
                }
                sided.fromSide(side);
            } else if (slotPositioning.shouldRender(level, blockPos, blockState)) {
                ms.pushPose();
                slotPositioning.transform(level, blockPos, blockState, ms);
                ValueBoxRenderer.renderItemIntoValueBox(behaviour.template, ms, buffer, light, overlay);
                ms.popPose();
            }
        }
    }
}
