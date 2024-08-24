package tfar.tanknull.network.client;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Player;
import tfar.tanknull.MLFluidStack;
import tfar.tanknull.client.ModClient;
import tfar.tanknull.world.ClientData;


public class S2CSyncSelectedFluidPacket implements S2CModPacket {

    private final MLFluidStack stack;

    public S2CSyncSelectedFluidPacket(MLFluidStack stack) {
        this.stack = stack;
    }

    public S2CSyncSelectedFluidPacket(FriendlyByteBuf buf) {
        stack = MLFluidStack.readFromPacket(buf);
    }

    @Override
    public void handleClient() {
        Player player = ModClient.getLocalPlayer();
        if (player != null) {
            ClientData.setData(stack);
        }
    }

    @Override
    public void write(FriendlyByteBuf buf) {
        stack.writeToPacket(buf);
    }
}