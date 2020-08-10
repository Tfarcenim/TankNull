package tfar.tanknull.container;

import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;
import tfar.tanknull.RegistryObjects;
import tfar.tanknull.TankStats;
import tfar.tanknull.Utils;
import tfar.tanknull.inventory.FluidStackHandler;
import tfar.tanknull.inventory.TankNullItemStackFluidStackHandler;

public class ItemStackTankNullMenu extends BlockTankNullMenu {


	//client
	public ItemStackTankNullMenu(ContainerType<?> type,int id, PlayerInventory inv, TankStats stats) {
		super(type,id, inv,stats,read(inv));
		addPlayerSlots(inv.currentItem);
	}

	//server
	public ItemStackTankNullMenu(ContainerType<?> type, int id, PlayerInventory inv, TankStats stats, FluidStackHandler fluidStackHandler) {
		super(type, id, inv,stats,fluidStackHandler);
		addPlayerSlots(inv.currentItem);
	}

	protected void addPlayerSlots(int locked) {
		int yStart = 32 + 18 * 3;
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
			if (row != locked)
				this.addSlot(new Slot(inv, row, x, y));
			else
				this.addSlot(new LockedSlot(inv, row, x, y));
		}
	}

	//client
	public static ItemStackTankNullMenu t1c(int i, PlayerInventory playerInventory) {
		return new ItemStackTankNullMenu(RegistryObjects.tank_1_portable,i,playerInventory, TankStats.one);
	}

	public static ItemStackTankNullMenu t2c(int i, PlayerInventory playerInventory) {
		return new ItemStackTankNullMenu(RegistryObjects.tank_2_portable,i,playerInventory,TankStats.two);
	}

	public static ItemStackTankNullMenu t3c(int i, PlayerInventory playerInventory) {
		return new ItemStackTankNullMenu(RegistryObjects.tank_3_portable,i,playerInventory,TankStats.three);
	}

	public static ItemStackTankNullMenu t4c(int i, PlayerInventory playerInventory) {
		return new ItemStackTankNullMenu(RegistryObjects.tank_4_portable,i,playerInventory,TankStats.four);
	}

	public static ItemStackTankNullMenu t5c(int i, PlayerInventory playerInventory) {
		return new ItemStackTankNullMenu(RegistryObjects.tank_5_portable,i,playerInventory,TankStats.five);
	}

	public static ItemStackTankNullMenu t6c(int i, PlayerInventory playerInventory) {
		return new ItemStackTankNullMenu(RegistryObjects.tank_6_portable,i,playerInventory,TankStats.six);
	}

	public static ItemStackTankNullMenu t7c(int i, PlayerInventory playerInventory) {
		return new ItemStackTankNullMenu(RegistryObjects.tank_7_portable,i,playerInventory,TankStats.seven);
	}

	public static FluidStackHandler read(PlayerInventory inv) {
		ItemStack stack = inv.player.getHeldItemMainhand();
		CompoundNBT nbt = stack.getTag();
		if (nbt == null) {
			TankStats stats = Utils.getStats(stack);
			return new FluidStackHandler(stats.slots,stats.capacity);
		} else {
			TankStats stats = Utils.getStats(stack);
			FluidStackHandler handler = new TankNullItemStackFluidStackHandler(stats.slots,stats.capacity,stack);
			handler.deserializeNBT(nbt.getCompound(Utils.FLUIDINV));
			return handler;
		}
	}

	//server

	public static ItemStackTankNullMenu t1s(int i, PlayerInventory playerInventory,FluidStackHandler fluidStackHandler) {
		return new ItemStackTankNullMenu(RegistryObjects.tank_1_portable,i,playerInventory, TankStats.one,fluidStackHandler);
	}

	public static ItemStackTankNullMenu t2s(int i, PlayerInventory playerInventory, FluidStackHandler fluidStackHandler) {
		return new ItemStackTankNullMenu(RegistryObjects.tank_2_portable,i,playerInventory,TankStats.two,fluidStackHandler);
	}

	public static ItemStackTankNullMenu t3s(int i, PlayerInventory playerInventory, FluidStackHandler fluidStackHandler) {
		return new ItemStackTankNullMenu(RegistryObjects.tank_3_portable,i,playerInventory,TankStats.three,fluidStackHandler);
	}

	public static ItemStackTankNullMenu t4s(int i, PlayerInventory playerInventory, FluidStackHandler fluidStackHandler) {
		return new ItemStackTankNullMenu(RegistryObjects.tank_4_portable,i,playerInventory,TankStats.four,fluidStackHandler);
	}

	public static ItemStackTankNullMenu t5s(int i, PlayerInventory playerInventory, FluidStackHandler fluidStackHandler) {
		return new ItemStackTankNullMenu(RegistryObjects.tank_5_portable,i,playerInventory,TankStats.five,fluidStackHandler);
	}

	public static ItemStackTankNullMenu t6s(int i, PlayerInventory playerInventory, FluidStackHandler fluidStackHandler) {
		return new ItemStackTankNullMenu(RegistryObjects.tank_6_portable,i,playerInventory,TankStats.six,fluidStackHandler);
	}

	public static ItemStackTankNullMenu t7s(int i, PlayerInventory playerInventory, FluidStackHandler fluidStackHandler) {
		return new ItemStackTankNullMenu(RegistryObjects.tank_7_portable,i,playerInventory,TankStats.seven,fluidStackHandler);
	}
}
