package org.shuangfa114.test.createtrackbuilder.mixin.accessor;

import com.simibubi.create.content.logistics.filter.FilterItemStack;
import com.simibubi.create.foundation.blockEntity.behaviour.filtering.FilteringBehaviour;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(value = FilteringBehaviour.class,remap = false)
public interface FilteringBehaviourAccessor{
    @Accessor("filter")
    void setFilter(FilterItemStack stack);
}
