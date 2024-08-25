package tfar.tanknull.menu;

import com.google.common.base.Suppliers;
import net.minecraft.core.NonNullList;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Container;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;
import tfar.tanknull.MLFluidStack;
import tfar.tanknull.TankStats;
import tfar.tanknull.init.ModMenuTypes;
import tfar.tanknull.inventory.FluidInventory;
import tfar.tanknull.inventory.FluidSlot;
import tfar.tanknull.inventory.LockedSlot;
import tfar.tanknull.inventory.SortingType;
import tfar.tanknull.network.client.S2CInitialSyncFluidInventoryPacket;
import tfar.tanknull.network.client.S2CSetFluidSlotPacket;
import tfar.tanknull.platform.Services;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.Objects;
import java.util.function.Supplier;

public class TankMenu extends AbstractContainerMenu {

    public final Inventory playerInventory;
    public final int rows;
    public final FluidInventory fluidInventory;

    private final NonNullList<MLFluidStack> lastFluidSlots = NonNullList.create();
    public final NonNullList<FluidSlot> fluidSlots = NonNullList.create();
    private final NonNullList<MLFluidStack> remoteFluidSlots = NonNullList.create();
    private final NonNullList<MLFluidStack> remoteGhostFluidSlots = NonNullList.create();

    private final Container holder = new SimpleContainer(1);

    public void initializeFluids(int stateID, List<MLFluidStack> stacks, List<MLFluidStack> ghostStacks) {
        for(int i = 0; i < stacks.size(); ++i) {
            FluidSlot fluidSlot = getFluidSlot(i);
            fluidSlot.setFluid(stacks.get(i));
            fluidSlot.setGhost(ghostStacks.get(i));
        }
        this.stateId = stateID;
    }

    public enum ButtonAction {
        LOCK_FREQUENCY, SORT,OPEN_CONFIG;
        static final ButtonAction[] VALUES = values();
    }


    public TankMenu(MenuType<?> type, int windowId, Inventory playerInventory, FluidInventory fluidInventory,ItemStack stack) {
        super(type, windowId);
        this.playerInventory = playerInventory;
        this.fluidInventory = fluidInventory;
        this.rows = fluidInventory.getSlots()/9;
        holder.setItem(0,stack);
        addPlayerSlots(playerInventory,-1);
        addTankSlots();
    }

    protected FluidSlot addFluidSlot(FluidSlot slot) {
        slot.index = this.fluidSlots.size();
        this.fluidSlots.add(slot);
        this.lastFluidSlots.add(MLFluidStack.EMPTY);
        this.remoteFluidSlots.add(MLFluidStack.EMPTY);
        remoteGhostFluidSlots.add(MLFluidStack.EMPTY);
        return slot;
    }

