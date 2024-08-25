package tfar.tanknull.inventory;

import tfar.tanknull.MLFluidStack;

import java.util.Comparator;

public enum SortingType {

    descending((stack1, stack2) -> stack2.getAmount() - stack1.getAmount()),
    ascending(Comparator.comparingInt(MLFluidStack::getAmount));

    public final Comparator<MLFluidStack> comparator;

    SortingType(Comparator<MLFluidStack> comparator) {
        this.comparator = comparator;
    }

}
