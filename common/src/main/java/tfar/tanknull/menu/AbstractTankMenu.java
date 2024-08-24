package tfar.tanknull.menu;

import com.google.common.base.Suppliers;
import net.minecraft.core.NonNullList;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.ItemStack;
import tfar.tanknull.MLFluidStack;
import tfar.tanknull.TankStats;
import tfar.tanknull.init.ModMenuTypes;
import tfar.tanknull.inventory.FluidInventory;
import tfar.tanknull.inventory.FluidSlot;
import tfar.tanknull.inventory.LockedSlot;
import tfar.tanknull.network.client.S2CInitialSyncFluidInventoryPacket;
import tfar.tanknull.platform.Services;

import javax.annotation.Nonnull;
import java.util.Objects;
import java.util.function.Supplier;

public class AbstractTankMenu extends AbstractContainerMenu {

    public final Inventory playerInventory;
    public final int rows;
    public final FluidInventory fluidInventory;

    private final NonNullList<MLFluidStack> lastFluidSlots = NonNullList.create();
    public final NonNullList<FluidSlot> fluidSlots = NonNullList.create();
    private final NonNullList<MLFluidStack> remoteFluidSlots = NonNullList.create();

    public void initializeFluids(int stateID, NonNullList<MLFluidStack> stacks) {
        for(int i = 0; i < stacks.size(); ++i) {
            this.getFluidSlot(i).set(stacks.get(i));
        }

        this.stateId = stateID;
    }

    public enum ButtonAction {
        LOCK_FREQUENCY, SORT;
        static final ButtonAction[] VALUES = values();
    }


    public AbstractTankMenu(MenuType<?> type, int windowId, Inventory playerInventory, FluidInventory fluidInventory) {
        super(type, windowId);
        this.playerInventory = playerInventory;
        this.fluidInventory = fluidInventory;
        this.rows = fluidInventory.getSlots()/9;
        addPlayerSlots(playerInventory,-1);
        addTankSlots();
    }

    protected FluidSlot addFluidSlot(FluidSlot $$0) {
        $$0.index = this.fluidSlots.size();
        this.fluidSlots.add($$0);
        this.lastFluidSlots.add(MLFluidStack.EMPTY);
        this.remoteFluidSlots.add(MLFluidStack.EMPTY);
        return $$0;
    }

    protected void addTankSlots() {
        int slotIndex = 0;
        for (int row = 0; row < rows; ++row) {
            for (int col = 0; col < 9; ++col) {
                int x = 8 + col * 18;
                int y = row * 18 + 18;
                this.addFluidSlot(new FluidSlot(fluidInventory, slotIndex, x, y));
                slotIndex++;
            }
        }
    }

    protected void addPlayerSlots(Inventory playerinventory, int locked) {
        int yStart = 32 + 18 * rows;
        for (int row = 0; row < 3; ++row) {
            for (int col = 0; col < 9; ++col) {
                int x = 8 + col * 18;
                int y = row * 18 + yStart;
                this.addSlot(new Slot(playerinventory, col + row * 9 + 9, x, y));
            }
        }

        for (int row = 0; row < 9; ++row) {
            int x = 8 + row * 18;
            int y = yStart + 58;
            if (row != locked)
                this.addSlot(new Slot(playerinventory, row, x, y));
            else
                this.addSlot(new LockedSlot(playerinventory, row, x, y));
        }
    }

    @Override
    public boolean clickMenuButton(Player player, int id) {
        if (id < 0 || id >= ButtonAction.VALUES.length) return false;
        ButtonAction buttonAction = ButtonAction.VALUES[id];
        if (player instanceof ServerPlayer serverPlayer) {
            switch (buttonAction) {
             //   case LOCK_FREQUENCY -> fluidInventory.toggleFrequencyLock();
               // case SORT -> fluidInventory.sort();
            }
        }
        return true;
    }

    @Nonnull
    @Override
    public ItemStack quickMoveStack(Player playerIn, int index) {
        ItemStack itemstack = ItemStack.EMPTY;
        Slot slot = this.slots.get(index);

        if (slot.hasItem()) {
            ItemStack slotStack = slot.getItem();
            itemstack = slotStack.copy();


            if (index < rows * 9) {
                if (!this.moveItemStackTo(slotStack, rows * 9, this.slots.size(), true)) {
                    return ItemStack.EMPTY;
                }
            } else if (!this.moveItemStackTo(slotStack, 0, rows * 9, false)) {
                return ItemStack.EMPTY;
            }

            if (slotStack.isEmpty()) {
                slot.set(ItemStack.EMPTY);
            } else {
                slot.setChanged();
            }
        }
        return itemstack;
    }

    @Override
    public boolean stillValid(@Nonnull Player playerIn) {
        return true;
    }


    //used by quick transfer, needs to respect locked slots
    @Override
    protected boolean moveItemStackTo(ItemStack stack, int startIndex, int endIndex, boolean reverse) {
        boolean didSomething = false;
        int i = startIndex;

        if (reverse) {
            i = endIndex - 1;
        }

        while (!stack.isEmpty()) {
            if (reverse) {
                if (i < startIndex) break;
            } else {
                if (i >= endIndex) break;
            }

            Slot slot = this.slots.get(i);
            ItemStack slotStack = slot.getItem();

            if (!slotStack.isEmpty() && slotStack.getItem() == stack.getItem() && ItemStack.isSameItemSameTags(stack, slotStack)) {
                int combinedCount = slotStack.getCount() + stack.getCount();
                int maxSize = slot.getMaxStackSize(slotStack);

                if (combinedCount <= maxSize) {
                    stack.setCount(0);
                    slotStack.setCount(combinedCount);
                    slot.setChanged();
                    didSomething = true;
                } else if (slotStack.getCount() < maxSize) {
                    stack.shrink(maxSize - slotStack.getCount());
                    slotStack.setCount(maxSize);
                    slot.setChanged();
                    didSomething = true;
                }
            }

            i += reverse ? -1 : 1;
        }

        if (!stack.isEmpty()) {
            if (reverse) i = endIndex - 1;
            else i = startIndex;

            while (true) {
                if (reverse) {
                    if (i < startIndex) break;
                } else {
                    if (i >= endIndex) break;
                }

                Slot slot = this.slots.get(i);
                ItemStack itemstack1 = slot.getItem();

                if (itemstack1.isEmpty() && slot.mayPlace(stack)) {
                    if (stack.getCount() > slot.getMaxStackSize(stack)) {
                        slot.set(stack.split(slot.getMaxStackSize(stack)));
                    } else {
                        slot.set(stack.split(stack.getCount()));
                    }

                    slot.setChanged();
                    didSomething = true;
                    break;
                }

                i += reverse ? -1 : 1;
            }
        }

        return didSomething;
    }

