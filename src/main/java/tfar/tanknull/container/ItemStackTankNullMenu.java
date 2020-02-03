package tfar.tanknull.container;

import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraftforge.items.SlotItemHandler;
import net.minecraftforge.items.wrapper.InvWrapper;
import tfar.tanknull.RegistryObjects;
import tfar.tanknull.inventory.TankNullItemStackFluidStackHandler;

public class ItemStackTankNullMenu extends ATankNullMenu {

  public ItemStack te;

  public ItemStackTankNullMenu(int id, PlayerInventory inv) {
    super(RegistryObjects.item_container.get(), id, inv);
    te = inv.player.getHeldItem(Hand.MAIN_HAND);
    addPlayerSlots(new InvWrapper(inv),inv.currentItem);
  }

  protected void addPlayerSlots(InvWrapper playerinventory, int locked) {
    int yStart = 32 + 18 * 3;
    for (int row = 0; row < 3; ++row) {
      for (int col = 0; col < 9; ++col) {
        int x = 8 + col * 18;
        int y = row * 18 + yStart;
        this.addSlot(new SlotItemHandler(playerinventory, col + row * 9 + 9, x, y) {
          @Override
          public int getItemStackLimit(ItemStack stack) {
            return Math.min(this.getSlotStackLimit(), stack.getMaxStackSize());
          }
        });
      }
    }

    for (int row = 0; row < 9; ++row) {
      int x = 8 + row * 18;
      int y = yStart + 58;
      if (row != locked)
        this.addSlot(new SlotItemHandler(playerinventory, row, x, y) {
          @Override
          public int getItemStackLimit(ItemStack stack) {
            return Math.min(this.getSlotStackLimit(), stack.getMaxStackSize());
          }
        });
      else
        this.addSlot(new LockedSlot(playerinventory, row, x, y));
    }
  }

  @Override
  public TankNullItemStackFluidStackHandler getHandler() {
    return TankNullItemStackFluidStackHandler.create(te);
  }
}
