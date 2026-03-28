package org.shuangfa114.test.createtrackbuilder.content.item.editor.client;

import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.vertex.PoseStack;
import com.simibubi.create.AllKeys;
import com.simibubi.create.foundation.networking.SimplePacketBase;
import net.createmod.catnip.render.SuperRenderTypeBuffer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import org.shuangfa114.test.createtrackbuilder.ModPackets;
import org.shuangfa114.test.createtrackbuilder.api.structures.Segment;
import org.shuangfa114.test.createtrackbuilder.content.item.editor.TrackEditor;
import org.shuangfa114.test.createtrackbuilder.content.item.editor.client.tools.EditorToolType;
import org.shuangfa114.test.createtrackbuilder.foundation.packet.editor.OwnerSettingPacket;
import org.shuangfa114.test.createtrackbuilder.foundation.packet.editor.SegmentIncrementalPacket;
import org.shuangfa114.test.createtrackbuilder.foundation.util.TrackPreview;

import java.util.LinkedList;
import java.util.List;

//I'm king of copying and pasting!!!!
public class EditorHandler {
    public List<Segment> segments;
    private ToolSelectionScreen selectionScreen;
    private boolean active;
    private EditorToolType currentTool;
    private int activeHotbarSlot;
    private ItemStack activeEditorItem;

    public EditorHandler() {
        currentTool = EditorToolType.SELECTION_ADD;
        selectionScreen = new ToolSelectionScreen(ImmutableList.of(EditorToolType.SELECTION_ADD), this::equip);
        segments = new LinkedList<>();
    }

    public boolean isActive() {
        return active;
    }

    public void tick() {
        Minecraft mc = Minecraft.getInstance();
        LocalPlayer player = mc.player;
        ItemStack stack = findEditorInHand(player);
        if (stack == null) {
            active = false;
            activeHotbarSlot = 0;
            activeEditorItem = null;
            TrackPreview.clearCaches();
            return;
        }
        if (!active) {
            init(player, stack);
        }
        selectionScreen.update();
        currentTool.getTool().updateSelection();
        currentTool.getTool().tick();
    }

    public void render(PoseStack ms, SuperRenderTypeBuffer buffer, Vec3 camera) {
        if (active) {
            ms.pushPose();
            currentTool.getTool().renderTool(ms, buffer, camera);
            //ms.last().pose().setTranslation();
            ms.popPose();
        }
    }

    public void renderGui(GuiGraphics graphics, float partialTicks, int width, int height) {
        if (Minecraft.getInstance().options.hideGui || !active) {
            return;
        }
//        if (activeEditorItem != null){
//            int x = mainWindow.getGuiScaledWidth() / 2 - 88;
//            int y = mainWindow.getGuiScaledHeight() - 19;
//            RenderSystem.enableDepthTest();
//            AllGuiTextures.SCHEMATIC_SLOT.render(graphics, x + 20 * slot, y);
//        }
        selectionScreen.renderPassive(graphics, partialTicks);
    }

    public void init(LocalPlayer player, ItemStack stack) {
        active = true;
        load(stack);
        sync(new OwnerSettingPacket(activeHotbarSlot, player.getGameProfile().getName()));
        EditorToolType toolBefore = currentTool;
        selectionScreen = new ToolSelectionScreen(EditorToolType.getTools(), this::equip);
        if (toolBefore != null) {
            selectionScreen.setSelectedElement(toolBefore);
            equip(toolBefore);
        }
    }

    public boolean onMouseInput(int button, boolean pressed) {
        if (!active)
            return false;
        if (!pressed)
            return false;
        Minecraft mc = Minecraft.getInstance();
        if (mc.player.isShiftKeyDown())
            return false;
        return currentTool.getTool().handleClick(button == 0);
    }

    public void onKeyInput(int key, boolean pressed) {
        if (!active)
            return;
        if (!AllKeys.TOOL_MENU.doesModifierAndCodeMatch(key))
            return;

        if (pressed && !selectionScreen.focused)
            selectionScreen.focused = true;
        if (!pressed && selectionScreen.focused) {
            selectionScreen.focused = false;
            selectionScreen.onClose();
        }
    }

    public boolean mouseScrolled(double delta) {
        if (!active)
            return false;

        if (selectionScreen.focused) {
            selectionScreen.cycle((int) Math.signum(delta));
            return true;
        }
        if (AllKeys.ctrlDown())
            return currentTool.getTool()
                    .handleMouseWheel(delta);
        return false;
    }

    public void sync(SimplePacketBase packet) {
        if (this.getActiveEditorItem() == null)
            return;
        ModPackets.getChannel().sendToServer(packet);
    }

    public void equip(EditorToolType tool) {
        this.currentTool = tool;
        currentTool.getTool()
                .init();
    }

    private ItemStack findEditorInHand(Player player) {
        ItemStack stack = player.getMainHandItem();
        if (!(stack.getItem() instanceof TrackEditor))
            return null;

        activeEditorItem = stack;
        activeHotbarSlot = player.getInventory().selected;
        return stack;
    }

    public ItemStack getActiveEditorItem() {
        return activeEditorItem;
    }

    public int getActiveHotbarSlot() {
        return activeHotbarSlot;
    }

    public void load(ItemStack stack) {
        segments = Segment.tagToList(stack.getOrCreateTag());
    }

    public void addSegment(Segment segment) {
        segments.add(segment);
        sync(new SegmentIncrementalPacket(segment, activeHotbarSlot));
    }
}
