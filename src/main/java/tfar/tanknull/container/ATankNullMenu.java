package tfar.tanknull.container;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.items.SlotItemHandler;
import net.minecraftforge.items.wrapper.InvWrapper;

import javax.annotation.Nonnull;

public abstract class ATankNullMenu extends Container {

  public PlayerInventory inv;
  protected ATankNullMenu(ContainerType<?> type, int id, PlayerInventory inv) {
    super(type, id);
    this.inv = inv;
  }

  /**
   * Determines whether supplied player can use this container
   *
   * @param playerIn
   */
  @Override
  public boolean canInteractWith(PlayerEntity playerIn) {
    return true;
  }

  public abstract IFluidHandler getHandler();

  protected void addPlayerSlots(InvWrapper playerinventory) {
    int yStart = 86;
    for (int row = 0; row < 3; ++row) {
      for (int col = 0; col < 9; ++col) {
        int x = 8 + col * 18;
        int y = row * 18 + yStart;
        this.addSlot(new SlotItemHandler(playerinventory, col + row * 9 + 9, x, y));
      }
    }

    for (int row = 0; row < 9; ++row) {
      int x = 8 + row * 18;
      int y = yStart + 58;
      this.addSlot(new SlotItemHandler(playerinventory, row, x, y));
    }
  }

  @Nonnull
  @Override
  public ItemStack transferStackInSlot(PlayerEntity playerIn, int index) {
    ItemStack itemstack = ItemStack.EMPTY;
    Slot slot = this.inventorySlots.get(index);

    if (slot != null && slot.getHasStack()) {
      ItemStack itemstack1 = slot.getStack();
      itemstack = itemstack1.copy();

      if (itemstack1.isEmpty()) {
        slot.putStack(ItemStack.EMPTY);
      } else {
        slot.onSlotChanged();
      }
    }

    return itemstack;
  }

}
