package org.shuangfa114.test.createtrackbuilder.foundation.util.api;

import com.simibubi.create.content.schematics.SchematicPrinter;
import net.createmod.catnip.levelWrappers.SchematicLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.nbt.Tag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import org.shuangfa114.test.createtrackbuilder.content.block.builder.BuilderBlockEntity;
import org.shuangfa114.test.createtrackbuilder.foundation.util.StructureHelper;
import org.shuangfa114.test.createtrackbuilder.foundation.util.structures.Segment;
import org.shuangfa114.test.createtrackbuilder.mixin.accessor.SchematicPrinterAccessor;

public class TrackPrinter extends SchematicPrinter {
    public int currentIndex;
    public BlockPos controller;
    SchematicPrinterAccessor accessor;

    public TrackPrinter() {
        super();
        currentIndex = 0;
        accessor = (SchematicPrinterAccessor) this;
    }

    @Override
    public boolean tryAdvanceCurrentPos() {
        if(currentIndex < accessor.getBlockReader().getAllPositions().size()){
            accessor.setCurrentPos(accessor.getBlockReader().getAllPositions().stream().toList().get(currentIndex));
            currentIndex++;
            return true;
        }
        return false;
    }

    @Override
    public boolean advanceCurrentPos() {
        return tryAdvanceCurrentPos();
    }

    @Override
    public void fromTag(CompoundTag compound, boolean clientPacket) {
        super.fromTag(compound, clientPacket);
        currentIndex = compound.getInt("CurrentIndex");
        if(compound.contains("Controller")){
            controller = NbtUtils.readBlockPos(compound.getCompound("Controller"));
        }
    }

    @Override
    public void write(CompoundTag compound) {
        super.write(compound);
        compound.putInt("CurrentIndex", currentIndex);
        if(controller!=null){
            compound.put("Controller", NbtUtils.writeBlockPos(controller));
        }
    }

    @Override
    public void loadSchematic(ItemStack blueprint, Level originalWorld, boolean processNBT) {
        loadSchematic(originalWorld);
    }

    public void loadSchematic(Level originalWorld) {
        if (originalWorld.getBlockEntity(controller) instanceof BuilderBlockEntity blockEntity) {
            ItemStack stack = blockEntity.inventory.getStackInSlot(0);
            BlockPos posToPlace = Segment.fromTag(stack.getTag().getList("Segments", Tag.TAG_COMPOUND).getCompound(0)).pos;
            SchematicLevel blockReader = StructureHelper.loadInSchematicLevel(originalWorld,
                    StructureHelper.CACHES_DIR.resolve(stack.getTag().getString("Owner")),
                    stack.getTag().getString("TemplateID"),
                    posToPlace);
            accessor.setPrintingEntityIndex(-1);
            accessor.setPrintStage(SchematicPrinter.PrintStage.BLOCKS);
            accessor.getDeferredBlocks().clear();
            BoundingBox bounds = blockReader.getBounds();
            accessor.setCurrentPos(new BlockPos(bounds.minX() - 1, bounds.minY(), bounds.minZ()));
            accessor.setBlockReader(blockReader);
            accessor.setSchematicAnchor(posToPlace);
            accessor.setSchematicLoaded(true);
        }
    }

    @Override
    public void resetSchematic() {
        super.resetSchematic();
        this.currentIndex = -1;
        this.controller = null;
    }
}
