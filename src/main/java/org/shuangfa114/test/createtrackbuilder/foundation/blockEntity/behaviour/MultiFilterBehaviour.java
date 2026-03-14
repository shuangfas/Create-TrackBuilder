package org.shuangfa114.test.createtrackbuilder.foundation.blockEntity.behaviour;

import com.simibubi.create.AllSoundEvents;
import com.simibubi.create.content.logistics.filter.FilterItemStack;
import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.BehaviourType;
import com.simibubi.create.foundation.blockEntity.behaviour.filtering.FilteringBehaviour;
import com.simibubi.create.foundation.utility.CreateLang;
import net.createmod.catnip.nbt.NBTHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import org.shuangfa114.test.createtrackbuilder.mixin.accessor.FilteringBehaviourAccessor;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.IntStream;

public class MultiFilterBehaviour extends FilteringBehaviour {
    public static final BehaviourType<MultiFilterBehaviour> TYPE = new BehaviourType<>();
    public List<FilteringBehaviour> filters;

    public MultiFilterBehaviour(SmartBlockEntity be) {
        super(be, new VoidTransform());
        filters = new LinkedList<>();
    }

    public MultiFilterBehaviour addFilter(FilteringBehaviour filter) {
        filters.add(filter);
        return this;
    }

    @Override
    public void write(CompoundTag nbt, boolean clientPacket) {
        List<ItemStack> stacks = new ArrayList<>(filters.stream().map(FilteringBehaviour::getFilter).toList());
        nbt.put("Filters", NBTHelper.writeItemList(stacks));
    }

    @Override
    public void read(CompoundTag nbt, boolean clientPacket) {
        List<ItemStack> stacks = NBTHelper.readItemList(nbt.getList("Filters", Tag.TAG_COMPOUND));
        IntStream.range(0, filters.size()).forEach(i -> ((FilteringBehaviourAccessor) filters.get(i))
                .setFilter(FilterItemStack.of(stacks.get(i))));
    }

    @Override
    public void tick() {
        super.tick();
        filters.forEach(FilteringBehaviour::tick);
    }

    @Override
    public void destroy() {
        super.destroy();
        filters.forEach(FilteringBehaviour::destroy);
    }

    public boolean setFilter(int i, ItemStack itemStack) {
        return filters.get(i).setFilter(itemStack);
    }

    public int getSelectedFilter(Level level, BlockPos blockPos, BlockState blockState, Vec3 hit) {
        for (int i = 0; i < filters.size(); i++) {
            if (filters.get(i).getSlotPositioning().testHit(level, blockPos, blockState, hit)) {
                return i;
            }
        }
        return -1;
    }

    @Override
    public boolean testHit(Vec3 hit) {
        BlockPos pos = this.getPos();
        BlockState state = blockEntity.getBlockState();
        Vec3 localHit = hit.subtract(Vec3.atLowerCornerOf(blockEntity.getBlockPos()));
        return getSelectedFilter(this.getWorld(), pos, state, localHit) != -1;
    }

    @Override
    public void onShortInteract(Player player, InteractionHand hand, Direction side, BlockHitResult hitResult) {
        BlockPos blockPos = this.getPos();
        Level level = this.getWorld();
        ItemStack itemInHand = player.getItemInHand(hand);
        if (!canShortInteract(itemInHand))
            return;
        if (level.isClientSide())
            return;
        int index = getSelectedFilter(level, blockPos, blockEntity.getBlockState(), toLocalHit(hitResult.getLocation()));
        if (index == -1) {
            return;
        }
        if (!setFilter(index, itemInHand)) {
            player.displayClientMessage(CreateLang.translateDirect("logistics.filter.invalid_item"), true);
            AllSoundEvents.DENY.playOnServer(player.level(), player.blockPosition(), 1, 1);
            return;
        }
        level.playSound(null, blockPos, SoundEvents.ITEM_FRAME_ADD_ITEM, SoundSource.BLOCKS, .25f, .1f);
    }

    @Override
    public BehaviourType<?> getType() {
        return TYPE;
    }

    public Vec3 toLocalHit(Vec3 origin) {
        return origin.subtract(Vec3.atLowerCornerOf(blockEntity.getBlockPos()));
    }
}
