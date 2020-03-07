package tfar.tanknull.inventory;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandlerItem;
import tfar.tanknull.TankNullItem;
import tfar.tanknull.Utils;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class TankNullItemStackFluidStackHandler extends ItemStackFluidStackHandler implements ICapabilityProvider {

  public int selectedTank = 0;
  protected final LazyOptional<IFluidHandlerItem> holder = LazyOptional.of(() -> TankNullItemStackFluidStackHandler.create(container));
  public boolean fill = false;
  public boolean sponge = false;

  public TankNullItemStackFluidStackHandler(int tanks, int capacity, ItemStack container) {
    super(tanks, capacity, container);
  }

  @Override
  public ItemStackFluidStackHandler setCapacity(int capacity) {
    super.setCapacity(Utils.getCapacity(container));
    return this;
  }

  @Override
  public ItemStackFluidStackHandler setTanks(int tanks) {
    super.setTanks(Utils.getTanks(container));
    return this;
  }

  @Override
  public void onContentsChanged() {
    super.onContentsChanged();
    saveToItemStack();
  }

  public void saveToItemStack() {
    CompoundNBT nbt = serializeNBT();
    container.getOrCreateTag().put("fluidinv", nbt);
  }

  @Nonnull
  public TankNullItemStackFluidStackHandler loadFromItemStack(ItemStack stack) {
    CompoundNBT nbt = stack.getOrCreateChildTag("fluidinv");
      deserializeNBT(nbt);
    return this;
  }

  public boolean canPickup(FluidStack stack) {
    return hasRoomForBlockFluid(stack);
  }

  public boolean hasRoomForBlockFluid(FluidStack toPickup) {
    return toPickup.getAmount() == fill1000(FluidAction.SIMULATE, toPickup);
  }

  public FluidStack drain1000(FluidAction action) {
    return drain(selectedTank, 1000, action);
  }

  public int fill1000(FluidAction action, FluidStack toPickup) {
    return fill(toPickup, action);
  }

  public FluidStack getSelectedFluid() {
    return getFluidInTank(selectedTank);
  }

  public void toggleFill() {
    fill = !fill;
    saveToItemStack();
  }

  public void scroll(boolean right) {
    if (right) {
      selectedTank++;
      if (selectedTank >= getTanks()) selectedTank = 0;
    } else {
      selectedTank--;
      if (selectedTank < 0) selectedTank = getTanks() - 1;
    }
    saveToItemStack();
  }

  @Override
  public CompoundNBT serializeNBT() {
    CompoundNBT nbt = super.serializeNBT();
    nbt.putInt("SelectedTank", selectedTank);
    nbt.putBoolean("fill", fill);
    nbt.putBoolean("sponge", sponge);
    return nbt;
  }

  @Override
  public void deserializeNBT(CompoundNBT nbt) {
    super.deserializeNBT(nbt);
    selectedTank = nbt.getInt("SelectedTank");
    fill = nbt.getBoolean("fill");
    sponge = nbt.getBoolean("sponge");
  }

  public static TankNullItemStackFluidStackHandler create(ItemStack stack) {
    if (stack.getItem() instanceof TankNullItem) {
      TankNullItemStackFluidStackHandler handler = new TankNullItemStackFluidStackHandler(Utils.getTanks(stack), Utils.getCapacity(stack), stack);
      return handler.loadFromItemStack(stack);
    }
    throw new IllegalStateException("no");
  }

  /**
   * Retrieves the Optional handler for the capability requested on the specific side.
   * The return value <strong>CAN</strong> be the same for multiple faces.
   * Modders are encouraged to cache this value, using the listener capabilities of the Optional to
   * be notified if the requested capability get lost.
   *
   * @param cap
   * @param side
   * @return The requested an optional holding the requested capability.
   */
  @Nonnull
  @Override
  public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
    return CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY.orEmpty(cap, holder);
  }

  public void toggleSponge() {
    sponge = !sponge;
    saveToItemStack();
  }
}