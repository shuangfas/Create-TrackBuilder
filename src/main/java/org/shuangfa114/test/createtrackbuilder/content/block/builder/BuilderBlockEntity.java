package org.shuangfa114.test.createtrackbuilder.content.block.builder;

import com.simibubi.create.AllBlocks;
import com.simibubi.create.AllTags;
import com.simibubi.create.content.kinetics.base.KineticBlockEntity;
import com.simibubi.create.content.schematics.cannon.SchematicannonBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import com.simibubi.create.foundation.blockEntity.behaviour.filtering.FilteringBehaviour;
import net.createmod.catnip.levelWrappers.SchematicLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.network.chat.Component;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.StairBlock;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.shuangfa114.test.createtrackbuilder.content.item.editor.TrackEditor;
import org.shuangfa114.test.createtrackbuilder.foundation.blockEntity.behaviour.BuilderTransform;
import org.shuangfa114.test.createtrackbuilder.foundation.blockEntity.behaviour.MultiFilterBehaviour;
import org.shuangfa114.test.createtrackbuilder.foundation.util.ModLang;
import org.shuangfa114.test.createtrackbuilder.foundation.util.StructureHelper;
import org.shuangfa114.test.createtrackbuilder.foundation.util.TrackPreview;
import org.shuangfa114.test.createtrackbuilder.foundation.util.api.TrackPrinter;
import org.shuangfa114.test.createtrackbuilder.foundation.util.structures.Segment;

import java.nio.file.Path;
import java.util.List;

public class BuilderBlockEntity extends KineticBlockEntity implements MenuProvider {

    public BuilderInventory inventory;
    public boolean validConnection;
    public State state;
    public MultiFilterBehaviour filters;
    public FilteringBehaviour pavementFilter;
    public FilteringBehaviour trackFilter;
    public BlockItem pavement;
    public SchematicannonBlockEntity attachedCannon;

    public BuilderBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
        inventory = new BuilderInventory(this);
        setLazyTickRate(30);
        this.state = State.VALID;
    }

    @Override
    public void addBehaviours(List<BlockEntityBehaviour> behaviours) {
        filters = new MultiFilterBehaviour(this);
        pavementFilter = new FilteringBehaviour(this, new BuilderTransform(-3))
                .withPredicate(this::isValidPavement)
                .withCallback(stack -> pavement = stack.getItem() instanceof BlockItem blockItem ? blockItem : null);
        pavementFilter.setLabel(ModLang.translateDirect("builder.behaviour.pavement"));
        trackFilter = new FilteringBehaviour(this, new BuilderTransform(3)).withPredicate(AllTags.AllBlockTags.TRACKS::matches);
        trackFilter.setLabel(ModLang.translateDirect("builder.behaviour.track"));
        trackFilter.setFilter(new ItemStack(AllBlocks.TRACK.get()));
        filters.addFilter(pavementFilter).addFilter(trackFilter);
        behaviours.add(filters);
        super.addBehaviours(behaviours);
    }

    @Override
    public float calculateStressApplied() {
        return 64;
    }

    @Override
    protected void write(CompoundTag compound, boolean clientPacket) {
        compound.put("Inventory", inventory.serializeNBT());
        compound.putString("State", state.name());
        super.write(compound, clientPacket);
    }

    @Override
    protected void read(CompoundTag compound, boolean clientPacket) {
        inventory.deserializeNBT(compound.getCompound("Inventory"));
        if (compound.contains("State")) {
            state = State.valueOf(compound.getString("State"));
        }
        super.read(compound, clientPacket);
    }

    @Override
    public void lazyTick() {
        super.lazyTick();
        findCannon();
    }

    public void findCannon() {
        attachedCannon = null;
        for (Direction dir : Direction.Plane.HORIZONTAL) {
            BlockPos offset = this.getBlockPos().relative(dir);
            if (!level.isLoaded(offset)) {
                continue;
            }
            if (level.getBlockEntity(offset) instanceof SchematicannonBlockEntity blockEntity) {
                attachedCannon = blockEntity;
                return;
            }
        }
    }

    @Override
    public @NotNull Component getDisplayName() {
        return ModLang.translate("gui.builder").component();
    }

    @Override
    public @Nullable AbstractContainerMenu createMenu(int pContainerId, Inventory pPlayerInventory, Player pPlayer) {
        return BuilderMenu.create(pContainerId, pPlayerInventory, this);
    }

    public boolean isValidPavement(ItemStack stack) {
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

    public void saveAndTransmit() {
        CompoundTag tag = inventory.getStackInSlot(0).getTag();
        if (!tag.getBoolean("Initialized")) {
            state = State.NOT_INIT;
            return;
        }
        List<Segment> segments = Segment.tagToList(tag);
        state = saveTemplate(segments);
        if (state == State.VALID) {
            BlockPos pos = segments.get(0).pos;
            tag.put("Anchor", NbtUtils.writeBlockPos(pos));
            transmit();
        }
    }

    private State saveTemplate(List<Segment> segments) {
        validConnection = false;
        if (segments.size() >= 2) {
            SchematicLevel reader = new SchematicLevel(level);
            for (int i = 0; i < segments.size() - 1; i++) {
                Segment start = segments.get(i);
                Segment end = segments.get(i + 1);
                TrackPreview.PlacementInfo info = TrackPreview.tryConnect(level, start, end, trackFilter.getFilter(), true);
                if (!info.valid) {
                    return State.NOT_VALID_CONNECTION;
                }
                boolean shouldPave = pavement != null;
                TrackPreview.placeTracksInSchematic(reader, info);
                if (shouldPave) {
                    TrackPreview.paveTracksInSchematic(reader, info, pavement, true);
                }
            }
            saveTemplate(StructureHelper.toTemplate(reader, segments.get(0).pos));
            validConnection = true;
            return State.VALID;
        }
        return State.NOT_INIT;
    }

    public void saveTemplate(StructureTemplate template) {
        ItemStack stack = inventory.getStackInSlot(0);
        if (!level.isClientSide && stack.getItem() instanceof TrackEditor) {
            Path dir = StructureHelper.CACHES_DIR.resolve(stack.getTag().getString("Owner"));
            StructureHelper.saveAsTemplate(template, dir, stack.getTag().getString("TemplateID"), true);
        }
    }

    public State transmit() {
        if (attachedCannon != null && attachedCannon.state == SchematicannonBlockEntity.State.STOPPED) {
            TrackPrinter printer = new TrackPrinter();
            printer.controller = this.getBlockPos();
            attachedCannon.printer = printer;
            attachedCannon.state=SchematicannonBlockEntity.State.RUNNING;
        }
        return State.FIND_NOT_CANNON;
    }

    public enum State {
        VALID(true),
        NOT_VALID_CONNECTION(false),
        FIND_NOT_CANNON(false),
        NOT_INIT(false),
        ;
        public final boolean valid;

        State(boolean valid) {
            this.valid = valid;
        }
    }
}
