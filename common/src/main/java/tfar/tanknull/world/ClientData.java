package tfar.tanknull.world;

import tfar.tanknull.MLFluidStack;

import java.util.ArrayList;
import java.util.List;

public class ClientData {

    public static List<MLFluidStack> cached = new ArrayList<>();

    public static void setList(List<MLFluidStack> stacks) {
        cached = stacks;
    }
}
