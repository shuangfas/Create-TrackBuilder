package org.shuangfa114.test.createtrackbuilder.content.item.editor;

import com.simibubi.create.content.trains.track.TrackShape;
import net.createmod.catnip.math.VecHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.shuangfa114.test.createtrackbuilder.foundation.util.Segment;

import java.util.ArrayList;
import java.util.List;

public class TrackEditor extends Item {
    public List<Segment> segments;

    public TrackEditor(Properties pProperties) {
        super(pProperties);
        segments = new ArrayList<>();
    }
//    public void calculatePath(Level level,Player player,BlockPos start,BlockPos end){
//        Vec3 axisStart;
//        Vec3 axisEnd;
//        BlockState stateStart = level.getBlockState(start);
//        BlockState stateEnd = level.getBlockState(end);
//        if(stateStart.getBlock() instanceof TrackBlock trackBlock){
//            List<Vec3> axesStart = trackBlock.getTrackAxes(level,start,stateStart);
//            List<Vec3> axesEnd = trackBlock.getTrackAxes(level,start,stateStart);
//            axisStart = axesStart.get(0);
//            axisEnd = axesEnd.get(0);
//        }
//    }

    @Override
    public @NotNull InteractionResult useOn(UseOnContext pContext) {
        Level level = pContext.getLevel();
        Player player = pContext.getPlayer();
        ItemStack itemstack = pContext.getItemInHand();
        BlockPos blockpos = pContext.getClickedPos().above(1);
        if (player != null) {
            if (player.isShiftKeyDown()) {
//                BlockState blockstate = level.getBlockState(blockpos);
//                if(blockstate.getBlock() instanceof TrackBlock trackBlock&&trackBlock.getTrackAxes(level,blockpos,blockstate).size()>1){
//                    player.displayClientMessage(Component.literal("select cross!!!"),true);
//                    return InteractionResult.PASS;
//                }
                TrackShape shape = getBestShape(player);
                if (shape != null) {
                    CompoundTag compoundtag = itemstack.getOrCreateTag();
                    boolean flag = compoundtag.getBoolean("isStart");
                    if (flag) {
                        compoundtag.putBoolean("isStart", false);
                        compoundtag.put("startPos", NbtUtils.writeBlockPos(blockpos));
                        compoundtag.putString("startShape", shape.name());
                        compoundtag.remove("endPos");
                        compoundtag.remove("endShape");
                        player.displayClientMessage(Component.literal("start"), true);
                    } else {
                        compoundtag.putBoolean("isStart", true);
                        compoundtag.put("endPos", NbtUtils.writeBlockPos(blockpos));
                        compoundtag.putString("endShape", shape.name());
                        player.displayClientMessage(Component.literal("end"), true);
                    }
                    return InteractionResult.SUCCESS;
                }
            }
        }
        return InteractionResult.PASS;
    }

    @Override
    public void appendHoverText(ItemStack pStack, @Nullable Level pLevel, List<Component> pTooltipComponents, TooltipFlag pIsAdvanced) {
        BlockPos start = NbtUtils.readBlockPos(pStack.getOrCreateTag().getCompound("startPos"));
        BlockPos end = NbtUtils.readBlockPos(pStack.getOrCreateTag().getCompound("endPos"));
        pTooltipComponents.add(Component.literal(String.format("start:[%s] end:[%s]", start, end)));
        super.appendHoverText(pStack, pLevel, pTooltipComponents, pIsAdvanced);
    }

    public boolean isStartPosValid(ItemStack pStack) {
        return pStack.getOrCreateTag().contains("startPos");
    }

    public boolean isEndPosValid(ItemStack pStack) {
        return pStack.getOrCreateTag().contains("endPos");
    }

    public boolean isPosValid(ItemStack pStack) {
        return isStartPosValid(pStack) && isEndPosValid(pStack);
    }

    public BlockPos getStartPos(ItemStack pStack) {
        return NbtUtils.readBlockPos(pStack.getOrCreateTag().getCompound("startPos"));
    }

    public BlockPos getEndPos(ItemStack pStack) {
        return NbtUtils.readBlockPos(pStack.getOrCreateTag().getCompound("endPos"));
    }

    public TrackShape getStartShape(ItemStack pStack) {
        TrackShape shape;
        try {
            shape = TrackShape.valueOf(pStack.getOrCreateTag().getString("startShape"));
        } catch (IllegalArgumentException e) {
            return TrackShape.NONE;
        }
        return shape;
    }
    public TrackShape getEndShape(ItemStack pStack) {
        TrackShape shape;
        try {
            shape = TrackShape.valueOf(pStack.getOrCreateTag().getString("endShape"));
        } catch (IllegalArgumentException e) {
            return TrackShape.NONE;
        }
        return shape;
    }



    public TrackShape getBestShape(Player player) {
        if (player != null) {
            Vec3 lookAngle = player.getLookAngle();
            lookAngle = lookAngle.multiply(1, 0, 1);
            if (Mth.equal(lookAngle.length(), 0))
                lookAngle = VecHelper.rotate(new Vec3(0, 0, 1), -player.getYRot(), Direction.Axis.Y);
            lookAngle = lookAngle.normalize();
            TrackShape best = TrackShape.ZO;
            double bestValue = Float.MAX_VALUE;
            for (TrackShape shape : TrackShape.values()) {
                if (shape.isJunction() || shape.isPortal())
                    continue;
                Vec3 axis = shape.getAxes()
                        .get(0);
                double distance = Math.min(axis.distanceToSqr(lookAngle), axis.normalize()
                        .scale(-1)
                        .distanceToSqr(lookAngle));
                if (distance > bestValue)
                    continue;
                bestValue = distance;
                best = shape;
            }
            return best;
        }
        return null;
    }

}
