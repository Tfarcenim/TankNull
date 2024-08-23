package tfar.tanknull.world;

import tfar.tanknull.MLFluidStack;

import java.util.List;

public class ClientData {

    public static MLFluidStack selected = MLFluidStack.EMPTY;

    public static List<MLFluidStack> cached;

    public static void setData(MLFluidStack selected) {
        ClientData.selected = selected;
    }

    public static void setList(List<MLFluidStack> stacks) {
        cached = stacks;
    }
}
