package tfar.tanknull.menu;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.ItemStack;
import tfar.tanknull.TankItem;
import tfar.tanknull.Utils;
import tfar.tanknull.init.ModMenuTypes;
import tfar.tanknull.inventory.FluidInventory;
import tfar.tanknull.inventory.LockedSlot;
import tfar.tanknull.inventory.SortingType;

public class TankConfigMenu extends AbstractContainerMenu {


    private final Container container = new SimpleContainer(1);
    private final ContainerData data;

    public TankConfigMenu(int id, Inventory inventory) {
        this(id,inventory,ItemStack.EMPTY,new SimpleContainerData(2));
    }

    public TankConfigMenu(int id, Inventory inventory, ItemStack bag,ContainerData data) {
        super(ModMenuTypes.CONFIG, id);
        this.data = data;
        container.setItem(0,bag);

        Slot slot = new LockedSlot(container,0,-100,-100) {
            @Override
            public boolean isActive() {
                return false;
            }
        };
        addSlot(slot);
        addDataSlots(data);
    }

    public ItemStack getTank() {
        return container.getItem(0);
    }

    public SortingType getSortingType() {
        return SortingType.values()[data.get(0)];
    }

    public boolean autoSort() {
        return data.get(1) != 0;
    }

    public enum ButtonAction {
        CHANGE_SORT_TYPE,
        TOGGLE_AUTO_SORT,
        CLOSE;
        static final ButtonAction[] VALUES = values();
    }

    @Override
    public ItemStack quickMoveStack(Player player, int i) {
        return ItemStack.EMPTY;
    }

    @Override
    public boolean stillValid(Player player) {
        return getTank().getItem() instanceof TankItem;
    }

    @Override
    public boolean clickMenuButton(Player player, int id) {
        if (id < 0 || id >= ButtonAction.VALUES.length) return false;
        ButtonAction buttonAction = ButtonAction.VALUES[id];
        if (player instanceof ServerPlayer serverPlayer) {
            switch (buttonAction) {
                case CHANGE_SORT_TYPE -> {
                    FluidInventory fluidInventory = TankItem.getInventoryFrom(getTank(), serverPlayer.server);
                    if (fluidInventory != null) {
                        fluidInventory.setSortingType(Utils.cycle(fluidInventory.getSortingType()));
                    }
                }
                case TOGGLE_AUTO_SORT -> {
                    FluidInventory fluidInventory = TankItem.getInventoryFrom(getTank(), serverPlayer.server);
                    if (fluidInventory != null) {
                        fluidInventory.toggleAutoSort();
                    }
                }
                case CLOSE -> {
                    if (getTank().getItem() instanceof TankItem tankItem) {
                        player.openMenu(tankItem.createProvider(getTank()));
                    }
                }//toggleFreqLock();
              //  case SORT -> dankInventory.sort();
              //  case COMPRESS -> dankInventory.compress(serverPlayer);
             //   case TOGGLE_TAG -> CommonUtils.toggleTagMode(serverPlayer);
            //    case TOGGLE_PICKUP -> CommonUtils.togglePickupMode(serverPlayer);
            }
        }
        return false;
    }

}
