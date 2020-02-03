package tfar.tanknull.inventory;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.util.NonNullList;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.fluids.FluidStack;

public class TankNullBlockFluidStackHandler extends FluidStackHandler {

  public TankNullBlockFluidStackHandler(int tanks, int capacity) {
    super(tanks,capacity);
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
    setCapacity(nbt.contains("Capacity", Constants.NBT.TAG_INT) && nbt.getInt("Capacity") > 0 ? nbt.getInt("Capacity") : capacity);
    setSize(nbt.contains("Size", Constants.NBT.TAG_INT) && nbt.getInt("Size") > 0 ? nbt.getInt("Size") : stacks.size());
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
}