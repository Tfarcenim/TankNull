package tfar.tanknull.inventory;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.fluids.FluidStack;
import tfar.tanknull.TankNullItem;
import tfar.tanknull.Utils;

import javax.annotation.Nonnull;

public class TankNullItemStackFluidStackHandler extends ItemStackFluidStackHandler {

  public int selectedTank = 0;
  public boolean isFill = false;
  public boolean sponge = false;
  public boolean smartPlacing = true;

  public TankNullItemStackFluidStackHandler(int tanks, int capacity, ItemStack container) {
    super(tanks, capacity, container);
  }

  @Override
  public void onContentsChanged(int slot) {
    super.onContentsChanged(slot);
    saveToItemStack();
  }

  public void saveToItemStack() {
    CompoundNBT nbt = serializeNBT();
    tank.getOrCreateTag().put("fluidinv", nbt);
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
    isFill = !isFill;
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
    nbt.putBoolean("fill", isFill);
    nbt.putBoolean("sponge", sponge);
    return nbt;
  }

  @Override
  public void deserializeNBT(CompoundNBT nbt) {

    ListNBT tagList = nbt.getList("Fluids", Constants.NBT.TAG_COMPOUND);
    for (int i = 0; i < tagList.size(); i++) {
      CompoundNBT fluidTags = tagList.getCompound(i);
      int tank = fluidTags.getInt("Tank");
      if (tank >= 0 && tank < stacks.size()) {
        stacks.set(tank, FluidStack.loadFluidStackFromNBT(fluidTags));
      }
    }

    selectedTank = nbt.getInt("SelectedTank");
    isFill = nbt.getBoolean("fill");
    sponge = nbt.getBoolean("sponge");
  }

  public static TankNullItemStackFluidStackHandler create(ItemStack stack) {
    if (stack.getItem() instanceof TankNullItem) {
      TankNullItemStackFluidStackHandler handler = new TankNullItemStackFluidStackHandler(Utils.getTanks(stack), Utils.getCapacity(stack), stack);
      return handler.loadFromItemStack(stack);
    }
    throw new IllegalStateException("not a tank null");
  }

  public void toggleSponge() {
    sponge = !sponge;
    saveToItemStack();
  }
}