    protected void addTankSlots() {

        addSlot(new LockedSlot(playerInventory,0,-100,-100){
            @Override
            public boolean isActive() {
                return false;
            }
        });

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
        int yStart = 31 + 18 * rows;
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
               case SORT -> fluidInventory.sort();
                case OPEN_CONFIG -> player.openMenu(new ConfigMenuProvider(holder.getItem(0)));
            }
        }
        return true;
    }

    public class ConfigMenuProvider implements MenuProvider {

        private final ItemStack stack;

        ConfigMenuProvider(ItemStack stack) {

            this.stack = stack;
        }
        @Override
        public Component getDisplayName() {
            return Component.literal("Tank Configs");
        }

        @Nullable
        @Override
        public AbstractContainerMenu createMenu(int i, Inventory inventory, Player player) {
            return new TankConfigMenu(i,inventory,stack,fluidInventory);
        }
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

    @Override
    public void broadcastChanges() {
        super.broadcastChanges();
        //the remote inventory needs to know about locked slots
        for(int i = 0; i < this.fluidSlots.size(); ++i) {
            MLFluidStack fluid = this.fluidSlots.get(i).getFluid();
            MLFluidStack ghost = this.fluidSlots.get(i).getGhost();
            Supplier<MLFluidStack> supplier = Suppliers.memoize(fluid::copy);
            Supplier<MLFluidStack> ghostSupplier = Suppliers.memoize(ghost::copy);
            //this.triggerSlotListeners(i, fluid, supplier);
            this.synchronizeFluidSlotToRemote(i, fluid,ghost, supplier,ghostSupplier);
        }
    }

    private void synchronizeFluidSlotToRemote(int slot, MLFluidStack stack, MLFluidStack ghost, Supplier<MLFluidStack> supplier,Supplier<MLFluidStack> ghostSupplier) {
       // if (!this.suppressRemoteUpdates) {
            MLFluidStack remoteFluid = this.remoteFluidSlots.get(slot);
            MLFluidStack remoteGhostFluid = this.remoteGhostFluidSlots.get(slot);
            if (!Objects.equals(remoteFluid,stack) || !Objects.equals(remoteGhostFluid,ghost)) {
                MLFluidStack copy = supplier.get();
                MLFluidStack ghostCopy = ghostSupplier.get();
                this.remoteFluidSlots.set(slot, copy);
                this.remoteGhostFluidSlots.set(slot, ghostCopy);
                Services.PLATFORM.sendToClient(new S2CSetFluidSlotPacket(stateId,containerId,slot,stack,ghostCopy), (ServerPlayer) playerInventory.player);

          //      if (this.synchronizer != null) {
          //          this.synchronizer.sendSlotChange(this, slot, copy);
         //       }

           }
     //   }
    }

    @Override
    public void sendAllDataToRemote() {
        super.sendAllDataToRemote();

        for(int i = 0; i < this.fluidSlots.size(); i++) {
            FluidSlot fluidSlot = fluidSlots.get(i);
            this.remoteFluidSlots.set(i, fluidSlot.getFluid().copy());
            this.remoteGhostFluidSlots.set(i,fluidSlot.getGhost().copy());
        }


        Services.PLATFORM.sendToClient(new S2CInitialSyncFluidInventoryPacket(incrementStateId(), containerId, fluidInventory.fluids,fluidInventory.ghostFluids), (ServerPlayer) playerInventory.player);
    }

    public void setFluid(int slot, int stateId, MLFluidStack stack, MLFluidStack ghost) {
        this.getFluidSlot(slot).setFluid(stack);
        this.getFluidSlot(slot).setGhost(ghost);
        this.stateId = stateId;
    }

    public FluidSlot getFluidSlot(int slot) {
        return this.fluidSlots.get(slot);
    }
    
    //////////////////////////////////////////////////////////////////////

    public static TankMenu t1(int id, Inventory inv) {
        return t1s(id, inv,  FluidInventory.dummy(TankStats.one), ItemStack.EMPTY);
    }

    public static TankMenu t2(int id, Inventory inv) {
        return t2s(id, inv,  FluidInventory.dummy(TankStats.two), ItemStack.EMPTY);
    }

    public static TankMenu t3(int id, Inventory inv) {
        return t3s(id, inv,  FluidInventory.dummy(TankStats.three), ItemStack.EMPTY);
    }

    public static TankMenu t4(int id, Inventory inv) {
        return t4s( id, inv,  FluidInventory.dummy(TankStats.four), ItemStack.EMPTY);
    }

    public static TankMenu t5(int id, Inventory inv) {
        return t5s( id, inv,  FluidInventory.dummy(TankStats.five),  ItemStack.EMPTY);
    }

    public static TankMenu t6(int id, Inventory inv) {
        return t6s(id, inv,  FluidInventory.dummy(TankStats.six),  ItemStack.EMPTY);
    }

    public static TankMenu t7(int id, Inventory inv) {
        return t7s(id, inv, FluidInventory.dummy(TankStats.seven), ItemStack.EMPTY);
    }

    public static TankMenu t1s(int id, Inventory inv, FluidInventory fluidInventory, ItemStack stack) {
        return new TankMenu(ModMenuTypes.TANK_1, id, inv, fluidInventory, stack);
    }

    public static TankMenu t2s(int id, Inventory inv, FluidInventory fluidInventory, ItemStack stack) {
        return new TankMenu(ModMenuTypes.TANK_2, id, inv, fluidInventory, stack);
    }

    public static TankMenu t3s(int id, Inventory inv, FluidInventory fluidInventory, ItemStack stack) {
        return new TankMenu(ModMenuTypes.TANK_3, id, inv, fluidInventory, stack);
    }

    public static TankMenu t4s(int id, Inventory inv, FluidInventory fluidInventory, ItemStack stack) {
        return new TankMenu(ModMenuTypes.TANK_4, id, inv, fluidInventory, stack);
    }

    public static TankMenu t5s(int id, Inventory inv, FluidInventory fluidInventory, ItemStack stack) {
        return new TankMenu(ModMenuTypes.TANK_5, id, inv, fluidInventory, stack);
    }

    public static TankMenu t6s(int id, Inventory inv, FluidInventory fluidInventory, ItemStack stack) {
        return new TankMenu(ModMenuTypes.TANK_6, id, inv, fluidInventory, stack);
    }

    public static TankMenu t7s(int id, Inventory inv, FluidInventory fluidInventory, ItemStack stack) {
        return new TankMenu(ModMenuTypes.TANK_7, id, inv, fluidInventory, stack);
    }
    
}
