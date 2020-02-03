package tfar.tanknull.inventory;

import net.minecraft.util.math.MathHelper;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Shameless clone of ItemHandlerHelper for fluids
 */
public class FluidHandlerHelper {

  public static int insertFluid(IFluidHandler dest, @Nonnull FluidStack stack, IFluidHandler.FluidAction simulate) {
    return dest == null || stack.isEmpty() ? 0 : dest.fill(stack, simulate);
  }

  /**
   * This method uses the standard vanilla algorithm to calculate a comparator output for how "full" the inventory is.
   * This method is an adaptation of Container#calcRedstoneFromInventory(IInventory).
   *
   * @param fluidinv The fluid handler to test.
   * @return A redstone value in the range [0,15] representing how "full" this inventory is.
   */
  public static int calcRedstoneFromInventory(@Nullable IFluidHandler fluidinv) {
    if (fluidinv == null) {
      return 0;
    } else {
      int fluidsFound = 0;
      float proportion = 0.0F;

      for (int j = 0; j < fluidinv.getTanks(); ++j) {
        FluidStack itemstack = fluidinv.getFluidInTank(j);

        if (!itemstack.isEmpty()) {
          proportion += (float) itemstack.getAmount() / (float) fluidinv.getTankCapacity(j);
          ++fluidsFound;
        }
      }

      proportion = proportion / (float) fluidinv.getTanks();
      return MathHelper.floor(proportion * 14.0F) + (fluidsFound > 0 ? 1 : 0);
    }
  }
}
