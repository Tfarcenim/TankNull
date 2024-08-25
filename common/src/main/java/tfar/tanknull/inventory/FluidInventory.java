package tfar.tanknull.inventory;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.inventory.ContainerData;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import tfar.tanknull.MLFluidStack;
import tfar.tanknull.TankStats;
import tfar.tanknull.platform.Services;
import tfar.tanknull.world.TankSavedData;

import java.util.*;


//ifluidhandler adapted to common
public class FluidInventory implements ContainerData {

    public NonNullList<MLFluidStack> fluids;
    public NonNullList<MLFluidStack> ghostFluids;
    public int capacity;
    @Nullable private final TankSavedData data;
    protected final Int2ObjectMap<Slot> wrappers = new Int2ObjectOpenHashMap<>();
    protected SortingType sortingType = SortingType.descending;
    protected boolean autoSort;
    protected boolean contentsChanged;

    public FluidInventory(TankStats stats,TankSavedData data) {
        this(stats.slots,stats.stacklimit,data);
    }

    public FluidInventory(int slots, int capacity, @Nullable TankSavedData data) {
        fluids = NonNullList.withSize(slots, MLFluidStack.EMPTY);
        ghostFluids = NonNullList.withSize(slots,MLFluidStack.EMPTY);
        this.capacity = capacity;
        this.data = data;
    }

    public Slot getWrapper(int slot) {
        return wrappers.computeIfAbsent(slot,value -> Services.PLATFORM.createWrapper(this,value));
    }

   static void merge(List<MLFluidStack> stacks, MLFluidStack toMerge) {
        for (MLFluidStack stack : stacks) {
            if (MLFluidStack.areFluidsEqual(stack, toMerge)) {
                int grow = Math.min(Integer.MAX_VALUE - stack.getAmount(), toMerge.getAmount());
                if (grow > 0) {
                    stack.grow(grow);
                    toMerge.shrink(grow);
                }
            }
        }
        if (!toMerge.isEmpty()) {
            stacks.add(toMerge);
        }
    }

    public void setAutoSort(boolean autoSort) {
        this.autoSort = autoSort;
        setDirty();
    }

    public void toggleAutoSort() {
        setAutoSort(!autoSort);
    }

    public void sort() {
        List<MLFluidStack> gathered = new ArrayList<>();
        Set<MLFluidStack> lockedItems = new HashSet<>();

        for (int i = 0; i < fluids.size(); i++) {
            MLFluidStack stack = fluids.get(i);
            MLFluidStack ghost = ghostFluids.get(i);
            if (!stack.isEmpty()) {
                merge(gathered, stack.copy());
                boolean unique = true;
                for (MLFluidStack stack1 : lockedItems) {
                    if (Objects.equals(stack1, stack)) {
                        unique = false;
                        break;
                    }
                }
                if (unique && !ghost.isEmpty()) {
                    lockedItems.add(stack.copyWithAmount(1000));
                }
            }
        }

        gathered.sort(sortingType.comparator);

        for (int i = 0; i < getSlots(); i++) {
            fluids.set(i, MLFluidStack.EMPTY);
            ghostFluids.set(i, MLFluidStack.EMPTY);
        }
        //split up the gathered and add them to the slot
        int slotId = 0;

        for (int i = 0; i < gathered.size(); i++) {
            MLFluidStack stack = gathered.get(i);
            int count = stack.getAmount();

            int tankSize = getTankSize(i);

            if (count > tankSize) {
                int fullStacks = count / tankSize;
                int partialStack = count - fullStacks * tankSize;

                for (int j = 0; j < fullStacks; j++) {
                    fluids.set(slotId, stack.copyWithAmount(tankSize));

                    if (anyMatch(stack,lockedItems)) {
                        ghostFluids.set(slotId,stack);
                    }

                    slotId++;
                }
                if (partialStack > 0) {
                    fluids.set(slotId, stack.copyWithAmount(partialStack));

                    if (anyMatch(stack,lockedItems)) {
                        ghostFluids.set(slotId,stack);
                    }

                    slotId++;
                }
            } else {
                fluids.set(slotId, stack);
                if (anyMatch(stack,lockedItems)) {
                    ghostFluids.set(slotId,stack);
                }

                slotId++;
            }
        }
    }

    public void setSortingType(SortingType sortingType) {
        this.sortingType = sortingType;
        setDirty();
    }

    public SortingType getSortingType() {
        return sortingType;
    }

