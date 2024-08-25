package tfar.tanknull.network.client;

import net.minecraft.core.NonNullList;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Player;
import tfar.tanknull.MLFluidStack;
import tfar.tanknull.client.ModClient;
import tfar.tanknull.menu.AbstractTankMenu;
import tfar.tanknull.network.PacketHandler;

import java.util.List;


public class S2CInitialSyncFluidInventoryPacket implements S2CModPacket {

    private final int stateID;
    private final int containerID;
    private final List<MLFluidStack> stacks;
    private final List<MLFluidStack> ghostStacks;

    public S2CInitialSyncFluidInventoryPacket(int stateID, int containerID, NonNullList<MLFluidStack> stacks,NonNullList<MLFluidStack> ghostStacks) {
        this.stateID = stateID;
        this.containerID = containerID;
        this.stacks = stacks;
        this.ghostStacks = ghostStacks;
    }

    public S2CInitialSyncFluidInventoryPacket(FriendlyByteBuf buf) {
        stateID = buf.readInt();
        containerID = buf.readInt();
        stacks = buf.readList(PacketHandler.FLUID_READER);
        ghostStacks = buf.readList(PacketHandler.FLUID_READER);
    }

    @Override
    public void handleClient() {
        Player player = ModClient.getLocalPlayer();
        if (player != null && player.containerMenu instanceof AbstractTankMenu abstractTankMenu && containerID == player.containerMenu.containerId) {
            abstractTankMenu.initializeFluids(stateID, stacks,ghostStacks);
        }
    }

    @Override
    public void write(FriendlyByteBuf buf) {
        buf.writeInt(stateID);
        buf.writeInt(containerID);
        buf.writeCollection(stacks, PacketHandler.FLUID_WRITER);
        buf.writeCollection(ghostStacks,PacketHandler.FLUID_WRITER);
    }
}