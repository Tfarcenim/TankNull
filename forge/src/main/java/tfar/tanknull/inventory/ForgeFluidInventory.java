package tfar.tanknull.inventory;

import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import org.jetbrains.annotations.NotNull;
import tfar.tanknull.TankStats;
import tfar.tanknull.platform.ForgePlatformHelper;

public class ForgeFluidInventory extends FluidInventory implements IFluidHandler {


    public ForgeFluidInventory(int slots,int capacity) {
        super(slots,capacity);
    }

    public ForgeFluidInventory(TankStats stats) {
        super(stats);
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
        return ForgePlatformHelper.convertToForge(drain(ForgePlatformHelper.convert(resource),ForgePlatformHelper.action(action)));
    }

    @Override
    public @NotNull FluidStack drain(int maxDrain, IFluidHandler.FluidAction action) {
        return ForgePlatformHelper.convertToForge(drain(maxDrain,ForgePlatformHelper.action(action)));
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
        return ForgePlatformHelper.convertToForge(getFluid(tank));
    }

}