    public boolean isDankSlot(Slot slot) {
        return slot.getClass().getName().endsWith("DankSlot");
    }

    @Override
    public void broadcastChanges() {
        super.broadcastChanges();
        //the remote inventory needs to know about locked slots
        for (int i = 0; i < fluidInventory.getSlots(); i++) {
       //     Services.PLATFORM.sendToClient(new S2CSendGhostSlotPacket(containerId,i, fluidInventory.getGhostItem(i)), (ServerPlayer)
        //            playerInventory.player);
        }

        for(int i = 0; i < this.fluidSlots.size(); ++i) {
            MLFluidStack fluid = this.fluidSlots.get(i).getFluid();
            Objects.requireNonNull(fluid);
            Supplier<MLFluidStack> $$2 = Suppliers.memoize(fluid::copy);
            //this.triggerSlotListeners(i, fluid, $$2);
            this.synchronizeFluidSlotToRemote(i, fluid, $$2);
        }

    }

    private void synchronizeFluidSlotToRemote(int $$0, MLFluidStack $$1, Supplier<MLFluidStack> $$2) {
       // if (!this.suppressRemoteUpdates) {
            MLFluidStack $$3 = this.remoteFluidSlots.get($$0);
            if (!$$3.isFluidStackIdentical($$1)) {
                MLFluidStack $$4 = $$2.get();
                this.remoteFluidSlots.set($$0, $$4);
          //      if (this.synchronizer != null) {
          //          this.synchronizer.sendSlotChange(this, $$0, $$4);
         //       }



            }
     //   }
    }

    @Override
    public void sendAllDataToRemote() {
        super.sendAllDataToRemote();
        Services.PLATFORM.sendToClient(new S2CInitialSyncFluidInventoryPacket(containerId, incrementStateId(), fluidInventory.fluids), (ServerPlayer) playerInventory.player);
    }

    public void setFluid(int slot, int stateId, MLFluidStack stack) {
        this.getFluidSlot(slot).set(stack);
        this.stateId = stateId;
    }

    public FluidSlot getFluidSlot(int $$0) {
        return this.fluidSlots.get($$0);
    }
    
    //////////////////////////////////////////////////////////////////////

    public static AbstractTankMenu t1(int id, Inventory inv) {
        return t1s(id, inv,  FluidInventory.dummy(TankStats.one));
    }

    public static AbstractTankMenu t2(int id, Inventory inv) {
        return t2s(id, inv,  FluidInventory.dummy(TankStats.two));
    }

    public static AbstractTankMenu t3(int id, Inventory inv) {
        return t3s(id, inv,  FluidInventory.dummy(TankStats.three));
    }

    public static AbstractTankMenu t4(int id, Inventory inv) {
        return t4s( id, inv,  FluidInventory.dummy(TankStats.four));
    }

    public static AbstractTankMenu t5(int id, Inventory inv) {
        return t5s( id, inv,  FluidInventory.dummy(TankStats.five));
    }

    public static AbstractTankMenu t6(int id, Inventory inv) {
        return t6s(id, inv,  FluidInventory.dummy(TankStats.six));
    }

    public static AbstractTankMenu t7(int id, Inventory inv) {
        return t7s(id, inv, FluidInventory.dummy(TankStats.seven));
    }

    public static AbstractTankMenu t1s(int id, Inventory inv, FluidInventory FluidInventory) {
        return new AbstractTankMenu(ModMenuTypes.TANK_1, id, inv, FluidInventory);
    }

    public static AbstractTankMenu t2s(int id, Inventory inv, FluidInventory FluidInventory) {
        return new AbstractTankMenu(ModMenuTypes.TANK_2, id, inv, FluidInventory);
    }

    public static AbstractTankMenu t3s(int id, Inventory inv, FluidInventory FluidInventory) {
        return new AbstractTankMenu(ModMenuTypes.TANK_3, id, inv, FluidInventory);
    }

    public static AbstractTankMenu t4s(int id, Inventory inv, FluidInventory FluidInventory) {
        return new AbstractTankMenu(ModMenuTypes.TANK_4, id, inv, FluidInventory);
    }

    public static AbstractTankMenu t5s(int id, Inventory inv, FluidInventory FluidInventory) {
        return new AbstractTankMenu(ModMenuTypes.TANK_5, id, inv, FluidInventory);
    }

    public static AbstractTankMenu t6s(int id, Inventory inv, FluidInventory FluidInventory) {
        return new AbstractTankMenu(ModMenuTypes.TANK_6, id, inv, FluidInventory);
    }

    public static AbstractTankMenu t7s(int id, Inventory inv, FluidInventory FluidInventory) {
        return new AbstractTankMenu(ModMenuTypes.TANK_7, id, inv, FluidInventory);
    }
    
}
