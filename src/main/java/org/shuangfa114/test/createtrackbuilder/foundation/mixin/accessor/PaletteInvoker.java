package org.shuangfa114.test.createtrackbuilder.foundation.mixin.accessor;

import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

import java.util.List;

@Mixin(StructureTemplate.Palette.class)
public interface PaletteInvoker {
    @Invoker("<init>")
    static StructureTemplate.Palette create(List<StructureTemplate.StructureBlockInfo> pBlocks){
        throw new AssertionError();
    }
}
