package org.shuangfa114.test.createtrackbuilder.content.block.builder;

import com.simibubi.create.content.kinetics.base.HorizontalAxisKineticBlock;
import com.simibubi.create.foundation.block.IBE;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import org.shuangfa114.test.createtrackbuilder.Register;

import java.util.Objects;

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

}
