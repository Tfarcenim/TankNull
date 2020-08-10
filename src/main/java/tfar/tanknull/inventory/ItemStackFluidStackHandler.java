package tfar.tanknull.inventory;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.util.NonNullList;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandlerItem;

import javax.annotation.Nonnull;

public class ItemStackFluidStackHandler extends FluidStackHandler implements IFluidHandlerItem {

  protected final ItemStack tank;

  public ItemStackFluidStackHandler(int tanks, int capacity, ItemStack tank) {
    super(tanks,capacity);
    this.tank = tank;
  }

  @Nonnull
  @Override
  public ItemStack getContainer() {
    return tank;
  }
}