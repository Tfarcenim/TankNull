package tfar.tanknull.inventory;

import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import org.jetbrains.annotations.NotNull;
import tfar.tanknull.TankStats;
import tfar.tanknull.platform.ForgePlatformHelper;
import tfar.tanknull.world.TankSavedData;

public class ForgeFluidInventory extends FluidInventory implements IFluidHandler {


    public ForgeFluidInventory(int slots,int capacity,TankSavedData data) {
        super(slots,capacity,data);
    }

    public ForgeFluidInventory(TankStats stats, TankSavedData data) {
        super(stats,data);
    }

    public ForgeSlot makeWrapper(int tank) {
        return new ForgeSlot(tank);
    }

    public class ForgeSlot extends Slot implements IFluidHandler {
        ForgeSlot(int slot) {
            super(slot);
        }

        @Override
        public int getTanks() {
            return 1;
        }

        @Override
        public @NotNull FluidStack getFluidInTank(int tank) {
            return ForgeFluidInventory.this.getFluidInTank(slot);
        }

        @Override
        public int getTankCapacity(int tank) {
            return capacity;
        }

        @Override
        public boolean isFluidValid(int tank, @NotNull FluidStack stack) {
            return ForgeFluidInventory.this.isFluidValid(slot,stack);
        }

        @Override
        public int fill(FluidStack resource, FluidAction action) {
            return ForgeFluidInventory.this.fillSpecific(ForgePlatformHelper.convert(resource),ForgePlatformHelper.action(action),slot);
        }

        @Override
        public @NotNull FluidStack drain(FluidStack resource, FluidAction action) {
            return ForgePlatformHelper.convert(ForgeFluidInventory.this.drainSpecific(ForgePlatformHelper.convert(resource),ForgePlatformHelper.action(action),slot));
        }

        @Override
        public @NotNull FluidStack drain(int maxDrain, FluidAction action) {
            return ForgePlatformHelper.convert(ForgeFluidInventory.this.drainSpecific(maxDrain,ForgePlatformHelper.action(action),slot));
        }
    }

    ////////////////////////////////////////delegates to API//////////////////////////////////////////

    @Override
    public boolean isFluidValid(int tank, @NotNull FluidStack stack) {
        return isFluidValid(tank, ForgePlatformHelper.convert(stack));
    }

    @Override
    public int fill(FluidStack resource, IFluidHandler.FluidAction action) {
        return fill(ForgePlatformHelper.convert(resource),ForgePlatformHelper.action(action));
    }

    @Override
    public @NotNull FluidStack drain(FluidStack resource, IFluidHandler.FluidAction action) {
        return ForgePlatformHelper.convert(drain(ForgePlatformHelper.convert(resource),ForgePlatformHelper.action(action)));
    }

    @Override
    public @NotNull FluidStack drain(int maxDrain, IFluidHandler.FluidAction action) {
        return ForgePlatformHelper.convert(drain(maxDrain,ForgePlatformHelper.action(action)));
    }

    @Override
    public int getTankCapacity(int tank) {
        return getTankSize(tank);
    }

    @Override
    public int getTanks() {
        return getSlots();
    }

    @Override
    public @NotNull FluidStack getFluidInTank(int tank) {
        return ForgePlatformHelper.convert(getFluid(tank));
    }

}
