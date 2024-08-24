package tfar.tanknull.inventory;

import net.minecraft.world.item.ItemStack;
import tfar.tanknull.MLFluidStack;

public class FluidSlot {
    private final int slot;
    public final FluidInventory inventory;
    public int index;
    public final int x;
    public final int y;

    public FluidSlot(FluidInventory inventory, int pSlot, int pX, int pY) {
        this.inventory = inventory;
        this.slot = pSlot;
        this.x = pX;
        this.y = pY;
    }

    public boolean mayPlace(MLFluidStack stack) {
        return true;
    }

    public MLFluidStack getFluid() {
        return inventory.fluids.get(slot);
    }

    public boolean hasFluid() {
        return !getFluid().isEmpty();
    }


    public void set(MLFluidStack pStack) {
        this.inventory.setFluid(this.slot, pStack);
        this.setChanged();
    }

    /**
     * Called when the stack in a Slot changes
     */
    public void setChanged() {
        this.inventory.setDirty();
    }
}
