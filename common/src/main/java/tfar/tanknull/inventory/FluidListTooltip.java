package tfar.tanknull.inventory;

import tfar.tanknull.MLFluidStack;

import java.util.List;

public class FluidListTooltip extends StackListTooltip<MLFluidStack> {
    public FluidListTooltip(List<MLFluidStack> stacks, int selected) {
        super(stacks, selected);
    }
}
