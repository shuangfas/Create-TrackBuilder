package org.shuangfa114.test.createtrackbuilder.foundation.mixin.accessor;

import net.minecraft.core.Vec3i;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.List;

@Mixin(value = StructureTemplate.class,remap = false)
public interface StructureTemplateAccessor {
    @Accessor("palettes")
    List<StructureTemplate.Palette> getPalettes();
    @Accessor("size")
    void setSize(Vec3i size);
}
