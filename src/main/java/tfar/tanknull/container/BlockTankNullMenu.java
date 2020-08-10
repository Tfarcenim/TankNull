package tfar.tanknull.container;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.items.SlotItemHandler;
import net.minecraftforge.items.wrapper.InvWrapper;
import tfar.tanknull.RegistryObjects;
import tfar.tanknull.TankStats;
import tfar.tanknull.inventory.FluidStackHandler;
import tfar.tanknull.inventory.ItemStackFluidStackHandler;

import javax.annotation.Nonnull;
import java.util.List;

public class BlockTankNullMenu extends Container {

  public PlayerInventory inv;

  public final FluidStackHandler fluidStackHandler;

  public TankStats stats;

  //client
  public BlockTankNullMenu(ContainerType<?> type, int id, PlayerInventory inv, TankStats stats,PacketBuffer buffer) {
    this(type,id,inv,stats,readPacket(new FluidStackHandler(stats.slots,stats.capacity),buffer));
  }

  public static FluidStackHandler readPacket(FluidStackHandler handler,PacketBuffer buf) {
    for (int i = 0; i < handler.getTanks();i++) {
      FluidStack stack = buf.readFluidStack();
      handler.setStackInSlot(i,stack);
    }
    return handler;
  }

  //server and itemstack client
  public BlockTankNullMenu(ContainerType<?> type, int id, PlayerInventory inv, TankStats stats, FluidStackHandler fluidStackHandler) {
    super(type, id);
    this.stats = stats;
    this.inv = inv;
    this.fluidStackHandler = fluidStackHandler;
    addPlayerSlots();
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

  protected void addPlayerSlots() {
    int yStart = 86;
    for (int row = 0; row < 3; ++row) {
      for (int col = 0; col < 9; ++col) {
        int x = 8 + col * 18;
        int y = row * 18 + yStart;
        this.addSlot(new Slot(inv, col + row * 9 + 9, x, y));
      }
    }

    for (int row = 0; row < 9; ++row) {
      int x = 8 + row * 18;
      int y = yStart + 58;
      this.addSlot(new Slot(inv, row, x, y));
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
      if (!this.mergeItemStack(itemstack1, 0, 0, false)) {
        return ItemStack.EMPTY;
      }

      if (itemstack1.isEmpty()) {
        slot.putStack(ItemStack.EMPTY);
      } else {
        slot.onSlotChanged();
      }
    }
    return itemstack;
  }

  //blockentity form
  //client
  public static BlockTankNullMenu t1(int i, PlayerInventory playerInventory, PacketBuffer buf) {
    return new BlockTankNullMenu(RegistryObjects.tank_1_container,i,playerInventory, TankStats.one,buf);
  }

  public static BlockTankNullMenu t2(int i, PlayerInventory playerInventory, PacketBuffer buf) {
    return new BlockTankNullMenu(RegistryObjects.tank_2_container,i,playerInventory,TankStats.two,buf);
  }

  public static BlockTankNullMenu t3(int i, PlayerInventory playerInventory, PacketBuffer buf) {
    return new BlockTankNullMenu(RegistryObjects.tank_3_container,i,playerInventory,TankStats.three,buf);
  }

  public static BlockTankNullMenu t4(int i, PlayerInventory playerInventory, PacketBuffer buf) {
    return new BlockTankNullMenu(RegistryObjects.tank_4_container,i,playerInventory,TankStats.four,buf);
  }

  public static BlockTankNullMenu t5(int i, PlayerInventory playerInventory, PacketBuffer buf) {
    return new BlockTankNullMenu(RegistryObjects.tank_5_container,i,playerInventory,TankStats.five,buf);
  }

  public static BlockTankNullMenu t6(int i, PlayerInventory playerInventory, PacketBuffer buf) {
    return new BlockTankNullMenu(RegistryObjects.tank_6_container,i,playerInventory,TankStats.six,buf);
  }

  public static BlockTankNullMenu t7(int i, PlayerInventory playerInventory, PacketBuffer buf) {
    return new BlockTankNullMenu(RegistryObjects.tank_7_container,i,playerInventory,TankStats.seven,buf);
  }

  //server

  public static BlockTankNullMenu t1s(int i, PlayerInventory playerInventory,FluidStackHandler fluidStackHandler) {
    return new BlockTankNullMenu(RegistryObjects.tank_1_container,i,playerInventory, TankStats.one,fluidStackHandler);
  }

  public static BlockTankNullMenu t2s(int i, PlayerInventory playerInventory, FluidStackHandler fluidStackHandler) {
    return new BlockTankNullMenu(RegistryObjects.tank_2_container,i,playerInventory,TankStats.two,fluidStackHandler);
  }

  public static BlockTankNullMenu t3s(int i, PlayerInventory playerInventory, FluidStackHandler fluidStackHandler) {
    return new BlockTankNullMenu(RegistryObjects.tank_3_container,i,playerInventory,TankStats.three,fluidStackHandler);
  }

  public static BlockTankNullMenu t4s(int i, PlayerInventory playerInventory, FluidStackHandler fluidStackHandler) {
    return new BlockTankNullMenu(RegistryObjects.tank_4_container,i,playerInventory,TankStats.four,fluidStackHandler);
  }

  public static BlockTankNullMenu t5s(int i, PlayerInventory playerInventory, FluidStackHandler fluidStackHandler) {
    return new BlockTankNullMenu(RegistryObjects.tank_5_container,i,playerInventory,TankStats.five,fluidStackHandler);
  }

  public static BlockTankNullMenu t6s(int i, PlayerInventory playerInventory, FluidStackHandler fluidStackHandler) {
    return new BlockTankNullMenu(RegistryObjects.tank_6_container,i,playerInventory,TankStats.six,fluidStackHandler);
  }

  public static BlockTankNullMenu t7s(int i, PlayerInventory playerInventory, FluidStackHandler fluidStackHandler) {
    return new BlockTankNullMenu(RegistryObjects.tank_7_container,i,playerInventory,TankStats.seven,fluidStackHandler);
  }
}
