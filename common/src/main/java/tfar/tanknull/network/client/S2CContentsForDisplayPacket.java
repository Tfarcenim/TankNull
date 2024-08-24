package tfar.tanknull.network.client;

import net.minecraft.core.NonNullList;
import net.minecraft.network.FriendlyByteBuf;
import tfar.tanknull.MLFluidStack;
import tfar.tanknull.world.ClientData;

import java.util.List;

public class S2CContentsForDisplayPacket implements S2CModPacket {
    private final List<MLFluidStack> stacks;

    public S2CContentsForDisplayPacket(NonNullList<MLFluidStack> stacks) {
        this.stacks = stacks;
    }

    public S2CContentsForDisplayPacket(FriendlyByteBuf buf) {
        stacks = buf.readList(MLFluidStack::readFromPacket);
    }

    @Override
    public void handleClient() {
            ClientData.setList(stacks);
    }

    @Override
    public void write(FriendlyByteBuf buf) {
        buf.writeCollection(stacks,(buf1, stack) -> stack.writeToPacket(buf1));
    }
}