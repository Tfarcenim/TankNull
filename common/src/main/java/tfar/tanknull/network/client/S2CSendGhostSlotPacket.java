package tfar.tanknull.network.client;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Player;
import tfar.tanknull.MLFluidStack;
import tfar.tanknull.client.ModClient;
import tfar.tanknull.menu.AbstractTankMenu;

public class S2CSendGhostSlotPacket implements S2CModPacket {

    int windowId;
    int slot;
    MLFluidStack stack;

    public S2CSendGhostSlotPacket(int windowId, int slot, MLFluidStack stack) {
        this.windowId = windowId;
        this.slot = slot;
        this.stack = stack;
    }

    public S2CSendGhostSlotPacket(FriendlyByteBuf buf) {
        windowId = buf.readInt();
        slot = buf.readInt();
        stack = MLFluidStack.readFromPacket(buf);
    }

    @Override
    public void write(FriendlyByteBuf buf) {
        buf.writeInt(windowId);
        buf.writeInt(slot);
        stack.writeToPacket(buf);
    }

    @Override
    public void handleClient() {
        Player player = ModClient.getLocalPlayer();
        if (player != null && player.containerMenu instanceof AbstractTankMenu dankMenu && windowId == player.containerMenu.containerId) {
         //   dankMenu.dankInventory.setGhostItem(slot,stack.getItem());
        }
    }
}