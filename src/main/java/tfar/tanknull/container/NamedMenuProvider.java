package tfar.tanknull.container;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.ITextComponent;
import tfar.tanknull.TankStats;
import tfar.tanknull.Utils;
import tfar.tanknull.inventory.FluidStackHandler;
import tfar.tanknull.inventory.TankNullBlockFluidStackHandler;
import tfar.tanknull.inventory.TankNullItemStackFluidStackHandler;

import javax.annotation.Nullable;

public class NamedMenuProvider implements INamedContainerProvider {

  private final ItemStack stack;

  public NamedMenuProvider(ItemStack stack){
    this.stack = stack;
  }

  @Override
  public ITextComponent getDisplayName() {
    return stack.getDisplayName();
  }

  @Nullable
  @Override
  public Container createMenu(int id, PlayerInventory inv, PlayerEntity player) {
    TankStats stats = Utils.getStats(stack);
    FluidStackHandler fluidStackHandler = TankNullItemStackFluidStackHandler.create(stack);
    switch (stats) {
      case one:return ItemStackTankNullMenu.t1s(id,inv,fluidStackHandler);
      case two:return ItemStackTankNullMenu.t2s(id,inv,fluidStackHandler);
      case three:return ItemStackTankNullMenu.t3s(id,inv,fluidStackHandler);
      case four:return ItemStackTankNullMenu.t4s(id,inv,fluidStackHandler);
      case five:return ItemStackTankNullMenu.t5s(id,inv,fluidStackHandler);
      case six:return ItemStackTankNullMenu.t6s(id,inv,fluidStackHandler);
      case seven:return ItemStackTankNullMenu.t7s(id,inv,fluidStackHandler);
    }
    return null;
  }
}
