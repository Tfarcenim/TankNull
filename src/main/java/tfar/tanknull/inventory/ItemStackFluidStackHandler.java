package tfar.tanknull.inventory;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.util.NonNullList;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.fluids.FluidStack;

import javax.annotation.Nonnull;

public class ItemStackFluidStackHandler implements IMultiTankItem, INBTSerializable<CompoundNBT> {

  protected NonNullList<FluidStack> stacks;
  protected int capacity;
  protected final ItemStack container;

  public ItemStackFluidStackHandler(int tanks, int capacity, ItemStack container) {
    this.stacks = NonNullList.withSize(tanks, FluidStack.EMPTY);
    this.capacity = capacity;
    this.container = container;
  }

  public ItemStackFluidStackHandler(int capacity, ItemStack container) {
    this(1, capacity, container);
  }

  public ItemStackFluidStackHandler setCapacity(int capacity) {
    this.capacity = capacity;
    return this;
  }

  public ItemStackFluidStackHandler setTanks(int tanks) {
    stacks = NonNullList.withSize(tanks, FluidStack.EMPTY);
    return this;
  }

  @Nonnull
  @Override
  public FluidStack getFluidInTank(int tank) {
    return this.stacks.get(tank);
  }

  public NonNullList<FluidStack> getContents() {
    return stacks;
  }

  @Override
  public int getTanks() {
    return stacks.size();
  }