    static boolean anyMatch(MLFluidStack stack, Set<MLFluidStack> stacks) {
        for (MLFluidStack stack1 : stacks) {
            if (stack1.isFluidEqual(stack)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public int get(int i) {
        return switch (i) {
            case 0 -> sortingType.ordinal();
            case 1 -> autoSort ? 1 : 0;
            default -> 0;
        };
    }

    @Override
    public void set(int i, int value) {
        switch (i) {
            case 0 -> sortingType = SortingType.values()[value];
            case 1 -> autoSort = value != 0;
        }
    }

    @Override
    public int getCount() {
        return 2;
    }

    public class Slot {

        final int slot;
        Slot(int slot) {
            this.slot = slot;
        }

    }

    public static final FluidInventory EMPTY = empty();

    public static FluidInventory empty() {
        return new FluidInventory(TankStats.zero,null);
    }

    public static FluidInventory dummy(TankStats stats) {
        return new FluidInventory(stats,null);
    }

    public void load(HolderLookup.Provider provider, CompoundTag tag) {
        loadList(fluids,tag.getList("Fluids", Tag.TAG_COMPOUND));
        loadList(ghostFluids,tag.getList("GhostFluids",Tag.TAG_COMPOUND));
        sortingType = tag.contains("SortingType") ? SortingType.valueOf(tag.getString("SortingType")) : SortingType.descending;
        autoSort = tag.getBoolean("AutoSort");
    }

    void loadList(List<MLFluidStack> list,ListTag tag) {
        for (int i = 0; i < tag.size(); i++) {
            CompoundTag fluidTags = tag.getCompound(i);
            int tank = fluidTags.getInt("Tank");
            if (tank >= 0 && tank < list.size()) {
                list.set(tank, MLFluidStack.fromNBT(fluidTags));
            }
        }
    }

    public CompoundTag save(HolderLookup.Provider provider) {
        CompoundTag nbt = new CompoundTag();
        nbt.put("Fluids",  saveList(fluids));
        nbt.put("GhostFluids",  saveList(ghostFluids));
        nbt.putString("SortingType",sortingType.name());
        nbt.putBoolean("AutoSort",autoSort);
        return nbt;
    }

    ListTag saveList(List<MLFluidStack> list) {
        ListTag nbtTagList = new ListTag();
        for (int i = 0; i < list.size(); i++) {
            if (!list.get(i).isEmpty()) {
                CompoundTag fluidTag = new CompoundTag();
                fluidTag.putInt("Tank", i);
                list.get(i).writeToNBT(fluidTag);
                nbtTagList.add(fluidTag);
            }
        }
        return nbtTagList;
    }

    public enum Action {
        EXECUTE, SIMULATE;

        public boolean execute() {
            return this == EXECUTE;
        }

        public boolean simulate() {
            return this == SIMULATE;
        }
    }

    /**
     * Returns the number of fluid storage units ("tanks") available
     *
     * @return The number of tanks available
     */
    public int getSlots() {
        return fluids.size();
    }

    /**
     * Returns the FluidStack in a given tank.
     *
     * <p>
     * <strong>IMPORTANT:</strong> This FluidStack <em>MUST NOT</em> be modified. This method is not for
     * altering internal contents. Any implementers who are able to detect modification via this method
     * should throw an exception. It is ENTIRELY reasonable and likely that the stack returned here will be a copy.
     * </p>
     *
     * <p>
     * <strong><em>SERIOUSLY: DO NOT MODIFY THE RETURNED FLUIDSTACK</em></strong>
     * </p>
     *
     * @param tank Tank to query.
     * @return FluidStack in a given tank. FluidStack.EMPTY if the tank is empty.
     */
    @NotNull
    MLFluidStack getFluid(int tank) {
        return fluids.get(tank).copy().immutable();
    }

    /**
     * Retrieves the maximum fluid amount for a given tank.
     *
     * @param tank Tank to query.
     * @return The maximum fluid amount held by the tank.
     */
    int getTankSize(int tank) {
        return capacity;
    }

    /**
     * This function is a way to determine which fluids can exist inside a given handler. General purpose tanks will
     * basically always return TRUE for this.
     *
     * @param tank  Tank to query for validity
     * @param stack Stack to test with for validity
     * @return TRUE if the tank can hold the FluidStack, not considering current state.
     * (Basically, is a given fluid EVER allowed in this tank?) Return FALSE if the answer to that question is 'no.'
     */
    boolean isFluidValid(int tank, @NotNull MLFluidStack stack) {
        return true;
    }

    /**
     * Fills fluid into internal tanks, distribution is left entirely to the IFluidHandler.
     *
     * @param resource FluidStack representing the Fluid and maximum amount of fluid to be filled.
     * @param action   If SIMULATE, fill will only be simulated.
     * @return Amount of resource that was (or would have been, if simulated) filled.
     */
    public int fill(MLFluidStack resource, Action action) {
        if (resource.isEmpty()) {
            return 0;
        }

        MLFluidStack remainder = resource.copy();
        int total = 0;
        for (int i = 0; i < getSlots();i++) {
            int fill = fillSpecific(remainder,action,i);
            remainder.shrink(fill);
            total+=fill;
        }
        return total;
    }

    public int fillSpecific(MLFluidStack resource, Action action, int tank) {
        if (resource.isEmpty()) {
            return 0;
        }

        int capacity = getTankSize(tank);

        MLFluidStack tankFluid = fluids.get(tank);
        MLFluidStack ghost = ghostFluids.get(tank);

        if (!isFluidCompatible(tankFluid,resource,ghost)) {
            return 0;
        }

        if (action.simulate()) {
            if (tankFluid.isEmpty()) {
                return Math.min(getTankSize(tank), resource.getAmount());
            }
            return Math.min(capacity - tankFluid.getAmount(), resource.getAmount());
        }
        if (tankFluid.isEmpty()) {
            tankFluid = resource.copyWithAmount(Math.min(capacity, resource.getAmount()));
            fluids.set(tank,tankFluid);
            contentsChanged = true;
            setDirty();
            return tankFluid.getAmount();
        }
        int filled = capacity - tankFluid.getAmount();

        if (resource.getAmount() < filled) {
            tankFluid.grow(resource.getAmount());
            filled = resource.getAmount();
        } else {
            tankFluid.setAmount(capacity);
        }
        if (filled > 0) {
            contentsChanged = true;
            setDirty();
        }
        return filled;
    }

    boolean isFluidCompatible(MLFluidStack tankFluid,MLFluidStack incoming,MLFluidStack ghost) {
        if (!tankFluid.isFluidEqual(incoming) && !tankFluid.isEmpty()) {
            return false;
        }

        if (ghost.isEmpty()) {
            return true;
        } else {
            return ghost.isFluidEqual(incoming);
        }
    }

    /**
     * Drains fluid out of internal tanks, distribution is left entirely to the IFluidHandler.
     *
     * @param resource FluidStack representing the Fluid and maximum amount of fluid to be drained.
     * @param action   If SIMULATE, drain will only be simulated.
     * @return FluidStack representing the Fluid and amount that was (or would have been, if
     * simulated) drained.
     */
    @NotNull
    MLFluidStack drain(MLFluidStack resource, Action action) {
        if (resource.isEmpty()) {
            return MLFluidStack.EMPTY;
        }
        return drain(resource.getAmount(), action);
    }

    public MLFluidStack drainSpecific(MLFluidStack resource, Action action, int tank) {
        MLFluidStack fluid = fluids.get(tank);
        if (resource.isEmpty() || !resource.isFluidEqual(fluid)) {
            return MLFluidStack.EMPTY;
        }
        return drainSpecific(resource.getAmount(), action,tank);
    }

    /**
     * Drains fluid out of internal tanks, distribution is left entirely to the IFluidHandler.
     * <p>
     * This method is not Fluid-sensitive.
     *
     * @param maxDrain Maximum amount of fluid to drain.
     * @param action   If SIMULATE, drain will only be simulated.
     * @return FluidStack representing the Fluid and amount that was (or would have been, if
     * simulated) drained.
     */
    @NotNull
    MLFluidStack drain(int maxDrain, Action action) {
        MLFluidStack totalDrained = null;
        for (int i = 0; i < getSlots();i++) {
            MLFluidStack drained = drainSpecific(maxDrain,action,i);
            if (totalDrained == null) {
                if (!drained.isEmpty()) {
                    totalDrained = drained;
                }
            } else {
                if (!drained.isEmpty()) {
                    totalDrained.grow(drained.getAmount());
                }
            }
        }
        return totalDrained != null ? totalDrained : MLFluidStack.EMPTY;
    }

    public MLFluidStack drainSpecific(int maxDrain,Action action,int tank) {
        int drained = maxDrain;

        MLFluidStack fluid = fluids.get(tank);

        if (fluid.getAmount() < drained) {
            drained = fluid.getAmount();
        }
        MLFluidStack stack = fluid.copyWithAmount(drained);
        if (action.execute() && drained > 0) {
            fluid.shrink(drained);
            contentsChanged = true;
            setDirty();
        }
        return stack;
    }

    public void updateStats(TankStats stats) {
        capacity = stats.stacklimit;

        NonNullList<MLFluidStack> nonNullList = NonNullList.withSize(stats.slots,MLFluidStack.EMPTY);
        for (int i = 0; i < fluids.size(); i++) {
            MLFluidStack fluidStack = fluids.get(i);
            nonNullList.set(i,fluidStack);
        }

        NonNullList<MLFluidStack> nonNullListGhost = NonNullList.withSize(stats.slots,MLFluidStack.EMPTY);
        for (int i = 0; i < ghostFluids.size(); i++) {
            MLFluidStack fluidStack = ghostFluids.get(i);
            nonNullListGhost.set(i,fluidStack);
        }

        fluids = nonNullList;
        ghostFluids = nonNullListGhost;

        setDirty();
    }

    public void setDirty() {
        if (contentsChanged) {
            if (autoSort) {
                sort();
            }
            contentsChanged = false;
        }
        if (data != null) {
            data.setDirty();
        }
    }
}
