package org.shuangfa114.test.createtrackbuilder.foundation.mixin.accessor;

import com.simibubi.create.content.schematics.SchematicPrinter;
import net.createmod.catnip.levelWrappers.SchematicLevel;
import net.minecraft.core.BlockPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.List;

@Mixin(value = SchematicPrinter.class,remap = false)
public interface SchematicPrinterAccessor {
    @Accessor("schematicLoaded")
    void setSchematicLoaded(boolean loaded);
    @Accessor("isErrored")
    void setIsErrored(boolean isErrored);
    @Accessor
    SchematicLevel getBlockReader();
    @Accessor
    void setBlockReader(SchematicLevel blockReader);
    @Accessor("schematicAnchor")
    void setSchematicAnchor(BlockPos schematicAnchor);
    @Accessor("currentPos")
    BlockPos getCurrentPos();
    @Accessor("currentPos")
    void setCurrentPos(BlockPos currentPos);
    @Accessor
    int getPrintingEntityIndex();
    @Accessor
    void setPrintingEntityIndex(int printingEntityIndex);
    @Accessor
    void setPrintStage(SchematicPrinter.PrintStage printStage);
    @Accessor
    List<BlockPos> getDeferredBlocks();
    
}
