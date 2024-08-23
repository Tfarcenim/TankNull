package tfar.tanknull.platform;

import net.minecraft.network.chat.Component;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import tfar.tanknull.MLFluidStack;
import tfar.tanknull.inventory.FluidInventory;
import tfar.tanknull.platform.services.IPlatformHelper;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.loading.FMLLoader;

public class ForgePlatformHelper implements IPlatformHelper {

    @Override
    public String getPlatformName() {

        return "Forge";
    }

    @Override
    public boolean isModLoaded(String modId) {

        return ModList.get().isLoaded(modId);
    }

    @Override
    public boolean isDevelopmentEnvironment() {

        return !FMLLoader.isProduction();
    }

    @Override
    public Component getDisplayName(MLFluidStack fluidStack) {
        return convertToForge(fluidStack).getDisplayName();
    }

    @Override
    public String getTranslationKey(MLFluidStack fluidStack) {
        return convertToForge(fluidStack).getTranslationKey();
    }

    public static FluidStack convertToForge(MLFluidStack fluidStack) {
        if (fluidStack.isEmpty()) {
            return FluidStack.EMPTY;
        } return new FluidStack(fluidStack.getFluid(),fluidStack.getAmount(),fluidStack.getTag());
    }

    public static MLFluidStack convert(FluidStack fluidStack) {
        if (fluidStack.isEmpty()) {
            return MLFluidStack.EMPTY;
        } return new MLFluidStack(fluidStack.getFluid(),fluidStack.getAmount(),fluidStack.getTag());
    }

    public static FluidInventory.Action action(IFluidHandler.FluidAction action) {
        switch (action){
            case EXECUTE -> {
                return FluidInventory.Action.EXECUTE;
            }
            case SIMULATE -> {
                return FluidInventory.Action.SIMULATE;
            }
        }
        return null;
    }
}