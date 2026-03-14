package org.shuangfa114.test.createtrackbuilder.content.block.builder;

import com.simibubi.create.AllTags;
import com.simibubi.create.content.kinetics.base.KineticBlockEntity;
import com.simibubi.create.content.logistics.crate.CreativeCrateBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import com.simibubi.create.foundation.blockEntity.behaviour.filtering.FilteringBehaviour;
import net.createmod.catnip.data.Iterate;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.StairBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.IItemHandler;
import org.jetbrains.annotations.Nullable;
import org.shuangfa114.test.createtrackbuilder.foundation.blockEntity.behaviour.MultiFilterBehaviour;
import org.shuangfa114.test.createtrackbuilder.foundation.util.BuilderTransform;
import org.shuangfa114.test.createtrackbuilder.foundation.util.ModLang;

import java.util.LinkedHashSet;
import java.util.List;

public class BuilderBlockEntity extends KineticBlockEntity implements MenuProvider {

    public BuilderInventory inventory;
    public LinkedHashSet<LazyOptional<IItemHandler>> attachedInventories;
    public boolean hasCreativeCrate;
    MultiFilterBehaviour filters;
    FilteringBehaviour materialFilter;
    FilteringBehaviour trackFilter;

    public BuilderBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
        inventory = new BuilderInventory(this);
        setLazyTickRate(30);
        attachedInventories = new LinkedHashSet<>();
    }

    @Override
    public void addBehaviours(List<BlockEntityBehaviour> behaviours) {
        filters = new MultiFilterBehaviour(this);
        materialFilter = new FilteringBehaviour(this, new BuilderTransform(-3)).withPredicate(this::isValidMaterial);
        materialFilter.setLabel(ModLang.translateDirect("builder.behaviour.material"));
        trackFilter = new FilteringBehaviour(this, new BuilderTransform(3)).withPredicate(AllTags.AllBlockTags.TRACKS::matches);
        trackFilter.setLabel(ModLang.translateDirect("builder.behaviour.track"));
        filters.addFilter(materialFilter).addFilter(trackFilter);
        behaviours.add(filters);
        super.addBehaviours(behaviours);
    }

    @Override
    public float calculateStressApplied() {
        return 64;
    }

    @Override
    protected void write(CompoundTag compound, boolean clientPacket) {
        if (!clientPacket) {
            compound.put("Inventory", inventory.serializeNBT());
        }
        super.write(compound, clientPacket);
    }

    @Override
    protected void read(CompoundTag compound, boolean clientPacket) {
        if (!clientPacket) {
            inventory.deserializeNBT(compound.getCompound("Inventory"));
        }
        super.read(compound, clientPacket);
    }

    @Override
    public void lazyTick() {
        super.lazyTick();
        findInventories();
    }

    public void findInventories() {
        hasCreativeCrate = false;
        attachedInventories.clear();
        for (Direction facing : Iterate.directions) {
            if (!level.isLoaded(worldPosition.relative(facing)))
                continue;
            BlockEntity blockEntity = level.getBlockEntity(worldPosition.relative(facing));
            if (blockEntity != null) {
                if (blockEntity instanceof CreativeCrateBlockEntity) {
                    hasCreativeCrate = true;
                    return;
                }
                LazyOptional<IItemHandler> capability = blockEntity.getCapability(ForgeCapabilities.ITEM_HANDLER, facing.getOpposite());
                if (capability.isPresent()) {
                    attachedInventories.add(capability);
                }
            }
        }
    }

    @Override
    public Component getDisplayName() {
        return ModLang.translate("gui.builder").component();
    }

    @Override
    public @Nullable AbstractContainerMenu createMenu(int pContainerId, Inventory pPlayerInventory, Player pPlayer) {
        return BuilderMenu.create(pContainerId, pPlayerInventory, this);
    }

    public boolean isValidMaterial(ItemStack stack) {
        if (this.getLevel() == null) {
            return false;
        }
        if (stack.isEmpty())
            return true;
        if (stack.getItem() instanceof BlockItem blockItem) {
            BlockState appliedState = blockItem.getBlock().defaultBlockState();
            if (appliedState.getBlock() instanceof EntityBlock)
                return false;
            if (appliedState.getBlock() instanceof StairBlock)
                return false;
            VoxelShape shape = appliedState.getShape(this.getLevel(), this.getBlockPos());
            if (shape.isEmpty() || !shape.bounds().equals(Shapes.block().bounds())) {
                return false;
            }
            VoxelShape collisionShape = appliedState.getCollisionShape(this.getLevel(), this.getBlockPos());
            return !collisionShape.isEmpty();
        } else {
            return false;
        }
    }
}
