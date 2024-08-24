package tfar.tanknull.inventory;

import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import tfar.tanknull.MLFluidStack;
import tfar.tanknull.TankStats;
import tfar.tanknull.world.TankSavedData;

import java.util.List;


//ifluidhandler adapted to common
public class FluidInventory {

    public NonNullList<MLFluidStack> fluids;
    public final int capacity;
    @Nullable private final TankSavedData data;

    public FluidInventory(TankStats stats,TankSavedData data) {
        this(stats.slots,stats.stacklimit,data);
    }

    public FluidInventory(int slots, int capacity, @Nullable TankSavedData data) {
        fluids = NonNullList.withSize(slots, MLFluidStack.EMPTY);
        this.capacity = capacity;
        this.data = data;
    }

    public static final FluidInventory EMPTY = empty();

    public static FluidInventory empty() {
        return new FluidInventory(TankStats.zero,null);
    }

    public static FluidInventory dummy(TankStats stats) {
        return new FluidInventory(stats,null);
    }

    public void load(HolderLookup.Provider provider, CompoundTag tag) {
        ListTag tagList = tag.getList("Fluids", Tag.TAG_COMPOUND);
        for (int i = 0; i < tagList.size(); i++) {
            CompoundTag fluidTags = tagList.getCompound(i);
            int tank = fluidTags.getInt("Tank");
            if (tank >= 0 && tank < fluids.size()) {
                fluids.set(tank, MLFluidStack.fromNBT(fluidTags));
            }
        }
    }

    public CompoundTag save(HolderLookup.Provider provider) {
        ListTag nbtTagList = new ListTag();
        for (int i = 0; i < fluids.size(); i++) {
            if (!fluids.get(i).isEmpty()) {
                CompoundTag fluidTag = new CompoundTag();
                fluidTag.putInt("Tank", i);
                fluids.get(i).writeToNBT(fluidTag);
                nbtTagList.add(fluidTag);
            }
        }
        CompoundTag nbt = new CompoundTag();
        nbt.put("Fluids", nbtTagList);
        return nbt;
    }

    public void setFluid(int slot, MLFluidStack stack) {
        fluids.set(slot,stack);
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

        MLFluidStack fluid = fluids.get(tank);

        if (action.simulate()) {
            if (fluid.isEmpty()) {
                return Math.min(getTankSize(tank), resource.getAmount());
            }
            if (!fluid.isFluidEqual(resource)) {
                return 0;
            }
            return Math.min(capacity - fluid.getAmount(), resource.getAmount());
        }
        if (fluid.isEmpty()) {
            fluid = resource.copyWithAmount(Math.min(capacity, resource.getAmount()));
            fluids.set(tank,fluid);
            setDirty();
            return fluid.getAmount();
        }
        if (!fluid.isFluidEqual(resource)) {
            return 0;
        }
        int filled = capacity - fluid.getAmount();

        if (resource.getAmount() < filled) {
            fluid.grow(resource.getAmount());
            filled = resource.getAmount();
        } else {
            fluid.setAmount(capacity);
        }
        if (filled > 0) {
            setDirty();
        }
        return filled;
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

    MLFluidStack drainSpecific(MLFluidStack resource, Action action,int tank) {
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

    MLFluidStack drainSpecific(int maxDrain,Action action,int tank) {
        int drained = maxDrain;

        MLFluidStack fluid = fluids.get(tank);

        if (fluid.getAmount() < drained) {
            drained = fluid.getAmount();
        }
        MLFluidStack stack = fluid.copyWithAmount(drained);
        if (action.execute() && drained > 0) {
            fluid.shrink(drained);
            setDirty();
        }
        return stack;
    }

    void setDirty() {
        if (data != null) {
            data.setDirty();
        }
    }
}
