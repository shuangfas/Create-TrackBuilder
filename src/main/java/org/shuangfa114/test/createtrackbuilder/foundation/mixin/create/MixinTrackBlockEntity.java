package org.shuangfa114.test.createtrackbuilder.foundation.mixin.create;

import com.llamalad7.mixinextras.sugar.Local;
import com.simibubi.create.content.trains.track.TrackBlock;
import com.simibubi.create.content.trains.track.TrackBlockEntity;
import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Debug;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
@Debug(export = true)
@Mixin(value = TrackBlockEntity.class,remap = false)
public abstract class MixinTrackBlockEntity extends SmartBlockEntity {
    public MixinTrackBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    @Inject(method = "validateConnections",at = @At(value = "INVOKE", target = "Lcom/simibubi/create/content/trains/track/ITrackBlock;getTrackAxes(Lnet/minecraft/world/level/BlockGetter;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;)Ljava/util/List;"))
    private void addBEProperty(CallbackInfo ci, @Local BlockState blockState,@Local BlockPos blockPos){
        level.setBlock(blockPos, blockState.setValue(TrackBlock.HAS_BE, true), 3);
    }
}
