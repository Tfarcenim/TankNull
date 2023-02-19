package tfar.tanknull;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.Direction;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandlerItem;
import net.minecraftforge.fml.loading.FMLEnvironment;
import tfar.tanknull.inventory.TankNullItemStackFluidStackHandler;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class Utils {

	public static final String FLUIDINV = "fluidinv";


	public static TankStats getStats(ItemStack tank) {
		return ((TankNullItem) tank.getItem()).stats;
	}

	public static int getTanks(Item block) {
		return ((TankNullItem) block).stats.slots;
	}

	public static Item getItem(int tier) {
		switch (tier) {
			case 1:
				return RegistryObjects.tank_1;
			case 2:
				return RegistryObjects.tank_2;
			case 3:
				return RegistryObjects.tank_3;
			case 4:
				return RegistryObjects.tank_4;
			case 5:
				return RegistryObjects.tank_5;
			case 6:
				return RegistryObjects.tank_6;
			case 7:
				return RegistryObjects.tank_7;
		}
		throw new IllegalArgumentException("no " + tier);
	}

	public static int getTanks(ItemStack stack) {
		Item block = stack.getItem();
		return getTanks(block);
	}

	public static int getCapacity(ItemStack stack) {
		Item block = stack.getItem();
		return getCapacity(block);
	}

	public static int getCapacity(Item block) {
		return ((TankNullItem) block).stats.capacity;
	}

	public static final boolean DEV = FMLEnvironment.production;

	public static boolean isFill(ItemStack bag, PlayerEntity player) {
		return TankNullItemStackFluidStackHandler.create(bag).isFill;
	}

	public static void toggleFill(ItemStack bag, PlayerEntity player) {
		TankNullItemStackFluidStackHandler handler = TankNullItemStackFluidStackHandler.create(bag);
		handler.toggleFill();
		player.sendStatusMessage(
						new TranslationTextComponent("text." + TankNull.MODID + ".mode." + (handler.isFill ? "fill" : "empty")), true);
	}

	public static boolean isSponge(ItemStack bag, PlayerEntity player) {
		return TankNullItemStackFluidStackHandler.create(bag).sponge;
	}

	public static void toggleSponge(ItemStack bag, PlayerEntity player) {
		TankNullItemStackFluidStackHandler.create(bag).toggleSponge();
	}

	public static void changeSlot(ItemStack bag, boolean right) {
		TankNullItemStackFluidStackHandler.create(bag).scroll(right);
	}

	public static Item.Properties copy(Item from) {
		return new Item.Properties()
						.containerItem(from.getContainerItem())
						.maxDamage(from.getMaxDamage())
						.maxStackSize(from.getMaxStackSize())
						.group(from.getGroup())
						.food(from.getFood())
						.rarity(new ItemStack(from).getRarity());
	}

	public static ICapabilityProvider createFluidProvider(IFluidHandlerItem iFluidHandler) {
		return new Provider(iFluidHandler);
	}

	public static class Provider implements ICapabilityProvider {
		final LazyOptional<IFluidHandler> capability;

		Provider(IFluidHandler iFluidHandler) {
			this.capability = LazyOptional.of(() -> iFluidHandler);
		}

		@Nonnull
		public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
			return CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY.orEmpty(cap, this.capability);
		}
	}

}
