package org.shuangfa114.test.createtrackbuilder;

import com.simibubi.create.content.kinetics.base.ShaftVisual;
import com.simibubi.create.foundation.data.BlockStateGen;
import com.simibubi.create.foundation.data.CreateRegistrate;
import com.tterrag.registrate.util.entry.BlockEntityEntry;
import com.tterrag.registrate.util.entry.BlockEntry;
import com.tterrag.registrate.util.entry.ItemEntry;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import org.shuangfa114.test.createtrackbuilder.content.block.builder.BuilderBlock;
import org.shuangfa114.test.createtrackbuilder.content.block.builder.BuilderBlockEntity;
import org.shuangfa114.test.createtrackbuilder.content.block.builder.BuilderBlockRenderer;
import org.shuangfa114.test.createtrackbuilder.content.item.editor.TrackEditor;

public class Register {

    public static final CreateRegistrate REGISTRATE = CreateTrackBuilder.registrate();

    // Registers
    public static final BlockEntry<BuilderBlock> BUILDER_BLOCK = REGISTRATE
            .block("builder_block", BuilderBlock::new)
            .properties(properties -> properties.sound(SoundType.NETHER_BRICKS))
            .properties(properties -> properties.mapColor(MapColor.COLOR_CYAN))
            .properties(properties -> properties.strength(1.2f, 3.2f))
            .properties(BlockBehaviour.Properties::requiresCorrectToolForDrops)
            .properties(BlockBehaviour.Properties::noOcclusion)
            .blockstate(BlockStateGen.horizontalAxisBlockProvider(true))
            .item()
            .build()
            .register();
    public static final BlockEntityEntry<BuilderBlockEntity> BUILDER_BLOCK_ENTITY = REGISTRATE
            .blockEntity("builder_block", BuilderBlockEntity::new)
            .visual(() -> ShaftVisual::new)
            .renderer(() -> BuilderBlockRenderer::new)
            .validBlock(BUILDER_BLOCK)
            .register();
    public static final ItemEntry<TrackEditor> TRACK_EDITOR = REGISTRATE
            .item("track_editor", TrackEditor::new)
            .properties(properties -> properties.stacksTo(1))
            .register();

    public static void register() {
    }
}