  @Override
  public int getTankCapacity(int tank) {
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
  @Override
  public boolean isFluidValid(int tank, @Nonnull FluidStack stack) {
    return true;
  }

  /**
   * Fills fluid into internal tanks, distribution is left entirely to the IFluidHandler.
   *
   * @param resource FluidStack representing the Fluid and maximum amount of fluid to be filled.
   * @param action   If SIMULATE, fill will only be simulated.
   * @return Amount of resource that was (or would have been, if simulated) filled.
   */
  @Override
  public int fill(FluidStack resource, FluidAction action) {
    int totalFill = 0;
    FluidStack rem = resource.copy();
    for (int i = 0; i < getTanks(); i++) {
      if (!rem.isEmpty()) {
        int singleFill = fill(i, rem, action);
        totalFill += singleFill;
        rem = new FluidStack(resource, rem.getAmount() - singleFill);
      }
    }
    return totalFill;
  }

  /**
   * Drains fluid out of internal tanks, distribution is left entirely to the IFluidHandler.
   *
   * @param resource FluidStack representing the Fluid and maximum amount of fluid to be drained.
   * @param action   If SIMULATE, drain will only be simulated.
   * @return FluidStack representing the Fluid and amount that was (or would have been, if
   * simulated) drained.
   */
  @Nonnull
  @Override
  public FluidStack drain(FluidStack resource, FluidAction action) {
    if (resource.isEmpty())return FluidStack.EMPTY;
    FluidStack totalDrain = FluidStack.EMPTY;
    FluidStack toDrain = resource.copy();
    for (int i = 0; i < getTanks(); i++) {
      if (toDrain.isEmpty()) {
        break;
      } else {
        FluidStack singleDrain = drain(i, toDrain, action);
        if (totalDrain.isEmpty()) {
          totalDrain = singleDrain.copy();
        } else {
          totalDrain.grow(singleDrain.getAmount());
        }
        int a = toDrain.getAmount();
        int b = singleDrain.getAmount();
        toDrain = new FluidStack(toDrain, a - b);
      }
    }
    return totalDrain;
  }

  /**
   * Drains fluid out of internal tanks, distribution is left entirely to the IFluidHandler.
   * <p/>
   * This method is not Fluid-sensitive.
   *
   * @param maxDrain Maximum amount of fluid to drain.
   * @param action   If SIMULATE, drain will only be simulated.
   * @return FluidStack representing the Fluid and amount that was (or would have been, if
   * simulated) drained.
   */
  @Nonnull
  @Override
  public FluidStack drain(int maxDrain, FluidAction action) {
    if (maxDrain <= 0)return FluidStack.EMPTY;
    FluidStack totalDrainStack = FluidStack.EMPTY;
    int toDrain = maxDrain;
    for (int i = 0; i < getTanks(); i++) {
      if (toDrain <= 0) {
        break;
      } else {
        FluidStack singleDrain = drain(i, toDrain, action);
        if (totalDrainStack.isEmpty()) {
          totalDrainStack = singleDrain.copy();
        } else {
          totalDrainStack.grow(singleDrain.getAmount());
        }
        toDrain -= singleDrain.getAmount();
      }
    }
    return totalDrainStack;
  }


  /**
   * Fills fluid into selected tank.
   *
   * @param tank     Tank to insert into.
   * @param resource FluidStack representing the Fluid and maximum amount of fluid to be filled.
   * @param action   If SIMULATE, fill will only be simulated.
   * @return Amount of resource that was (or would have been, if simulated) filled.
   */
  @Override
  public int fill(int tank, FluidStack resource, FluidAction action) {
    if (resource.isEmpty() || !isFluidValid(tank, resource)) {
      return 0;
    }
    FluidStack existingFluid = getFluidInTank(tank);
    if (!existingFluid.isFluidEqual(resource) && !existingFluid.isEmpty()) {
      return 0;
    }
    validateSlotIndex(tank);
    if (existingFluid.isEmpty()) {
      int filled;
      if (resource.getAmount() > capacity) {
        filled = capacity;
        if (action.execute()) {
          stacks.set(tank, new FluidStack(resource,capacity));
          onContentsChanged();
        }
      } else {
        filled = resource.getAmount();
        if (action.execute()) {
          stacks.set(tank, resource.copy());
          onContentsChanged();
        }
      }
      return filled;
    } else {
      int filled = capacity - existingFluid.getAmount();
      if (resource.getAmount() < filled) {
        if (action.execute()) {
          existingFluid.grow(resource.getAmount());
        }
        filled = resource.getAmount();
      } else {
        if (action.execute()) {
          existingFluid.setAmount(capacity);
        }
      }
      if (filled > 0 && action.execute()) {
        onContentsChanged();
      }
      return filled;
    }
  }

  /**
   * Drains fluid out of selected tank.
   *
   * @param tank     Tank to extract from.
   * @param resource FluidStack representing the Fluid and maximum amount of fluid to be drained.
   * @param action   If SIMULATE, drain will only be simulated.
   * @return FluidStack representing the Fluid and amount that was (or would have been, if
   * simulated) drained.
   */
  @Nonnull
  @Override
  public FluidStack drain(int tank, FluidStack resource, FluidAction action) {
    if (resource.isEmpty() || !resource.isFluidEqual(getFluidInTank(tank)) || !isFluidValid(tank, resource)) {
      return FluidStack.EMPTY;
    }
    return drain(tank, resource.getAmount(), action);
  }

  /**
   * Drains fluid out of selected tank.
   * <p/>
   * This method is not Fluid-sensitive.
   *
   * @param tank     Tank to extract from.
   * @param maxDrain Maximum amount of fluid to drain.
   * @param action   If SIMULATE, drain will only be simulated.
   * @return FluidStack representing the Fluid and amount that was (or would have been, if
   * simulated) drained.
   */
  @Nonnull
  @Override
  public FluidStack drain(int tank, int maxDrain, FluidAction action) {
    if (maxDrain <= 0) {
      return FluidStack.EMPTY;
    }
    validateSlotIndex(tank);
    FluidStack existingFluid = getFluidInTank(tank);
    if (existingFluid.isEmpty()) {
      return FluidStack.EMPTY;
    }
    int drained = maxDrain;
    if (existingFluid.getAmount() <= maxDrain) {
      drained = existingFluid.getAmount();
      if (action.execute()) {
        stacks.set(tank, FluidStack.EMPTY);
        onContentsChanged();
      }
    } else {
      if (action.execute()) {
        stacks.set(tank, new FluidStack(existingFluid, existingFluid.getAmount() - drained));
        onContentsChanged();
      }
    }
    return new FluidStack(existingFluid, drained);
  }

  @Override
  public CompoundNBT serializeNBT() {
    ListNBT nbtTagList = new ListNBT();
    for (int i = 0; i < stacks.size(); i++) {
      if (!stacks.get(i).isEmpty()) {
        CompoundNBT fluidTag = new CompoundNBT();
        fluidTag.putInt("Tank", i);
        stacks.get(i).writeToNBT(fluidTag);
        nbtTagList.add(fluidTag);
      }
    }
    CompoundNBT nbt = new CompoundNBT();
    nbt.put("Fluids", nbtTagList);
    nbt.putInt("Size", stacks.size());
    nbt.putInt("Capacity", capacity);
    return nbt;
  }

  @Override
  public void deserializeNBT(CompoundNBT nbt) {
    setCapacity(nbt.contains("Capacity", Constants.NBT.TAG_INT) ? nbt.getInt("Capacity") : capacity);
    setTanks(nbt.contains("Size", Constants.NBT.TAG_INT) ? nbt.getInt("Size") : stacks.size());
    ListNBT tagList = nbt.getList("Fluids", Constants.NBT.TAG_COMPOUND);
    for (int i = 0; i < tagList.size(); i++) {
      CompoundNBT fluidTags = tagList.getCompound(i);
      int tank = fluidTags.getInt("Tank");
      if (tank >= 0 && tank < stacks.size()) {
        stacks.set(tank, FluidStack.loadFluidStackFromNBT(fluidTags));
      }
    }
    onLoad();
  }

  protected void onLoad() {
  }

  public void onContentsChanged() {

  }

  protected void validateSlotIndex(int tank) {
    if (tank < 0 || tank >= stacks.size()) {
      throw new RuntimeException("Tank " + tank + " not in valid range - [0," + stacks.size() + ")");
    }
  }

  public boolean isEmpty() {
    return stacks.stream().allMatch(FluidStack::isEmpty);
  }

  /**
   * Get the container currently acted on by this fluid handler.
   * The ItemStack may be different from its initial state, in the case of fluid containers that have different items
   * for their filled and empty states.
   * May be an empty item if the container was drained and is consumable.
   */
  @Nonnull
  @Override
  public ItemStack getContainer() {
    return container;
  }
}