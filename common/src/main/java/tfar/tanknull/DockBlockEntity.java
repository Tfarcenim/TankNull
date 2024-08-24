package tfar.tanknull;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.Nameable;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;
import tfar.tanknull.init.ModBlockEntityTypes;
import tfar.tanknull.inventory.FluidInventory;
import tfar.tanknull.world.TankSavedData;

public class DockBlockEntity extends BlockEntity implements MenuProvider, Nameable {

    public ItemStack tank = ItemStack.EMPTY;

    public DockBlockEntity(BlockEntityType<?> $$0, BlockPos $$1, BlockState $$2) {
        super($$0, $$1, $$2);
    }

    public DockBlockEntity(BlockPos $$1, BlockState $$2) {
        this(ModBlockEntityTypes.DOCK, $$1, $$2);
    }

    public FluidInventory getInventory() {
        if (tank.isEmpty()) return FluidInventory.EMPTY;

        return TankSavedData.get(TankItem.getFrequency(tank),level.getServer()).getOrCreateInventory();
    }

    @Override
    protected void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        tag.put("tank",tank.save(new CompoundTag()));
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        tank = ItemStack.of(tag.getCompound("tank"));
    }

    public void addTank(ItemStack tank) {
        if (tank.getItem() instanceof TankItem tankItem) {
            TankStats stats = tankItem.stats;
            level.setBlockAndUpdate(worldPosition, getBlockState().setValue(DockBlock.TIER, stats.ordinal()));
            this.tank = tank.split(1);
            setChanged();
        }
    }

    public void giveToPlayer(Player player) {
        ItemStack tank = removeTankWithoutItemSpawn();

        if (!player.addItem(tank)) {
            ItemEntity entity = new ItemEntity(level, worldPosition.getX(), worldPosition.getY(), worldPosition.getZ(), tank);
            level.addFreshEntity(entity);
        }
    }

    @Override
    public CompoundTag getUpdateTag() {
        return saveWithoutMetadata();
    }

    public void removeTankWithItemSpawn() {
        ItemStack dankInStack = removeTankWithoutItemSpawn();
        ItemEntity entity = new ItemEntity(level, worldPosition.getX(), worldPosition.getY(), worldPosition.getZ(), dankInStack);
        level.addFreshEntity(entity);
    }

    public ItemStack removeTankWithoutItemSpawn() {
        level.setBlockAndUpdate(worldPosition, getBlockState().setValue(DockBlock.TIER, 0));
        ItemStack stack = tank.copy();
        tank = ItemStack.EMPTY;
        setChanged();
        return stack;
    }

    public Component getDefaultName() {
        return getBlockState().getBlock().getName();
    }

    @Override
    public Component getName() {
        Component custom = getCustomName();
        return custom != null ? custom : getDefaultName();
    }

    @Override
    public Component getDisplayName() {
        return getName();
    }

    @Nullable
    @Override
    public Component getCustomName() {
        return tank.isEmpty() ? null : tank.getDisplayName();
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int i, Inventory inventory, Player player) {
        if (tank.getItem() instanceof TankItem tankItem) {
            return tankItem.createProvider(tank).createMenu(i, inventory, player);
        }
        return null;
    }
}
