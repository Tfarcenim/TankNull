package tfar.tanknull.network.client;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Player;
import tfar.tanknull.MLFluidStack;
import tfar.tanknull.client.ModClient;
import tfar.tanknull.menu.AbstractTankMenu;

public class S2CSetFluidSlotPacket implements S2CModPacket{

    private final int containerId;
    private final int stateId;
    private final int slot;
    private final MLFluidStack stack;

    public S2CSetFluidSlotPacket(int pContainerId, int pStateId, int pSlot, MLFluidStack stack) {
        this.containerId = pContainerId;
        this.stateId = pStateId;
        this.slot = pSlot;
        this.stack = stack;
    }

    public S2CSetFluidSlotPacket(FriendlyByteBuf pBuffer) {
        this.containerId = pBuffer.readByte();
        this.stateId = pBuffer.readVarInt();
        this.slot = pBuffer.readShort();
        this.stack = MLFluidStack.readFromPacket(pBuffer);
    }

    @Override
    public void handleClient() {
        Player player = ModClient.getLocalPlayer();
        if (containerId == player.containerMenu.containerId && player.containerMenu instanceof AbstractTankMenu abstractTankMenu) {
            abstractTankMenu.setFluid(slot, stateId, stack);
        }
    }

    @Override
    public void write(FriendlyByteBuf to) {

    }
}
