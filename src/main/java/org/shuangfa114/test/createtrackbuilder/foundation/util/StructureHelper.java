package org.shuangfa114.test.createtrackbuilder.foundation.util;

import com.simibubi.create.content.schematics.SchematicExport;
import com.simibubi.create.foundation.utility.CreatePaths;
import com.simibubi.create.foundation.utility.FilesHelper;
import net.createmod.catnip.levelWrappers.SchematicLevel;
import net.createmod.catnip.math.BBHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtAccounter;
import net.minecraft.nbt.NbtIo;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import org.shuangfa114.test.createtrackbuilder.foundation.mixin.accessor.PaletteInvoker;
import org.shuangfa114.test.createtrackbuilder.foundation.mixin.accessor.StructureTemplateAccessor;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.LinkedList;
import java.util.List;
import java.util.zip.GZIPInputStream;

public class StructureHelper {
    public static final Path CACHES_DIR = CreatePaths.SCHEMATICS_DIR.resolve("caches");

    public static StructureTemplate toTemplate(SchematicLevel schematicLevel) {
        return toTemplate(schematicLevel, BlockPos.ZERO);
    }

    public static StructureTemplate toTemplate(SchematicLevel schematicLevel, BlockPos anchor) {
        StructureTemplate template = new StructureTemplate();
        List<StructureTemplate.StructureBlockInfo> info = new LinkedList<>();
        int[] max = new int[3];
        for (BlockPos origin : schematicLevel.getAllPositions()) {
            BlockPos blockPos = origin.subtract(anchor);
            int x = blockPos.getX();
            int y = blockPos.getY();
            int z = blockPos.getZ();
            max[0] = Math.max(max[0], Math.abs(x));
            max[1] = Math.max(max[1], Math.abs(y));
            max[2] = Math.max(max[2], Math.abs(z));
            BlockEntity blockEntity = schematicLevel.getBlockEntity(origin);
            //why add ,ask simibubi,lol about his logic
            BlockState blockstate = schematicLevel.getBlockState(origin);
            if (!blockstate.isAir()) {
                CompoundTag tag = blockEntity == null ? null : blockEntity.saveWithId();
                info.add(new StructureTemplate.StructureBlockInfo(blockPos, blockstate, tag));
            }
        }
        ((StructureTemplateAccessor) template).getPalettes().add(PaletteInvoker.create(info));
        ((StructureTemplateAccessor) template).setSize(new Vec3i(max[0] + 1, max[1] + 1, max[2] + 1));
        return template;
    }

    public static SchematicExport.SchematicExportResult saveAsTemplate(StructureTemplate template, Path dir, String fileName, boolean overwrite) {
        CompoundTag data = template.save(new CompoundTag());
        if (fileName.isEmpty())
            fileName = String.valueOf(data.hashCode());
        if (!overwrite)
            fileName = FilesHelper.findFirstValidFilename(fileName, dir, "nbt");
        if (!fileName.endsWith(".nbt"))
            fileName += ".nbt";
        Path file = dir.resolve(fileName).toAbsolutePath();
        try {
            Files.createDirectories(dir);
            boolean overwritten = Files.deleteIfExists(file);
            try (OutputStream out = Files.newOutputStream(file, StandardOpenOption.CREATE)) {
                NbtIo.writeCompressed(data, out);
            }
            return new SchematicExport.SchematicExportResult(file, dir, fileName, overwritten, null, null);
        } catch (IOException e) {
            System.out.print("An error occurred while saving schematic " + fileName + e);
            return null;
        }
    }

    public static StructureTemplate loadTemplate(Level level, Path path) {
        StructureTemplate t = new StructureTemplate();
        try (DataInputStream stream = new DataInputStream(new BufferedInputStream(
                new GZIPInputStream(Files.newInputStream(path, StandardOpenOption.READ))))) {
            CompoundTag nbt = NbtIo.read(stream, new NbtAccounter(0x20000000L));
            t.load(level.holderLookup(Registries.BLOCK), nbt);
        } catch (IOException e) {
            System.out.print("Failed to read schematic" + e);
        }
        return t;
    }

    public static SchematicLevel loadInSchematicLevel(Level origin, Path path, String fileName, BlockPos anchor) {
        if (!fileName.endsWith(".nbt")) {
            fileName += ".nbt";
        }
        StructureTemplate t = loadTemplate(origin, path.resolve(fileName));
        SchematicLevel blockReader = new SchematicLevel(anchor, origin);
        try {
            t.placeInWorld(blockReader, anchor, anchor, new StructurePlaceSettings(), blockReader.getRandom(), Block.UPDATE_CLIENTS);
        } catch (Exception e) {
            System.out.print("Failed to load Schematic for Printing" + e);
        }
        BlockPos extraBounds = StructureTemplate.calculateRelativePosition(new StructurePlaceSettings(), new BlockPos(t.getSize())
                .offset(-1, -1, -1));
        blockReader.setBounds(BBHelper.encapsulate(blockReader.getBounds(), extraBounds));
        return blockReader;
    }
}
