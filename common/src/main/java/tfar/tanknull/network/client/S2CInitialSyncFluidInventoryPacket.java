package tfar.tanknull.network.client;

import net.minecraft.core.NonNullList;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Player;
import tfar.tanknull.MLFluidStack;
import tfar.tanknull.client.ModClient;
import tfar.tanknull.menu.AbstractTankMenu;


public class S2CInitialSyncFluidInventoryPacket implements S2CModPacket {

    private final int stateID;
    private final int containerID;
    private final NonNullList<MLFluidStack> stacks;

    public S2CInitialSyncFluidInventoryPacket(int stateID, int containerID, NonNullList<MLFluidStack> stacks) {
        this.stateID = stateID;
        this.containerID = containerID;
        this.stacks = stacks;
    }

    public S2CInitialSyncFluidInventoryPacket(FriendlyByteBuf buf) {
        stateID = buf.readInt();
        containerID = buf.readInt();
        int i = buf.readShort();
        stacks = NonNullList.withSize(i, MLFluidStack.EMPTY);
        for(int j = 0; j < i; ++j) {
            stacks.set(j, MLFluidStack.readFromPacket(buf));
        }
    }

    @Override
    public void handleClient() {
        Player player = ModClient.getLocalPlayer();
        if (player != null && player.containerMenu instanceof AbstractTankMenu abstractTankMenu && containerID == player.containerMenu.containerId) {
            abstractTankMenu.initializeFluids(stateID, stacks);
        }
    }

    @Override
    public void write(FriendlyByteBuf buf) {
        buf.writeInt(stateID);
        buf.writeInt(containerID);
        buf.writeShort(stacks.size());
        for (MLFluidStack stack : stacks) {
            stack.writeToPacket(buf);
        }
    }
}