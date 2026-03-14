package org.shuangfa114.test.createtrackbuilder;

import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public class ModTab {
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS =
            DeferredRegister.create(Registries.CREATIVE_MODE_TAB, CreateTrackBuilder.MODID);
    public static final RegistryObject<CreativeModeTab> TAB = CREATIVE_MODE_TABS.register("tab", () -> CreativeModeTab
            .builder()
            .icon(() -> new ItemStack(Register.BUILDER_BLOCK.get()))
            .title(Component.translatable("modname.create_track_builder"))
            .displayItems((pParameters, pOutput) -> {
                pOutput.accept(Register.BUILDER_BLOCK.get());
                pOutput.accept(Register.TRACK_EDITOR);
            })
            .build());
}
