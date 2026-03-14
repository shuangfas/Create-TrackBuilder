package org.shuangfa114.test.createtrackbuilder.content.block.builder;

import com.simibubi.create.content.kinetics.base.HorizontalAxisKineticBlock;
import com.simibubi.create.foundation.block.IBE;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.network.NetworkHooks;
import org.shuangfa114.test.createtrackbuilder.Register;

public class BuilderBlock extends HorizontalAxisKineticBlock implements IBE<BuilderBlockEntity> {

    public BuilderBlock(Properties properties) {
        super(properties);
    }

    @Override
    public Class<BuilderBlockEntity> getBlockEntityClass() {
        return BuilderBlockEntity.class;
    }

    @Override
    public BlockEntityType<? extends BuilderBlockEntity> getBlockEntityType() {
        return Register.BUILDER_BLOCK_ENTITY.get();
    }

    @Override
    public InteractionResult use(BlockState pState, Level pLevel, BlockPos pPos, Player pPlayer, InteractionHand pHand, BlockHitResult pHit) {
        if (pLevel.isClientSide) {
            return InteractionResult.SUCCESS;
        }
        withBlockEntityDo(pLevel, pPos, blockEntity -> NetworkHooks.openScreen((ServerPlayer) pPlayer, blockEntity, blockEntity::sendToMenu));
        return InteractionResult.SUCCESS;

    }
}
